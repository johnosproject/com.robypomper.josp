package com.robypomper.josp.test;

import javafx.util.Pair;

public class ClientSettingsJcp extends ClientSettings {

    // JOSP environment settings

    private static final String CLIENT_PROTO = "https";
    private static final int CLIENT_PORT = 9001;
    private static final int RES_PORT = 9001;
    private static final int AUTH_PORT = 8998;
    public static final String AUTH_REALM = "jcp";

    // Client credentials

    private static final Pair<String,String> CLIENT_ID_OBJ = new Pair<>("test-client-obj","2d1f9b96-70d3-443b-b21b-08a401ddc16c");
    private static final Pair<String,String> CLIENT_ID_SRV = new Pair<>("test-client-srv","88bc8baf-c924-42b9-b691-b2d7c5cec696");
    private static final Pair<String,String> CLIENT_ID_SWG = new Pair<>("test-client-swagger","cf319f62-5275-4092-b346-65448535748d");

    public static Pair<String, String> findClientCredentials(String cliCredName) {
        if (cliCredName.compareToIgnoreCase(AUTH_REALM + "-" + CLIENT_ID_OBJ.getKey())==0) return CLIENT_ID_OBJ;
        if (cliCredName.compareToIgnoreCase(AUTH_REALM + "-" + CLIENT_ID_SRV.getKey())==0) return CLIENT_ID_SRV;
        if (cliCredName.compareToIgnoreCase(AUTH_REALM + "-" + CLIENT_ID_SWG.getKey())==0) return CLIENT_ID_SWG;
        return null;
    }

    // Constructors

    public ClientSettingsJcp(Pair<String,String> clientCredentials, int resServerPort) {
        super(CLIENT_PROTO,CLIENT_PORT,clientCredentials,resServerPort!=-1?resServerPort:RES_PORT,AUTH_PORT,AUTH_REALM);
    }

}
