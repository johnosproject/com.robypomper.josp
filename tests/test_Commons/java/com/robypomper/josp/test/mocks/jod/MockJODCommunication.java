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

package com.robypomper.josp.test.mocks.jod;

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODGwO2SClient;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;

public class MockJODCommunication implements JODCommunication {

    @Override
    public boolean sendToServices(String msg, JOSPPerm.Type minPermReq) {
        return false;
    }

    @Override
    public boolean sendToCloud(String msg) throws CloudNotConnected {
        return false;
    }

    @Override
    public boolean sendToSingleLocalService(JODLocalClientInfo locConn, String msg, JOSPPerm.Type minReqPerm) throws ServiceNotConnected {
        return false;
    }

    @Override
    public void sendObjectUpdMsg(JODState component, JODStateUpdate update) {

    }

    @Override
    public void syncObject() {}

    @Override
    public boolean processFromServiceMsg(String msg, JOSPPerm.Connection connType) {
        return false;
    }

    @Override
    public JODGwO2SClient getGwO2SClient() {
        return null;
    }

    @Override
    public List<JODLocalClientInfo> getAllLocalClientsInfo() {
        return null;
    }

    @Override
    public JODLocalClientInfo findLocalClientsInfo(String serviceId) {
        return null;
    }

    @Override
    public boolean isLocalRunning() {
        return false;
    }

    @Override
    public void startLocal() {

    }

    @Override
    public void stopLocal() {

    }

    @Override
    public boolean isCloudConnected() {
        return false;
    }

    @Override
    public void connectCloud() {

    }

    @Override
    public void disconnectCloud() {

    }

    @Override
    public void setStructure(JODStructure structure) {

    }

    @Override
    public JODStructure getStructure() {
        return null;
    }
}
