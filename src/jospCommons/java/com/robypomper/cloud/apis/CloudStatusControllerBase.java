package com.robypomper.cloud.apis;

import com.robypomper.josp.params.cloud.CloudStatus;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CloudStatusControllerBase {

    public CloudStatus.Process getProcess() {
        return new CloudStatus.Process();
    }

    public CloudStatus.Java getJava() {
        return new CloudStatus.Java();
    }

    public List<CloudStatus.JavaThread> getJavaThreads() {
        List<CloudStatus.JavaThread> threads = new ArrayList<>();
        for (long thId : ManagementFactory.getThreadMXBean().getAllThreadIds())
            threads.add(new CloudStatus.JavaThread(thId));

        return threads;
    }

    public CloudStatus.Os getOs() {
        return new CloudStatus.Os();
    }

    public CloudStatus.CPU getCPU() {
        return new CloudStatus.CPU();
    }

    public CloudStatus.Memory getMemory() {
        return new CloudStatus.Memory();
    }

    public CloudStatus.Disks getDisks() {
        return new CloudStatus.Disks();
    }

    public CloudStatus.Network getNetwork() {
        return new CloudStatus.Network();
    }

    public List<CloudStatus.NetworkIntf> getNetworkIntfs() {
        List<CloudStatus.NetworkIntf> listInterfaces = new ArrayList<>();
        List<NetworkInterface> itfs;
        try {
            itfs = Collections.list(NetworkInterface.getNetworkInterfaces());

        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            for (NetworkInterface itf : itfs)
                listInterfaces.add(new CloudStatus.NetworkIntf(itf));

        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return listInterfaces;
    }

}
