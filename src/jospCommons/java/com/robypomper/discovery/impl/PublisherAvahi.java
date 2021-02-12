package com.robypomper.discovery.impl;

import com.robypomper.discovery.Discover;
import com.robypomper.discovery.PublisherAbs;
import com.robypomper.java.JavaAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Avahi publisher.
 */
public class PublisherAvahi extends PublisherAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(PublisherAvahi.class);
    public Process publishProcess = null;


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType   the service type to publish.
     * @param srvName   the service name to publish.
     * @param srvPort   the service port to publish.
     * @param extraText string containing extra text related to service to publish.
     */
    public PublisherAvahi(String srvType, String srvName, int srvPort, String extraText) {
        super(srvType, srvName, srvPort, extraText);
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
            startAutoDiscovery(Avahi.IMPL_NAME);

        } catch (Discover.DiscoveryException e) {
            JavaAssertions.makeAssertionFailed(e, "Can't start internal discover for Avahi Publisher, continue publishing service");
        }

        JavaAssertions.makeAssertion(publishProcess == null, "Can't call PublisherAvahi::publish() with publishProcess!=null.");
        try {

            String[] cmdArray;
            if (getServiceExtraText() != null && !getServiceExtraText().isEmpty())
                cmdArray = new String[]{"avahi-publish", "-s", getServiceName(), getServiceType() + ".", Integer.toString(getServicePort()), getServiceExtraText()};
            else
                cmdArray = new String[]{"avahi-publish", "-s", getServiceName(), getServiceType() + ".", Integer.toString(getServicePort()),};
            publishProcess = Runtime.getRuntime().exec(cmdArray);

        } catch (IOException e) {
            throw new PublishException("Can't publish Avahi Discover", e);
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

        JavaAssertions.makeAssertion(publishProcess != null, "Can't call PublisherAvahi::stop() with publishProcess==null.");
        if (publishProcess != null)
            try {
                publishProcess.destroy();
                publishProcess.waitFor(PROCESS_WAITING_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                JavaAssertions.makeAssertionFailed(e, "Can't stop Avahi Publisher, error on destroy browser thread");
            }
        publishProcess = null;

        if (waitForDepublished)
            waitServiceDepublication();

        stopAutoDiscovery();

        emitOnStop(this, log);
    }

}
