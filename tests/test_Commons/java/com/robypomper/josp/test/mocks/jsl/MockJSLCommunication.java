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
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;

public class MockJSLCommunication implements JSLCommunication {

    @Override
    public boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) {
        return false;
    }

    @Override
    public JSLGwS2OClient getCloudConnection() {
        return null;
    }

    @Override
    public List<JSLLocalClient> getAllLocalServers() {
        return null;
    }

    @Override
    public void removeServer(JSLLocalClient server) {

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
    public void addListener(CommunicationListener listener) {}

    @Override
    public void removeListener(CommunicationListener listener) {}

}
