package com.robypomper.josp.jsl;

import com.robypomper.josp.jcp.apis.paths.JcpAPI;
import com.robypomper.settings.DefaultSettings;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JSLSettings_002 extends DefaultSettings implements JSL.Settings {

    //@formatter:off
    public static final String JSLVERSION_REQUIRED = "jsl.version";
    public static final String JSLVERSION_REQUIRED_DEF = JSL_002.VERSION;

    public static final String JCP_CONNECT              = "jcp.connect";
    public static final String JCP_CONNECT_DEF          = "true";
    public static final String JCP_REFRESH_TIME         = "jcp.client.refresh";
    public static final String JCP_REFRESH_TIME_DEF     = "30";
    public static final String JCP_URL                  = "jcp.url";
    public static final String JCP_URL_DEF              = JcpAPI.URL_DOM_API;
    public static final String JCP_CLIENT_ID            = "jcp.client.id";
    public static final String JCP_CLIENT_ID_DEF        = "";
    public static final String JCP_CLIENT_SECRET        = "jcp.client.secret";
    public static final String JCP_CLIENT_SECRET_DEF    = "";
    public static final String JCP_CLIENT_CALLBACK      = "jcp.client.callback";
    public static final String JCP_CLIENT_CALLBACK_DEF  = "https://localhost:9001";
    public static final String JCP_CLIENT_TOKEN         = "jcp.client.token";
    public static final String JCP_CLIENT_TOKEN_DEF     = "";

    public static final String JSLSRV_NAME              = "jsl.srv.name";
    public static final String JSLSRV_NAME_DEF          = "";
    public static final String JSLSRV_ID                = "jsl.srv.id";
    public static final String JSLSRV_ID_DEF            = "";

    public static final String JSLUSR_NAME              = "jsl.usr.name";
    public static final String JSLUSR_NAME_DEF          = "";
    public static final String JSLUSR_ID                = "jsl.usr.id";
    public static final String JSLUSR_ID_DEF            = "";

    public static final String JSLCOMM_LOCAL_ENABLED        = "jsl.comm.local.enabled";
    public static final String JSLCOMM_LOCAL_ENABLED_DEF    = "true";
    public static final String JSLCOMM_LOCAL_DISCOVERY      = "jsl.comm.local.impl";
    public static final String JSLCOMM_LOCAL_DISCOVERY_DEF  = "avahi";
    public static final String JSLCOMM_LOCAL_KS_FILE        = "jsl.comm.local.keystore.path";
    public static final String JSLCOMM_LOCAL_KS_FILE_DEF    = "certs/private/client.p12";
    public static final String JSLCOMM_LOCAL_KS_PASS        = "jsl.comm.local.keystore.pass";
    public static final String JSLCOMM_LOCAL_KS_PASS_DEF    = "servicePass";

    public static final String JSLCOMM_CLOUD_ENABLED        = "jsl.comm.cloud.enabled";
    public static final String JSLCOMM_CLOUD_ENABLED_DEF    = "true";
    public static final String JSLCOMM_CLOUD_CERT           = "jsl.comm.cloud.cert.path";
    public static final String JSLCOMM_CLOUD_CERT_DEF       = "certs/public/clientCloud.crt";
    public static final String JSLCOMM_CLOUD_CERT_REMOTE    = "jsl.comm.cloud.remote.cert.path";
    public static final String JSLCOMM_CLOUD_CERT_REMOTE_DEF= "certs/public/mainServer@GwObjService.crt";
    //@formatter:on

    // Internal vars

    private String jslVer = null;


    // Constructor

    public static JSL.Settings instance(File file) throws IOException {
        return new JSLSettings_002(file);
    }

    public JSLSettings_002(File file) throws IOException {
        super(file);
    }

    public JSLSettings_002(Map<String, Object> properties) {
        super(properties);
    }


    // JSL

    @Override
    public String version() {
        return JSL_002.VERSION;
    }

    @Override
    public String getJSLVersion_Required() {
        return jslVer != null ? jslVer :
                getString(JSLVERSION_REQUIRED, JSLVERSION_REQUIRED_DEF);
    }

    @Override
    public void setJSLVersion_Required(String jslVer, boolean storageUpd) {
        this.jslVer = jslVer;
        if (storageUpd)
            store(JSLVERSION_REQUIRED, jslVer, true);
    }


    // JCP Client

    //@Override
    public boolean getJCPConnect() {
        return getBoolean(JCP_CONNECT, JCP_CONNECT_DEF);
    }

    //@Override
    public int getJCPRefreshTime() {
        return getInt(JCP_REFRESH_TIME, JCP_REFRESH_TIME_DEF);
    }

    //@Override
    public String getJCPUrl() {
        return getString(JCP_URL, JCP_URL_DEF);
    }

    //@Override
    public String getJCPId() {
        return getString(JCP_CLIENT_ID, JCP_CLIENT_ID_DEF);
    }

    //@Override
    public String getJCPSecret() {
        return getString(JCP_CLIENT_SECRET, JCP_CLIENT_SECRET_DEF);
    }

    public String getJCPCallback() {
        return getString(JCP_CLIENT_CALLBACK, JCP_CLIENT_CALLBACK_DEF);
    }

    public String getRefreshToken() {
        return getString(JCP_CLIENT_TOKEN, JCP_CLIENT_TOKEN_DEF);
    }

    public void setRefreshToken(String refreshToken) {
        store(JCP_CLIENT_TOKEN, refreshToken, true);
    }


    // Service info

    public String getSrvId() {
        return getString(JSLSRV_ID, getString(JCP_CLIENT_ID, JSLSRV_ID_DEF));
    }

    public void setSrvId(String srvId) {
        store(JSLSRV_ID, srvId, true);
    }

    public String getSrvName() {
        return getString(JSLSRV_NAME, JSLSRV_NAME_DEF);
    }

    public void setSrvName(String srvName) {
        store(JSLSRV_NAME, srvName, true);
    }


    // User info

    public String getUsrId() {
        return getString(JSLUSR_ID, JSLUSR_ID_DEF);
    }

    public void setUsrId(String userId) {
        store(JSLUSR_ID, userId, true);
    }

    public String getUsrName() {
        return getString(JSLUSR_NAME, JSLUSR_NAME_DEF);
    }

    public void setUsrName(String username) {
        store(JSLUSR_NAME, username, true);
    }


    // JSL Comm

    //@Override
    public boolean getLocalEnabled() {
        return getBoolean(JSLCOMM_LOCAL_ENABLED, JSLCOMM_LOCAL_ENABLED_DEF);
    }

    public String getJSLDiscovery() {
        return getString(JSLCOMM_LOCAL_DISCOVERY, JSLCOMM_LOCAL_DISCOVERY_DEF);
    }

    //@Override
    public String getLocalClientKeyStore() {
        return getString(JSLCOMM_LOCAL_KS_FILE, JSLCOMM_LOCAL_KS_FILE_DEF);
    }

    //@Override
    public String getLocalClientKeyStorePass() {
        return getString(JSLCOMM_LOCAL_KS_PASS, JSLCOMM_LOCAL_KS_PASS_DEF);
    }

    //@Override
    public boolean getCloudEnabled() {
        return getBoolean(JSLCOMM_CLOUD_ENABLED, JSLCOMM_CLOUD_ENABLED_DEF);
    }

    //@Override
    public String getCloudClientPublicCertificate() {
        return getString(JSLCOMM_CLOUD_CERT, JSLCOMM_CLOUD_CERT_DEF);
    }

    //@Override
    public String getCloudServerPublicCertificate() {
        return getString(JSLCOMM_CLOUD_CERT_REMOTE, JSLCOMM_CLOUD_CERT_REMOTE_DEF);
    }

}
