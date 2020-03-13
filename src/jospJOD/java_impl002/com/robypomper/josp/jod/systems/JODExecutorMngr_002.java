package com.robypomper.josp.jod.systems;

import com.robypomper.josp.jod.JOD_002;
import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.jod.executor.JODListener;
import com.robypomper.josp.jod.executor.JODPuller;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.executor.factories.AbsFactoryJODWorker;
import com.robypomper.josp.jod.executor.factories.FactoryJODExecutor;
import com.robypomper.josp.jod.executor.factories.FactoryJODListener;
import com.robypomper.josp.jod.executor.factories.FactoryJODPuller;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.executor.JODComponentExecutor;
import com.robypomper.josp.jod.structure.executor.JODComponentListener;
import com.robypomper.josp.jod.structure.executor.JODComponentPuller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the JOD executor manager (shortly ExM) implementation.
 * <p>
 * It manage JOD Pullers, Listeners and Executors (this group is also called
 * JOD Workers for executor manager). JOD workers support {@link JODComponent}s
 * from the JOD structure system interfacing with firmware and external systems.
 *
 * <h1>Usage from JOD Structure</h1>
 * JOD Components can use the methods provided by ExM to create new JOD workers:
 * <ul>
 *     <li>{@link #initPuller(JODComponentPuller)}</li>
 *     <li>{@link #initListener(JODComponentListener)}</li>
 *     <li>{@link #initExecutor(JODComponentExecutor)}</li>
 * </ul>
 * This method return a JOD Worker object that can be used, by JOD Component to
 * manage the JOD execution manager's worker.
 * <p>
 * JOD Pullers and Listeners helps {@link com.robypomper.josp.jod.structure.JODState}
 * components to monitoring a resource (hardware or software). When a puller
 * detect / a listener receive a status update, they can call the
 * <code>AbsJODWorker#sendUpdate()</code> method.
 * <p>
 * On the other side, the JOD Executor allow {@link com.robypomper.josp.jod.structure.JODAction}
 * execute actions on managed resource (hardware or software). The JODAction can
 * call the {@link JODExecutor#exec()} method of the returned JODExecutor object
 * from the {@link #initExecutor(JODComponentExecutor)} method.
 * <p>
 * ...
 *
 * <h1>Executor manager's Workers implementations</h1>
 * JOD Workers are instances of {@link JODPuller}, {@link JODListener} and
 * {@link JODExecutor} interfaces and can be implemented extending corresponding
 * classes {@link com.robypomper.josp.jod.executor.AbsJODPuller},
 * {@link com.robypomper.josp.jod.executor.AbsJODListener} (or
 * {@link com.robypomper.josp.jod.executor.AbsJODListenerLoop}) and
 * {@link com.robypomper.josp.jod.executor.AbsJODExecutor}.
 * <p>
 * During ExM startup, it register all implementations contained in
 * {@value JOD_002.Settings#JODPULLER_IMPLS}, {@value JOD_002.Settings#JODLISTENER_IMPLS}
 * and {@value JOD_002.Settings#JODEXECUTOR_IMPLS} JOD properties.<br>
 * This properties contains a list of protocol/implementations pairs
 * (<code>{Proto1}://{Class1};{Proto2}://{Class2};{ProtoN}://{ClassN}</code>).
 * During registration each implementation class is associated to a specific, unique
 * protocol.
 * <p>
 * The implementation's protocols will be use from worker's factories
 * ({@link FactoryJODPuller}, {@link FactoryJODListener} and {@link FactoryJODExecutor})
 * to identify right worker's implementation to instantiate. Factories classes
 * will pass, to the implementation constructors, also the configs string
 * containing all properties set from the {@link JODComponent} configuration and
 * required to initialize the specific implementation.
 */
public class JODExecutorMngr_002 implements JODExecutorMngr {

    // Internal vars

    private final JOD_002.Settings settings;
    private final Map<JODComponent, JODPuller> pullers = new HashMap<>();
    private final Map<JODComponent, JODListener> listeners = new HashMap<>();
    private final Map<JODComponent, JODExecutor> executors = new HashMap<>();


    // Constructor

    /**
     * Create new executor manager.
     * <p>
     * This constructor load Pullers, Listeners and Executor implementations and
     * associate them to a specific, unique protocol name. Protocl/implementation
     * pairs are read from <code>settings</code>.
     *
     * @param settings the JOD settings.
     */
    public JODExecutorMngr_002(JOD_002.Settings settings) {
        System.out.println("DEB: JOD Execution Mngr initialization...");

        this.settings = settings;
        loadPullerImpls();
        loadListenerImpls();
        loadExecutorImpls();

        System.out.println("DEB: JOD Execution Mngr initialized");
    }


    // Protocols/Implementations loaders

    /**
     * Load JOD Pullers implementations from
     * {@link com.robypomper.josp.jod.JOD_002.Settings#getJODExecutorImpls()}
     * JOD's property.
     */
    private void loadPullerImpls() {
        loadWorkerImpls(settings.getJODPullerImpls(), FactoryJODPuller.instance());
    }

    /**
     * Load JOD Listeners implementations from
     * {@link com.robypomper.josp.jod.JOD_002.Settings#getJODExecutorImpls()}
     * JOD's property.
     */
    private void loadListenerImpls() {
        loadWorkerImpls(settings.getJODListenerImpls(), FactoryJODListener.instance());
    }

    /**
     * Load JOD Executors implementations from
     * {@link com.robypomper.josp.jod.JOD_002.Settings#getJODExecutorImpls()}
     * JOD's property.
     */
    private void loadExecutorImpls() {
        loadWorkerImpls(settings.getJODExecutorImpls(), FactoryJODExecutor.instance());
    }

    /**
     * Split and parse <code>implsString</code> String containing the list of
     * protocol/implementation pairs.
     * <p>
     * For each extracted pair, register it to the given <code>factory</code>
     * object.
     * <p>
     * This method print log warning messages on registration errors and when no
     * implementations are available.
     *
     * @param implsString String containing the proto/implementation pairs.
     * @param factory     the factory where register the proto/implementations pairs.
     */
    private void loadWorkerImpls(String implsString, AbsFactoryJODWorker<? extends AbsJODWorker> factory) {
        String workName = factory.getType();

        if (implsString.isEmpty()) {
            System.out.println(String.format("WAR: No JOD %ss implementations loaded", workName));
            return;
        }

        String[] implClasses = implsString.split(";|\\s");

        for (String iClass : implClasses) {
            if (iClass.isEmpty()) continue;

            if (!iClass.contains("://")) {
                System.out.println(String.format("WAR: String '%s' don't contain a valid protocol/implementation pair ({proto}://{impl})", iClass));
                continue;
            }

            String proto = iClass.substring(0, iClass.indexOf("://")).trim();
            String iClassName = iClass.substring(iClass.indexOf("://") + 3).trim();
            try {
                factory.register(proto, iClassName);
            } catch (JODWorker.FactoryException e) {
                System.out.println("WAR: " + e.getMessage());
            }
        }
    }


    // JOD Component's interaction methods (from structure)

    /**
     * {@inheritDoc}
     */
    @Override
    public JODPuller initPuller(JODComponentPuller component) throws JODPuller.FactoryException {
        JODPuller puller = FactoryJODPuller.instance().create(component);
        pullers.put(component.getComponent(), puller);
        return puller;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODListener initListener(JODComponentListener component) throws JODListener.FactoryException {
        JODListener listener = FactoryJODListener.instance().create(component);
        listeners.put(component.getComponent(), listener);
        return listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JODExecutor initExecutor(JODComponentExecutor component) throws JODExecutor.FactoryException {
        JODExecutor executor = FactoryJODExecutor.instance().create(component);
        executors.put(component.getComponent(), executor);
        return executor;
    }


    // Mngm methods

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JODPuller> getPullers() {
        return pullers.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JODListener> getListeners() {
        return listeners.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JODExecutor> getExecutors() {
        return executors.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateAll() {
        startAllPullers();
        connectAllListeners();
        enableAllExecutors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateAll() {
        stopAllPullers();
        disconnectAllListeners();
        disableAllExecutors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startAllPullers() {
        System.out.println("INF: Start all pullers timers");
        for (JODPuller p : getPullers()) {
            p.startTimer();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopAllPullers() {
        System.out.println("INF: Stop all pullers timers");
        for (JODPuller p : getPullers()) {
            p.stopTimer();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectAllListeners() {
        System.out.println("INF: Start all listeners servers");
        for (JODListener l : getListeners()) {
            l.listen();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectAllListeners() {
        System.out.println("INF: Stop all listeners servers");
        for (JODListener l : getListeners()) {
            l.halt();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableAllExecutors() {
        System.out.println("INF: Enable all executors");
        for (JODExecutor e : getExecutors()) {
            e.enable();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableAllExecutors() {
        System.out.println("INF: Disable all executors");
        for (JODExecutor e : getExecutors()) {
            e.disable();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startPuller(JODComponent comp) {
        JODPuller p = pullers.get(comp);
        if (p != null)
            p.startTimer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopPuller(JODComponent comp) {
        JODPuller p = pullers.get(comp);
        if (p != null)
            p.stopTimer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectListener(JODComponent comp) {
        JODListener l = listeners.get(comp);
        if (l != null)
            l.listen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnectListener(JODComponent comp) {
        JODListener l = listeners.get(comp);
        if (l != null)
            l.halt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableExecutor(JODComponent comp) {
        JODExecutor e = executors.get(comp);
        if (e != null)
            e.disable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableExecutor(JODComponent comp) {
        JODExecutor e = executors.get(comp);
        if (e != null)
            e.disable();
    }

}
