package com.robypomper.josp.jcp.params.jslwb;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.robypomper.josp.jsl.JSL;

import javax.servlet.http.HttpSession;

@JsonAutoDetect
public class JOSPSrvHtml {

    public final String name;
    public final JSL.Status status;
    public final boolean isJCPConnected;
    public final boolean isCloudConnected;
    public final boolean isLocalRunning;
    public final String srvId;
    public final String usrId;
    public final String instId;
    public final String jslVersion;
    public final String[] supportedJCPAPIsVersions;
    public final String[] supportedJOSPProtocolVersions;
    public final String[] supportedJODVersions;
    public final String sessionId;

    public JOSPSrvHtml(HttpSession session, JSL jsl) {
        this.name = jsl.getServiceInfo().getSrvName();
        this.status = jsl.status();
        this.isJCPConnected = jsl.getJCPClient().isConnected();
        this.isCloudConnected = jsl.getCommunication().isCloudConnected();
        this.isLocalRunning = jsl.getCommunication().isLocalRunning();
        this.srvId = jsl.getServiceInfo().getSrvId();
        this.usrId = jsl.getServiceInfo().getUserId();
        this.instId = jsl.getServiceInfo().getInstanceId();
        this.jslVersion = jsl.version();
        this.supportedJCPAPIsVersions = jsl.versionsJCPAPIs();
        this.supportedJOSPProtocolVersions = jsl.versionsJOSPProtocol();
        this.supportedJODVersions = jsl.versionsJOSPObject();
        this.sessionId = session.getId();
    }

}
