package com.robypomper.josp.test;

import javafx.util.Pair;

public class ClientSettings {

    private final String proto;
    private final int test_clientPort;
    private final Pair<String,String> test_clientCredentials;
    private final int test_resServerPort;
    private final int test_authServerPort;
    private final String test_authServerRealm;


    // Constructors

    protected ClientSettings(String proto, int clientPort, Pair<String, String> clientCredentials, int resServerPort, int authServerPort, String authServerRealm) {
        this.proto = proto;
        this.test_clientPort = clientPort;
        this.test_clientCredentials = clientCredentials;
        this.test_resServerPort = resServerPort;
        this.test_authServerPort = authServerPort;
        this.test_authServerRealm = authServerRealm;
    }


    // Getters

    public String getClientProtocol() {
        return proto;
    }

    public int getClientPort() {
        return test_clientPort;
    }

    public Pair<String, String> getClientCredentials() {
        return test_clientCredentials;
    }

    public int getResServerPort() {
        return test_resServerPort;
    }

    public int getAuthServerPort() {
        return test_authServerPort;
    }

    public String getAuthServerRealm() {
        return test_authServerRealm;
    }
}
