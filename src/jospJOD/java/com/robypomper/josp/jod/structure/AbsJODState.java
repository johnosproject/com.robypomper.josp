package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.structure.executor.JODComponentListener;
import com.robypomper.josp.jod.structure.executor.JODComponentPuller;
import com.robypomper.josp.jod.systems.JODExecutorMngr;
import com.robypomper.josp.jod.systems.JODStructure;


/**
 * Default implementation of {@link JODState} interface.
 * <p>
 * In addition to the method defined by the interface, this class manage the
 * instance of the JODWorker delegated to monitoring the status's resource (FW&Apps).
 * The instance Worker can be both type: a {@link com.robypomper.josp.jod.executor.JODListener}
 * or a {@link com.robypomper.josp.jod.executor.JODPuller}, depending on params
 * given to the constructor.
 */
public class AbsJODState extends AbsJODComponent
        implements JODState {

    // Internal vars

    private final JODWorker state;


    // Constructor

    /**
     * Default constructor that initialize the status component with his
     * properties and worker.
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
     */
    public AbsJODState(JODStructure structure, JODExecutorMngr execMngr, String name, String descr, String listener, String puller) throws JODStructure.ComponentInitException {
        super(structure, name, descr);

        try {
            if (listener != null && puller == null) {
                JODComponentListener compWorker = new JODComponentListener(this, name, AbsJODWorker.extractProto(listener), AbsJODWorker.extractConfigsStr(listener));
                state = execMngr.initListener(compWorker);
            } else if (puller != null && listener == null) {
                JODComponentPuller compWorker = new JODComponentPuller(this, name, AbsJODWorker.extractProto(puller), AbsJODWorker.extractConfigsStr(puller));
                state = execMngr.initPuller(compWorker);
            } else
                throw new JODStructure.ComponentInitException(String.format("Can't initialize '%s' component action, one of listener or puller params must set.", getName()));

        } catch (JODWorker.FactoryException e) {
            throw new JODStructure.ComponentInitException(String.format("Error on initialize '%s' component status '%s' worker.", getName(), getWorker()), e);
        }
    }


    // Status's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWorker() {
        return AbsJODWorker.mergeConfigsStr(state.getProto(), state.getName());
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return StructureDefinitions.TYPE_STATE;
    }


    // Status upd flow (struct)

    /**
     * {@inheritDoc}
     */
    @Override
    public void propagateState(JODStateUpdate statusUpd) throws JODStructure.CommunicationSetException {
        getStructure().getCommunication().dispatchUpdate(this, statusUpd);
    }

}
