package com.robypomper.josp.jod.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPConfigs;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.core.jcpclient.JCPClient_CliCredFlow;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Object default implementation of {@link JCPClient} interface.
 * <p>
 * This class initialize a JCPClient object that can be used by Objects.
 */
public class DefaultJCPClient_Object extends JCPClient_CliCredFlow
        implements JCPClient_Object {

    // Class constants

    public static final String TH_CONNECTION_NAME = "_JCP_CONNECTION_";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODSettings_002 locSettings;
    private Timer connectionTimer = null;


    // Constructor

    /**
     * Object JCPClient constructor, it setup the client for Object's requests.
     * <p>
     * If the {@link JODSettings_002#getJCPConnect()} return <code>true</code>
     * then this method connect the created client to the JCP.
     *
     * @param settings the JOD settings.
     */
    public DefaultJCPClient_Object(JODSettings_002 settings) throws ConnectionSettingsException {
        super(new DefaultJCPConfigs(settings.getJCPId(),
                        settings.getJCPSecret(),
                        "openid",
                        "",
                        settings.getJCPUrl(),
                        "jcp"),
                settings.getJCPConnect());
        this.locSettings = settings;
    }

    // Headers default values setters

    @Override
    public void setObjectId(String objId) {
        if (objId != null)
            addDefaultHeader(APIObjs.HEADER_OBJID, objId);
        else
            removeDefaultHeader(APIObjs.HEADER_OBJID);
    }

    @Override
    public void connect() throws CredentialsException {
        if (isConnected() || isConnecting())
            return;

        try {
            log.debug(Mrk_JOD.JOD_COMM_JCPCL, "Connecting JCPClient to JCP with client credential flow");
            super.connect();
            log.debug(Mrk_JOD.JOD_COMM_JCPCL, "JCPClient connected to JCP with client credential flow");

        } catch (ConnectionException e) {
            log.warn(Mrk_JOD.JOD_COMM_JCPCL, String.format("Error on connecting JCPClient to JCP because %s", e.getMessage()), e);
            startConnectionTimer();
        }
    }

    @Override
    public void disconnect() {
        if (!isConnected() && !isConnecting())
            return;

        if (isConnecting()) {
            stopConnectionTimer();
            return;
        }

        super.disconnect();
    }

    public boolean isConnecting() {
        return connectionTimer != null;
    }

    private void startConnectionTimer() {
        if (isConnecting())
            return;

        log.debug(Mrk_JOD.JOD_PERM, "Starting JCP Client connection's timer");
        connectionTimer = new Timer(true);
        connectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName(TH_CONNECTION_NAME);

                try {
                    log.debug(Mrk_JOD.JOD_PERM, "Connecting JCP Client");
                    DefaultJCPClient_Object.super.connect();
                    if (!isConnected()) {
                        log.debug(Mrk_JOD.JOD_PERM, "JCP Client NOT connected");
                        return;
                    }
                    stopConnectionTimer();
                    log.debug(Mrk_JOD.JOD_PERM, "JCP Client connected");

                } catch (ConnectionException ignore) {
                } catch (CredentialsException e) {
                    log.warn(Mrk_JOD.JOD_COMM_JCPCL, String.format("Error on authenticate JCPClient to JCP because %s", e.getMessage()), e);
                    stopConnectionTimer();
                }
            }
        }, 0, locSettings.getPermissionsRefreshTime() * 1000);
        log.debug(Mrk_JOD.JOD_PERM, "JCP Client connection's timer started");
    }

    private void stopConnectionTimer() {
        log.debug(Mrk_JOD.JOD_PERM, "Stopping JCP Client connection's timer");
        connectionTimer.cancel();
        connectionTimer = null;
        log.debug(Mrk_JOD.JOD_PERM, "JCP Client connection's timer stopped");
    }
}
