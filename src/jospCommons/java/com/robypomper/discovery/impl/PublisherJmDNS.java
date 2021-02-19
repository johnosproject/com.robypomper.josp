package com.robypomper.discovery.impl;

import com.robypomper.discovery.Discover;
import com.robypomper.discovery.PublisherAbs;
import com.robypomper.java.JavaAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JmDNS publisher.
 */
public class PublisherJmDNS extends PublisherAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(PublisherJmDNS.class);
    private final JmDNS.Service internalService;


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType   the service type to publish.
     * @param srvName   the service name to publish.
     * @param srvPort   the service port to publish.
     * @param extraText string containing extra text related to service to publish.
     */
    public PublisherJmDNS(String srvType, String srvName, int srvPort, String extraText) {
        super(srvType, srvName, srvPort, extraText);
        internalService = new JmDNS.Service(getServiceType(), getServiceName(), getServicePort(), getServiceExtraText());
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
            startAutoDiscovery(JmDNS.IMPL_NAME);

        } catch (Discover.DiscoveryException e) {
            JavaAssertions.makeAssertion_Failed(e, "Can't start internal discover for JmDNS Publisher, continue publishing service");
        }

        JmDNS.startJmDNSSubSystem(this);
        try {
            JmDNS.addPubService(internalService);

        } catch (JmDNS.JmDNSException e) {
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

        try {
            JmDNS.removePubService(internalService);

        } catch (JmDNS.JmDNSException e) {
            JavaAssertions.makeAssertion_Failed(e, "");
        }
        JmDNS.stopJmDNSSubSystem(this);

        if (waitForDepublished)
            waitServiceDepublication();

        stopAutoDiscovery();

        emitOnStop(this, log);
    }

}
