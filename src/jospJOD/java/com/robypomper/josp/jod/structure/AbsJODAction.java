package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.structure.executor.JODComponentExecutor;
import com.robypomper.josp.jod.systems.JODExecutorMngr;
import com.robypomper.josp.jod.systems.JODStructure;


/**
 * Default implementation of {@link JODAction} interface.
 * <p>
 * In addition to the method defined by the interface, this class manage the
 * instance of the JODExecutor delegated to execute the action on the represented
 * resources (FW&Apps).
 * <p>
 * This class inherits {@link AbsJODState} so it manage also a JODWorker to
 * monitoring the state of the represented resource (FW&App).
 */
public class AbsJODAction extends AbsJODState
        implements JODAction {

    // Internal vars

    private final JODExecutor exec;


    // Constructor

    /**
     * Default constructor that initialize the action component with his
     * properties and executor.
     *
     * <b>NB:</b> only once of <code>listener</code> and <code>puller</code>
     * params can be set, the other one must be null.
     * <p>
     * ToDo set AbsJODState constructor protected
     *
     * @param structure the JOD Structure system.
     * @param execMngr  the JOD Executor Mngr system.
     * @param name      the name of the component.
     * @param descr     the description of the component.
     * @param listener  the listener full configs string.
     * @param puller    the puller full configs string.
     * @param executor  the executor full configs string.
     */
    public AbsJODAction(JODStructure structure, JODExecutorMngr execMngr, String name, String descr, String listener, String puller, String executor) throws JODStructure.ComponentInitException {
        super(structure, execMngr, name, descr, listener, puller);

        try {
            if (executor != null) {
                JODComponentExecutor compWorker = new JODComponentExecutor(this, name, AbsJODWorker.extractProto(executor), AbsJODWorker.extractConfigsStr(executor));
                exec = execMngr.initExecutor(compWorker);
            } else
                throw new JODStructure.ComponentInitException(String.format("Can't initialize '%s' component action, executor param must set.", getName()));

        } catch (JODWorker.FactoryException e) {
            throw new JODStructure.ComponentInitException(String.format("Error on initialize '%s' component action '%s' executor.", getName(), getExecutor()), e);
        }
    }


    // Status upd flow (struct)

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execAction(JODActionParams params) {
        return exec.exec();
    }


    // Implementation methods

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExecutor() {
        return AbsJODWorker.mergeConfigsStr(exec.getProto(), exec.getName());
    }

}
