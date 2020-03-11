package com.robypomper.josp.jod.systems;

import com.robypomper.josp.jod.JOD;

/**
 * ToDo: doc JODObjectInfo_002
 */
public class JODObjectInfo_002 implements JODObjectInfo {

    //private final JOD jod;
    private final JOD.Settings settings;

    public JODObjectInfo_002(JOD.Settings settings) {
        System.out.println("DEB: JODObjectInfo_002()");
        this.settings = settings;
    }


    // Info

    @Override
    public String getObjId() {
        System.out.println("DEB: JODObjectInfo_002::getObjIdObj()");
        return "Not impl.";
    }

    @Override
    public String getObjName() {
        System.out.println("DEB: JODObjectInfo_002::getObjName()");
        return "Not impl.";
    }

    @Override
    public String getObjIdHw() {
        System.out.println("DEB: JODObjectInfo_002::getObjIdHw()");
        return "Not impl.";
    }


    // Mngm methods

    @Override
    public void startAutoRefresh() {
        System.out.println("DEB: JODObjectInfo_002::startAutoRefresh()");
    }

    @Override
    public void stopAutoRefresh() {
        System.out.println("DEB: JODObjectInfo_002::stopAutoRefresh()");
    }
}
