package com.robypomper.josp.jcp.gws.services;

import com.robypomper.comm.server.Server;
import com.robypomper.comm.server.ServerAbsSSL;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.server.ServerClientsListener;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.josp.protocol.JOSPProtocol;

import javax.net.ssl.SSLContext;
import java.security.cert.Certificate;

public class GWServer extends ServerAbsSSL {

    // Internal vars

    private final GWServiceAbs gwService;
    private final AbsCustomTrustManager trustManager;
    private final Certificate publicCertificate;


    // Constructors

    public GWServer(GWServiceAbs gwService, SSLContext sslCtx, String idServer, int port, AbsCustomTrustManager trustManager, Certificate publicCertificate) {
        super(idServer, port, JOSPProtocol.JOSP_PROTO_NAME, sslCtx, trustManager, publicCertificate, true, false);
        this.gwService = gwService;
        this.trustManager = trustManager;
        this.publicCertificate = publicCertificate;
        addListener(serverClientListener);
    }


    // Getters

    public AbsCustomTrustManager getTrustManager() {
        return trustManager;
    }

    public Certificate getPublicCertificate() {
        return publicCertificate;
    }


    //

    private final ServerClientsListener serverClientListener = new ServerClientsListener() {

        @Override
        public void onConnect(Server server, ServerClient client) {
            gwService.onClientConnection(client);
        }

        @Override
        public void onDisconnect(Server server, ServerClient client) {
            gwService.onClientDisconnection(client);
        }

        @Override
        public void onFail(Server server, ServerClient client, String failMsg, Throwable exception) {
            System.out.println(failMsg + " - " + exception);
        }

    };

    // Messages methods

    @Override
    public boolean processData(ServerClient client, byte[] data) {
        return false;
    }

    @Override
    public boolean processData(ServerClient client, String data) {
        return gwService.processData(client, data);
    }

}
