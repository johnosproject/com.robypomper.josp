package com.robypomper.josp.params.jcp;

import com.robypomper.BuildInfo;

import java.lang.management.ManagementFactory;
import java.util.Date;

public class ServiceStatus {

    public BuildInfo buildInfo;
    public Date timeStart;
    public long timeRunning;
    public double cpuUsed;
    public double memoryUsed;

    public ServiceStatus() {

    }

    public ServiceStatus(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
        timeStart = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
        timeRunning = ManagementFactory.getRuntimeMXBean().getUptime();
        cpuUsed = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        memoryUsed = (double) Runtime.getRuntime().freeMemory() / JCPStatus.BYTE_TRANSFORM;
    }

}
