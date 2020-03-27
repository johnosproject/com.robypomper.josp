package com.robypomper.josp.jcp.apis.paths;


/**
 * Auth API Paths class definitions.
 * <p>
 * APIs group is used for the access to the auth server APIs, like that one to
 * retrieve user profile.
 * <p>
 * The Auth Service path is used by JOSP components to send auth requests.
 */
public class APIAuth {
//@formatter:off

    // Class constants

    public static final String REALM="jcp";


    // Base

    public static final String PATH_BASE = "";

    public static final String FULL_PATH_BASE   = JcpAPI.PATH_AUTH_BASE + PATH_BASE;

    public static final String URL_PATH_BASE    = JcpAPI.URL_PATH_AUTH_BASE + PATH_BASE;


    // Keycloak APIs

    public static final String PATH_USER    = "/admin/realms/" + REALM + "/users";

    public static final String FULL_PATH_USER   = FULL_PATH_BASE + PATH_USER;

    public static final String URL_PATH_USER    = URL_PATH_BASE + PATH_USER;


    // Auth service

    public static final String PATH_AUTH        = "/realms/" + REALM + "/protocol/openid-connect/auth";
    public static final String PATH_TOKEN       = "/realms/" + REALM + "/protocol/openid-connect/token";
    public static final String PATH_CALLBACK    = "/callback";

    public static final String FULL_PATH_AUTH       = FULL_PATH_BASE + PATH_AUTH;
    public static final String FULL_PATH_TOKEN      = FULL_PATH_BASE + PATH_TOKEN;
    public static final String FULL_PATH_CALLBACK   = "/callback";

    public static final String URL_PATH_AUTH        = URL_PATH_BASE + PATH_AUTH;
    public static final String URL_PATH_TOKEN       = URL_PATH_BASE + PATH_TOKEN;
    public static final String URL_PATH_CALLBACK    = JcpAPI.URL_DOM_API + PATH_CALLBACK;

//@formatter:on
}
