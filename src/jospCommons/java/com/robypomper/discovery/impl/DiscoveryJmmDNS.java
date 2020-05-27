package com.robypomper.discovery.impl;

import com.robypomper.discovery.AbsDiscover;
import com.robypomper.discovery.AbsPublisher;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;


/**
 * Implementations of the discovery system based on JmmDNS (multi cast).
 */
public class DiscoveryJmmDNS {

    // Class constants

    public static final String IMPL_NAME = "JmmDNS";


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Publisher and discover implementations

    /**
     * JmmDNS publisher.
     */
    public static class Publisher extends AbsPublisher {

        // Internal vars

        private static JmmDNS jmmDNS = null;


        // Constructor

        /**
         * Default constructor.
         *
         * @param srvType   the service type to publish.
         * @param srvName   the service name to publish.
         * @param srvPort   the service port to publish.
         * @param extraText string containing extra text related to service to publish.
         */
        public Publisher(String srvType, String srvName, int srvPort, String extraText) {
            super(srvType, srvName, srvPort, extraText);
            log.debug(Mrk_Commons.DISC_PUB_IMPL, String.format("Initialized JmmDNS::Publisher instance for '%s:%s' service on port '%d'", srvType, srvName, srvPort));
        }


        // Publication mngm

        /**
         * {@inheritDoc}
         */
        @Override
        public void publish(boolean waitForPublished) throws PublishException {
            log.info(Mrk_Commons.DISC_PUB_IMPL, String.format("Publish service '%s' on '%d'", getServiceName(), getServicePort()));
            if (!setIsPublishing(true)) {return;}

            // Init discovery system for service publication checks
            startAutoDiscovery(IMPL_NAME);

            // Publish service with JmmDNS instance
            jmmDNS = JmmDNS.Factory.getInstance();
            try {
                jmmDNS.registerService(toServiceInfo());
            } catch (IOException e) {
                throw new PublishException(String.format("ERR: can't publish service '%s' because %s", getServiceName(), e.getMessage()));
            }

            // Wait for service published
            if (waitForPublished)
                waitServicePublication();

            setIsPublishing(false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void hide(boolean waitForDepublished) throws PublishException {
            log.info(Mrk_Commons.DISC_PUB_IMPL, String.format("Hide service '%s'", getServiceName()));
            if (!setIsDepublishing(true)) {return;}

            // Halt JmmDNS instance used to publish the service
            jmmDNS.unregisterService(toServiceInfo());
            try {
                jmmDNS.close();
            } catch (IOException e) {
                throw new PublishException(String.format("ERR: can't hide service '%s' because %s.", getServiceName(), e.getMessage()));
            }

            // Wait for service de-publication
            if (!waitForDepublished)
                waitServiceDepublication();

            // Stop and destroy discovery system for service publication checks
            stopAutoDiscovery();

            setIsDepublishing(false);
        }

        /**
         * Create new {@link ServiceInfo} instance corresponding to current
         * publishing service.
         *
         * @return an instance of {@link ServiceInfo}.
         */
        private ServiceInfo toServiceInfo() {
            return ServiceInfo.create(getServiceType() + ".local.", getServiceName(), getServicePort(), getServiceExtraText());
        }

    }

    /**
     * JmmDNS discover.
     */
    public static class Discover extends AbsDiscover implements ServiceListener {

        // Internal vars

        private static JmmDNS jmmDNS = null;


        // Constructor

        /**
         * Default constructor.
         *
         * @param srvType the service type to looking for.
         */
        public Discover(String srvType) {
            super(srvType);
            log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Initialized JmmDNS::Discover instance for '%s' service type", srvType));
        }


        // Discovery mngm

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRunning() {
            return jmmDNS != null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() {
            if (isRunning()) {
                log.warn(Mrk_Commons.DISC_DISC_IMPL, String.format("Can't start discovery services '%s' because already started", getServiceType()));
                return;
            }

            log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Start discovery services '%s'", getServiceType()));
            jmmDNS = JmmDNS.Factory.getInstance();
            jmmDNS.addServiceListener(getServiceType() + ".local.", this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() throws DiscoveryException {
            if (!isRunning()) {
                log.warn(Mrk_Commons.DISC_DISC_IMPL, String.format("Can't stop discovery services '%s' because already stopped", getServiceType()));
                return;
            }

            log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Stop discovery services '%s'", getServiceType()));
            jmmDNS.removeServiceListener(getServiceType(), this);

            try {
                jmmDNS.close();
            } catch (IOException e) {
                throw new DiscoveryException(String.format("WAR: error closing discovery system '%s' because %s.", getServiceType(), e.getMessage()));
            }
        }


        // JmDNS listener methods

        /**
         * {@inheritDoc}
         */
        @Override
        public void serviceAdded(ServiceEvent event) {
            log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Detected service string '%s'", event.getName()));
            jmmDNS.requestServiceInfo(getServiceType(), event.getName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serviceResolved(ServiceEvent event) {
            ServiceInfo si = event.getInfo();
            log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Resolved service string '%s'", si.getNiceTextString()));
            String extraText = new String(si.getTextBytes()).trim();
            log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Discover service '%s'", si.getNiceTextString()));
            emitOnServiceDiscovered(event.getType(), event.getName(), si.getInetAddresses()[0], si.getPort(), extraText);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serviceRemoved(ServiceEvent event) {
            log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Lost service '%s'", event.getName()));
            log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Lost service '%s'", event.getInfo().getNiceTextString()));
            emitOnServiceLost(event.getType(), event.getName());
        }

    }

}
