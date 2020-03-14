package com.robypomper.josp.jod;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.jcpclient.DefaultJCPClient_Object;
import com.robypomper.josp.jod.systems.JODCommunication;
import com.robypomper.josp.jod.systems.JODCommunication_002;
import com.robypomper.josp.jod.systems.JODExecutorMngr;
import com.robypomper.josp.jod.systems.JODExecutorMngr_002;
import com.robypomper.josp.jod.systems.JODObjectInfo;
import com.robypomper.josp.jod.systems.JODObjectInfo_002;
import com.robypomper.josp.jod.systems.JODPermissions;
import com.robypomper.josp.jod.systems.JODPermissions_002;
import com.robypomper.josp.jod.systems.JODStructure;
import com.robypomper.josp.jod.systems.JODStructure_002;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class JOD_002 extends AbsJOD {

    private static final String VERSION = FactoryJOD.JOD_VER_002;

    protected JOD_002(Settings settings, JODObjectInfo objInfo, JODStructure structure, JODCommunication comm, JODExecutorMngr executor, JODPermissions permissions) {
        super(settings, objInfo, structure, comm, executor, permissions);
    }

    public static JOD instance(Settings settings) {
        JCPClient jcpClient = new DefaultJCPClient_Object(settings);
        JODObjectInfo objInfo = new JODObjectInfo_002(settings);
        JODExecutorMngr executor = new JODExecutorMngr_002(settings);

        JODStructure structure = new JODStructure_002(objInfo, executor);

        JODPermissions permissions = new JODPermissions_002(settings, objInfo, jcpClient);
        JODCommunication comm = new JODCommunication_002(settings, objInfo, jcpClient, permissions);

        comm.setStructure(structure);
        structure.setCommunication(comm);

        return new JOD_002(settings, objInfo, structure, comm, executor, permissions);
    }

    public static class Settings implements JOD.Settings {

        public static final String JODVERSION_REQUIRED = "jod.version";
        public static final String JODVERSION_REQUIRED_DEF = JOD_002.VERSION;

        public static final String JCP_CONNECT = "jcp.connect";
        public static final String JCP_URL = "jcp.url";
        public static final String JCP_URL_DEF = "https://jcp.johnosproject.com";
        public static final String JCP_CLIENT_ID = "jcp.client.id";
        public static final String JCP_CLIENT_ID_DEF = "";
        public static final String JCP_CLIENT_SECRET = "jcp.client.secret";
        public static final String JCP_CLIENT_SECRET_DEF = "";

        public static final String JODPULLER_IMPLS = "jod.executor_mngr.pullers";
        public static final String JODPULLER_IMPLS_DEF = "";
        public static final String JODLISTENER_IMPLS = "jod.executor_mngr.listeners";
        public static final String JODLISTENER_IMPLS_DEF = "";
        public static final String JODEXECUTOR_IMPLS = "jod.executor_mngr.executors";
        public static final String JODEXECUTOR_IMPLS_DEF = "";

        private final File file;
        private final Map<String, String> properties;
        private String jodVer = null;

        public static JOD.Settings instance(File file) {
            return new Settings(file);
        }

        @Override
        public String version() {
            return VERSION;
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
            new Yaml().dump(properties, writer);
        }


        // JOD

        @Override
        public String getJODVersion_Required() {
            return jodVer != null ? jodVer :
                    properties.get(JODVERSION_REQUIRED) != null ? properties.get(JODVERSION_REQUIRED) :
                            JODVERSION_REQUIRED_DEF;
        }

        @Override
        public void setJODVersion_Required(String jodVer, boolean storageUpd) {
            this.jodVer = jodVer;
            if (storageUpd)
                store(JODVERSION_REQUIRED, jodVer);
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


        // Executor Manager

        //@Override
        public String getJODPullerImpls() {
            return properties.get(JODPULLER_IMPLS) != null ? properties.get(JODPULLER_IMPLS) :
                    JODPULLER_IMPLS_DEF;
        }

        //@Override
        public String getJODListenerImpls() {
            return properties.get(JODLISTENER_IMPLS) != null ? properties.get(JODLISTENER_IMPLS) :
                    JODLISTENER_IMPLS_DEF;
        }

        //@Override
        public String getJODExecutorImpls() {
            return properties.get(JODEXECUTOR_IMPLS) != null ? properties.get(JODEXECUTOR_IMPLS) :
                    JODEXECUTOR_IMPLS_DEF;
        }

    }

    @Override
    public String version() {
        return VERSION;
    }
}
