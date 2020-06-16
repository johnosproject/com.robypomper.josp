package com.robypomper.josp.jsl;

import com.robypomper.josp.core.jcpclient.JCPClient;
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
import com.robypomper.log.Mrk_JSL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class JSL_002 extends AbsJSL {

    // Class constants

    public static final String VERSION = FactoryJSL.JSL_VER_002;
    private static final int MAX_INSTANCE_ID = 10000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    public JSL_002(JSLSettings_002 settings, JCPClient_Service jcpClient, JSLServiceInfo srvInfo, JSLUserMngr usr, JSLObjsMngr objs, JSLCommunication comm) {
        super(settings, jcpClient, srvInfo, usr, objs, comm);
    }

    public static JSL instance(JSLSettings_002 settings) throws JCPClient.ConnectionSettingsException, JSLCommunication.LocalCommunicationException, JSLCommunication.CloudCommunicationException {
        String instanceId = Integer.toString(new Random().nextInt(MAX_INSTANCE_ID));
        log.info(Mrk_JSL.JSL_MAIN, String.format("Init JSL instance id '%s'", instanceId));

        JCPClient_Service jcpClient = new DefaultJCPClient_Service(settings);

        JSLServiceInfo srvInfo = new JSLServiceInfo_002(settings, jcpClient, instanceId);

        JSLUserMngr usr = new JSLUserMngr_002(settings, jcpClient);

        JSLObjsMngr objs = new JSLObjsMngr_002(settings, srvInfo);

        srvInfo.setSystems(usr, objs);

        JSLCommunication comm = new JSLCommunication_002(settings, srvInfo, jcpClient, usr, objs, instanceId);

        srvInfo.setCommunication(comm);
        objs.setCommunication(comm);

        return new JSL_002(settings, jcpClient, srvInfo, usr, objs, comm);
    }

    @Override
    public String version() {
        return VERSION;
    }

}
