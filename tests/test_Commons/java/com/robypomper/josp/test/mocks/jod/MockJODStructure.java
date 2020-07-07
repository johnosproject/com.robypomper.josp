package com.robypomper.josp.test.mocks.jod;

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODComponentPath;
import com.robypomper.josp.jod.structure.JODRoot;
import com.robypomper.josp.jod.structure.JODStructure;

import java.util.Date;

public class MockJODStructure implements JODStructure {
    @Override
    public JODRoot getRoot() {
        return null;
    }

    @Override
    public JODComponent getComponent(String pathStr) {
        return null;
    }

    @Override
    public JODComponent getComponent(JODComponentPath path) {
        return null;
    }

    @Override
    public JODCommunication getCommunication() {
        return null;
    }

    @Override
    public void setCommunication(JODCommunication comm) {

    }

    @Override
    public void startAutoRefresh() {

    }

    @Override
    public void stopAutoRefresh() {

    }

    @Override
    public void syncObjStruct() {

    }

    @Override
    public String getStructForJSL() {
        return null;
    }

    @Override
    public Date getLastStructureUpdate() {
        return null;
    }
}
