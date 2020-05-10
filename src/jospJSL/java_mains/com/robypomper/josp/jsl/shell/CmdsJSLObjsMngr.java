package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.systems.JSLObjsMngr;

public class CmdsJSLObjsMngr {

    private final JSLObjsMngr objs;

    public CmdsJSLObjsMngr(JSLObjsMngr objs) {
        this.objs = objs;
    }


    // All objects

    @Command(description = "Print all known objects.")
    public String objsPrintAll() {
        StringBuilder s = new StringBuilder("KNOWN OBJECTS LIST\n");
        for (JSLRemoteObject obj : objs.getAllObjects())
            s.append(String.format("- %-30s (%s)\n", obj.getName(), obj.getId()));

        return s.toString();
    }

    @Command(description = "Print all connected objects.")
    public String objsPrintAllConnected() {
        StringBuilder s = new StringBuilder("CONNECTED OBJECTS LIST\n");
        for (JSLRemoteObject obj : objs.getAllConnectedObjects())
            s.append(String.format("- %-30s (%s)\n", obj.getName(), obj.getId()));

        return s.toString();
    }


    // Single object

    @Command(description = "Print object's info.")
    public String objPrintObjectInfo(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        String s = "";
        s += "Obj. Id:          " + obj.getId() + "\n";
        s += "Obj. Name:        " + obj.getName() + "\n";
        s += "Owner Id:         " + obj.getOwnerId() + "\n";
        s += "Obj. JOD version: " + obj.getJODVersion() + "\n";

        return s;
    }

    @Command(description = "Print all connection of given objId.")
    public String objPrintObjectConnections(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        StringBuilder s = new StringBuilder("KNOWN OBJECTS LIST\n");
        for (JSLLocalClient client : obj.getLocalClients())
            s.append(String.format("- %s:%d (%s)\n", client.getServerAddr(), client.getServerPort(), client.isConnected() ? "connected" : "disconnected"));

        return s.toString();
    }

}
