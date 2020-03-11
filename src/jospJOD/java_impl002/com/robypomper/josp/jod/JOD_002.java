package com.robypomper.josp.jod;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
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
        JODObjectInfo objInfo = new JODObjectInfo_002(settings);
        JCPClient jcpClient = new JCPClient_Object(settings,objInfo);
        JODExecutorMngr executor = new JODExecutorMngr_002(settings);

        JODStructure structure = new JODStructure_002(objInfo,executor);

        JODPermissions permissions = new JODPermissions_002(settings,objInfo,jcpClient);
        JODCommunication comm = new JODCommunication_002(settings,objInfo,jcpClient,permissions);

        comm.setStructure(structure);
        structure.setCommunication(comm);

        return new JOD_002(settings,objInfo,structure,comm,executor,permissions);
    }

    public static class Settings implements JOD.Settings {

        protected static final String JODVERSION_REQUIRED = "jod.version";
        protected static final String JODVERSION_REQUIRED_DEF = JOD_002.VERSION;

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

            Map<String,String> tmpProp = new Yaml().load(inputStream);
            if (tmpProp==null)
                tmpProp = new HashMap<>();
            properties = tmpProp;
        }

        private void store(String property, String value) {
            properties.put(property,value);

            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
            } catch (IOException e) {
                assert false; // File exist already checked
            }
            new Yaml().dump(properties, writer);
        }

        @Override
        public String getJODVersion_Required() {
            return jodVer!=null ? jodVer :
                    properties.get(JODVERSION_REQUIRED)!=null ? properties.get(JODVERSION_REQUIRED) :
                    JODVERSION_REQUIRED_DEF;
        }

        @Override
        public void setJODVersion_Required(String jodVer, boolean storageUpd) {
            this.jodVer = jodVer;
            if (storageUpd)
                store(JODVERSION_REQUIRED,jodVer);
        }
    }

    @Override
    public String version() {
        return VERSION;
    }
}
