package com.robypomper.java;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils class for listener implementations.
 */
public class JavaListeners {

    // Emitters

    /**
     * Helper class to mapping method to a generic type.
     *
     * @param <T> the generic type to map method with.
     */
    public interface ListenerMapper<T> {
        void map(T t);
    }

    /**
     * Helper method to emit events.
     * <p>
     * This method create a new listeners list to prevent
     * {@link java.util.ConcurrentModificationException} in case of a listener
     * add/remove him self or another listener to the list.
     * <p>
     * Thie method, also catch exception thrown by listener and print an
     * {@link JavaAssertions} error.
     *
     * @param instance  object that emit the event.
     * @param listeners list of listener waiting for emitted event.
     * @param eventName string containing event name (aka the method name to be
     *                  executed)
     * @param mapper    mapper object to allow execute listeners event's method.
     * @param <T>       type of managed listeners.
     */
    public static <T> void emitter(Object instance, List<T> listeners, String eventName, ListenerMapper<T> mapper) {
        List<T> list = new ArrayList<>(listeners);
        for (T l : list)
            try {
                mapper.map(l);

            } catch (Throwable e) {
                String listenerType = l != null ? l.getClass().getSimpleName() : "N/A";
                JavaAssertions.makeAssertion_Failed(e, String.format("Catch exception executing event %s.%s() of '%s' instance for '%s' listener.", listenerType, eventName, instance, l));
            }
    }

}
