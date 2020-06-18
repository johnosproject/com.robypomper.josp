package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.structure.executor.JODComponentListener;
import com.robypomper.josp.jod.structure.executor.JODComponentPuller;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Default implementation of {@link JODState} interface.
 * <p>
 * In addition to the method defined by the interface, this class manage the
 * instance of the JODWorker delegated to monitoring the status's resource (FW&Apps).
 * The instance Worker can be both type: a {@link com.robypomper.josp.jod.executor.JODListener}
 * or a {@link com.robypomper.josp.jod.executor.JODPuller}, depending on params
 * given to the constructor.
 */
public abstract class AbsJODState extends AbsJODComponent
        implements JODState {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JODWorker stateWorker;


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
                log.trace(Mrk_JOD.JOD_STRU_SUB, String.format("Setting state component '%s' listener '%s'", getName(), listener));
                JODComponentListener compWorker = new JODComponentListener(this, name, AbsJODWorker.extractProto(listener), AbsJODWorker.extractConfigsStr(listener));
                stateWorker = execMngr.initListener(compWorker);

            } else if (puller != null && listener == null) {
                log.trace(Mrk_JOD.JOD_STRU_SUB, String.format("Setting state component '%s' puller '%s'", getName(), puller));
                JODComponentPuller compWorker = new JODComponentPuller(this, name, AbsJODWorker.extractProto(puller), AbsJODWorker.extractConfigsStr(puller));
                stateWorker = execMngr.initPuller(compWorker);

            } else {
                log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on setting state component '%s' listener or puller because no listener nor puller given", getName()));
                throw new JODStructure.ComponentInitException(String.format("Can't initialize '%s' component action, one of listener or puller params must set.", getName()));
            }

        } catch (JODWorker.FactoryException e) {
            log.warn(String.format("Error on setting state component '%s' listener or puller because %s", getName(), e.getMessage()), e);
            throw new JODStructure.ComponentInitException(String.format("Error on setting state component '%s' listener/puller", getName()), e);
        }
    }


    // Status's properties

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWorker() {
        return AbsJODWorker.mergeConfigsStr(stateWorker.getProto(), stateWorker.getName());
    }


    // Status upd flow (struct)

    /**
     * {@inheritDoc}
     */
    @Override
    public void propagateState(JODStateUpdate statusUpd) throws JODStructure.CommunicationSetException {
        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Propagating component '%s' state", getName()));
        getStructure().getCommunication().dispatchUpdate(this, statusUpd);
        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Component '%s' propagated state", getName()));
    }

}
