package com.robypomper.josp.test.mocks.jsl;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.user.JSLUserMngr;

public class MockJSLUserMngr_002 implements JSLUserMngr {
    @Override
    public boolean isUserAuthenticated() {
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
    public void setCommunication(JSLCommunication comm) {}
}
