package com.robypomper.discovery;

import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Base class for {@link Discover} implementations.
 * <p>
 * This class manage discover service type and the
 * {@link DiscoverListener} for all
 * {@link Discover} subclasses.
 */
public abstract class AbsDiscover implements Discover {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final String srvType;
    private final List<DiscoverListener> listeners = new ArrayList<>();
    private final List<DiscoveryService> discoveredServices = new ArrayList<>();
    private final List<String> interfaces = new ArrayList<>();


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType the service type to looking for.
     */
    protected AbsDiscover(String srvType) {
        this.srvType = srvType;
        log.info(Mrk_Commons.DISC_DISC, String.format("Initialized AbsDiscover instance for '%s' service type", srvType));
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceType() {
        return srvType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DiscoveryService> getServicesDiscovered() {
        return discoveredServices;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getInterfaces(){
        return interfaces;
    }


    // Listener mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(DiscoverListener listener) {
        listeners.add(listener);
        for (DiscoveryService srv : getServicesDiscovered())
            listener.onServiceDiscovered(srv);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(DiscoverListener listener) {
        listeners.remove(listener);
        for (DiscoveryService srv : getServicesDiscovered())
            listener.onServiceDiscovered(srv);
    }

    /**
     * Method for sub-classes to emit service discovered event to all registered
     * listeners.
     *
     * @param discSrv the discovered service info.
     */
    private void emitOnServiceDiscovered(DiscoveryService discSrv) {
        for (DiscoverListener l : listeners)
            l.onServiceDiscovered(discSrv);
    }

    /**
     * Method for sub-classes to emit service lost event to all registered
     * listeners.
     *
     * @param lostSrv the lost service info.
     */
    private void emitOnServiceLost(DiscoveryService lostSrv) {
        for (DiscoverListener l : listeners)
            l.onServiceLost(lostSrv);
    }


    // Service registration methods

    protected void registerService(DiscoveryService discSrv) {
        if (discSrv.alreadyIn(discoveredServices))
            return;

        System.out.println(String.format("Discoverer(%s)\tAdd %s", Integer.toHexString(hashCode()), discSrv));
        discoveredServices.add(discSrv);
        if (!interfaces.contains(discSrv.intf))
            registerInterface(discSrv.intf);
        emitOnServiceDiscovered(discSrv);
    }

    protected void deregisterService(DiscoveryService lostSrv) {
        if (!lostSrv.alreadyIn(discoveredServices))
            return;

        System.out.println(String.format("Discoverer(%s)\tRem %s", Integer.toHexString(hashCode()), lostSrv));
        discoveredServices.remove(lostSrv.extractFrom(discoveredServices));
        emitOnServiceLost(lostSrv);
    }

    protected void deregisterAllServices() {
        List<DiscoveryService> toRemove = new ArrayList<>(discoveredServices);
        for (DiscoveryService srv : toRemove) {
            deregisterService(srv);
        }
    }


    // Interfaces registration methods

    protected void registerInterface(String addIntf) {
        if (interfaces.contains(addIntf))
            return;

        interfaces.add(addIntf);
    }

    protected void deregisterInterface(String remIntf) {
        if (!interfaces.contains(remIntf))
            return;

        interfaces.remove(remIntf);
    }

}
