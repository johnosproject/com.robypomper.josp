package com.robypomper.josp.jod.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPConfigs;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.core.jcpclient.JCPClient_CliCredFlow;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Object default implementation of {@link JCPClient} interface.
 * <p>
 * This class initialize a JCPClient object that can be used by Objects.
 */
public class DefaultJCPClient_Object extends JCPClient_CliCredFlow
        implements JCPClient_Object {

    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    /**
     * Object JCPClient constructor, it setup the client for Object's requests.
     * <p>
     * Moreover this constructor disable SSL checks and bypass the
     * {@link com.robypomper.josp.core.jcpclient.AbsJCPClient} autoConnect
     * mechanism.
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
                false);

        try {
            log.debug(Mrk_JOD.JOD_COMM_JCPCL, "Connecting JCPClient to JCP with client credential flow");
            connect();
            log.debug(Mrk_JOD.JOD_COMM_JCPCL, "JCPClient connected to JCP with client credential flow");
        } catch (ConnectionException e) {
            log.warn(Mrk_JOD.JOD_COMM_JCPCL, String.format("Error on connecting JCPClient to JCP because %s", e.getMessage()), e);
        }
    }

    // Headers default values setters

    @Override
    public void setObjectId(String objId) {
        if (objId != null)
            addDefaultHeader(APIObjs.HEADER_OBJID, objId);
        else
            removeDefaultHeader(APIObjs.HEADER_OBJID);
    }

}
