package com.robypomper.discovery.impl;

import com.robypomper.discovery.AbsDiscover;
import com.robypomper.discovery.DiscoveryService;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.net.InetAddress;

public abstract class AbsJmDNSDiscover extends AbsDiscover implements ServiceListener {

    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType the service type to looking for.
     */
    protected AbsJmDNSDiscover(String srvType) {
        super(srvType);
    }


    // JmDNS listener methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void serviceAdded(ServiceEvent event) {
        log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Detected service string '%s'", event.getName()));
        event.getDNS().requestServiceInfo(event.getType(), event.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serviceResolved(ServiceEvent event) {
        log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Resolved service string '%s'", event.getName()));

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
        log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Lost service '%s'", event.getName()));

        deregisterService(new DiscoveryService(event.getName(), event.getType(), event.getDNS().getName(), null, null, null, null));
    }

}
