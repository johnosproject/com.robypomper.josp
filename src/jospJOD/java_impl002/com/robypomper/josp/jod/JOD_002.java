package com.robypomper.josp.jod;

import com.robypomper.josp.core.jcpclient.JCPClient;
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

import java.util.Random;

public class JOD_002 extends AbsJOD {

    // Class constants

    public static final String VERSION = FactoryJOD.JOD_VER_002;
    private static final int MAX_INSTANCE_ID = 10000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    protected JOD_002(JODSettings_002 settings, JCPClient_Object jcpClient, JODObjectInfo objInfo, JODStructure structure, JODCommunication comm, JODExecutorMngr executor, JODPermissions permissions) {
        super(settings, jcpClient, objInfo, structure, comm, executor, permissions);
    }

    public static JOD instance(JODSettings_002 settings) throws JCPClient.ConnectionSettingsException, JODStructure.ParsingException, JODCommunication.LocalCommunicationException, JODCommunication.CloudCommunicationException, JODPermissions.PermissionsFileException {
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
            permissions.setCommunication(comm);
            structure.setCommunication(comm);
        } catch (JODStructure.CommunicationSetException ignore) {
            assert false;
        }

        objInfo.setSystems(structure, executor, comm, permissions);

        return new JOD_002(settings, jcpClient, objInfo, structure, comm, executor, permissions);
    }

    @Override
    public String version() {
        return VERSION;
    }

}
