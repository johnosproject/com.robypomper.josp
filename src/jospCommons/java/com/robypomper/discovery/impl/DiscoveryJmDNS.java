package com.robypomper.discovery.impl;

import com.robypomper.discovery.AbsDiscover;
import com.robypomper.discovery.AbsPublisher;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Implementations of the discovery system based on JmDNS (single cast).
 * <p>
 * This implementation try to publish service on all available interfaces, like
 * the {@link DiscoveryJmmDNS} implementation. Differently from {@link DiscoveryJmmDNS},
 * this class monitor all {@link NetworkInterface} detected by Java and add them
 * to the interface list. Then create a JmDNS object for each interface detected.
 * <p>
 * Ths JmDNS sub-system is implemented as static method, so active interfaces,
 * published services and registered listeners are shared among all instance
 * of this class.
 */
public class DiscoveryJmDNS {

    // Class constants

    public static final String IMPL_NAME = "JmDNS";
    public static final long UPD_TIME = 5000;


    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private static Timer intfTimer = null;
    private static final List<NetworkInterface> interfaces = new ArrayList<>();
    private static final Map<String, JmDNS> jmDNSs = new HashMap<>();
    private static final List<Service> pubServices = new ArrayList<>();
    private static final Map<ServiceListener, String> discListeners = new HashMap<>();


    // JmDNS sub-system mngm

    /**
     * Return an unique id related to interface, address and protocol.
     * This id is used in conjunction with {@link #jmDNSs} map to associate
     * uniquely a {@link JmDNS} object to his interface/address/protocol.
     *
     * @param intf    interface to associate at returned unique id.
     * @param address address (and protocol) to associate at returned unique id.
     * @return a string containing the unique id.
     */
    private static String toUniqueId(NetworkInterface intf, InetAddress address) {
        String proto = address instanceof Inet4Address ? "Ipv4" : "IPv6";
        return intf.getDisplayName() + "/" + address + "/" + proto;
    }

    /**
     * @return <code>true</code> if the JmDNS sub-system is running.
     */
    public static boolean isStarted() {
        return intfTimer != null;
    }

    /**
     * Start the JmDNS sub-system.
     * <p>
     * It start a timer to monitor the network interfaces.
     * Each time a new interface is found, a new JmDNS instance is created.
     */
    public static void startJmDNSSubSystem() {
        if (isStarted()) {
            log.warn(Mrk_Commons.DISC_IMPL, "JmDNS sub system already started.");
            return;
        }

//        // Enable JmDNS logger
//        Logger logger = Logger.getLogger(JmDNS.class.toString());
//        ConsoleHandler handler = new ConsoleHandler();
//        logger.addHandler(handler);
//        logger.setLevel(Level.FINER);
//        handler.setLevel(Level.FINER);

        log.info(Mrk_Commons.DISC_IMPL, "Starting JmDNS sub system");

        // Setup interface monitor timer
        intfTimer = new Timer();
        intfTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Thread.currentThread().setName("JmDNS_Thread");
                try {
                    List<NetworkInterface> newInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                    List<NetworkInterface> oldInterfaces = Collections.unmodifiableList(interfaces);

                    // check for added interfaces
                    for (NetworkInterface intf : newInterfaces)
                        if (!interfaces.contains(intf))
                            addInterface(intf);

                    // check for removed interfaces
                    for (NetworkInterface intf : oldInterfaces)
                        if (!newInterfaces.contains(intf))
                            removeInterface(intf);

                } catch (SocketException e) {
                    log.error(Mrk_Commons.DISC_IMPL, String.format("Can't list localhost interfaces because %s.", e.getMessage()));
                }
            }
        }, 0, UPD_TIME);
    }

    /**
     * Stop the JmDNS sub-system.
     * <p>
     * It terminate the timer to monitor the network interfaces and close all
     * JmDNS instances created.
     */
    public static void stopJmDNSSubSystem() {
        if (!isStarted()) {
            log.warn(Mrk_Commons.DISC_IMPL, "JmDNS sub system not running.");
            return;
        }

        log.info(Mrk_Commons.DISC_IMPL, "Stopping JmDNS sub system");

        // Stop interface monitor timer
        intfTimer.cancel();
        intfTimer = null;

        // Clos all JmDNS interfaces
        for (JmDNS jmDNS : jmDNSs.values())
            try {
                jmDNS.close();
            } catch (IOException e) {
                log.error(Mrk_Commons.DISC_IMPL, String.format("Can't close JmDNS instance because %s.", e.getMessage()));
            }

        interfaces.clear();
        jmDNSs.clear();


    }


    // JmDNS sub-system private methods

    /**
     * Create a JmDNS instance for given interface, then register listener and
     * public services (already registered or publicized) the created JmDNS
     * instance.
     *
     * @param intf interface to add to the jmDNS sub-system.
     */
    private static void addInterface(NetworkInterface intf) {
        if (!isStarted()) {
            log.error(Mrk_Commons.DISC_IMPL, "JmDNS sub system must be started before add interfaces");
            return;
        }
        interfaces.add(intf);

        // Get interface address
        for (InterfaceAddress iAddr : intf.getInterfaceAddresses()) {
            InetAddress addr = iAddr.getAddress();
            String uniqueId = toUniqueId(intf, addr);
            if (jmDNSs.containsKey(uniqueId)) {
                log.warn(Mrk_Commons.DISC_IMPL, String.format("Interface '%s' already added as JmDNS interface", addr));
                return;
            }
            try {
                if (!intf.supportsMulticast()) {
                    log.warn(Mrk_Commons.DISC_IMPL, String.format("Interface '%s' don't support multicast", addr));
                    return;
                }
            } catch (SocketException e) {
                log.warn(Mrk_Commons.DISC_IMPL, String.format("Interface '%s' don't support multicast", addr));
                return;
            }

            // Add interface
            JmDNS jmDNS;
            try {
                log.debug(Mrk_Commons.DISC_IMPL, String.format("Adding interface '%s' from address '%s'.", intf.getDisplayName(), addr));
                jmDNS = JmDNS.create(addr);
                jmDNSs.put(uniqueId, jmDNS);

            } catch (IOException e) {
                log.error(Mrk_Commons.DISC_IMPL, String.format("Error adding interface '%s' because %s.", intf.getDisplayName(), e.getMessage()));
                return;
            }

            // Register already registered listeners
            for (Map.Entry<ServiceListener, String> s : discListeners.entrySet()) {
                log.debug(Mrk_Commons.DISC_IMPL, String.format("register listener for service type '%s' on interface '%s'.", s.getValue(), intf.getDisplayName()));
                jmDNS.addServiceListener(s.getValue(), s.getKey());
            }

            // Publish already publicized services
            for (Service s : pubServices)
                try {
                    log.debug(Mrk_Commons.DISC_IMPL, String.format("Publish service '%s' on interface '%s'.", s.getName(), intf.getDisplayName()));
                    jmDNS.registerService(s.generateServiceInfo());

                } catch (IOException e) {
                    log.error(Mrk_Commons.DISC_IMPL, String.format("Error publishing service '%s' on interface '%s' because %s.", s.getName(), intf.getDisplayName(), e.getMessage()));
                    return;
                }
        }
    }

    /**
     * Remove the JmDNS instance corresponding to the given interface, then
     * de-register all listener and de-public all services.
     *
     * @param intf interface to add to the jmDNS sub-system.
     */
    private static void removeInterface(NetworkInterface intf) {
        if (!isStarted()) {
            log.error(Mrk_Commons.DISC_IMPL, "ERR: JmDNS sub system must be started before remove interfaces");
            return;
        }
        interfaces.remove(intf);

        for (InterfaceAddress iAddr : intf.getInterfaceAddresses()) {
            InetAddress addr = iAddr.getAddress();
            String uniqueId = toUniqueId(intf, addr);
            if (!jmDNSs.containsKey(uniqueId)) {
                log.warn(Mrk_Commons.DISC_IMPL, String.format("Interface '%s' not present in JmDNS interfaces list", addr));
                return;
            }

            // Remove interface
            log.debug(Mrk_Commons.DISC_IMPL, String.format("Remove interface '%s'.", intf.getDisplayName()));
            JmDNS jmDNS = jmDNSs.remove(uniqueId);

            // De-publish all services
            for (Service s : pubServices) {
                log.debug(Mrk_Commons.DISC_IMPL, String.format("De-publish service '%s' on interface '%s'.", s.getName(), intf.getDisplayName()));
                jmDNS.unregisterService(s.generateServiceInfo());
            }

            // De-register all listeners
            for (Map.Entry<ServiceListener, String> s : discListeners.entrySet()) {
                log.debug(Mrk_Commons.DISC_IMPL, String.format("De-register listener for service type '%s' on interface '%s'.", s.getValue(), intf.getDisplayName()));
                jmDNS.removeServiceListener(s.getValue(), s.getKey());
            }
        }
    }

    /**
     * Publish given service to all JmDNS instances.
     *
     * @param srv           the service to publish.
     * @param removeOnError if <code>true</code>, when occurs an error on
     *                      publishing given service on some interface, then it
     *                      remove given service also from all other interfaces.
     */
    private static void addPubService(Service srv, boolean removeOnError) throws JmDNSException {
        if (!isStarted())
            throw new JmDNSException("ERR: JmDNS sub system must be started before add services");
        if (pubServices.contains(srv))
            throw new JmDNSException(String.format("WAR: service '%s' already added as JmDNS published service", srv.getName()));

        // Add service from published services list
        log.debug(Mrk_Commons.DISC_IMPL, String.format("Adding published service '%s' of type '%s'.", srv.getName(), srv.getType()));
        pubServices.add(srv);

        // Publish service on all interfaces
        for (JmDNS jmDNS : jmDNSs.values())
            try {
                log.debug(Mrk_Commons.DISC_IMPL, String.format("Adding published service '%s' to '%s' interface.", srv.getName(), jmDNS.getName()));
                jmDNS.registerService(srv.generateServiceInfo());

            } catch (IOException e) {
                if (removeOnError)
                    removePubService(srv);
                throw new JmDNSException(String.format("WAR: error adding service '%s' to interface '%s' because %s.", srv.getName(), jmDNS.getName(), e.getMessage()));
            }
    }

    /**
     * De-publish given service from all JmDNS instances.
     *
     * @param srv the service to de-publish, this reference must be the same
     *            used in the {@link #addPubService(Service, boolean)}.
     */
    private static void removePubService(Service srv) throws JmDNSException {
        if (!isStarted())
            throw new JmDNSException("ERR: JmDNS sub system must be started before add services");
        if (!pubServices.contains(srv))
            throw new JmDNSException(String.format("WAR: service '%s' not present in JmDNS published service", srv.getName()));

        // Remove service from published services list
        log.debug(Mrk_Commons.DISC_IMPL, String.format("Removing published service '%s' of type '%s'.", srv.getName(), srv.getType()));
        pubServices.remove(srv);

        // Hide service from all interfaces
        for (JmDNS jmDNS : jmDNSs.values()) {
            log.debug(Mrk_Commons.DISC_IMPL, String.format("Removing published service '%s' from '%s' interface.", srv.getName(), jmDNS.getName()));
            jmDNS.unregisterService(srv.generateServiceInfo());
        }
    }

    /**
     * Register given listener to all JmDNS instances.
     *
     * @param listener the listener to register.
     */
    private static void addDiscoveryListener(String type, ServiceListener listener) throws JmDNSException {
        if (!isStarted())
            throw new JmDNSException("ERR: JmDNS sub system must be started before add listeners");
        if (discListeners.containsKey(listener))
            throw new JmDNSException(String.format("WAR: listener '%s' already added as JmDNS listener", listener));

        // Add listener to listeners list
        log.debug(Mrk_Commons.DISC_IMPL, String.format("Adding discovery listener for service type '%s'.", type));
        discListeners.put(listener, type);

        // Register listener to all interfaces
        for (JmDNS jmDNS : jmDNSs.values()) {
            log.debug(Mrk_Commons.DISC_IMPL, String.format("Registering discovery listener for service type '%s'@'%s'.", type, jmDNS.getName()));
            jmDNS.addServiceListener(type, listener);
        }
    }

    /**
     * De-register given listener from all JmDNS instances.
     *
     * @param listener the listener to de-register.
     */
    private static void removeDiscoveryListener(String type, ServiceListener listener) throws JmDNSException {
        if (!isStarted())
            throw new JmDNSException("ERR: JmDNS sub system must be started before add listeners");
        if (!discListeners.containsKey(listener))
            throw new JmDNSException(String.format("WAR: listener '%s' not present in JmDNS listeners", listener));

        // Remove listener to listeners list
        log.debug(Mrk_Commons.DISC_IMPL, String.format("Removed discovery listener for service type '%s'.", type));
        discListeners.remove(listener);

        // Register listener to all interfaces
        for (JmDNS jmDNS : jmDNSs.values()) {
            log.debug(Mrk_Commons.DISC_IMPL, String.format("De-registering discovery listener for service type '%s'@'%s'.", type, jmDNS.getName()));
            jmDNS.removeServiceListener(type, listener);
        }
    }

    /**
     * Transitional class: from group of fields to {@link ServiceInfo} instance.
     * <p>
     * JmDNS require dedicated instance of ServiceInfo for each pair service-JmDNS
     * instance. This class allow to create {@link ServiceInfo} objects starting
     * from the same values.
     */
    private static class Service {

        // Internal vars

        String type;
        String name;
        int port;
        String extraText;

        // Constructor

        /**
         * Default constructor.
         *
         * @param type      the service type to publish.
         * @param name      the service name to publish.
         * @param port      the service port to publish.
         * @param extraText the extra text related to the service to publish.
         */
        public Service(String type, String name, int port, String extraText) {
            this.type = type;
            this.name = name;
            this.port = port;
            this.extraText = extraText;
        }

        public ServiceInfo generateServiceInfo() {
            return ServiceInfo.create(type + ".local.", name, port, extraText);
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public int getPort() {
            return port;
        }

    }


    // Publisher and discover implementations

    /**
     * JmDNS publisher.
     */
    public static class Publisher extends AbsPublisher {

        // Internal vars

        private final Service internalService;


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
            internalService = new Service(getServiceType(), getServiceName(), getServicePort(), getServiceExtraText());
            log.debug(Mrk_Commons.DISC_PUB_IMPL, String.format("Initialized JmDNS::Publisher instance for '%s:%s' service on port '%d'", srvType, srvName, srvPort));
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

            // Publish service on all available JmDNS instance
            try {
                addPubService(internalService, true);
            } catch (JmDNSException e) {
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

            // Remove service from all available JmDNS instance used to publish the service
            try {
                removePubService(internalService);
            } catch (JmDNSException e) {
                throw new PublishException(String.format("ERR: can't hide service '%s' because %s", getServiceName(), e.getMessage()));
            }

            // Wait for service de-publication
            if (waitForDepublished)
                waitServiceDepublication();

            // Stop and destroy discovery system for service publication checks
            stopAutoDiscovery();

            setIsDepublishing(false);
        }

    }

    /**
     * JmDNS discover.
     */
    public static class Discover extends AbsDiscover implements ServiceListener {

        // Internal vars

        private boolean isRunning = false;


        // Constructor

        /**
         * Default constructor.
         *
         * @param srvType the service type to looking for.
         */
        public Discover(String srvType) {
            super(srvType);
            log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Initialized JmDNS::Discover instance for '%s' service type", srvType));
        }


        // Discovery mngm

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRunning() {
            return isRunning;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() throws DiscoveryException {
            if (isRunning()) {
                log.warn(Mrk_Commons.DISC_DISC_IMPL, String.format("Can't start discovery services '%s' because already started", getServiceType()));
                return;
            }
            log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Start discovery services '%s'", getServiceType()));

            // Add listener to all JmDNS instances
            try {
                addDiscoveryListener(getServiceType() + ".local.", this);

            } catch (JmDNSException e) {
                throw new DiscoveryException(String.format("ERR: can't add '%s' service type to discovery system because %s", getServiceType(), e.getMessage()), e);
            }

            isRunning = true;
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

            // Add listener to all JmDNS instances
            try {
                removeDiscoveryListener(getServiceType() + ".local.", this);

            } catch (JmDNSException e) {
                throw new DiscoveryException(String.format("ERR: can't remove '%s' service type to discovery system because %s", getServiceType(), e.getMessage()), e);
            }

            isRunning = false;
        }


        // JmDNS listener methods

        /**
         * {@inheritDoc}
         */
        @Override
        public void serviceAdded(ServiceEvent event) {
            log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Detected service string '%s'", event.getName()));
            event.getDNS().requestServiceInfo(getServiceType(), event.getName());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serviceResolved(ServiceEvent event) {
            ServiceInfo si = event.getInfo();
            log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Resolved service string '%s'", event.getName()));
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


    // Exceptions

    private static class JmDNSException extends Throwable {
        public JmDNSException(String msg) {
            super(msg);
        }

        public JmDNSException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
