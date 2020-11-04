/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jod;

import com.robypomper.java.JavaVersionUtils;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.info.JCPAPIsVersions;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODCommunication_002;
import com.robypomper.josp.jod.events.Events;
import com.robypomper.josp.jod.events.JODEvents;
import com.robypomper.josp.jod.events.JODEvents_002;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.executor.JODExecutorMngr_002;
import com.robypomper.josp.jod.history.JODHistory;
import com.robypomper.josp.jod.history.JODHistory_002;
import com.robypomper.josp.jod.jcpclient.DefaultJCPClient_Object;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.objinfo.JODObjectInfo_002;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.permissions.JODPermissions_002;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jod.structure.JODStructure_002;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Random;

public class JOD_002 extends AbsJOD {

    // Class constants

    public static final String VERSION = FactoryJOD.JOD_VER_2_0_0;   // Upgraded to 2.0.0
    private static final int MAX_INSTANCE_ID = 10000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    protected JOD_002(Settings settings,
                      JCPClient_Object jcpClient,
                      JODObjectInfo objInfo,
                      JODStructure structure,
                      JODCommunication comm,
                      JODExecutorMngr executor,
                      JODPermissions permissions,
                      JODEvents events,
                      JODHistory history) {
        super(settings, jcpClient, objInfo, structure, comm, executor, permissions, events, history);
    }

    public static JOD instance(JODSettings_002 settings) throws JODStructure.ParsingException, JODCommunication.LocalCommunicationException, JODCommunication.CloudCommunicationException, JODPermissions.PermissionsFileException {
        log.info("\n\n" + JavaVersionUtils.buildJavaVersionStr("John Object Daemon", VERSION));

        long start = new Date().getTime();

        JODEvents events = new JODEvents_002(settings, null);
        Events.setInstance(events);

        String instanceId = Integer.toString(new Random().nextInt(MAX_INSTANCE_ID));
        log.info(Mrk_JOD.JOD_MAIN, String.format("Init JOD instance id '%s'", instanceId));

        Events.registerJODStart("Start sub-system creation", instanceId);
        JCPClient_Object jcpClient = new DefaultJCPClient_Object(settings);
        if (settings.getJCPConnect())
            try {
                jcpClient.connect();
                Events.registerJCPConnection("JCP Connected", jcpClient);

            } catch (JCPClient2.AuthenticationException e) {
                Events.registerJCPConnection("JCP Connection failed", jcpClient, e);

            } catch (JCPClient2.ConnectionException e) {
                Events.registerJCPConnection("JCP Connection failed", jcpClient, e);
                e.printStackTrace();

            } catch (JCPClient2.JCPNotReachableException e) {
                Events.registerJCPConnection("JCP Connection failed", jcpClient, e);
                jcpClient.startConnectionTimer();
            }
        events.setJCPClient(jcpClient);

        JODObjectInfo objInfo = new JODObjectInfo_002(settings, jcpClient, VERSION);

        JODExecutorMngr executor = new JODExecutorMngr_002(settings, objInfo);
        JODHistory history = new JODHistory_002(settings, jcpClient);
        JODStructure structure = new JODStructure_002(objInfo, executor, history);

        JODPermissions permissions = new JODPermissions_002(settings, objInfo, jcpClient);

        JODCommunication comm = new JODCommunication_002(settings, objInfo, jcpClient, permissions, events, instanceId);

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

        long time = new Date().getTime() - start;
        Events.registerJODStart("End sub-system creation", time);

        return new JOD_002(settings, jcpClient, objInfo, structure, comm, executor, permissions, events, history);
    }

    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public String[] versionsJOSPProtocol() {
        return new String[]{JOSPProtocol.JOSP_PROTO_VERSION_2_0};
    }

    @Override
    public String[] versionsJCPAPIs() {
        return new String[]{JCPAPIsVersions.VER_JCP_APIs_2_0};
    }

}
