package com.robypomper.josp.defs.auth.keycloak;

public class Versions extends com.robypomper.josp.defs.auth.Versions {

    // Class Constants

    // API Group
    public static final String API_GROUP = "Keycloak";
    public static final String API_GROUP_FULL = com.robypomper.josp.defs.auth.Versions.API_GROUP_FULL + " / " + API_GROUP;
    public static final String API_GROUP_DESCR = "";
    // Urls
    public static final String API_PATH_BASE = com.robypomper.josp.defs.auth.Versions.API_PATH_BASE;    // + "/keycloak";       // Path doesn't respect JCP API convention
    public static final String AUTH_PATH_BASE = com.robypomper.josp.defs.auth.Versions.AUTH_PATH_BASE;

}