package com.robypomper.comm.server;

import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;

import javax.net.ssl.SSLContext;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.cert.Certificate;

public class ServerAbsSSL_Impl extends ServerAbsSSL {

    public String lastDataRx;

    public ServerAbsSSL_Impl(String localId, int bindPort, String protoName, SSLContext sslCtx) {
        super(localId, bindPort, protoName, sslCtx);
    }

    public ServerAbsSSL_Impl(String localId, int bindPort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing) {
        super(localId, bindPort, protoName, sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing);
    }

    public ServerAbsSSL_Impl(String localId, int bindPort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing, Charset charset, byte[] delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, bindPort, protoName, sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg);
    }

    public ServerAbsSSL_Impl(String localId, int bindPort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing, Charset charset, String delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, String byeMsg) {
        super(localId, bindPort, protoName, sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg);
    }

    public ServerAbsSSL_Impl(String localId, InetAddress bindAddr, int bindPort, String protoName, SSLContext sslCtx) {
        super(localId, bindAddr, bindPort, protoName, sslCtx);
    }

    public ServerAbsSSL_Impl(String localId, InetAddress bindAddr, int bindPort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing) {
        super(localId, bindAddr, bindPort, protoName, sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing);
    }

    public ServerAbsSSL_Impl(String localId, InetAddress bindAddr, int bindPort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing, Charset charset, byte[] delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, byte[] byeMsg) {
        super(localId, bindAddr, bindPort, protoName, sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg);
    }

    public ServerAbsSSL_Impl(String localId, InetAddress bindAddr, int bindPort, String protoName, SSLContext sslCtx, AbsCustomTrustManager trustManager, Certificate localPublicCertificate, boolean requireAuth, boolean enableCertSharing, Charset charset, String delimiter, int hbTimeoutMs, int hbTimeoutHBMs, Boolean enableHBRes, Boolean enableByeMsg, String byeMsg) {
        super(localId, bindAddr, bindPort, protoName, sslCtx, trustManager, localPublicCertificate, requireAuth, enableCertSharing, charset, delimiter, hbTimeoutMs, hbTimeoutHBMs, enableHBRes, enableByeMsg, byeMsg);
    }

    @Override
    public boolean processData(ServerClient client, byte[] data) {
        return false;
    }

    @Override
    public boolean processData(ServerClient client, String data) {
        lastDataRx = data;
        return true;
    }

}
