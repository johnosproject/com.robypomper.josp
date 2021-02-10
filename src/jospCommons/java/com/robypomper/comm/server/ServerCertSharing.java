package com.robypomper.comm.server;

import com.robypomper.comm.exception.PeerException;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.java.JavaAssertions;
import com.robypomper.java.JavaByteArrays;
import com.robypomper.java.JavaJKS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ServerCertSharing extends ServerAbsTCP {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ServerCertSharing.class);
    private final Server server;
    private final Certificate localPublicCertificate;
    private final AbsCustomTrustManager certTrustManager;
    private final Map<ServerClient, byte[]> serverCertBuffer = new HashMap<>();

    // Class constants

    public static final String PROTO_NAME = "CertSharing";


    // Constructors

    public ServerCertSharing(String localId, InetAddress bindAddr, int bindPort,
                             Server server, AbsCustomTrustManager certTrustManager, Certificate localPublicCertFile) {
        super(localId, bindAddr, bindPort, PROTO_NAME);
        addListener(listenerClient);
        this.server = server;
        this.certTrustManager = certTrustManager;
        this.localPublicCertificate = localPublicCertFile;
    }

    public static ServerCertSharing generate(Server server, InetAddress bindAddr, int bindPort, AbsCustomTrustManager trustManager, Certificate localPublicCertificate) {
        return new ServerCertSharing(server.getLocalId() + "-CertSharing", bindAddr, bindPort + 1, server, trustManager, localPublicCertificate);
    }


    // Message methods

    @Override
    public boolean processData(ServerClient client, byte[] data) {
        bufferClientCertificate(client, data);
        return true;
    }

    @Override
    public boolean processData(ServerClient client, String data) {
        return false;
    }

    private void bufferClientCertificate(ServerClient client, byte[] readData) {
        if (serverCertBuffer.get(client) == null)
            serverCertBuffer.put(client, new byte[0]);

        serverCertBuffer.put(client, JavaByteArrays.append(serverCertBuffer.get(client), readData));
        if (storeClientCertificate(client, serverCertBuffer.get(client)))
            serverCertBuffer.remove(client);
    }

    private boolean storeClientCertificate(ServerClient client, byte[] serverCert) {
        if (serverCert.length == 0)
            return false;

        try {
            log.trace(String.format("Server store client '%s''s certificate '%s'", client.getLocalId(), String.format("CL@%s", client.getRemoteId())));
            certTrustManager.addCertificateByte(String.format("CL@%s", client.getRemoteId()), serverCert);

        } catch (AbsCustomTrustManager.UpdateException | JavaJKS.LoadingException e) {
            log.trace(String.format("ERROR on Server store client '%s''s certificate [%s] %s", client.getLocalId(), e.getClass().getSimpleName(), e.getMessage()), e);
            return false;
        }

        return true;
    }

    private void sendServerCertificateToClient(ServerClient client) {
        if (localPublicCertificate == null)
            return;

        try {
            log.trace(String.format("Server send his certificate to client '%s'", client.getLocalId()));
            client.sendData(localPublicCertificate.getEncoded());

        } catch (PeerException e) {
            JavaAssertions.makeAssertionFailed(e, String.format("Method sendClientCertificate() is called by onConnect() event, so it don't throw any exception. [%s] %s", e.getClass().getSimpleName(), e.getMessage()));
        } catch (CertificateEncodingException e) {
            JavaAssertions.makeAssertionFailed(e, String.format("Method Certificate.getEncoded() i used to convert a valid certificate to byte[], that should not throw exception. [%s] %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }


    // Server listener

    ServerClientsListener listenerClient = new ServerClientsListener() {

        @Override
        public void onConnect(Server server, ServerClient client) {
            sendServerCertificateToClient(client);
        }

        @Override
        public void onDisconnect(Server server, ServerClient client) {
        }

        @Override
        public void onFail(Server server, ServerClient client, String failMsg, Throwable exception) {
        }

    };

}
