package com.robypomper.josp.jsl.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPConfigs;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.core.jcpclient.JCPClient_AuthFlow;
import com.robypomper.josp.core.jcpclient.JCPClient_CliCredFlow;
import com.robypomper.josp.jcp.apis.paths.APISrvs;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Object default implementation of {@link JCPClient} interface.
 * <p>
 * This class initialize a JCPClient object that can be used by Services.
 */
public class DefaultJCPClient_Service implements JCPClient_Service {

    // Class constants

    public static final String TH_CONNECTION_NAME = "_JCP_CONNECTION_";


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLSettings_002 locSettings;
    private Timer connectionTimer = null;
    private final JCPClient_CliCredFlow cliCredFlowClient;
    private final JCPClient_AuthFlow authFlowClient;
    private boolean isAuthFlow = false;
    private LoginManager loginMngr = null;


    // Constructor

    /**
     * ObjServiceect JCPClient constructor, it setup the client for Service's requests.
     * <p>
     * Moreover this constructor disable SSL checks and bypass the
     * {@link com.robypomper.josp.core.jcpclient.AbsJCPClient} autoConnect
     * mechanism.
     *
     * @param settings the JOD settings.
     */
    public DefaultJCPClient_Service(JSLSettings_002 settings) throws ConnectionSettingsException {
        DefaultJCPConfigs jclClientConfigs = new DefaultJCPConfigs(settings.getJCPId(),
                settings.getJCPSecret(),
                "openid",
                settings.getJCPCallback(),
                settings.getJCPUrl(),
                "jcp");
        this.locSettings = settings;
        cliCredFlowClient = new JCPClient_CliCredFlow(jclClientConfigs, false);
        authFlowClient = new JCPClient_AuthFlow(jclClientConfigs, false);

        log.debug(Mrk_JSL.JSL_COMM_JCPCL, "Connecting JCPClient to JCP");
        if (!settings.getRefreshToken().isEmpty())
            setRefreshToken(settings.getRefreshToken());

        if (locSettings.getJCPConnect()) {
            try {
                connect();
            } catch (CredentialsException e) {
                log.warn(Mrk_JSL.JSL_COMM_JCPCL, String.format("Error on authenticate to JCPClient because %s", e.getMessage()), e);
                if (isAuthFlow) {
                    try {
                        userLogout();
                    } catch (LoginException ignore) {/*Checked because isAuthFlow==true*/}
                    tryConnect();
                }
            }
        }

        log.debug(Mrk_JSL.JSL_COMM_JCPCL, String.format("JCPClient%s connected to JCP", isConnected() ? "" : " NOT"));
        if (isConnected()) {
            log.info(Mrk_JSL.JSL_COMM_JCPCL, String.format("Initialized DefaultJCPClient_Service and connected to JCP via %s flow", isAuthFlow ? "authentication" : "client credential"));
        } else
            log.info(Mrk_JSL.JSL_COMM_JCPCL, "Initialized DefaultJCPClient_Service and NOT connected to JCP");
    }


    // Headers default values setters

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServiceId(String srvId) {
        if (srvId != null) {
            cliCredFlowClient.addDefaultHeader(APISrvs.HEADER_SRVID, srvId);
            authFlowClient.addDefaultHeader(APISrvs.HEADER_SRVID, srvId);
        } else {
            cliCredFlowClient.removeDefaultHeader(APISrvs.HEADER_SRVID);
            authFlowClient.removeDefaultHeader(APISrvs.HEADER_SRVID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUserId(String usrId) {
        if (usrId != null) {
            cliCredFlowClient.addDefaultHeader(APISrvs.HEADER_USRID, usrId);
            authFlowClient.addDefaultHeader(APISrvs.HEADER_USRID, usrId);
        } else {
            cliCredFlowClient.removeDefaultHeader(APISrvs.HEADER_USRID);
            authFlowClient.removeDefaultHeader(APISrvs.HEADER_USRID);
        }
    }


    // User methods

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCliCredFlowEnabled() {
        return !isAuthFlow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthCodeFlowEnabled() {
        return isAuthFlow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLoginUrl() throws ConnectionException, LoginException {
        if (isAuthFlow)
            throw new LoginException("Error on get login url user already logged in");
        if (!isConnected())
            throw new ConnectionException("Error on get login url because JCP client is not connected");
        return authFlowClient.getLoginUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setLoginCode(String code) throws ConnectionException, LoginException {
        if (!isConnected())
            throw new ConnectionException("Error on perform user login because JCP client is not connected");
        if (isAuthFlow)
            throw new LoginException("Error on perform user login because user already logged in");

        // set and connect
        if (!authFlowClient.setLoginCode(code))
            return false;

        isAuthFlow = true;
        loginMngr.onLogin();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRefreshToken() {
        return authFlowClient.getRefreshToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRefreshToken(String refreshToken) {
        authFlowClient.setRefreshToken(refreshToken);
        isAuthFlow = refreshToken != null && !refreshToken.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean userLogout() throws LoginException {
        if (!isAuthFlow)
            throw new LoginException("Error on perform user logout because user already logged out");

        boolean wasConnected = isConnected();
        setRefreshToken(null);

        if (loginMngr != null)
            loginMngr.onLogout();

        if (wasConnected)
            tryConnect();

        return true;
    }


    // Login manager

    @Override
    public void setLoginManager(LoginManager loginMngr) {
        this.loginMngr = loginMngr;
    }


    // Connection timer

    public boolean isConnecting() {
        return connectionTimer != null;
    }

    private void startConnectionTimer() {
        if (isConnecting())
            return;

        log.debug(Mrk_JSL.JSL_COMM_JCPCL, "Starting JCP Client connection's timer");
        connectionTimer = new Timer(true);
        connectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName(TH_CONNECTION_NAME);

                try {
                    log.debug(Mrk_JSL.JSL_COMM_JCPCL, "Connecting JCPClient to JCP");
                    String flowName;
                    if (!authFlowClient.getRefreshToken().isEmpty() || !authFlowClient.getLoginCode().isEmpty()) {
                        flowName = "auth";
                        authFlowClient.connect();

                    } else {
                        flowName = "client credentials";
                        cliCredFlowClient.connect();
                    }

                    if (!isConnected()) {
                        log.debug(Mrk_JSL.JSL_COMM_JCPCL, String.format("JCP Client NOT connected to JCP with %s flow", flowName));
                        return;
                    }
                    stopConnectionTimer();
                    log.debug(Mrk_JSL.JSL_COMM_JCPCL, String.format("JCPClient connected to JCP with %s flow", flowName));

                } catch (ConnectionException ignore) {
                } catch (CredentialsException e) {
                    log.warn(Mrk_JSL.JSL_COMM_JCPCL, String.format("Error on authenticate JCPClient to JCP because %s", e.getMessage()), e);
                    stopConnectionTimer();
                }
            }
        }, 0, locSettings.getJCPRefreshTime() * 1000);
        log.debug(Mrk_JSL.JSL_COMM_JCPCL, "JCP Client connection's timer started");
    }

    private void stopConnectionTimer() {
        log.debug(Mrk_JSL.JSL_COMM_JCPCL, "Stopping JCP Client connection's timer");
        connectionTimer.cancel();
        connectionTimer = null;
        log.debug(Mrk_JSL.JSL_COMM_JCPCL, "JCP Client connection's timer stopped");
    }


    // Client connection methods

    @Override
    public boolean isConnected() {
        return !isAuthFlow ?
                cliCredFlowClient.isConnected() :
                authFlowClient.isConnected();
    }

    @Override
    public void connect() throws CredentialsException {
        if (isConnected() || isConnecting())
            return;

        try {
            log.debug(Mrk_JSL.JSL_COMM_JCPCL, "Connecting JCPClient to JCP");
            String flowName;
            if (authFlowClient.getRefreshToken() != null || authFlowClient.getLoginCode() != null) {
                flowName = "auth";
                authFlowClient.connect();

            } else {
                flowName = "client credentials";
                cliCredFlowClient.connect();
            }
            log.debug(Mrk_JSL.JSL_COMM_JCPCL, String.format("JCPClient connected to JCP with %s flow", flowName));

        } catch (ConnectionException e) {
            log.warn(Mrk_JSL.JSL_COMM_JCPCL, String.format("Error on connecting JCPClient to JCP because %s", e.getMessage()), e);
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

        if (cliCredFlowClient.isConnected())
            cliCredFlowClient.disconnect();

        if (authFlowClient.isConnected())
            authFlowClient.disconnect();
    }

    @Override
    public void tryConnect() {
        try {
            connect();

        } catch (CredentialsException e) {
            log.warn(Mrk_JSL.JSL_COMM_JCPCL, String.format("Error on connecting JCPClient to the JCP because %s", e.getMessage()), e);
        }
    }

    @Override
    public void tryDisconnect() {
        if (!isAuthFlow)
            cliCredFlowClient.tryDisconnect();
        else
            authFlowClient.tryDisconnect();
    }


    // Get requests

    @Override
    public void execGetReq(String url, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            cliCredFlowClient.execGetReq(url, secure);
        else
            authFlowClient.execGetReq(url, secure);
    }

    @Override
    public void execGetReq(String url, Map<String, String> params, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            cliCredFlowClient.execGetReq(url, params, secure);
        else
            authFlowClient.execGetReq(url, params, secure);
    }

    @Override
    public <T> T execGetReq(String url, Class<T> reqObject, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            return cliCredFlowClient.execGetReq(url, reqObject, secure);
        else
            return authFlowClient.execGetReq(url, reqObject, secure);
    }

    @Override
    public <T> T execGetReq(String url, Class<T> reqObject, Map<String, String> params, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            return cliCredFlowClient.execGetReq(url, reqObject, params, secure);
        else
            return authFlowClient.execGetReq(url, reqObject, params, secure);
    }


    // Post requests

    @Override
    public void execPostReq(String url, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            cliCredFlowClient.execPostReq(url, secure);
        else
            authFlowClient.execPostReq(url, secure);
    }

    @Override
    public void execPostReq(String url, Object param, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            cliCredFlowClient.execPostReq(url, param, secure);
        else
            authFlowClient.execPostReq(url, param, secure);
    }

    @Override
    public <T> T execPostReq(String url, Class<T> reqObject, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            return cliCredFlowClient.execPostReq(url, reqObject, secure);
        else
            return authFlowClient.execPostReq(url, reqObject, secure);
    }

    @Override
    public <T> T execPostReq(String url, Class<T> reqObject, Object param, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            return cliCredFlowClient.execPostReq(url, reqObject, param, secure);
        else
            return authFlowClient.execPostReq(url, reqObject, param, secure);
    }


    // Delete requests

    @Override
    public void execDeleteReq(String url, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            cliCredFlowClient.execDeleteReq(url, secure);
        else
            authFlowClient.execDeleteReq(url, secure);
    }

    @Override
    public void execDeleteReq(String url, Object param, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            cliCredFlowClient.execDeleteReq(url, param, secure);
        else
            authFlowClient.execDeleteReq(url, param, secure);
    }

    @Override
    public <T> T execDeleteReq(String url, Class<T> reqObject, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            return cliCredFlowClient.execDeleteReq(url, reqObject, secure);
        else
            return authFlowClient.execDeleteReq(url, reqObject, secure);
    }

    @Override
    public <T> T execDeleteReq(String url, Class<T> reqObject, Object param, boolean secure) throws RequestException, ConnectionException {
        if (!isAuthFlow)
            return cliCredFlowClient.execDeleteReq(url, reqObject, param, secure);
        else
            return authFlowClient.execDeleteReq(url, reqObject, param, secure);
    }

}
