package com.robypomper.josp.jcp.defs.gateways.internal.clients.registration;


/**
 * JCP Gateways - Clients / Registration 2.0
 */
public class Params20 {

    public static class AccessRequest {
        public String instanceId;
        public byte[] clientCertificate;
    }

    public static class O2SAccessRequest extends AccessRequest {
    }

    public static class S2OAccessRequest extends AccessRequest {
    }

    public static class AccessInfo {
        public String gwAddress;
        public int gwPort;
        public byte[] gwCertificate;
    }

    public static class O2SAccessInfo extends AccessInfo {
    }

    public static class S2OAccessInfo extends AccessInfo {
    }

}
