package com.robypomper.josp.jcp.paths.jslwb;

import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.paths.jcp.JCPStatusAbs;

public class JCPJSLWBStatus {
//@formatter:off

    // API info

    public static final String API_NAME = JCPStatusAbs.API_NAME;
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupJSLWebBridgeStatus {
        public final static String NAME = "(internal) JCP JSL Web Bridge Status APIs";
        public final static String DESCR = "Return JCP JSL WebBridge's instance status";
    }


    // API's methods

    // '{mthdName}

    // '/apis/{apiName}/{apiVersion}/{mthdName}

//@formatter:on
}
