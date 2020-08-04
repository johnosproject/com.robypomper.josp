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

package com.robypomper.josp.test.mocks.jsl;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;

public class MockJSLRemoteObject implements JSLRemoteObject {

    @Override
    public boolean isLocalConnected() {
        return false;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String newName) throws ObjectNotConnected {}

    @Override
    public JSLRoot getStructure() {
        return null;
    }

    @Override
    public String getOwnerId() {
        return null;
    }

    @Override
    public void setOwnerId(String newOwnerId) throws ObjectNotConnected {}

    @Override
    public String getJODVersion() {
        return null;
    }

    @Override
    public String getModel() {
        return null;
    }

    @Override
    public String getBrand() {
        return null;
    }

    @Override
    public String getLongDescr() {
        return null;
    }

    @Override
    public List<JOSPPerm> getPerms() {
        return null;
    }

    @Override
    public JOSPPerm.Type getServicePerm(JOSPPerm.Connection connType) {
        return null;
    }

    @Override
    public void addPerm(String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws ObjectNotConnected, MissingPermission {}

    @Override
    public void updPerm(String permId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws ObjectNotConnected, MissingPermission {}

    @Override
    public void remPerm(String permId) throws ObjectNotConnected, MissingPermission {}

    @Override
    public JSLCommunication getCommunication() {
        return null;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isCloudConnected() {
        return false;
    }

    @Override
    public List<JSLLocalClient> getLocalClients() {
        return null;
    }

    @Override
    public JSLLocalClient getConnectedLocalClient() {
        return null;
    }

    @Override
    public void sendObjectCmdMsg(JSLAction component, JSLActionParams command) throws ObjectNotConnected {

    }

    @Override
    public void addLocalClient(JSLLocalClient localClient) {

    }

    @Override
    public void removeLocalClient(JSLLocalClient localClient) {}

    @Override
    public void replaceLocalClient(JSLLocalClient oldConnection, JSLLocalClient newConnection) {

    }


    @Override
    public boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) {
        return false;
    }

    @Override
    public void addListener(RemoteObjectConnListener listener) {}

    @Override
    public void removeListener(RemoteObjectConnListener listener) {}

    @Override
    public void addListener(RemoteObjectInfoListener listener) {}

    @Override
    public void removeListener(RemoteObjectInfoListener listener) {}

}
