package com.robypomper.josp.jsl;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.paths.JcpAPI;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLCommunication_002;
import com.robypomper.josp.jsl.jcpclient.DefaultJCPClient_Service;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo_002;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.jsl.user.JSLUserMngr_002;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class JSL_002 extends AbsJSL {

    // Class constants

    private static final String VERSION = FactoryJSL.JSL_VER_002;
    private static final int MAX_INSTANCE_ID = 10000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    public JSL_002(Settings settings, JCPClient_Service jcpClient, JSLServiceInfo srvInfo, JSLUserMngr usr, JSLObjsMngr objs, JSLCommunication comm) {
        super(settings, jcpClient, srvInfo, usr, objs, comm);
    }

    public static JSL instance(Settings settings) throws JCPClient.ConnectionException, JSLCommunication.LocalCommunicationException {
        String instanceId = Integer.toString(new Random().nextInt(MAX_INSTANCE_ID));
        log.info(Mrk_JSL.JSL_MAIN, String.format("Init JSL instance id '%s'", instanceId));

        JCPClient_Service jcpClient = new DefaultJCPClient_Service(settings);

        JSLServiceInfo srvInfo = new JSLServiceInfo_002(settings, jcpClient, instanceId);

        JSLUserMngr usr = new JSLUserMngr_002(settings, jcpClient);

        JSLObjsMngr objs = new JSLObjsMngr_002(settings, srvInfo);

        srvInfo.setSystems(usr, objs);

        JSLCommunication comm = new JSLCommunication_002(settings, srvInfo, jcpClient, usr, objs);

        srvInfo.setCommunication(comm);

        return new JSL_002(settings, jcpClient, srvInfo, usr, objs, comm);
    }

    public static class Settings implements JSL.Settings {

        //@formatter:off
        public static final String JSLVERSION_REQUIRED = "jsl.version";
        public static final String JSLVERSION_REQUIRED_DEF = JSL_002.VERSION;

        public static final String JCP_CONNECT              = "jcp.connect";
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

        private File file;
        private final Map<String, String> properties;
        private String jslVer = null;
        private boolean errorAlreadyPrinted = false;

        public static JSL.Settings instance(File file) {
            return new Settings(file);
        }

        public Settings(File file) {
            this.file = file;

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                assert false; // File exist already checked
            }

            Map<String, String> tmpProp = new Yaml().load(inputStream);
            if (tmpProp == null)
                tmpProp = new HashMap<>();
            properties = tmpProp;
        }

        public Settings(Map<String, String> properties) {
            this.properties = properties;
        }

        private void store(String property, String value) {
            if (file == null) {
                if (!errorAlreadyPrinted) {
                    log.error(Mrk_JSL.JSL_MAIN, "Can't store configs on file, because settings are loaded from properties.");
                    errorAlreadyPrinted = true;
                }
                return;
            }
            properties.put(property, value);

            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
            } catch (IOException e) {
                assert false; // File exist already checked
            }
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            new Yaml(options).dump(properties, writer);
        }


        // JSL

        @Override
        public String version() {
            return VERSION;
        }

        @Override
        public String getJSLVersion_Required() {
            return jslVer != null ? jslVer :
                    properties.get(JSLVERSION_REQUIRED) != null ? properties.get(JSLVERSION_REQUIRED) :
                            JSLVERSION_REQUIRED_DEF;
        }

        @Override
        public void setJSLVersion_Required(String jslVer, boolean storageUpd) {
            this.jslVer = jslVer;
            if (storageUpd)
                store(JSLVERSION_REQUIRED, jslVer);
        }


        // JCP Client

        //@Override
        public boolean getJCPConnect() {
            return properties.get(JCP_CONNECT) == null || Boolean.parseBoolean(properties.get(JCP_CONNECT));
        }

        //@Override
        public String getJCPUrl() {
            return properties.get(JCP_URL) != null ? properties.get(JCP_URL) :
                    JCP_URL_DEF;
        }

        //@Override
        public String getJCPId() {
            return properties.get(JCP_CLIENT_ID) != null ? properties.get(JCP_CLIENT_ID) :
                    JCP_CLIENT_ID_DEF;
        }

        //@Override
        public String getJCPSecret() {
            return properties.get(JCP_CLIENT_SECRET) != null ? properties.get(JCP_CLIENT_SECRET) :
                    JCP_CLIENT_SECRET_DEF;
        }

        public String getJCPCallback() {
            return properties.get(JCP_CLIENT_CALLBACK) != null ? properties.get(JCP_CLIENT_CALLBACK) :
                    JCP_CLIENT_CALLBACK_DEF;
        }

        public String getRefreshToken() {
            return properties.get(JCP_CLIENT_TOKEN) != null ? properties.get(JCP_CLIENT_TOKEN) :
                    JCP_CLIENT_TOKEN_DEF;
        }

        public void setRefreshToken(String refreshToken) {
            store(JCP_CLIENT_TOKEN, refreshToken);
        }

        // Service info

        public String getSrvId() {
            return properties.get(JSLSRV_ID) != null ? properties.get(JSLSRV_ID) :
                    JSLSRV_ID_DEF;
        }

        public void setSrvId(String srvId) {
            store(JSLSRV_ID, srvId);
        }

        public String getSrvName() {
            return properties.get(JSLSRV_NAME) != null ? properties.get(JSLSRV_NAME) :
                    JSLSRV_NAME_DEF;
        }

        public void setSrvName(String srvName) {
            store(JSLSRV_NAME, srvName);
        }

        // User info

        public String getUsrId() {
            return properties.get(JSLUSR_ID) != null ? properties.get(JSLUSR_ID) :
                    JSLUSR_ID_DEF;
        }

        public void setUsrId(String userId) {
            store(JSLUSR_ID, userId);
        }

        public String getUsrName() {
            return properties.get(JSLUSR_NAME) != null ? properties.get(JSLUSR_NAME) :
                    JSLUSR_NAME_DEF;
        }

        public void setUsrName(String username) {
            store(JSLUSR_NAME, username);
        }

        // JSL Comm

        //@Override
        public boolean getLocalEnabled() {
            try {
                String val = properties.get(JSLCOMM_LOCAL_ENABLED) != null ?
                        properties.get(JSLCOMM_LOCAL_ENABLED) :
                        JSLCOMM_LOCAL_ENABLED_DEF;
                return Boolean.parseBoolean(val);

            } catch (ClassCastException e) {
                return (boolean) (Object) properties.get(JSLCOMM_LOCAL_ENABLED);
            }
        }

        public String getJSLDiscovery() {
            return properties.get(JSLCOMM_LOCAL_DISCOVERY) != null ? properties.get(JSLCOMM_LOCAL_DISCOVERY) :
                    JSLCOMM_LOCAL_DISCOVERY_DEF;
        }

        //@Override
        public String getLocalClientKeyStore() {
            return properties.get(JSLCOMM_LOCAL_KS_FILE) != null ? properties.get(JSLCOMM_LOCAL_KS_FILE) :
                    JSLCOMM_LOCAL_KS_FILE_DEF;
        }

        //@Override
        public String getLocalClientKeyStorePass() {
            return properties.get(JSLCOMM_LOCAL_KS_PASS) != null ? properties.get(JSLCOMM_LOCAL_KS_PASS) :
                    JSLCOMM_LOCAL_KS_PASS_DEF;
        }


        // Communication cloud

        //@Override
        public boolean getCloudEnabled() {
            try {
                String val = properties.get(JSLCOMM_CLOUD_ENABLED) != null ?
                        properties.get(JSLCOMM_CLOUD_ENABLED) :
                        JSLCOMM_CLOUD_ENABLED_DEF;
                return Boolean.parseBoolean(val);

            } catch (ClassCastException e) {
                return (boolean) (Object) properties.get(JSLCOMM_CLOUD_ENABLED);
            }
        }

        //@Override
        public String getCloudClientPublicCertificate() {
            return properties.get(JSLCOMM_CLOUD_CERT) != null ? properties.get(JSLCOMM_CLOUD_CERT) :
                    JSLCOMM_CLOUD_CERT_DEF;
        }

        //@Override
        public String getCloudServerPublicCertificate() {
            return properties.get(JSLCOMM_CLOUD_CERT_REMOTE) != null ? properties.get(JSLCOMM_CLOUD_CERT_REMOTE) :
                    JSLCOMM_CLOUD_CERT_REMOTE_DEF;
        }

    }

    @Override
    public String version() {
        return VERSION;
    }

}
