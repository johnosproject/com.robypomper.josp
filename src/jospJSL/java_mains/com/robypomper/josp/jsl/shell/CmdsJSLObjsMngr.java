package com.robypomper.josp.jsl.shell;

import asg.cliche.Command;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;


public class CmdsJSLObjsMngr {

    public static final String PRE = "\n\n";
    public static final String POST = "\n\n";
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


    // Object's info

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
        s += "    perm:         " + obj.getServicePerm(JOSPPerm.Connection.LocalAndCloud) + "\n";
        s += "Direct Comm:      " + obj.isLocalConnected() + "\n";
        s += "       perm:      " + obj.getServicePerm(JOSPPerm.Connection.OnlyLocal) + "\n";

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
        else if (comp instanceof JSLRangeState)
            compVal = Double.toString(((JSLRangeState) comp).getState());

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

    @Command(description = "Print all permissions of given objId.")
    public String objPrintObjectPermissions(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        String s = "OBJECT'S PERMISSIONS LIST\n";
        s += JOSPPerm.logPermissions(obj.getPerms());
        return s;
    }

    @Command(description = "Set object's name.")
    public String objSetObjectName(String objId, String objName) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        String oldName = obj.getName();
        try {
            obj.setName(objName);
        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't update name", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't update name\n%s", obj.getId(), e.getMessage());
        }

        return String.format("Object '%s' name updated from '%s' to '%s'", obj.getId(), oldName, obj.getName());
    }

    // Object's status

    @Command(description = "Print object's component status.")
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
        } else if (comp instanceof JSLRangeState) {
            compVal = Double.toString(((JSLRangeState) comp).getState());
        }

        if (!compVal.isEmpty())
            return String.format("%s::%s = %s", objId, compPath, compVal);

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }


    // Object's actions

    @Command(description = "Exec object's boolean action.")
    public String objActionBooleanSwitch(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLBooleanAction) {
            try {
                ((JSLBooleanAction) comp).execSwitch();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send boolean action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send boolean action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Boolean action executed on component with path '%s' in '%s' object with switched value", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's boolean true action.")
    public String objActionBooleanTrue(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLBooleanAction) {
            try {
                ((JSLBooleanAction) comp).execSetTrue();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send boolean action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send boolean action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Boolean action executed on component with path '%s' in '%s' object with 'true' value", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's boolean false action.")
    public String objActionBooleanFalse(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLBooleanAction) {
            try {
                ((JSLBooleanAction) comp).execSetFalse();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send boolean action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send boolean action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Boolean action executed on component with path '%s' in '%s' object with 'false' value", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range action.")
    public String objActionRange(String objId, String compPath, String actionDoubleParam) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execSetValue(Double.parseDouble(actionDoubleParam));
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with '%s' value", compPath, objId, actionDoubleParam);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range increase action.")
    public String objActionRangeIncrease(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execIncrease();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with increase", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range decrease action.")
    public String objActionRangeDecrease(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execDecrease();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with decrease", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range max action.")
    public String objActionRangeMax(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execSetMax();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with max", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }

    @Command(description = "Exec object's range min action.")
    public String objActionRangeMin(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        String compVal = "";
        if (comp instanceof JSLRangeAction) {
            try {
                ((JSLRangeAction) comp).execSetMin();
            } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
                return String.format("Object '%s' not connected, can't send range action", obj.getId());
            } catch (JSLRemoteObject.MissingPermission e) {
                return String.format("Missing permission to object '%s', can't send range action\n%s", obj.getId(), e.getMessage());
            }
            return String.format("Range action executed on component with path '%s' in '%s' object with min", compPath, objId);
        }

        return String.format("Component '%s' in '%s' object is not supported (%s)", compPath, objId, comp.getClass().getName());
    }


    // Object's permissions

    @Command(description = "Set object's owner id.")
    public String objSetObjectOwner(String objId, String objOwnerId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        String oldOwner = obj.getOwnerId();
        try {
            obj.setOwnerId(objOwnerId);
        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't update owner id", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't update owner id\n%s", obj.getId(), e.getMessage());
        }

        return String.format("Object '%s' owner updated from '%s' to '%s'", obj.getId(), oldOwner, obj.getOwnerId());
    }

    @Command(description = "Add new object's permission.")
    public String objAddPerm(String objId, String srvId, String usrId, String permTypeStr, String connTypeStr) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        JOSPPerm.Type permType = JOSPPerm.Type.valueOf(permTypeStr);
        JOSPPerm.Connection connType = JOSPPerm.Connection.valueOf(connTypeStr);

        try {
            obj.addPerm(srvId, usrId, permType, connType);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            return String.format("Object '%s' not connected, can't add permission", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't add permission\n%s", obj.getId(), e.getMessage());
        }

        return String.format("Object '%s' permission added", obj.getId());
    }

    @Command(description = "Update object's permission.")
    public String objUpdPerm(String objId, String permId, String srvId, String usrId, String permTypeStr, String connTypeStr) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        JOSPPerm.Type permType = JOSPPerm.Type.valueOf(permTypeStr);
        JOSPPerm.Connection connType = JOSPPerm.Connection.valueOf(connTypeStr);

        try {
            obj.updPerm(permId, srvId, usrId, permType, connType);
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't update permission\n%s", obj.getId(), e.getMessage());

        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't update permission", obj.getId());
        }

        return String.format("Object '%s' permission updated", obj.getId());
    }

    @Command(description = "Remove object's permission.")
    public String objRemPerm(String objId, String permId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        try {
            obj.remPerm(permId);

        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            return String.format("Object '%s' not connected, can't remove permission", obj.getId());
        } catch (JSLRemoteObject.MissingPermission e) {
            return String.format("Missing permission to object '%s', can't remove permission\n%s", obj.getId(), e.getMessage());
        }

        return String.format("Object '%s' permission removed", obj.getId());
    }


    // Object's listeners

    @Command(description = "Add logger listener to objects manager's events.")
    public String objsMngrAddListeners() {
        objs.addListener(new JSLObjsMngr.ObjsMngrListener() {

            @Override
            public void onObjAdded(JSLRemoteObject obj) {
                System.out.println(PRE + String.format("added '%s' object", obj.getId()) + POST);
            }

            @Override
            public void onObjRemoved(JSLRemoteObject obj) {
                System.out.println(PRE + String.format("removed '%s' object", obj.getId()) + POST);
            }
        });

        return "ok";
    }

    @Command(description = "Add logger listener to object's events.")
    public String objAddListeners(String objId) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        obj.addListener(new JSLRemoteObject.RemoteObjectConnListener() {
            @Override
            public void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient) {
                System.out.println(PRE + String.format("local object '%s' connected (client id: %s, client addr: %s", obj.getId(), localClient.getClientId(), localClient.getClientAddr()) + POST);
            }

            @Override
            public void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient) {
                System.out.println(PRE + String.format("local object '%s' disconnected (client id: %s, client addr: %s", obj.getId(), localClient.getClientId(), localClient.getClientAddr()) + POST);
            }

            @Override
            public void onCloudConnected(JSLRemoteObject obj) {
                System.out.println(PRE + String.format("cloud object '%s' connected", obj.getId()) + POST);
            }

            @Override
            public void onCloudDisconnected(JSLRemoteObject obj) {
                System.out.println(PRE + String.format("cloud object '%s' disconnected", obj.getId()) + POST);
            }
        });

        obj.addListener(new JSLRemoteObject.RemoteObjectInfoListener() {

            @Override
            public void onNameChanged(JSLRemoteObject obj, String newName, String oldName) {
                System.out.println(PRE + String.format("Name changed object '%s' %-15s > %-15s", obj.getId(), oldName, newName) + POST);
            }

            @Override
            public void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId) {
                System.out.println(PRE + String.format("OwnerId changed object '%s' %-15s > %-15s", obj.getId(), oldOwnerId, newOwnerId) + POST);
            }

            @Override
            public void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion) {
                System.out.println(PRE + String.format("JODVersion changed object '%s' %-15s > %-15s", obj.getId(), oldJODVersion, newJODVersion) + POST);
            }

            @Override
            public void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel) {
                System.out.println(PRE + String.format("Model changed object '%s' %-15s > %-15s", obj.getId(), oldModel, newModel) + POST);
            }

            @Override
            public void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand) {
                System.out.println(PRE + String.format("Brand changed object '%s' %-15s > %-15s", obj.getId(), oldBrand, newBrand) + POST);
            }

            @Override
            public void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr) {
                System.out.println(PRE + String.format("LongDescr changed object '%s' %-15s > %-15s", obj.getId(), oldLongDescr, newLongDescr) + POST);
            }

            @Override
            public void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot) {
                System.out.println(PRE + String.format("Structure changed object '%s'", obj.getId()) + POST);
            }

            @Override
            public void onPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms) {
                System.out.println(PRE + String.format("Permissions changed object '%s' %-15s > %-15s", obj.getId(), oldPerms.size(), newPerms.size()) + POST);
            }

            @Override
            public void onServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType) {
                System.out.println(PRE + String.format("Service's permission changed object '%s' %s %-15s > %-15s", obj.getId(), connType, oldPermType, newPermType) + POST);
            }
        });

        return "ok";
    }

    @Command(description = "Add logger listener to object's component events.")
    public String objComponentAddListeners(String objId, String compPath) {
        JSLRemoteObject obj = objs.getById(objId);
        if (obj == null)
            return String.format("No object found with id '%s'", objId);

        // search destination object/components
        JSLComponentPath componentPath = new DefaultJSLComponentPath(compPath);
        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), componentPath);
        if (comp == null)
            return String.format("No component found with path '%s' in '%s' object", compPath, objId);

        if (comp instanceof JSLBooleanState) {
            ((JSLBooleanState) comp).addListener(new JSLBooleanState.BooleanStateListener() {
                @Override
                public void onStateChanged(JSLBooleanState component, boolean newState, boolean oldState) {
                    System.out.println(PRE + String.format("%s state changed object '%s' %-15s > %-15s", component.getRemoteObject().getId(), component.getName(), oldState, newState) + POST);
                }
            });
        } else if (comp instanceof JSLRangeState) {
            ((JSLRangeState) comp).addListener(new JSLRangeState.RangeStateListener() {
                @Override
                public void onStateChanged(JSLRangeState component, double newState, double oldState) {
                    System.out.println(PRE + String.format("%s state changed object '%s' %-15s > %-15s", component.getRemoteObject().getId(), component.getName(), oldState, newState) + POST);
                }

                @Override
                public void onMinReached(JSLRangeState component, double state, double min) {
                    System.out.println(PRE + String.format("%s min state reached object '%s' %-15s (max: %s)", component.getRemoteObject().getId(), component.getName(), state, min) + POST);
                }

                @Override
                public void onMaxReached(JSLRangeState component, double state, double max) {
                    System.out.println(PRE + String.format("%s max state reached object '%s' %-15s (max: %s)", component.getRemoteObject().getId(), component.getName(), state, max) + POST);
                }
            });
        }

        return "OK";
    }

}
