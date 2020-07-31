package com.robypomper.discovery.impl;

import com.robypomper.discovery.AbsPublisher;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceInfo;
import javax.jmdns.impl.JmmDNSImpl;
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

        private JmmDNS jmmDNS = null;


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
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public boolean isPublishedFully() {
//            // published on ALL jmdns
//            return jmmDNS!=null && discover!=null && jmmDNS.getDNS().length == discover.getServicesDiscovered().size();
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public boolean isPublishedPartially() {
//            // published at last ONE jmdns (found one self-publication with internal discovery)
//            return discover!=null && discover.getServicesDiscovered().size() > 0;
//        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void publish(boolean waitForPublished) throws PublishException {
            log.info(Mrk_Commons.DISC_PUB_IMPL, String.format("Publish service '%s' on '%d'", getServiceName(), getServicePort()));
            if (!setIsPublishing(true)) {
                return;
            }

            // Init discovery system for service publication checks
            startAutoDiscovery(IMPL_NAME);

            // Publish service with JmmDNS instance
            try {
                jmmDNS = new JmmDNSImpl();
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
            if (!setIsDepublishing(true)) {
                return;
            }

            // Halt JmmDNS instance used to publish the service
            jmmDNS.unregisterService(toServiceInfo());
            jmmDNS = null;

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
    public static class Discover extends AbsJmDNSDiscover {

        // Internal vars

        private JmmDNS jmmDNS = null;


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

            // Create and start JmmDNS instance
            if (jmmDNS == null) {
                log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Create JmmDNS instance for '%s' type, this take a while...    (20\"ca.)", getServiceType()));
                jmmDNS = new JmmDNSImpl();
                log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Discovery services '%s' started with JmmDNS", getServiceType()));
            }

            // Register service type to JmmDNS instance
            jmmDNS.addServiceListener(getServiceType() + ".local.", this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() {
            if (!isRunning()) {
                log.warn(Mrk_Commons.DISC_DISC_IMPL, String.format("Can't stop discovery services '%s' because already stopped", getServiceType()));
                return;
            }
            log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Stop discovery services '%s'", getServiceType()));

            // Deregister service type to JmmDNS instance
            jmmDNS.removeServiceListener(getServiceType(), this);

            // Set JmmDNS instance to null
            jmmDNS = null;

            deregisterAllServices();
        }

    }

}
