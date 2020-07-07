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
