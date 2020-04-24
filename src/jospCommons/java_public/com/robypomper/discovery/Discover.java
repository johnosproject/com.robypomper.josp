package com.robypomper.discovery;


/**
 * Interface for Discover implementations.
 * <p>
 * Each AbsDiscover instance is looking for a specific type of services. The
 * Discover's type can be queried with the method {@link #getServiceType()}
 */
public interface Discover {

    // Discovery mngm

    /**
     * @return <code>true</code> if the discovery system is running.
     */
    boolean isRunning();

    /**
     * Start the discovery system.
     * <p>
     * After calling this method, the discovery system start to emit
     * {@link DiscoverListener} events on service discovered/lost.
     */
    void start() throws DiscoveryException;

    /**
     * Start the discovery system.
     * <p>
     * After calling this method, the discovery system start to emit
     * {@link DiscoverListener} events on service discovered/lost.
     */
    void stop() throws DiscoveryException;


    // Getters

    /**
     * Return the service type looked from current discover object.
     */
    String getServiceType();


    // Listener mngm

    /**
     * Add given listener to discovery system listener list.
     *
     * @param listener the listener to add.
     */
    void addListener(DiscoverListener listener);

    /**
     * Remove given listener to discovery system listener list.
     *
     * @param listener the listener to remove.
     */
    void removeListener(DiscoverListener listener);


    // Exceptions

    /**
     * Exceptions thrown on discovery errors.
     */
    class DiscoveryException extends Throwable {
        public DiscoveryException(String msg) {
            super(msg);
        }

        public DiscoveryException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
