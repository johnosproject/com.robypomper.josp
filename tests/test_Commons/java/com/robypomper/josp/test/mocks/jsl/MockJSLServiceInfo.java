package com.robypomper.josp.test.mocks.jsl;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;

public class MockJSLServiceInfo implements JSLServiceInfo {

    private final String fullId;

    public MockJSLServiceInfo(String fullId) {
        this.fullId = fullId;
    }

    @Override
    public void setSystems(JSLUserMngr user, JSLObjsMngr objs) {

    }

    @Override
    public void setCommunication(JSLCommunication comm) {

    }

    @Override
    public String getSrvId() {
        return null;
    }

    @Override
    public String getSrvName() {
        return null;
    }

    @Override
    public boolean isUserLogged() {
        return false;
    }

    @Override
    public String getUserId() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getInstanceId() {
        return null;
    }

    @Override
    public String getFullId() {
        return fullId;
    }

    @Override
    public int getConnectedObjectsCount() {
        return 0;
    }

    @Override
    public int getKnownObjectsCount() {
        return 0;
    }

    @Override
    public void startAutoRefresh() {

    }

    @Override
    public void stopAutoRefresh() {

    }
}
