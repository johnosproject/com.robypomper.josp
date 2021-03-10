package com.robypomper.josp.jcp.paths.fe;

import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.paths.jcp.JCPStatusAbs;

public class JCPFEStatus {
//@formatter:off

    // API info

    public static final String API_NAME = JCPStatusAbs.API_NAME;
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupFEStatus {
        public final static String NAME = "(internal) JCP FE Status APIs";
        public final static String DESCR = "Return JCP FE's instance status";
    }


    // API's methods

    // '{mthdName}

    // '/apis/{apiName}/{apiVersion}/{mthdName}

//@formatter:on
}
