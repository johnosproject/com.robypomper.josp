package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFileWatcher;
import com.robypomper.java.JavaFiles;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


/**
 * JOD Listener for Files.
 * <p>
 * This listener wait for {@link JavaFileWatcher} events on specified file. On each file's update it read file's content
 * and parse it as component's status.
 */
public class ListenerFiles extends AbsJODListener {

    // Class constants

    private static final String PROP_FILE_PATH = "path";


    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    protected final String filePath;
    protected boolean isWatching = false;


    // Constructor

    /**
     * Default ListenerFile constructor.
     *
     * @param name       name of the listener.
     * @param proto      proto of the listener.
     * @param configsStr configs string, can be an empty string.
     */
    public ListenerFiles(String name, String proto, String configsStr, JODComponent component) {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));

        Map<String, String> properties = splitConfigsStrings(configsStr);

        filePath = genericSubstitution(properties.get(PROP_FILE_PATH), getComponent());
        if (filePath == null || filePath.isEmpty()) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' property '%s' not set on component's config string", getName(), PROP_FILE_PATH));
            return;
        }

        try {
            JavaFiles.createParentIfNotExist(filePath);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' file '%s' not exist and can't create watcher file", getName(), new File(filePath).getAbsolutePath()), e);
        }
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return isWatching;
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void listen() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Start '%s' listener", getName()));
        if (isEnabled()) return;

        try {
            JavaFileWatcher.addListener(Paths.get(filePath), fileListener);
            isWatching = true;

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' can't register watcher on '%s' file", getName(), filePath), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void halt() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Stop '%s' listener server", getName()));
        if (!isEnabled()) return;

        try {
            JavaFileWatcher.removeListener(Paths.get(filePath), fileListener);
            isWatching = false;

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' can't register watcher on '%s' file", getName(), filePath), e);
        }
    }

    private void readAndUpdStatus(Path filePath) {
        String status = readStatus(filePath);
        if (status == null)
            return;

        updateStatus(status);
    }

    private String readStatus(Path filePath) {
        String state;
        try {
            state = JavaFiles.readString(filePath);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' can't read status from '%s' file because %s", getName(), filePath, e.getMessage()), e);
            return null;
        }

        if (state.isEmpty())
            return null;

        return state.trim();
    }

    private void updateStatus(String newStatus) {
        if (!convertAndSetStatus(newStatus))
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' can't update his component because not supported (%s)", getName(), getComponent().getClass().getSimpleName()));
    }


    // File Watcher listener

    private final JavaFileWatcher.JavaFileWatcherListener fileListener = new JavaFileWatcher.JavaFileWatcherListener() {
        @Override
        public void onCreate(Path filePath) {
        }

        @Override
        public void onUpdate(Path filePath) {
        }

        @Override
        public void onDelete(Path filePath) {
        }

        @Override
        public void onAnyUpdate(Path filePath) {
            readAndUpdStatus(filePath);
        }
    };

}
