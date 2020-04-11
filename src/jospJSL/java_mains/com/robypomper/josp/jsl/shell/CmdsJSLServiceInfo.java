package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.systems.JSLServiceInfo;

public class CmdsJSLServiceInfo {

    private final JSLServiceInfo srvInfo;

    public CmdsJSLServiceInfo(JSLServiceInfo srvInfo) {
        this.srvInfo = srvInfo;
    }


    @Command(description = "Print Service Info > Srv.")
    public String infoSrv() {
        String s = "";
        s += "SERVICE'S INFO \n";
        s += String.format("  SrvId . . . . . %s\n", srvInfo.getSrvId());
        s += String.format("  SrvName . . . . %s\n", srvInfo.getSrvName());
        s += "\n";
        s += String.format("  is user auth  . %s\n", srvInfo.isUserLogged());
        if (srvInfo.isUserLogged()) {
            s += String.format("  UsrId . . . . . %s\n", srvInfo.getUserId());
            s += String.format("  UsrName . . . . %s\n", srvInfo.getUsername());
        }

        return s;
    }

}
