package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Default Executor class used by {@link JODExecutor} implementations.
 */
public abstract class AbsJODExecutor extends AbsJODWorker implements JODExecutor {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    private boolean enabled = false;


    // Constructor

    /**
     * {@inheritDoc}
     */
    public AbsJODExecutor(String name, String proto, JODComponent component) {
        super(name, proto, component);
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }


    // JODExecutor's impl

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exec() {
        log.debug(Mrk_JOD.JOD_EXEC_SUB, String.format("Executing '%s' executor", getName()));
        if (!enabled) {
            log.warn(Mrk_JOD.JOD_EXEC_SUB, String.format("Error on exec '%s' executor because disabled", getName()));
            return false;
        }

        if (!subExec()) {
            log.warn(Mrk_JOD.JOD_EXEC_SUB, String.format("Error on executing '%s' executor action because returned false", getName()));
            return false;
        }

        log.debug(Mrk_JOD.JOD_EXEC_SUB, String.format("Executor '%s' executed", getName()));
        return true;
    }

    protected abstract boolean subExec();


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Enable '%s' executor", getName()));
        if (isEnabled()) return;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Enabling executor");
        enabled = true;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Executor enabled");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disable() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Disable '%s' executor", getName()));
        if (!isEnabled()) return;

        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Disabling executor");
        enabled = false;
        log.debug(Mrk_JOD.JOD_EXEC_SUB, "Executor disabled");
    }


    // Substutution methods

    protected static String actionSubstitution(String cmd, JODComponent component, JOSPProtocol.ActionCmd commandAction, JODBooleanAction.JOSPBoolean cmdAction) {
        if (cmd == null) return null;
        if (cmd.isEmpty()) return cmd;
        return genericSubstitution(cmd, component)
                .replaceAll(Substitutions.ACTION_VAL, cmdAction.newState ? "TRUE" : "FALSE")
                .replaceAll(Substitutions.ACTION_VAL_BOOL, cmdAction.newState ? "TRUE" : "FALSE")
                .replaceAll(Substitutions.ACTION_VAL_BIN, cmdAction.newState ? "1" : "0");

    }

    protected static String actionSubstitution(String cmd, JODComponent component, JOSPProtocol.ActionCmd commandAction, JODRangeAction.JOSPRange cmdAction) {
        if (cmd == null) return null;
        if (cmd.isEmpty()) return cmd;

        return genericSubstitution(cmd, component)
                .replaceAll(Substitutions.ACTION_VAL, JavaFormatter.doubleToStr(cmdAction.newState))
                .replaceAll(Substitutions.ACTION_VAL_POINT, JavaFormatter.doubleToStr_Point(cmdAction.newState))
                .replaceAll(Substitutions.ACTION_VAL_COMMA, JavaFormatter.doubleToStr_Comma(cmdAction.newState));
    }

}
