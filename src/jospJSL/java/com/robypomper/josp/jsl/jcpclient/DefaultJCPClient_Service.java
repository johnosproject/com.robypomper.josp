package com.robypomper.josp.jsl.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPClient2;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.paths.APISrvs;
import com.robypomper.josp.jsl.JSLSettings_002;


/**
 * Service default implementation of {@link DefaultJCPClient2} class.
 * <p>
 * This class initialize a JCPClient object that can be used by Objects.
 */
public class DefaultJCPClient_Service extends DefaultJCPClient2 implements JCPClient_Service, JCPClient2.ConnectListener {

    private final JSLSettings_002 locSettings;

    // Constructor

    public DefaultJCPClient_Service(JSLSettings_002 settings) {
        super(settings.getJCPId(),
                settings.getJCPSecret(),
                settings.getJCPUrlAPIs(),
                settings.getJCPUrlAuth(),
                "openid",
                settings.getJCPCallback(),
                "jcp",
                settings.getJCPAuthCodeRefreshToken(),
                settings.getJCPRefreshTime());
        addConnectListener(this);
        locSettings = settings;
    }


    // Headers default values setters

    @Override
    public void setServiceId(String srvId) {
        if (srvId != null && !srvId.isEmpty())
            addDefaultHeader(APISrvs.HEADER_SRVID, srvId);
        else
            removeDefaultHeader(APISrvs.HEADER_SRVID);
    }

    @Override
    public void setUserId(String usrId) {
        if (usrId != null && !usrId.isEmpty())
            addDefaultHeader(APISrvs.HEADER_USRID, usrId);
        else
            removeDefaultHeader(APISrvs.HEADER_USRID);
    }


    @Override
    public void setLoginCodeAndReconnect(String loginCode) throws ConnectionException, AuthenticationException, JCPNotReachableException {
        disconnect();
        setLoginCode(loginCode);
        connect();
    }

    // Store refresh tokens

    @Override
    public void onConnected(JCPClient2 jcpClient) {
        if (jcpClient.isClientCredentialFlowEnabled())
            locSettings.setJCPAuthCodeRefreshToken(null);
        if (jcpClient.isAuthCodeFlowEnabled())
            locSettings.setJCPAuthCodeRefreshToken(getAuthCodeRefreshToken());
    }

    @Override
    public void onConnectionFailed(JCPClient2 jcpClient, Throwable t) {}

}
