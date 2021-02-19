package com.robypomper.discovery.impl;

import com.robypomper.discovery.DiscoverAbs;
import com.robypomper.discovery.DiscoveryService;
import com.robypomper.java.JavaAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.net.InetAddress;

/**
 * JmDNS discover.
 */
public class DiscoverJmDNS extends DiscoverAbs implements ServiceListener {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(DiscoverJmDNS.class);


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType the service type to looking for.
     */
    public DiscoverJmDNS(String srvType) {
        super(srvType);
    }


    // Discovery mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws DiscoveryException {
        if (getState().isRunning())
            return;

        if (getState().isStartup())
            return;

        emitOnStarting(this, log);

        JmDNS.startJmDNSSubSystem(this);
        try {
            JmDNS.addDiscoveryListener(getServiceType() + ".local.", this);

        } catch (JmDNS.JmDNSException e) {
            throw new DiscoveryException(String.format("Can't discover services type '%s'", getServiceType()), e);
        }

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

        try {
            JmDNS.removeDiscoveryListener(getServiceType() + ".local.", this);

        } catch (JmDNS.JmDNSException e) {
            JavaAssertions.makeAssertion_Failed(String.format("Error on JmDNSDiscover '%s''s JmDNS can't remove discovery listener", this));
        }
        JmDNS.stopJmDNSSubSystem(this);

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
