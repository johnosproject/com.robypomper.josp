package com.robypomper.josp.params.jcp;

import java.lang.management.ManagementFactory;
import java.util.Date;

public class JCPFEStatus {

    public final Date timeStart;
    public final long timeRunning;
    public final double cpuUsed;
    public final double memoryUsed;

    public JCPFEStatus() {
        timeStart = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
        timeRunning = ManagementFactory.getRuntimeMXBean().getUptime();
        cpuUsed = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        ;
        memoryUsed = (double) Runtime.getRuntime().freeMemory() / JCPStatus.BYTE_TRANSFORM;
    }

}
