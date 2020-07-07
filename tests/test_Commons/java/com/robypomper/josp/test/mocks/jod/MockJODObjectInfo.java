package com.robypomper.josp.test.mocks.jod;

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;

public class MockJODObjectInfo implements JODObjectInfo {

    private final String objId;

    public MockJODObjectInfo() {
        objId = "";
    }

    public MockJODObjectInfo(String objId) {
        this.objId = objId;
    }

    @Override
    public void setSystems(JODStructure structure, JODExecutorMngr executor, JODCommunication comm, JODPermissions permissions) {

    }

    @Override
    public String getJODVersion() {
        return null;
    }

    @Override
    public String getObjId() {
        return objId;
    }

    @Override
    public String getObjName() {
        return null;
    }

    @Override
    public void setObjName(String newName) {}

    @Override
    public String getOwnerId() {
        return null;
    }

    @Override
    public String getStructurePath() {
        return null;
    }

    @Override
    public String readStructureStr() {
        return null;
    }

    @Override
    public String getStructForJSL() throws JODStructure.ParsingException {
        return null;
    }

    @Override
    public String getPermsForJSL() throws JODStructure.ParsingException {
        return null;
    }

    @Override
    public String getBrand() {
        return null;
    }

    @Override
    public String getModel() {
        return null;
    }

    @Override
    public String getLongDescr() {
        return null;
    }

    @Override
    public String getPermissionsPath() {
        return null;
    }

    @Override
    public String readPermissionsStr() {
        return null;
    }

    @Override
    public void startAutoRefresh() {

    }

    @Override
    public void stopAutoRefresh() {

    }

    @Override
    public void syncObjInfo() {

    }

    @Override
    public void regenerateObjId() {}

}
