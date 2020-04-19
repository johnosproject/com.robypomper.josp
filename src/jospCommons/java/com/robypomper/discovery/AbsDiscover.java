package com.robypomper.discovery;

import java.net.InetAddress;
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

    private final String srvType;
    private final List<DiscoverListener> listeners = new ArrayList<>();


    // Constructor

    /**
     * Default constructor.
     *
     * @param srvType the service type to looking for.
     */
    protected AbsDiscover(String srvType) {
        this.srvType = srvType;
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceType() {
        return srvType;
    }


    // Listener mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(DiscoverListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(DiscoverListener listener) {
        listeners.remove(listener);
    }

    /**
     * Method for sub-classes to emit service discovered event to all registered
     * listeners.
     *
     * @param type      the type of the service discovered.
     * @param name      the name of the service discovered.
     * @param address   the address of the service discovered.
     * @param port      the port of the service discovered.
     * @param extraText the extra text related to the service discovered.
     */
    protected void emitOnServiceDiscovered(String type, String name, InetAddress address, int port, String extraText) {
        for (DiscoverListener l : listeners)
            l.onServiceDiscovered(type, name, address, port, extraText);
    }

    /**
     * Method for sub-classes to emit service lost event to all registered
     * listeners.
     *
     * @param type the type of the service discovered.
     * @param name the name of the service discovered.
     */
    protected void emitOnServiceLost(String type, String name) {
        for (DiscoverListener l : listeners)
            l.onServiceLost(type, name);
    }

}
