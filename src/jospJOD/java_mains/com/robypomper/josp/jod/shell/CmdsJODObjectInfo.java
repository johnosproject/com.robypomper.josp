package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.jod.systems.JODObjectInfo;

public class CmdsJODObjectInfo {

    private final JODObjectInfo objInfo;

    public CmdsJODObjectInfo(JODObjectInfo objInfo) {
        this.objInfo = objInfo;
    }


    @Command(description = "Print Object Info > Obj.")
    public String infoObj() {
        String s = "";
        s += "OBJECT'S INFO \n";
        s += String.format("  ObjId . . . . . %s\n", objInfo.getObjId());
        s += String.format("  ObjName . . . . %s\n", objInfo.getObjName());
        s += String.format("  Model . . . . . %s\n", objInfo.getModel());
        s += String.format("  Brand . . . . . %s\n", objInfo.getBrand());
        s += String.format("  Descr . . . . . %s\n", objInfo.getLongDescr());

        return s;
    }

    @Command(description = "Print Object Info > User")
    public String infoUser() {
        String s = "";
        s += "USER'S INFO \n";
        s += String.format("  OwnerId . . . . %s\n", objInfo.getOwnerId());

        return s;
    }

    @Command(description = "Print all Object Info")
    public String info() {
        String s = "";
        s += infoObj();
        s += infoUser();

        return s;
    }

}
