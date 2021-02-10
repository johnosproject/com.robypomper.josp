package com.robypomper.comm.server;

import com.robypomper.comm.behaviours.ByeMsgConfigs;
import com.robypomper.comm.behaviours.HeartBeatConfigs;
import com.robypomper.comm.configs.DataEncodingConfigs;
import com.robypomper.comm.exception.ServerShutdownException;
import com.robypomper.comm.exception.ServerStartupException;
import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.security.cert.Certificate;

public abstract class ServerAbsSSL extends ServerAbs {

    // Class constants

    public static final AbsCustomTrustManager TRUST_MANAGER = null;
    public static final Certificate LOCAL_PUBLIC_CERTIFICATE = null;
    public static final boolean REQUIRE_AUTH = false;
    public static final boolean ENABLE_CERT_SHARING_SERVER = false;
    public static final int TIMEOUT_CERT_SHARING_SERVER = 0;


    // Internal vars

    private final SSLContext sslCtx;
    private final boolean requireAuth;
    private final ServerCertSharing serverCertSharing;


    // Constructors

    protected ServerAbsSSL(String localId, int bindPort, String protoName,
                           SSLContext sslCtx) {
        this(localId, null, bindPort, protoName,
                sslCtx);
    }

    protected ServerAbsSSL(String localId, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing) {
        this(localId, null, bindPort, protoName,
                sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG);
    }

    protected ServerAbsSSL(String localId, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing,
                           Charset charset, byte[] delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, null, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;
        this.serverCertSharing = enableCertSharing ? new ServerCertSharing(localId + "-CertSharing", null, bindPort + 1, this, trustManager, localPublicCertificate) : null;
    }

    protected ServerAbsSSL(String localId, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing,
                           Charset charset, String delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, String byeMsg) {
        super(localId, null, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;

        this.serverCertSharing = enableCertSharing ? ServerCertSharing.generate(this, null, bindPort, trustManager, localPublicCertificate) : null;
    }

    protected ServerAbsSSL(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           SSLContext sslCtx) {
        this(localId, bindAddr, bindPort, protoName,
                sslCtx, TRUST_MANAGER, LOCAL_PUBLIC_CERTIFICATE, REQUIRE_AUTH, ENABLE_CERT_SHARING_SERVER);
    }

    protected ServerAbsSSL(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing) {
        this(localId, bindAddr, bindPort, protoName,
                sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing,
                DataEncodingConfigs.CHARSET, DataEncodingConfigs.DELIMITER,
                HeartBeatConfigs.TIMEOUT_MS, HeartBeatConfigs.TIMEOUT_HB_MS, HeartBeatConfigs.ENABLE_HB_RES,
                ByeMsgConfigs.ENABLE, ByeMsgConfigs.BYE_MSG);
    }

    protected ServerAbsSSL(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing,
                           Charset charset, byte[] delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, bindAddr, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;
        this.serverCertSharing = enableCertSharing ? new ServerCertSharing(localId + "-CertSharing", bindAddr, bindPort + 1, this, trustManager, localPublicCertificate) : null;
    }

    protected ServerAbsSSL(String localId, InetAddress bindAddr, int bindPort, String protoName,
                           SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing,
                           Charset charset, String delimiter,
                           int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes,
                           Boolean enableByeMsg, String byeMsg) {
        super(localId, bindAddr, bindPort, protoName,
                charset, delimiter,
                hbTimeoutMs, hbTimeoutHBMs, enableHBRes,
                enableByeMsg, byeMsg);
        this.sslCtx = sslCtx;
        this.requireAuth = requireAuth;

        this.serverCertSharing = enableCertSharing ? ServerCertSharing.generate(this, bindAddr, bindPort, trustManager, localPublicCertificate) : null;
    }


    // Getters

    public SSLContext getSSLContext() {
        return sslCtx;
    }

    public boolean isAuthRequired() {
        return requireAuth;
    }


    // Server startup methods

    public void startup() throws ServerStartupException {
        super.startup();
        if (serverCertSharing != null)
            serverCertSharing.startup();
    }

    public void shutdown() throws ServerShutdownException {
        super.shutdown();
        if (serverCertSharing != null)
            serverCertSharing.shutdown();
    }


    // Connection methods

    @Override
    protected ServerSocket generateBindedServerSocket(InetAddress bindAddr, int bindPort) throws ServerStartupException {
        try {
            SSLServerSocket sslSS = (SSLServerSocket) sslCtx.getServerSocketFactory().createServerSocket(bindPort, 50, bindAddr);
            sslSS.setEnabledProtocols(new String[]{"TLSv1.2"});
            sslSS.setNeedClientAuth(requireAuth);
            return sslSS;

        } catch (IOException e) {
            throw new ServerStartupException(this, bindAddr, bindPort, e);
        }
    }

}
