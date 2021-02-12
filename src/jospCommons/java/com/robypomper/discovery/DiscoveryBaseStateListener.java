package com.robypomper.discovery;

/**
 * Listener class for Discover System status
 */
public interface DiscoveryBaseStateListener<T> {

    // Events

    void onStart(T discover);

    void onStop(T discover);

    void onFail(T discover, String failMsg, Throwable exception);

}
