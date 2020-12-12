package com.robypomper.josp.jcp.params.jslwb;

import com.robypomper.josp.jcp.paths.jslwb.APIJSLWBStruct;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;

public class JOSPComponentHtml {

    public final String name;
    public final String description;
    public final String objId;
    public final String parentPath;
    public final String componentPath;
    public final String type;
    public final String pathSelf;

    public JOSPComponentHtml(JSLComponent component) {
        this.name = component.getName();
        this.description = component.getDescr();
        this.objId = component.getRemoteObject().getId();
        this.parentPath = component.getParent() != null ? component.getParent().getPath().getString() : "";
        this.componentPath = component.getPath().getString();
        this.type = component.getType();

        this.pathSelf = APIJSLWBStruct.FULL_PATH_COMP(objId, component instanceof JSLRoot ? "-" : componentPath);
    }
}
