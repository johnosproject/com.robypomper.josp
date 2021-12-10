package com.robypomper.josp.logging;

import com.robypomper.java.JavaLogging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Base class to create Java Utils Loggers setupper classes.
 * <p>
 * All applications' setup with this class can specify logging params in their
 * command line args. This class is demanded to parse command line args and
 * extract only logging params and setups the logging environment consequently.
 * <p>
 * Subclassing this class you can easily configure a Java Utils Logger's envs
 * with custom {@link java.util.logging.Logger}, {@link Handler}s and
 * {@link java.util.logging.Formatter}.
 * <p>
 * The basic implementation provide only an instance of the {@link LoggerGroupsIntf}:
 * <pre>
 *     public class JODShellLoggerConfigurator extends JOSPLoggerConfigurator {
 *
 *     private static final String FILE_NAME_DEFAULT = "logs/jospJOD.log";
 *
 *     public JODShellLoggerConfigurator() {
 *         super(new JODLoggers(), FILE_NAME_DEFAULT);
 *     }
 *
 * }
 * </pre>
 * <p>
 * Then you can re-implement all <code>setup[Root]Loggers[Development| Release]()</code>
 * methods and customize the logging environment you desire.
 */
@SuppressWarnings("unused")
public class JOSPLoggerConfigurator {

    // Static fields

    public static final String LOG_FILE_DEFAULT = "logs/josp.log";


    // Internal vars and constants

    private final LoggerGroupsIntf loggerGroups;
    private final String fileName;


    // Constructors

    /**
     * Simple {@link JOSPLoggerConfigurator} constructor, that use default
     * {@link #LOG_FILE_DEFAULT} log file.
     *
     * @param loggerGroups the {@link LoggerGroupsIntf} that define Logger groups.
     */
    public JOSPLoggerConfigurator(LoggerGroupsIntf loggerGroups) {
        this(loggerGroups, LOG_FILE_DEFAULT);
    }

    /**
     * Simple {@link JOSPLoggerConfigurator} constructor, that use specify
     * a custom log file.
     *
     * @param loggerGroups the {@link LoggerGroupsIntf} that define Logger groups.
     * @param fileName     the path of the log file to use.
     */
    public JOSPLoggerConfigurator(LoggerGroupsIntf loggerGroups, String fileName) {
        this.loggerGroups = loggerGroups;
        this.fileName = fileName;
    }


    // Logging environment setup

    /**
     * Main method to configure the Java Utils Logging system.
     * <p>
     * This method parse command line arguments looking for logging params, then
     * initialize the logging environment according.
     *
     * @param args the command line params.
     * @return a filtered command line params array.
     */
    public String[] setupLoggingEnvironment(String[] args) {
        List<String> argsList = Arrays.asList(args);
        boolean isDisabled = isDisabled(argsList);
        boolean isDev = isDev(argsList);
        boolean isLibsDev = isLibsDev(argsList);
        boolean isOnlyConsole = isOnlyConsole(argsList);
        boolean isOnlyFile = isOnlyFile(argsList);
        String logFileName = getFileNameAndCreateParentFolder(argsList, fileName);
        boolean isColored = !isColorsDisabled(argsList);

        if (isDisabled) {
            JavaLogging.setupRootLoggerNOP();
            logInfo("Logs are disabled");

        } else if (isDev) {
            Handler[] handlers = setupRootLoggersDevelopment(isColored);
            setupLoggersDevelopment(handlers, isLibsDev);

        } else {
            Handler[] handlers = setupRootLoggersRelease(isOnlyConsole, isOnlyFile, isColored, logFileName);
            setupLoggersRelease(handlers);
        }

        return cleanLoggingArgs(argsList);
    }

    /**
     * Reset current logging environment and setup a console output for development.
     *
     * @param isColored if true, ASCII colors will be used to highlight log levels.
     * @return the list of created handlers.
     */
    protected Handler[] setupRootLoggersDevelopment(boolean isColored) {
        return JavaLogging.setupRootLoggerDevelopment(isColored);
    }

    /**
     * Reset current logging environment and setup a console output for release.
     * <p>
     * With given flags, this method can setups different kind of logging
     * environments (console+file, only console, only file).
     *
     * @param isOnlyConsole if true, logs will be printed only on console.
     * @param isOnlyFile    if true, logs will be printed only on file.
     * @param isColored     if true, ASCII colors will be used to highlight log levels.
     * @return the list of created handlers.
     */
    protected Handler[] setupRootLoggersRelease(boolean isOnlyConsole, boolean isOnlyFile, boolean isColored, String logFileName) {
        Handler[] handlers;
        try {
            if (isOnlyConsole) handlers = JavaLogging.setupRootLoggerReleaseConsole(isColored);
            else if (isOnlyFile) {
                handlers = JavaLogging.setupRootLoggerReleaseFile(logFileName);
                logInfo(String.format("Logs are printed on '%s' file", Paths.get(logFileName).toAbsolutePath()));
            } else {
                handlers = JavaLogging.setupRootLoggerRelease(isColored, logFileName);
                logInfo(String.format("Logs are printed also on '%s' file", Paths.get(logFileName).toAbsolutePath()));
            }
        } catch (IOException e) {
            logError(String.format("Error setting up the file appender for release logger (%s),%nfallback to console appender.", e));
            handlers = JavaLogging.setupRootLoggerReleaseConsole(isColored);
        }
        return handlers;
    }

    /**
     * Set loggers levels by group for development logging configuration:
     * <ul>
     *     <li>Main: FINER</li>
     *     <li>Internal Libs: FINER or WARN</li>
     *     <li>External Libs: WARN</li>
     * </ul>
     *
     * @param handlers  handlers to be registered to the loggers.
     * @param isLibsDev if <code>true</code> then Internal libraries loggers
     *                  are threaded as Main application loggers.
     */
    protected void setupLoggersDevelopment(Handler[] handlers, boolean isLibsDev) {
        //@formatter:off
        JavaLogging.setLoggerLevel(loggerGroups.getAllLoggers()              ,Level.FINER, handlers);
        JavaLogging.setLoggerLevel(loggerGroups.getAllInternalLibsLoggers()  ,isLibsDev ? Level.FINER : Level.WARNING, handlers);
        JavaLogging.setLoggerLevel(loggerGroups.getAllExternalLibsLoggers()  ,Level.WARNING, handlers);
        //@formatter:on
    }

    /**
     * Set loggers levels by group for release logging configuration:
     * <ul>
     *     <li>Main: INFO</li>
     *     <li>Internal Libs: WARN</li>
     *     <li>External Libs: WARN</li>
     * </ul>
     *
     * @param handlers handlers to be registered to the loggers.
     */
    protected void setupLoggersRelease(Handler[] handlers) {
        //@formatter:off
        JavaLogging.setLoggerLevel(loggerGroups.getAllLoggers()              ,Level.INFO, handlers);
        JavaLogging.setLoggerLevel(loggerGroups.getAllInternalLibsLoggers()  ,Level.WARNING, handlers);
        JavaLogging.setLoggerLevel(loggerGroups.getAllExternalLibsLoggers()  ,Level.WARNING, handlers);
        //@formatter:on
    }


    // Logger groups

    /**
     * Logger's groups interface.
     * <p>
     * Implements this interface to specify to the {@link JOSPLoggerConfigurator}
     * which loggers (their names) thread as MAIN application loggers, which as
     * INTERNAL libraries and which as EXTERNAL libraries.
     */
    public interface LoggerGroupsIntf {

        // Logger list methods

        /**
         * @return the list of Main loggers.
         */
        String[] getAllLoggers();

        /**
         * @return the list of Internal libraries loggers.
         */
        String[] getAllInternalLibsLoggers();

        /**
         * @return the list of External libraries loggers.
         */
        String[] getAllExternalLibsLoggers();

    }

    /**
     * Default implementation of the {@link LoggerGroupsIntf} interface.
     * <p>
     * All methods return an empty list.
     */
    public static class LoggerGroupsDefault implements LoggerGroupsIntf {

        // Logger list methods

        @Override
        public String[] getAllLoggers() {
            return new String[0];
        }

        @Override
        public String[] getAllInternalLibsLoggers() {
            return new String[0];
        }

        @Override
        public String[] getAllExternalLibsLoggers() {
            return new String[0];
        }

    }


    // Internal logging system

    /**
     * Internal logging method.
     * <p>
     * To be sure that the message will be printed to the user, this method use
     * the <code>System.out.println()</code> function.
     *
     * @param message the message to print.
     */
    protected void logInfo(String message) {
        System.out.println("LOGGER: " + message);
    }

    /**
     * Internal logging method for errors.
     * <p>
     * To be sure that the message will be printed to the user, this method use
     * the <code>System.out.println()</code> function.
     *
     * @param message the message to print.
     */
    protected void logError(String message) {
        System.err.println("LOGGER: " + message);
    }


    // Params management methods

    /**
     * @param args the logging params {@link List}.
     * @return <code>true</code> if and only if {@link #PARAM_IS_DISABLED}
     * or {@link #PARAM_IS_DISABLED_SHORT} values are contained in the
     * <code>args</code> list.
     */
    protected static boolean isDisabled(List<String> args) {
        return args.contains(PARAM_IS_DISABLED) || args.contains(PARAM_IS_DISABLED_SHORT);
    }

    /**
     * @param args the logging params {@link List}.
     * @return <code>true</code> if and only if {@link #PARAM_IS_DEV}
     * or {@link #PARAM_IS_DEV_SHORT} values are contained in the
     * <code>args</code> list.
     */
    protected static boolean isDev(List<String> args) {
        return args.contains(PARAM_IS_DEV) || args.contains(PARAM_IS_DEV_SHORT);
    }

    /**
     * @param args the logging params {@link List}.
     * @return <code>true</code> if and only if {@link #PARAM_IS_LIBS_DEV}
     * or {@link #PARAM_IS_LIBS_DEV_SHORT} values are contained in the
     * <code>args</code> list.
     */
    protected static boolean isLibsDev(List<String> args) {
        return args.contains(PARAM_IS_LIBS_DEV) || args.contains(PARAM_IS_LIBS_DEV_SHORT);
    }

    /**
     * @param args the logging params {@link List}.
     * @return <code>true</code> if and only if {@link #PARAM_ONLY_CONSOLE}
     * or {@link #PARAM_ONLY_CONSOLE_SHORT} values are contained in the
     * <code>args</code> list.
     */
    protected static boolean isOnlyConsole(List<String> args) {
        return args.contains(PARAM_ONLY_CONSOLE) || args.contains(PARAM_ONLY_CONSOLE_SHORT);
    }

    /**
     * @param args the logging params {@link List}.
     * @return <code>true</code> if and only if {@link #PARAM_ONLY_FILE}
     * or {@link #PARAM_ONLY_FILE_SHORT} values are contained in the
     * <code>args</code> list.
     */
    protected static boolean isOnlyFile(List<String> args) {
        return args.contains(PARAM_ONLY_FILE) || args.contains(PARAM_ONLY_FILE_SHORT);
    }

    /**
     * Extract the <code>fileName</code> from the <code>args</code> list and
     * create his parent folder.
     * <p>
     * Like the {@link #getFileName(List, String)} method, it looks for
     * {@link #PARAM_FILE_NAME} and {@link #PARAM_FILE_NAME_SHORT} values
     * and return founded <code>fileName</code> value or <code>defaultValue</code>.
     * <p>
     * In both cases this method create also the file's parent directory. That
     * avoid the logger exception, when it try to create the file in a
     * non-existing folder.
     *
     * @param args         the logging params {@link List}.
     * @param defaultValue the value returned if <code>args</code> doesn't
     *                     contains fileName param.
     * @return the <code>fileName</code> params, or <code>defaultValue</code>.
     */
    protected static String getFileNameAndCreateParentFolder(List<String> args, String defaultValue) {
        String fileName = getFileName(args, defaultValue);
        File f = new File(fileName);
        if (f.exists() || f.getParentFile().exists()) return fileName;

        if (f.getParentFile().mkdirs()) return fileName;

        throw new RuntimeException(String.format("Can't create parent folder for log file '%s', current working dir '%s'.", fileName, new File(".").getAbsolutePath()));
    }

    /**
     * Extract the <code>fileName</code> from the <code>args</code> list.
     * <p>
     * Looks for {@link #PARAM_FILE_NAME} and {@link #PARAM_FILE_NAME_SHORT}
     * values in the <code>args</code> list, then return following value.
     * <p>
     * If it exceeds the list's bounds, or there's no {@link #PARAM_FILE_NAME}
     * or {@link #PARAM_FILE_NAME_SHORT} values, the <code>defaultValue</code>
     * is returned.
     *
     * @param args         the logging params {@link List}.
     * @param defaultValue the value returned if <code>args</code> doesn't
     *                     contains fileName param.
     * @return the <code>fileName</code> params, or <code>defaultValue</code>.
     */
    protected static String getFileName(List<String> args, String defaultValue) {
        if (!args.contains(PARAM_FILE_NAME) && !args.contains(PARAM_FILE_NAME_SHORT)) return defaultValue;

        int idx = args.indexOf(PARAM_FILE_NAME) > 0 ? args.indexOf(PARAM_FILE_NAME) : args.indexOf(PARAM_FILE_NAME_SHORT);
        idx++;
        if (idx >= args.size()) {
            // --loggingFileName as latest option, invalid
            return defaultValue;
        }

        return args.get(++idx);
    }

    /**
     * @param args the logging params {@link List}.
     * @return <code>true</code> if and only if {@link #PARAM_COLORS}
     * or {@link #PARAM_COLORS_SHORT} values are contained in the
     * <code>args</code> list.
     */
    protected static boolean isColorsDisabled(List<String> args) {
        return args.contains(PARAM_COLORS) || args.contains(PARAM_COLORS_SHORT);
    }

    /**
     * Scan and remove all loggings params that can be present in the given
     * <code>args</code> list.
     * <p>
     * Because many programs, checks all command line args, this method remove
     * all logging params from given list; and return a cleaned {@link String}
     * array to be used as command line args list.
     *
     * @param args the <code>args</code> list to scan.
     * @return the {@link String} array that contains all <code>args</code>
     * values, excepted those for logging configuration.
     */
    protected static String[] cleanLoggingArgs(List<String> args) {
        List<String> argsCopy = new ArrayList<>(args);
        argsCopy.remove(PARAM_IS_DISABLED);
        argsCopy.remove(PARAM_IS_DISABLED_SHORT);
        argsCopy.remove(PARAM_IS_DEV);
        argsCopy.remove(PARAM_IS_DEV_SHORT);
        argsCopy.remove(PARAM_IS_LIBS_DEV);
        argsCopy.remove(PARAM_IS_LIBS_DEV_SHORT);
        argsCopy.remove(PARAM_ONLY_CONSOLE);
        argsCopy.remove(PARAM_ONLY_CONSOLE_SHORT);
        argsCopy.remove(PARAM_ONLY_FILE);
        argsCopy.remove(PARAM_ONLY_FILE_SHORT);
        String fileNameValue = getFileName(argsCopy, "");
        if (!fileNameValue.isEmpty()) argsCopy.remove(fileNameValue);
        argsCopy.remove(PARAM_FILE_NAME);
        argsCopy.remove(PARAM_FILE_NAME_SHORT);
        argsCopy.remove(PARAM_COLORS);
        argsCopy.remove(PARAM_COLORS_SHORT);
        return argsCopy.toArray(new String[0]);
    }


    // Params static constants

    /**
     * Disable any log message .
     */
    public static final String PARAM_IS_DISABLED = "--loggingDisabled";

    /**
     * Disable any log message, short format.
     */
    public static final String PARAM_IS_DISABLED_SHORT = "-lDis";

    /**
     * Enable logging for development.
     */
    public static final String PARAM_IS_DEV = "--loggingDevelopment";

    /**
     * Enable logging for development, short format.
     */
    public static final String PARAM_IS_DEV_SHORT = "-lDev";

    /**
     * Enable logging for internal libs development (only if --loggingDevelopment enabled).
     */
    public static final String PARAM_IS_LIBS_DEV = "--loggingLibsDevelopment";

    /**
     * Enable logging for internal libs development (only if --loggingDevelopment enabled), short format.
     */
    public static final String PARAM_IS_LIBS_DEV_SHORT = "-lLD";

    /**
     * Enable logging only on console (only if --loggingDevelopment disabled).
     */
    public static final String PARAM_ONLY_CONSOLE = "--loggingOnlyConsole";

    /**
     * Enable logging only on console (only if --loggingDevelopment disabled), short format.
     */
    public static final String PARAM_ONLY_CONSOLE_SHORT = "-lCons";

    /**
     * Enable logging only on file (only if --loggingDevelopment disabled).
     */
    public static final String PARAM_ONLY_FILE = "--loggingOnlyFile";

    /**
     * Enable logging only on file (only if --loggingDevelopment disabled), short format.
     */
    public static final String PARAM_ONLY_FILE_SHORT = "-lFile";

    /**
     * Use a custom file for logging (only if --loggingDevelopment and --loggingOnlyConsole disabled).
     */
    public static final String PARAM_FILE_NAME = "--loggingFileName";

    /**
     * Use a custom file for logging (only if --loggingDevelopment and --loggingOnlyConsole disabled), short format.
     */
    public static final String PARAM_FILE_NAME_SHORT = "-lFileName";

    /**
     * Disable ASCI colors on console printed logs.
     */
    public static final String PARAM_COLORS = "--loggingNoColors";

    /**
     * Disable ASCI colors on console printed logs, short format.
     */
    public static final String PARAM_COLORS_SHORT = "-lNoC";

}
