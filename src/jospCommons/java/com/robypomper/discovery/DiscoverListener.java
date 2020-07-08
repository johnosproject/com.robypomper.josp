package com.robypomper.discovery;

import java.net.InetAddress;


/**
 * Interface to listen for events emitted from {@link Discover} classes.
 */
public interface DiscoverListener {

    /**
     * Event triggered on service discovered.
     *
     * @param type      the type of the service discovered.
     * @param name      the name of the service discovered.
     * @param address   the address of the service discovered.
     * @param port      the port of the service discovered.
     * @param extraText the extra text related to the service discovered.
     */
    void onServiceDiscovered(String type, String name, InetAddress address, int port, String extraText);

    /**
     * Event triggered on service lost.
     *
     * @param type the type of the service discovered.
     * @param name the name of the service discovered.
     */
    void onServiceLost(String type, String name);

}
