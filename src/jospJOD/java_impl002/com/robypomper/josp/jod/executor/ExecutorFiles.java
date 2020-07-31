package com.robypomper.josp.jod.executor;


import com.robypomper.java.JavaFiles;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.josp.jod.structure.pillars.JODBooleanAction;
import com.robypomper.josp.jod.structure.pillars.JODRangeAction;
import com.robypomper.josp.protocol.JOSPProtocol;
import com.robypomper.log.Mrk_Commons;
import com.robypomper.log.Mrk_JOD;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * JOD Puller for Files.
 * <p>
 * Each time this executor must execute an action command, it write given value on specified file.
 */
public class ExecutorFiles extends AbsJODExecutor implements JODBooleanAction.JOSPBoolean.Executor, JODRangeAction.JOSPRange.Executor {

    // Class constants

    private static final String PROP_FILE_PATH = "path";
    private static final String PROP_DEF_FORMAT = "format";


    // Internal vars

    private final String filePath;
    private final String format;


    // Constructor

    /**
     * Default ExecutorUnixShell constructor.
     *
     * @param name       name of the executor.
     * @param proto      proto of the executor.
     * @param configsStr configs string, can be an empty string.
     */
    public ExecutorFiles(String name, String proto, String configsStr, JODComponent component) {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorFiles for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));

        Map<String, String> properties = splitConfigsStrings(configsStr);
        format = properties.get(PROP_DEF_FORMAT) != null ? properties.get(PROP_DEF_FORMAT) : Substitutions.ACTION_VAL;

        filePath = genericSubstitution(properties.get(PROP_FILE_PATH), getComponent());
        if (filePath == null || filePath.isEmpty()) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorFiles for component '%s' property '%s' not set on component's config string", getName(), PROP_FILE_PATH));
            return;
        }

        try {
            JavaFiles.createParentIfNotExist(filePath);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' file '%s' not exist and can't create watcher file", getName(), new File(filePath).getAbsolutePath()), e);
        }
    }


    // Mngm

    /**
     * Exec action method: print a log messages and return <code>true</code>.
     */
    @Deprecated
    @Override
    protected boolean subExec() {
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorFiles for component '%s' of proto '%s' exec", getName(), getProto()));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODBooleanAction.JOSPBoolean cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %b%n", cmdAction.newState);
        System.out.printf("\toldState %b%n", cmdAction.oldState);

        String stateStr = actionSubstitution(format, getComponent(), commandAction, cmdAction);
        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Write ExecutorFiles state '%s' on '%s' file", stateStr, filePath));

        try {
            JavaFiles.writeString(filePath, stateStr);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorFiles error on writing file '%s' for component '%s'", filePath, getName()));
            return false;
        }

        return true;
    }


    @Override
    public boolean exec(JOSPProtocol.ActionCmd commandAction, JODRangeAction.JOSPRange cmdAction) {
        System.out.printf("\n\nReceived action command from %s::%s (srv::usr) for %s::%s (obj::component)%n", commandAction.getServiceId(), commandAction.getUserId(), commandAction.getObjectId(), commandAction.getComponentPath());
        System.out.printf("\tnewState %f%n", cmdAction.newState);
        System.out.printf("\toldState %f%n", cmdAction.oldState);


        String stateStr = actionSubstitution(format, getComponent(), commandAction, cmdAction);
        log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Write ExecutorFiles state '%s' on '%s' file", stateStr, filePath));

        if (stateStr != null)
            try {
                JavaFiles.writeString(filePath, stateStr);

            } catch (IOException e) {
                log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ExecutorFiles error on writing file '%s' for component '%s'", filePath, getName()));
                return false;
            }

        return true;
    }

}