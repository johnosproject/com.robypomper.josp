package com.robypomper.josp.test.mocks.jod;

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;

public class MockJODPermissions implements JODPermissions {

    @Override
    public void setCommunication(JODCommunication comm) throws JODStructure.CommunicationSetException {

    }

    @Override
    public void syncObjPermissions() {

    }

    @Override
    public List<JOSPPerm> getPermissions() {
        return null;
    }

    @Override
    public boolean checkPermission(String srvId, String usrId, JOSPPerm.Type minReqPerm, JOSPPerm.Connection connType) {
        return false;
    }

    @Override
    public JOSPPerm.Type getServicePermission(String srvId, String usrId, JOSPPerm.Connection connType) {
        return null;
    }

    @Override
    public boolean addPermissions(String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
        return false;
    }

    @Override
    public boolean updPermissions(String permId, String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
        return false;
    }

    @Override
    public boolean remPermissions(String permId) {
        return false;
    }

    @Override
    public String getOwnerId() {
        return null;
    }

    @Override
    public void setOwnerId(String ownerId) {

    }

    @Override
    public void resetOwnerId() {

    }

    @Override
    public void startAutoRefresh() {

    }

    @Override
    public void stopAutoRefresh() {

    }

    @Override
    public void regeneratePermissions() throws PermissionsFileException {

    }
}
