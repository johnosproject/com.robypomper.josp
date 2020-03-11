package com.robypomper.josp.jod.systems;

import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.jod.executor.JODListener;
import com.robypomper.josp.jod.executor.JODPuller;
import com.robypomper.josp.jod.structure.JODComponent;

import java.util.Collection;

/**
 * Interface for Object's executor system.
 *
 * This system interface the JOD Object with firmware or external software.
 *
 * When the {@link JODStructure} initialize the object's components, it also
 * initialize corresponding puller ({@link #initPuller(JODComponent)}), listener
 * ({@link #initListener(JODComponent)}) and executor ({@link #initExecutor(JODComponent)}).
 *
 * {@link JODComponent} define which puller, listener and executable types must
 * be initialized.
 *
 * {@link JODPuller}, {@link JODListener} and {@link JODExecutor} are java
 * interfaces that can be implemented to support different inter-process
 * communications protocols and standards. For example
 * JODObjectInfo implementations can access to the JCP API and JOD settings file
 * to load and store values of Object's info (for example the hardware id must
 * be generated from the JCP APIs Object and stored on local settings file).
 *
 * Pullers, listeners and executables can be activated/deactivated globally
 * or individually.
 */
public interface JODExecutorMngr {

    // JOD Component's interaction methods (from structure)

    /**
     * Init a state puller for given component.
     *
     * @param component to component containing puller settings.
     * @return the created puller object.
     */
    JODPuller initPuller(JODComponent component);

    /**
     * Init a state listener for given component.
     *
     * @param component to component containing listener settings.
     * @return the created listener object.
     */
    JODListener initListener(JODComponent component);

    /**
     * Init a action executor for given component.
     *
     * @param component to component containing executor settings.
     * @return the created executor object.
     */
    JODExecutor initExecutor(JODComponent component);


    // Mngm methods

    /**
     * @return list of all pullers.
     */
    Collection<JODPuller> getPullers();

    /**
     * @return list of all listeners.
     */
    Collection<JODListener> getListeners();

    /**
     * @return list of all executors.
     */
    Collection<JODExecutor> getExecutions();

    /**
     * Start all pullers and enable all listeners and executors.
     */
    void activateAll();

    /**
     * Stop all pullers and disable all listeners and executors.
     */
    void deactivateAll();

    /**
     * Start given puller.
     *
     * @param puller the puller object to start.
     */
    void startPuller(JODPuller puller);

    /**
     * Stop given puller.
     *
     * @param puller the puller object to stop.
     */
    void stopPuller(JODPuller puller);

    /**
     * Start all pullers.
     */
    void startAllPullers();

    /**
     * Stop all pullers.
     */
    void stopAllPullers();

    /**
     * Connect given listener.
     *
     * @param listener the listener object to connect.
     */
    void connectListener(JODListener listener);

    /**
     * Disconnect given listener.
     *
     * @param listener the listener object to disconnect.
     */
    void disconnectListener(JODListener listener);

    /**
     * Connect all listeners.
     */
    void connectAllListeners();

    /**
     * Disconnect all listeners.
     */
    void disconnectAllListeners();

    /**
     * Enable given executor.
     *
     * @param executor the executor object to enable.
     */
    void enableExecutor(JODExecutor executor);

    /**
     * Disable given executor.
     *
     * @param executor the executor object to disable.
     */
    void disableExecutor(JODExecutor executor);

    /**
     * Enable all executors.
     */
    void enableAllExecutors();

    /**
     * Disable all executors.
     */
    void disableAllExecutors();
}
