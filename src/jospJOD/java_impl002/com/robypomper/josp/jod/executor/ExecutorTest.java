package com.robypomper.josp.jod.executor;


/**
 * JOD Executor test.
 * <p>
 * This executor action print a log message and return <code>true</code>.
 * <p>
 * The action's method chain (exec(), subExec(), don't use any params because
 * was not yet implemented.
 */
public class ExecutorTest extends AbsJODExecutor {


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
        System.out.println(String.format("JOD Executor init %s://%s.", getProto(), configsStr));
    }

    // Mngm

    /**
     * Exec action method: print a log messages and return <code>true</code>.
     */
    @Override
    protected boolean subExec() {
        System.out.println(String.format("JOD Executor exec %s://%s.", getProto(), getName()));
        return true;
    }
}
