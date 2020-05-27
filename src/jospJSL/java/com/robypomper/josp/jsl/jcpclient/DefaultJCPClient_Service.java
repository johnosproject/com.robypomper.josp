package com.robypomper.josp.jsl.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPConfigs;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.core.jcpclient.JCPClient_AuthFlow;
import com.robypomper.josp.core.jcpclient.JCPClient_CliCredFlow;
import com.robypomper.josp.jcp.apis.paths.APISrvs;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;


/**
 * Object default implementation of {@link JCPClient} interface.
 * <p>
 * This class initialize a JCPClient object that can be used by Services.
 */
public class DefaultJCPClient_Service implements JCPClient_Service {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
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
    public DefaultJCPClient_Service(JSL_002.Settings settings) throws ConnectionException {
        AbsJCPClient.disableSSLChecks(); // ToDo necessario??

        DefaultJCPConfigs jclClientConfigs = new DefaultJCPConfigs(settings.getJCPId(),
                settings.getJCPSecret(),
                "openid",
                settings.getJCPCallback(),
                settings.getJCPUrl(),
                "jcp");
        cliCredFlowClient = new JCPClient_CliCredFlow(jclClientConfigs, false);
        authFlowClient = new JCPClient_AuthFlow(jclClientConfigs, false);

        log.debug(Mrk_JSL.JSL_COMM_JCPCL, "Connecting JCPClient to JCP");
        if (!settings.getRefreshToken().isEmpty()) {
            authFlowClient.setRefreshToken(settings.getRefreshToken());
            try {
                log.debug(Mrk_JSL.JSL_COMM_JCPCL, "Connecting JCPClient to JCP with authentication flow");
                authFlowClient.connect();
                log.debug(Mrk_JSL.JSL_COMM_JCPCL, "JCPClient connected to JCP with authentication flow");
                isAuthFlow = true;
            } catch (ConnectionException e) {
                log.warn(Mrk_JSL.JSL_COMM_JCPCL, String.format("Error on connecting JCPClient to JCP with authentication flow because %s", e.getMessage()), e);
            }
        }

        if (!authFlowClient.isConnected()) {
            try {
                log.debug(Mrk_JSL.JSL_COMM_JCPCL, "Connecting JCPClient to JCP with client credential flow");
                cliCredFlowClient.connect();
                log.debug(Mrk_JSL.JSL_COMM_JCPCL, "JCPClient connected to JCP with client credential flow");
            } catch (ConnectionException e) {
                log.warn(Mrk_JSL.JSL_COMM_JCPCL, String.format("Error on connecting JCPClient to JCP with client credential flow because %s", e.getMessage()), e);
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
    public String getLoginUrl() {
        return authFlowClient.getLoginUrl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setLoginCode(String code) {
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
    public boolean setRefreshToken(String refreshToken) {
        if (refreshToken == null)
            refreshToken = "";

        // set
        authFlowClient.setRefreshToken(refreshToken);

        if (refreshToken.isEmpty())
            return true;

        // connect
        try {
            authFlowClient.connect();
        } catch (ConnectionException e) {
            return false;
        }

        isAuthFlow = true;
        loginMngr.onLogin();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean userLogout() {
        setRefreshToken("");

        isAuthFlow = false;
        loginMngr.onLogout();

        return true;
    }


    // Login manager

    @Override
    public void setLoginManager(LoginManager loginMngr) {
        this.loginMngr = loginMngr;
    }


    @Override
    public boolean isConnected() {
        return !isAuthFlow ?
                cliCredFlowClient.isConnected() :
                authFlowClient.isConnected();
    }

    @Override
    public void connect() throws ConnectionException {
        cliCredFlowClient.connect();

        if (authFlowClient.getRefreshToken().isEmpty() || authFlowClient.getLoginCode().isEmpty())
            authFlowClient.connect();
    }

    @Override
    public void disconnect() {
        if (cliCredFlowClient.isConnected())
            cliCredFlowClient.disconnect();

        if (authFlowClient.isConnected())
            authFlowClient.disconnect();
    }

    @Override
    public void tryConnect() {
        if (!isAuthFlow)
            cliCredFlowClient.tryConnect();
        else
            authFlowClient.tryConnect();
    }

    @Override
    public void tryDisconnect() {
        if (!isAuthFlow)
            cliCredFlowClient.tryDisconnect();
        else
            authFlowClient.tryDisconnect();
    }


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
