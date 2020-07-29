package com.robypomper.discovery;


/**
 * Interface to listen for events emitted from {@link Discover} classes.
 */
public interface DiscoverListener {

    /**
     * Event triggered on service discovered.
     *
     * @param discSrv the discovered service info.
     */
    void onServiceDiscovered(DiscoveryService discSrv);

    /**
     * Event triggered on service lost.
     *
     * @param lostSrv the lost service info.
     */
    void onServiceLost(DiscoveryService lostSrv);

}
