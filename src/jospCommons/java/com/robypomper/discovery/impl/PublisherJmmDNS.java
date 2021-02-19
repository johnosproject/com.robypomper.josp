package com.robypomper.discovery.impl;

import com.robypomper.discovery.Discover;
import com.robypomper.discovery.PublisherAbs;
import com.robypomper.java.JavaAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jmdns.ServiceInfo;
import javax.jmdns.impl.JmmDNSImpl;
import java.io.IOException;

/**
 * JmmDNS publisher.
 */
public class PublisherJmmDNS extends PublisherAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(PublisherJmmDNS.class);
    private final javax.jmdns.JmmDNS jmmDNS;
    private final javax.jmdns.ServiceInfo serviceInfo;


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType   the service type to publish.
     * @param srvName   the service name to publish.
     * @param srvPort   the service port to publish.
     * @param extraText string containing extra text related to service to publish.
     */
    public PublisherJmmDNS(String srvType, String srvName, int srvPort, String extraText) {
        super(srvType, srvName, srvPort, extraText);
        this.jmmDNS = new JmmDNSImpl();
        this.serviceInfo = ServiceInfo.create(getServiceType() + ".local.", getServiceName(), getServicePort(), getServiceExtraText());
    }


    // Publication mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(boolean waitForPublished) throws PublishException {
        if (getState().isRunning())
            return;

        if (getState().isStartup())
            return;

        emitOnStarting(this, log);

        try {
            startAutoDiscovery(JmmDNS.IMPL_NAME);

        } catch (Discover.DiscoveryException e) {
            JavaAssertions.makeAssertion_Failed(e, "Can't start internal discover for JmmDNS Publisher, continue publishing service");
        }

        try {
            jmmDNS.registerService(serviceInfo);

        } catch (IOException e) {
            throw new PublishException(String.format("Can't publish service '%s'", getServiceName()), e);
        }

        if (waitForPublished)
            waitServicePublication();

        emitOnStart(this, log);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hide(boolean waitForDepublished) {
        if (getState().isStopped())
            return;

        if (getState().isShutdown())
            return;

        emitOnStopping(this, log);

        jmmDNS.unregisterService(serviceInfo);

        if (waitForDepublished)
            waitServiceDepublication();

        stopAutoDiscovery();

        emitOnStop(this, log);
    }

}
