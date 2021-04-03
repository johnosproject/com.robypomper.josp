package com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.objects.structure;

import java.util.List;


/**
 * JCP JSL Web Bridge - Objects / Structure 2.0
 */
public class Params20 {
    
    public static class JOSPComponentHtml {

        public String name;
        public String description;
        public String objId;
        public String parentPath;
        public String componentPath;
        public String type;
        public String pathSelf;
        
    }
    
    public static class JOSPContainerHtml extends JOSPComponentHtml {

        public List<JOSPComponentHtml> subComps;

    }
    
    public static class JOSPStructHtml extends JOSPContainerHtml {

        public String brand;
        public String model;
        public String descrLong;
        
    }
    
    public static class JOSPBooleanStateHtml extends JOSPComponentHtml {

        public boolean state;
        public String pathState;
        
    }

    public static class JOSPBooleanActionHtml extends JOSPBooleanStateHtml {

        public String pathSwitch;
        public String pathTrue;
        public String pathFalse;

    }
    public static class JOSPRangeStateHtml extends JOSPComponentHtml {

        public double state;
        public String pathState;
        public double max;
        public double min;
        public double step;

    }

    public static class JOSPRangeActionHtml extends JOSPRangeStateHtml {

        public String pathSetValue;
        public String pathInc;
        public String pathDec;
        public String pathMax;
        public String pathMin;
        public String pathSet1_2;
        public String pathSet1_3;
        public String pathSet2_3;

    }
    
}
