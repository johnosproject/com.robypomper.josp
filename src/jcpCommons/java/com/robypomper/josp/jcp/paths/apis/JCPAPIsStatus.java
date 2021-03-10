package com.robypomper.josp.jcp.paths.apis;

import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.paths.jcp.JCPStatusAbs;

public class JCPAPIsStatus {
//@formatter:off

    // API info

    public static final String API_NAME = JCPStatusAbs.API_NAME;
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupAPIsStatus {
        public final static String NAME = "(internal) JCP APIs Status APIs";
        public final static String DESCR = "Return JCP APIs's instance status";
    }


    // API's methods

    // '{mthdName}
    public static final String MTHD_APIS_STATUS_OBJS           = "apis/status/objs";
    public static final String MTHD_APIS_STATUS_SRVS           = "apis/status/srvs";
    public static final String MTHD_APIS_STATUS_USRS           = "apis/status/usrs";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_APIS_STATUS_OBJS           = API_PATH + "/" + API_VER + "/" + MTHD_APIS_STATUS_OBJS;
    public static final String FULL_PATH_APIS_STATUS_SRVS           = API_PATH + "/" + API_VER + "/" + MTHD_APIS_STATUS_SRVS;
    public static final String FULL_PATH_APIS_STATUS_USRS           = API_PATH + "/" + API_VER + "/" + MTHD_APIS_STATUS_USRS;


    // API's descriptions

    public static final String DESCR_PATH_APIS_STATUS_OBJS  = "Return JCP objects stats";
    public static final String DESCR_PATH_APIS_STATUS_SRVS  = "Return JCP services stats";
    public static final String DESCR_PATH_APIS_STATUS_USRS  = "Return JCP users stats";

//@formatter:on
}
