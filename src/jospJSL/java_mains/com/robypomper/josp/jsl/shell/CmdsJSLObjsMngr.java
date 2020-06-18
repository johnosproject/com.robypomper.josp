package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;


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
        s += "JCP Comm:         " + obj.isCloudConnected() + "\n";
        s += "Direct Comm:      " + obj.isLocalConnected() + "\n";

        return s;
    }


    @Command(description = "Print object's info.")
    public String objPrintObjectStruct(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        if (obj.getStructure() == null)
            return String.format("Object '%s' not presented to current service", objId);

        return printRecursive(obj.getStructure(), 0);
    }

    private String printRecursive(JSLComponent comp, int indent) {
        String indentStr = new String(new char[indent]).replace('\0', ' ');
        String compStr = String.format("%s- %s", indentStr, comp.getName());

        String compVal = "";
        if (comp instanceof JSLBooleanState)
            compVal = Boolean.toString(((JSLBooleanState) comp).getState());

        System.out.printf("%-30s %-15s %s%n", compStr, comp.getType(), compVal);

        if (comp instanceof JSLContainer)
            for (JSLComponent subComp : ((JSLContainer) comp).getComponents())
                printRecursive(subComp, indent + 2);
        return null;
    }

    @Command(description = "Print all connection of given objId.")
    public String objPrintObjectConnections(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        StringBuilder s = new StringBuilder("KNOWN OBJECTS LIST\n");
        s.append(String.format("- %-30s (status: %s)\n", "JCP", obj.isCloudConnected() ? "connected" : "NOT conn."));
        for (JSLLocalClient client : obj.getLocalClients()) {
            String fullAddr = String.format("%s:%d", client.getServerAddr(), client.getServerPort());
            s.append(String.format("- %-30s (status: %s; local: %s)\n", fullAddr, client.isConnected() ? "connected" : "NOT conn.", client.getServerInfo().getLocalFullAddress()));
        }

        return s.toString();
    }


    @Command(description = "Print object's info.")
    public String objStatus(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLBooleanState) {
            compVal = Boolean.toString(((JSLBooleanState) comp).getState());
        }

        if (!compVal.isEmpty())
            return String.format("%s::%s = %s", objId, compPath, compVal);

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }


    @Command(description = "Print object's info.")
    public String objActionInt(String objId, String compPath, String integer) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof AbsJSLAction) {
            AbsJSLAction compAbsAction = (AbsJSLAction) comp;
            AbsJSLAction.JOSPIntTest cmd = new AbsJSLAction.JOSPIntTest(Integer.parseInt(integer), compAbsAction);
            compAbsAction.execAction(cmd);
            return String.format("Action executed on component with path '%s' in '%s' object with '%s' value", compPath, objId, integer);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

}
