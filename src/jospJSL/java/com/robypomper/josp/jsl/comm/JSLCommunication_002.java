/* *****************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************** */

package com.robypomper.josp.jsl.comm;

import com.robypomper.josp.clients.AbsGWsClient;
import com.robypomper.josp.clients.JCPAPIsClientSrv;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol_ObjectToService;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Implementation of the {@link JSLCommunication} interface.
 */
public class JSLCommunication_002 implements JSLCommunication {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    // JSL
    private final JSLSettings_002 locSettings;
    private final JSLObjsMngr_002 jslObjsMngr;
    // Comms
    private final JCPAPIsClientSrv jcpClient;
    private final JSLGwS2OClient gwClient;
    private final JSLLocalClientsMngr localClients;


    // Constructor

    /**
     * @param settings    the JSL settings.
     * @param srvInfo     the service's info.
     * @param jcpClient   the jcp service client.
     * @param jslObjsMngr the {@link JSLObjsMngr} instance used to update component
     *                    status.
     */
    public JSLCommunication_002(JSL_002 jsl, JSLSettings_002 settings, JSLServiceInfo srvInfo, JCPAPIsClientSrv jcpClient, JSLObjsMngr_002 jslObjsMngr, String instanceId) throws LocalCommunicationException, AbsGWsClient.GWsClientException {
        this.locSettings = settings;
        this.jslObjsMngr = jslObjsMngr;
        this.jcpClient = jcpClient;

        this.localClients = new JSLLocalClientsMngr(jsl, this, jslObjsMngr, locSettings, srvInfo);
        this.gwClient = new JSLGwS2OClient(this, srvInfo, jcpClient, instanceId);

        log.info(Mrk_JSL.JSL_COMM, String.format("Initialized JODCommunication instance for '%s' ('%s') service", srvInfo.getSrvName(), srvInfo.getSrvId()));
    }


    // To Object Msg

    // see JSLRemoteObject


    // From Object Msg

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) {
        log.info(Mrk_JSL.JSL_COMM, String.format("Received '%s' message from %s", msg.substring(0, msg.indexOf('\n')), connType == JOSPPerm.Connection.OnlyLocal ? "local object" : "cloud"));

        try {
            String objId = JOSPProtocol_ObjectToService.getObjId(msg);

            JSLRemoteObject obj = jslObjsMngr.getById(objId);
            int count = 0;
            while (obj == null && count < 5) {
                count++;
                Thread.sleep(100);
                obj = jslObjsMngr.getById(objId);
            }

            if (obj == null && connType == JOSPPerm.Connection.LocalAndCloud && JOSPProtocol_ObjectToService.isObjectInfoMsg(msg)) {
                jslObjsMngr.addCloudObject(objId);
                obj = jslObjsMngr.getById(objId);
            }

            if (obj == null)
                throw new Throwable(String.format("Object '%s' not found", objId));//throw new ObjectNotFound(objId)

            if (!obj.processFromObjectMsg(msg, connType))
                throw new Throwable(String.format("Unknown error on processing '%s' message", msg.substring(0, msg.indexOf('\n'))));

            log.info(Mrk_JSL.JSL_COMM, String.format("Message '%s' processed successfully", msg.substring(0, msg.indexOf('\n'))));
            return true;

        } catch (Throwable t) {
            log.warn(Mrk_JSL.JSL_COMM, String.format("Error on processing '%s' message from %s because %s", msg.substring(0, msg.indexOf('\n')), connType == JOSPPerm.Connection.OnlyLocal ? "local object" : "cloud", t.getMessage()), t);
            return false;
        }
    }


    // Connections access

    /**
     * {@inheritDoc}
     */
    @Override
    public JCPAPIsClientSrv getCloudAPIs() {
        return jcpClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLGwS2OClient getCloudConnection() {
        return gwClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSLLocalClientsMngr getLocalConnections() {
        return localClients;
    }

}
