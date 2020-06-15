package com.robypomper.josp.jod.structure;

import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODExecutor;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.structure.executor.JODComponentExecutor;
import com.robypomper.josp.protocol.JOSPActionCommandParams;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


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

    private static final Logger log = LogManager.getLogger();
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
                log.trace(Mrk_JOD.JOD_STRU_SUB, String.format("Setting action component '%s' executor '%s'", getName(), listener));
                JODComponentExecutor compWorker = new JODComponentExecutor(this, name, AbsJODWorker.extractProto(executor), AbsJODWorker.extractConfigsStr(executor));
                exec = execMngr.initExecutor(compWorker);

            } else {
                log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on setting action component '%s' executor because no executor given", getName()));
                throw new JODStructure.ComponentInitException(String.format("Error on setting action component '%s' executor because not set", getName()));
            }

        } catch (JODWorker.FactoryException e) {
            log.warn(String.format("Error on setting action component '%s' executor because %s", getName(), e.getMessage()), e);
            throw new JODStructure.ComponentInitException(String.format("Error on setting action component '%s' executor", getName()), e);
        }
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return StructureDefinitions.TYPE_ACTION;
    }


    // Status upd flow (struct)

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execAction(JOSPProtocol.ActionCmd commandAction) {
        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Executing component '%s' action", getName()));
        if (commandAction.getCommand() instanceof JOSPIntTest) {
            JOSPIntTest cmdAction = (JOSPIntTest) commandAction.getCommand();
            if (exec instanceof JOSPIntTest.Executor)
                if (!((JOSPIntTest.Executor) exec).exec(commandAction, cmdAction)) {
                    log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on executing component '%s' action", getName()));
                    return false;
                }
        } else {
            log.warn(Mrk_JOD.JOD_STRU_SUB, String.format("Error on executing component '%s' action because command type '%s' not supported", getName(), commandAction.getCommand().getType()));
            return false;
        }
        log.debug(Mrk_JOD.JOD_STRU_SUB, String.format("Component '%s' executed action", getName()));
        return true;
    }


    // Implementation methods

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExecutor() {
        return AbsJODWorker.mergeConfigsStr(exec.getProto(), exec.getName());
    }


    // Status classes

    private static final Map<String, Class<? extends JOSPActionCommandParams>> actionClasses = new HashMap<>();

    public static void loadAllActionClasses() {
        registerActionClass(JOSPIntTest.class.getSimpleName(), JOSPIntTest.class);
    }

    public static void registerActionClass(String typeName, Class<? extends JOSPActionCommandParams> cl) {
        actionClasses.put(typeName, cl);
    }

    public static Map<String, Class<? extends JOSPActionCommandParams>> getActionClasses() {
        return actionClasses;
    }


    // Temporary: this class must be defined and implemented by AbsJODAction sub classes
    public static class JOSPIntTest implements JODActionParams {

        public final int newState;
        public final int oldState;

        public JOSPIntTest(String updData) {
            String[] lines = updData.split("\n");

            newState = Integer.parseInt(lines[0].substring(lines[0].indexOf(":") + 1));
            oldState = Integer.parseInt(lines[1].substring(lines[1].indexOf(":") + 1));
        }

        @Override
        public String getType() {
            return this.getClass().getSimpleName();
        }

        @Override
        public String encode() {
            throw new RuntimeException("JSL JOSPIntTest::encode() method must be NOT called");
        }

        public interface Executor {

            boolean exec(JOSPProtocol.ActionCmd commandAction, JOSPIntTest cmdAction);

        }

    }

}
