/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.discovery.impl;

import com.robypomper.discovery.Discover;
import com.robypomper.discovery.PublisherAbs;
import com.robypomper.java.JavaAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * DNSSD publisher.
 */
public class PublisherDNSSD extends PublisherAbs {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(PublisherDNSSD.class);
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
    public PublisherDNSSD(String srvType, String srvName, int srvPort, String extraText) {
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
            startAutoDiscovery(DNSSD.IMPL_NAME);

        } catch (Discover.DiscoveryException e) {
            JavaAssertions.makeAssertion_Failed(e, "Can't start internal discover for DNSSD Publisher, continue publishing service");
        }

        JavaAssertions.makeAssertion(publishProcess == null, "Can't call PublisherDNSSD::publish() with publishProcess!=null.");
        try {
            String[] cmdArray = new String[]{"dns-sd", "-R", getServiceName(), getServiceType(), ".", Integer.toString(getServicePort()), getServiceExtraText()};
            publishProcess = Runtime.getRuntime().exec(cmdArray);

        } catch (IOException e) {
            throw new PublishException("Can't publish DNSSD Discover", e);
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

        JavaAssertions.makeAssertion(publishProcess != null, "Can't call PublisherDNSSD::stop() with publishProcess==null.");
        if (publishProcess != null)
            try {
                publishProcess.destroy();
                publishProcess.waitFor(PROCESS_WAITING_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            } catch (InterruptedException e) {
                JavaAssertions.makeAssertion_Failed(e, "Can't stop DNSSD Publisher, error on destroy browser thread");
            }
        publishProcess = null;

        // CAN'T LISTEN FOR SERVICE DEPUBLICATION <- This fail on DNSSDDiscoveryTest.DNSSD_PublishDiscoverAndPublishTest
        //if (waitForDepublished)
        //    waitServiceDepublication();

        stopAutoDiscovery();

        emitOnStop(this, log);
    }

}
