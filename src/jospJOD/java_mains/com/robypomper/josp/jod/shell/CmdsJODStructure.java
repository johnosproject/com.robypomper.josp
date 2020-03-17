package com.robypomper.josp.jod.shell;

import asg.cliche.Command;
import com.robypomper.josp.jod.structure.DefaultJODComponentPath;
import com.robypomper.josp.jod.structure.JODAction;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.JODComponentPath;
import com.robypomper.josp.jod.structure.UtilsStructure;
import com.robypomper.josp.jod.systems.JODStructure;

public class CmdsJODStructure {

    private final JODStructure structure;

    public CmdsJODStructure(JODStructure structure) {
        this.structure = structure;
    }


    @Command(description = "Print object's Structure tree")
    public String objTree() {
        return UtilsStructure.compToPrettyPrint(structure.getRoot(), true);
    }

    @Command(description = "Print object's Structure Root Component")
    public String objComponent() {
        return UtilsStructure.compToFullPrint(structure.getRoot());
    }

    @Command(description = "Print object's Structure component specified as JOD Component Path")
    public String objComponent(String compPath) {
        JODComponentPath path = new DefaultJODComponentPath(compPath);
        if (!path.isUnique())
            return "Please specify an unique JOD Component Path";

        JODComponent comp = structure.getComponent(path);
        if (comp == null)
            return String.format("No component found for '%s' path.", compPath);

        return UtilsStructure.compToFullPrint(comp);
    }

    @Command(description = "Print object's Structure Root Component")
    public String objExec(String compPath) {
        JODComponentPath path = new DefaultJODComponentPath(compPath);
        if (!path.isUnique())
            return "Please specify an unique JOD Component Path";

        JODComponent comp = structure.getComponent(path);
        if (comp == null)
            return String.format("No component found for '%s' path.", compPath);

        if (!(comp instanceof JODAction))
            return String.format("Component '%s' found, but not JODAction component.", comp.getName());

        if (((JODAction) comp).execAction(null))
            return String.format("Action executed successfully on component '%s'", comp.getName());
        return String.format("Error occurred executing action on component '%s'", comp.getName());
    }

}
