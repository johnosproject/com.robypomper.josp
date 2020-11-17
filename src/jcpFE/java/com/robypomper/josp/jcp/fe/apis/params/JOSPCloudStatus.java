package com.robypomper.josp.jcp.fe.apis.params;

import com.robypomper.cloud.params.CloudStatus;
import com.robypomper.josp.jsl.JSL;

import javax.servlet.http.HttpSession;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class JOSPCloudStatus {

    public static class JCPFE {

        public final Date timeStart;        // from Java
        public final long timeRunning;      // from Java
        public final double cpuUsed;
        public final double memoryUsed;
        public final int sessionsCount;
        public final int sessionsJslCount;
        //public final List<JSLInstance> jslInstances;

        public final boolean jcpAPIisConnected;


        public JCPFE(JSL current, List<JSL> all, Collection<HttpSession> sessions) {
            timeStart = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
            timeRunning = ManagementFactory.getRuntimeMXBean().getUptime();
            cpuUsed = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
            ;
            memoryUsed = (double) Runtime.getRuntime().freeMemory() / CloudStatus.BYTE_TRANSFORM;
            sessionsCount = sessions.size();
            sessionsJslCount = all.size();
            //jslInstances = new ArrayList<>();
            //for (JSL jsl : all)
            //    jslInstances.add(new JSLInstance(jsl));

            jcpAPIisConnected = current.getJCPClient().isConnected();
        }
    }

    public static class JSLInstance {

        public final String srvName;
        public final String srvId;
        public final String usrId;
        public final String instId;
        public final String usrName;

        public JSLInstance(JSL jsl) {
            srvName = jsl.getServiceInfo().getSrvName();
            srvId = jsl.getServiceInfo().getSrvId();
            usrId = jsl.getServiceInfo().getUserId();
            instId = jsl.getServiceInfo().getInstanceId();
            usrName = jsl.getServiceInfo().getUsername();
        }
    }

}
