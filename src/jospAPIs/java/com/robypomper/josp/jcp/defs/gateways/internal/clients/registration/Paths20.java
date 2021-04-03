package com.robypomper.josp.jcp.defs.gateways.internal.clients.registration;


/**
 * JCP Gateways - Clients / Registration 2.0
 */
public class Paths20 {

    // API Info

    public static final String API_NAME = Versions.API_NAME;
    public static final String API_GROUP_NAME = Versions.API_GROUP;
    public static final String API_VER = Versions.VER_JCP_APIs_2_0;
    public static final String API_PATH = Versions.API_PATH_BASE;
    public final static String DOCS_NAME = Versions.API_GROUP_FULL;
    public final static String DOCS_DESCR = "JCP APIs get JCP GW's access info";


    // API Methods

    //@formatter:off
    // Access methods
    protected static final String MTHD_GW_O2S_ACCESS      = "o2s";
    protected static final String MTHD_GW_S2O_ACCESS      = "s2o";
    //@formatter:on


    // API Paths

    //@formatter:off
    // Access methods
    public static final String FULL_PATH_GW_O2S_ACCESS     = API_PATH + "/" + API_VER + "/" + MTHD_GW_O2S_ACCESS;
    public static final String FULL_PATH_GW_S2O_ACCESS     = API_PATH + "/" + API_VER + "/" + MTHD_GW_S2O_ACCESS;
    //@formatter:on


    // API Descriptions

    //@formatter:off
    // Access methods
    public static final String DESCR_PATH_GW_O2S_ACCESS    = "Set object's certificate and return JCP GW Obj2Srv's access info";
    public static final String DESCR_PATH_GW_S2O_ACCESS    = "Set service's certificate and return JCP GW Srv2Obj's access info";
    //@formatter:on

}
