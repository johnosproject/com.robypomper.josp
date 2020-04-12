package com.robypomper.josp.jsl;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.paths.JcpAPI;
import com.robypomper.josp.jsl.jcpclient.DefaultJCPClient_Service;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.systems.JSLCommunication;
import com.robypomper.josp.jsl.systems.JSLCommunication_002;
import com.robypomper.josp.jsl.systems.JSLObjsMngr;
import com.robypomper.josp.jsl.systems.JSLObjsMngr_002;
import com.robypomper.josp.jsl.systems.JSLServiceInfo;
import com.robypomper.josp.jsl.systems.JSLServiceInfo_002;
import com.robypomper.josp.jsl.systems.JSLUserMngr;
import com.robypomper.josp.jsl.systems.JSLUserMngr_002;
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

public class JSL_002 extends AbsJSL {

    private static final String VERSION = FactoryJSL.JSL_VER_002;

    public JSL_002(Settings settings, JCPClient_Service jcpClient, JSLServiceInfo srvInfo, JSLUserMngr usr, JSLObjsMngr objs, JSLCommunication comm) {
        super(settings, jcpClient, srvInfo, usr, objs, comm);
    }

    public static JSL instance(Settings settings) throws JCPClient.ConnectionException {
        JCPClient_Service jcpClient = new DefaultJCPClient_Service(settings);
        JSLServiceInfo srvInfo = new JSLServiceInfo_002(settings, jcpClient);

        JSLUserMngr usr = new JSLUserMngr_002(settings, jcpClient);
        JSLObjsMngr objs = new JSLObjsMngr_002(settings);

        JSLCommunication comm = new JSLCommunication_002(settings, srvInfo, usr, objs, jcpClient);

        srvInfo.setSystems(usr, objs, comm);

        return new JSL_002(settings, jcpClient, srvInfo, usr, objs, comm);
    }

    public static class Settings implements JSL.Settings {

        //@formatter:off
        public static final String JSLVERSION_REQUIRED = "jod.version";
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
        //@formatter:on

        private final File file;
        private final Map<String, String> properties;
        private String jslVer = null;

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

        private void store(String property, String value) {
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


        // JOD

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
    }

    @Override
    public String version() {
        return VERSION;
    }

}
