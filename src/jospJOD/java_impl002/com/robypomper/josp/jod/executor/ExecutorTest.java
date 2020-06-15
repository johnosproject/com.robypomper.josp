package com.robypomper.josp.jod.executor;


import com.robypomper.josp.jod.structure.AbsJODAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_JOD;

/**
 * JOD Executor test.
 * <p>
 * This executor action print a log message and return <code>true</code>.
 * <p>
 * The action's method chain (exec(), subExec(), don't use any params because
 * was not yet implemented.
 */
public class ExecutorTest extends AbsJODExecutor implements AbsJODAction.JOSPIntTest.Executor {


    // Constructor

    /**
     * Default ExecutorTest constructor.
     *
     * @param name       name of the executor.
     * @param proto      proto of the executor.
     * @param configsStr configs string, can be an empty string.
     */
    public ExecutorTest(String name, String proto, String configsStr) {
        super(name, proto);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorTest for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));
    }

    // Mngm

    /**
     * Exec action method: print a log messages and return <code>true</code>.
     */
    @Override
    protected boolean subExec() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorTest for component '%s' of proto '%s' exec", getName(), getProto()));
        return true;
    }

    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, AbsJODAction.JOSPIntTest cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %d%n", cmdAction.newState);
        System.out.printf("\toldState %d%n", cmdAction.oldState);
        return true;
    }
}
