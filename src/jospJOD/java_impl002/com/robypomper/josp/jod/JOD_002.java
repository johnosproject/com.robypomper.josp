package com.robypomper.josp.jod;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jcp.apis.paths.JcpAPI;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODCommunication_002;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.executor.JODExecutorMngr_002;
import com.robypomper.josp.jod.jcpclient.DefaultJCPClient_Object;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.objinfo.JODObjectInfo_002;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.permissions.JODPermissions_002;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.JODStructure_002;
import com.robypomper.log.Mrk_JOD;
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

public class JOD_002 extends AbsJOD {

    // Class constants

    private static final String VERSION = FactoryJOD.JOD_VER_002;
    private static final int MAX_INSTANCE_ID = 10000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    protected JOD_002(Settings settings, JCPClient_Object jcpClient, JODObjectInfo objInfo, JODStructure structure, JODCommunication comm, JODExecutorMngr executor, JODPermissions permissions) {
        super(settings, jcpClient, objInfo, structure, comm, executor, permissions);
    }

    public static JOD instance(Settings settings) throws JCPClient.ConnectionSettingsException, JODStructure.ParsingException, JODCommunication.LocalCommunicationException {
        String instanceId = Integer.toString(new Random().nextInt(MAX_INSTANCE_ID));
        log.info(Mrk_JOD.JOD_MAIN, String.format("Init JOD instance id '%s'", instanceId));

        JCPClient_Object jcpClient = new DefaultJCPClient_Object(settings);

        JODObjectInfo objInfo = new JODObjectInfo_002(settings, jcpClient, VERSION);

        JODExecutorMngr executor = new JODExecutorMngr_002(settings, objInfo);

        JODStructure structure = new JODStructure_002(objInfo, executor);

        JODPermissions permissions = new JODPermissions_002(settings, objInfo, jcpClient);

        JODCommunication comm = new JODCommunication_002(settings, objInfo, jcpClient, permissions, instanceId);

        try {
            comm.setStructure(structure);
        } catch (JODCommunication.StructureSetException ignore) {
            assert false;
        }
        try {
            structure.setCommunication(comm);
        } catch (JODStructure.CommunicationSetException ignore) {
            assert false;
        }

        objInfo.setSystems(structure, executor, comm, permissions);

        return new JOD_002(settings, jcpClient, objInfo, structure, comm, executor, permissions);
    }

    public static class Settings implements JOD.Settings {

        //@formatter:off
        public static final String JODVERSION_REQUIRED = "jod.version";
        public static final String JODVERSION_REQUIRED_DEF = JOD_002.VERSION;

        public static final String JCP_CONNECT              = "jcp.connect";
        public static final String JCP_URL                  = "jcp.url";
        public static final String JCP_URL_DEF              = JcpAPI.URL_DOM_API;
        public static final String JCP_CLIENT_ID            = "jcp.client.id";
        public static final String JCP_CLIENT_ID_DEF        = "";
        public static final String JCP_CLIENT_SECRET        = "jcp.client.secret";
        public static final String JCP_CLIENT_SECRET_DEF    = "";

        public static final String JODOBJ_NAME              = "jod.obj.name";
        public static final String JODOBJ_NAME_DEF          = "";
        public static final String JODOBJ_IDCLOUD           = "jod.obj.id_cloud";
        public static final String JODOBJ_IDCLOUD_DEF       = "";
        public static final String JODOBJ_IDHW              = "jod.obj.id_hw";
        public static final String JODOBJ_IDHW_DEF          = "";

        public static final String JODPULLER_IMPLS          = "jod.executor_mngr.pullers";
        public static final String JODPULLER_IMPLS_DEF      = "";
        public static final String JODLISTENER_IMPLS        = "jod.executor_mngr.listeners";
        public static final String JODLISTENER_IMPLS_DEF    = "";
        public static final String JODEXECUTOR_IMPLS        = "jod.executor_mngr.executors";
        public static final String JODEXECUTOR_IMPLS_DEF    = "";

        public static final String JODSTRUCT_PATH           = "jod.structure.path";
        public static final String JODSTRUCT_PATH_DEF       = "struct.jod";

        public static final String JODPERM_PATH             = "jod.permissions.path";
        public static final String JODPERM_PATH_DEF         = "perms.jod";
        public static final String JODPERM_REFRESH          = "jod.permissions.refresh";
        public static final String JODPERM_REFRESH_DEF      = "300";
        public static final String JODPERM_GENSTARTEGY      = "jod.permissions.generation_strategy";
        public static final String JODPERM_GENSTARTEGY_DEF  = "standard";
        public static final String JODPERM_OWNER            = "jod.permissions.owner";
        public static final String JODPERM_OWNER_DEF        = "00000-00000-00000";

        public static final String JODCOMM_LOCAL_ENABLED        = "jod.comm.local.enabled";
        public static final String JODCOMM_LOCAL_ENABLED_DEF    = "true";
        public static final String JODCOMM_LOCAL_DISCOVERY      = "jod.comm.local.discovery";
        public static final String JODCOMM_LOCAL_DISCOVERY_DEF  = "avahi";
        public static final String JODCOMM_LOCAL_PORT           = "jod.comm.local.port";
        public static final String JODCOMM_LOCAL_PORT_DEF       = "1234";
        public static final String JODCOMM_LOCAL_KS_FILE        = "jod.comm.local.keystore.path";
        public static final String JODCOMM_LOCAL_KS_FILE_DEF    = "certs/private/server.p12";
        public static final String JODCOMM_LOCAL_KS_PASS        = "jod.comm.local.keystore.pass";
        public static final String JODCOMM_LOCAL_KS_PASS_DEF    = "objectPass";
        public static final String JODCOMM_LOCAL_CERT           = "jod.comm.local.cert.path";
        public static final String JODCOMM_LOCAL_CERT_DEF       = "certs/public/server.crt";

        public static final String JODCOMM_CLOUD_ENABLED        = "jod.comm.cloud.enabled";
        public static final String JODCOMM_CLOUD_ENABLED_DEF    = "true";
        public static final String JODCOMM_CLOUD_CERT           = "jod.comm.cloud.cert.path";
        public static final String JODCOMM_CLOUD_CERT_DEF       = "certs/public/clientCloud.crt";
        public static final String JODCOMM_CLOUD_CERT_REMOTE    = "jod.comm.cloud.remote.cert.path";
        public static final String JODCOMM_CLOUD_CERT_REMOTE_DEF= "certs/public/mainServer@GwObjService.crt";
        //@formatter:on


        // Internal vars

        private File file;
        private final Map<String, String> properties;
        private String jodVer = null;
        private boolean errorAlreadyPrinted = false;

        public static JOD.Settings instance(File file) {
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
            properties.put(property, value);
            if (file == null) {
                if (!errorAlreadyPrinted) {
                    log.error(Mrk_JOD.JOD_MAIN, "Can't store configs on file, because settings are loaded from properties.");
                    errorAlreadyPrinted = true;
                }
                return;
            }

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


        // Object info

        //@Override
        public String getObjName() {
            return properties.get(JODOBJ_NAME) != null ? properties.get(JODOBJ_NAME) :
                    JODOBJ_NAME_DEF;
        }

        //@Override
        public void setObjName(String objName) {
            store(JODOBJ_NAME, objName);
        }

        //@Override
        public String getObjIdCloud() {
            return properties.get(JODOBJ_IDCLOUD) != null ? properties.get(JODOBJ_IDCLOUD) :
                    JODOBJ_IDCLOUD_DEF;
        }

        //@Override
        public void setObjIdCloud(String objId) {
            store(JODOBJ_IDCLOUD, objId);
        }

        //@Override
        public String getObjIdHw() {
            return properties.get(JODOBJ_IDHW) != null ? properties.get(JODOBJ_IDHW) :
                    JODOBJ_IDHW_DEF;
        }

        //@Override
        public void setObjIdHw(String objIdHw) {
            store(JODOBJ_IDHW, objIdHw);
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


        // Structure

        //@Override
        public File getStructurePath() {
            String fileName = properties.get(JODSTRUCT_PATH) != null ? properties.get(JODSTRUCT_PATH) :
                    JODSTRUCT_PATH_DEF;
            return new File(fileName);
        }


        // Permissions

        //@Override
        public File getPermissionsPath() {
            String fileName = properties.get(JODPERM_PATH) != null ? properties.get(JODPERM_PATH) :
                    JODPERM_PATH_DEF;
            return new File(fileName);
        }

        //@Override
        public int getPermissionsRefreshTime() {
            try {
                String val = properties.get(JODPERM_REFRESH) != null ?
                        properties.get(JODPERM_REFRESH) :
                        JODPERM_REFRESH_DEF;
                return Integer.parseInt(val);

            } catch (ClassCastException e) {
                return (int) (Object) properties.get(JODPERM_REFRESH);
            }
        }

        //@Override
        public PermissionsTypes.GenerateStrategy getPermissionsGenerationStrategy() {
            String val = properties.get(JODPERM_GENSTARTEGY) != null ?
                    properties.get(JODPERM_GENSTARTEGY) :
                    JODPERM_GENSTARTEGY_DEF;
            return PermissionsTypes.GenerateStrategy.valueOf(val.toUpperCase());
        }

        /**
         * Object startup
         * - init jod
         * - read from local configs                             when: owner set
         * on error                                            when: owner not set
         * - set default anonymous owner on local configs
         * - read from local configs
         * - start jod
         * <p>
         * <p>
         * Owner set from service 2 object
         * - store on local configs
         * - set owner on cloud
         * <p>
         * Owner set from service 2 object via cloud
         * - jcp: store on cloud object's temporary properties
         * - jcp: set owner on object
         * - store on local configs
         * - set owner on cloud
         */
        //@Override
        public String getOwnerId() {
            return properties.get(JODPERM_OWNER) != null ? properties.get(JODPERM_OWNER) :
                    JODPERM_OWNER_DEF;
        }

        //@Override
        public void setOwnerId(String ownerId) {
            store(JODPERM_OWNER, ownerId);
        }


        // Communication local

        //@Override
        public boolean getLocalEnabled() {
            try {
                String val = properties.get(JODCOMM_LOCAL_ENABLED) != null ?
                        properties.get(JODCOMM_LOCAL_ENABLED) :
                        JODCOMM_LOCAL_ENABLED_DEF;
                return Boolean.parseBoolean(val);

            } catch (ClassCastException e) {
                return (boolean) (Object) properties.get(JODCOMM_LOCAL_ENABLED);
            }
        }

        //@Override
        public String getLocalDiscovery() {
            return properties.get(JODCOMM_LOCAL_DISCOVERY) != null ? properties.get(JODCOMM_LOCAL_DISCOVERY) :
                    JODCOMM_LOCAL_DISCOVERY_DEF;
        }

        //@Override
        public int getLocalServerPort() {
            try {
                String val = properties.get(JODCOMM_LOCAL_PORT) != null ?
                        properties.get(JODCOMM_LOCAL_PORT) :
                        JODCOMM_LOCAL_PORT_DEF;
                return Integer.parseInt(val);

            } catch (ClassCastException e) {
                return (int) (Object) properties.get(JODCOMM_LOCAL_PORT);
            }
        }

        //@Override
        public String getLocalServerKeyStore() {
            return properties.get(JODCOMM_LOCAL_KS_FILE) != null ? properties.get(JODCOMM_LOCAL_KS_FILE) :
                    JODCOMM_LOCAL_KS_FILE_DEF;
        }

        //@Override
        public String getLocalServerKeyStorePass() {
            return properties.get(JODCOMM_LOCAL_KS_PASS) != null ? properties.get(JODCOMM_LOCAL_KS_PASS) :
                    JODCOMM_LOCAL_KS_PASS_DEF;
        }

        //@Override
        public String getLocalServerPublicCertificate() {
            return properties.get(JODCOMM_LOCAL_CERT) != null ? properties.get(JODCOMM_LOCAL_CERT) :
                    JODCOMM_LOCAL_CERT_DEF;
        }


        // Communication cloud

        //@Override
        public boolean getCloudEnabled() {
            try {
                String val = properties.get(JODCOMM_CLOUD_ENABLED) != null ?
                        properties.get(JODCOMM_CLOUD_ENABLED) :
                        JODCOMM_CLOUD_ENABLED_DEF;
                return Boolean.parseBoolean(val);

            } catch (ClassCastException e) {
                return (boolean) (Object) properties.get(JODCOMM_CLOUD_ENABLED);
            }
        }

        //@Override
        public String getCloudClientPublicCertificate() {
            return properties.get(JODCOMM_CLOUD_CERT) != null ? properties.get(JODCOMM_CLOUD_CERT) :
                    JODCOMM_CLOUD_CERT_DEF;
        }

        //@Override
        public String getCloudServerPublicCertificate() {
            return properties.get(JODCOMM_CLOUD_CERT_REMOTE) != null ? properties.get(JODCOMM_CLOUD_CERT_REMOTE) :
                    JODCOMM_CLOUD_CERT_REMOTE_DEF;
        }

    }

    @Override
    public String version() {
        return VERSION;
    }

}
