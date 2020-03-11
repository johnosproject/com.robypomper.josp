package com.robypomper.josp.jod.systems;

import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODComponentPath;
import com.robypomper.josp.jod.structure.JODRoot;

/**
 * ToDo: doc JODStructure_002
 */
public class JODStructure_002 implements JODStructure {

    public JODStructure_002(JODObjectInfo objInfo, JODExecutorMngr executor) {
        System.out.println("DEB: JODStructure_002()");
    }

    @Override
    public JODRoot getRoot() {
        System.out.println("DEB: JODStructure_002::getRoot()");
        return null;
    }

    @Override
    public JODComponent getComponent(JODComponentPath path) {
        System.out.println("DEB: JODStructure_002::getComponent()");
        return null;
    }

    @Override
    public void startAutoRefresh() {
        System.out.println("DEB: JODStructure_002::startAutoRefresh()");
    }

    @Override
    public void stopAutoRefresh() {
        System.out.println("DEB: JODStructure_002::stopAutoRefresh()");
    }

    @Override
    public void loadStructure() {
        System.out.println("DEB: JODStructure_002::stoploadStructureAutoRefresh()");
    }

    @Override
    public void setCommunication(JODCommunication comm) {
        System.out.println("DEB: JODStructure_002::setCommunication()");
    }
}
