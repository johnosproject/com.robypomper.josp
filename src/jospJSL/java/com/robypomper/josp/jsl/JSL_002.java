package com.robypomper.josp.jsl;

import com.robypomper.java.JavaVersionUtils;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.info.JCPAPIsVersions;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLCommunication_002;
import com.robypomper.josp.jsl.jcpclient.DefaultJCPClient_Service;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo_002;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.jsl.user.JSLUserMngr_002;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class JSL_002 extends AbsJSL {

    // Class constants

    public static final String VERSION = FactoryJSL.JSL_VER_2_0_0;   // Upgraded to 2.0.0
    private static final int MAX_INSTANCE_ID = 10000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    public JSL_002(JSLSettings_002 settings, JCPClient_Service jcpClient, JSLServiceInfo srvInfo, JSLUserMngr usr, JSLObjsMngr objs, JSLCommunication comm) {
        super(settings, jcpClient, srvInfo, usr, objs, comm);
    }

    public static JSL instance(JSLSettings_002 settings) throws JSLCommunication.LocalCommunicationException, JSLCommunication.CloudCommunicationException {
        log.info("\n\n" + JavaVersionUtils.buildJavaVersionStr("John Service Library",VERSION));

        String instanceId = Integer.toString(new Random().nextInt(MAX_INSTANCE_ID));
        log.info(Mrk_JSL.JSL_MAIN, String.format("Init JSL instance id '%s'", instanceId));

        JCPClient_Service jcpClient = new DefaultJCPClient_Service(settings);
        try {
            try {
                jcpClient.connect();

            } catch (JCPClient2.AuthenticationException e) {
                log.warn(Mrk_JSL.JSL_MAIN, String.format("Error on user authentication to the JCP %s", e.getMessage()), e);
                jcpClient.connect();
            }

        } catch (JCPClient2.AuthenticationException ignore) {

        } catch (JCPClient2.ConnectionException e) {
            e.printStackTrace();

        } catch (JCPClient2.JCPNotReachableException e) {
            jcpClient.startConnectionTimer();
        }

        JSLServiceInfo srvInfo = new JSLServiceInfo_002(settings, jcpClient, instanceId);

        JSLUserMngr usr = new JSLUserMngr_002(settings, jcpClient);

        JSLObjsMngr objs = new JSLObjsMngr_002(settings, srvInfo);

        srvInfo.setSystems(usr, objs);

        JSLCommunication comm = new JSLCommunication_002(settings, srvInfo, jcpClient, usr, objs, instanceId);

        srvInfo.setCommunication(comm);
        usr.setCommunication(comm);
        objs.setCommunication(comm);

        return new JSL_002(settings, jcpClient, srvInfo, usr, objs, comm);
    }

    @Override
    public String version() {
        return VERSION;
    }

    @Override
    public String[] versionsJOSPObject() {
        return new String[]{"2.0.0","2.0.1"};
    }

    @Override
    public String[] versionsJOSPProtocol() {
        return new String[]{JOSPProtocol.JOSP_PROTO_VERSION_2_0};
    }

    @Override
    public String[] versionsJCPAPIs() {
        return new String[]{JCPAPIsVersions.VER_JCP_APIs_2_0};
    }

}
