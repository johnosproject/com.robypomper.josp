package com.robypomper.discovery.impl;

import com.robypomper.discovery.DiscoverAbs;
import com.robypomper.discovery.DiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.impl.JmmDNSImpl;
import java.net.InetAddress;

/**
 * JmmDNS discover.
 */
public class DiscoverJmmDNS extends DiscoverAbs implements ServiceListener {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DiscoverJmmDNS.class);
    private final javax.jmdns.JmmDNS jmmDNS;


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType the service type to looking for.
     */
    public DiscoverJmmDNS(String srvType) {
        super(srvType);
        jmmDNS = new JmmDNSImpl();
    }


    // Discovery mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (getState().isRunning())
            return;

        if (getState().isStartup())
            return;

        emitOnStarting(this, log);

        jmmDNS.addServiceListener(getServiceType() + ".local.", this);

        emitOnStart(this, log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (getState().isStopped())
            return;

        if (getState().isShutdown())
            return;

        emitOnStopping(this, log);

        jmmDNS.removeServiceListener(getServiceType(), this);

        deregisterAllServices();

        emitOnStop(this, log);
    }


    // JmDNS listener methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void serviceAdded(ServiceEvent event) {
        if (!disableLogs)
            log.trace(String.format("Found service '%s'", event.getName()));
        event.getDNS().requestServiceInfo(event.getType(), event.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serviceResolved(ServiceEvent event) {
        ServiceInfo si = event.getInfo();

        for (InetAddress add : si.getInet4Addresses())
            registerService(new DiscoveryService(event.getName(), event.getType(), event.getDNS().getName(), "IPv4", add, si.getPort(), new String(si.getTextBytes())));
        for (InetAddress add : si.getInet6Addresses())
            registerService(new DiscoveryService(event.getName(), event.getType(), event.getDNS().getName(), "IPv6", add, si.getPort(), new String(si.getTextBytes())));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serviceRemoved(ServiceEvent event) {
        deregisterService(new DiscoveryService(event.getName(), event.getType(), event.getDNS().getName(), null, null, null, null));
    }

}
