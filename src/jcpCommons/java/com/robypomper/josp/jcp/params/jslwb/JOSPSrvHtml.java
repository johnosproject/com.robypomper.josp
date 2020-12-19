package com.robypomper.josp.jcp.params.jslwb;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.states.JCPClient2State;
import com.robypomper.josp.states.JSLLocalState;
import com.robypomper.josp.states.JSLState;

import javax.servlet.http.HttpSession;

@JsonAutoDetect
public class JOSPSrvHtml {

    public final String name;
    public final JSLState state;
    public final JCPClient2State stateJCP;
    public final boolean isJCPConnected;
    //public final GWClientState stateCloud;
    //public final boolean isCloudConnected;
    public final JSLLocalState stateLocal;
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
        this.state = jsl.getState();
        this.stateJCP = jsl.getJCPClient().getState();
        this.isJCPConnected = jsl.getJCPClient().isConnected();
        //this.stateCloud         = jsl.getCommunication().getCloudConnection().getState();
        //this.isCloudConnected   = jsl.getCommunication().getCloudConnection().isConnected();
        this.stateLocal = jsl.getCommunication().getLocalConnections().getState();
        this.isLocalRunning = jsl.getCommunication().getLocalConnections().isRunning();
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
