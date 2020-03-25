package com.robypomper.josp.jod;

import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import com.robypomper.josp.jod.info.JODInfo;
import com.robypomper.josp.jod.shell.CmdsJCPClient;
import com.robypomper.josp.jod.shell.CmdsJOD;
import com.robypomper.josp.jod.shell.CmdsJODExecutorMngr;
import com.robypomper.josp.jod.shell.CmdsJODObjectInfo;
import com.robypomper.josp.jod.shell.CmdsJODStructure;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 * Runnable class for JOD Shell.
 *
 * For more details on accepted cmdLine params check the method {@link #createArgsParser()}.
 */
public class JODShell {

    // Internal vars

    private JOD jod;
    private Shell shell;
    private boolean fatalThroned = false;


    // CmdLine Args

    private static final String ARG_CONFIGS_FILE = "configs";
    private static final String ARG_CONFIGS_FILE_SHORT = "c";
    private static final String ARG_CONFIGS_FILE_DESCR = "specify JOD config file path (default: jod.yml)";
    private static final String ARGS_DEF_CONFIGS_FILE = "jod.yml";
    private static final String ARG_JOD_VERSION = "jod-version";
    private static final String ARG_JOD_VERSION_SHORT = "v";
    private static final String ARG_JOD_VERSION_DESCR = "specify which JOD version to use (default: jod.version property from JOD config file)";


    // Exit codes

    private static final int EXIT_OK = 0;
    private static final int EXIT_ERROR_CONFIG = -1;
    private static final int EXIT_ERROR_START = -2;
    private static final int EXIT_ERROR_STOP = -3;
    private static final int EXIT_ERROR_SHELL = -4;


    // Main method

    /**
     * Initialize a Shell object and execute it.
     *
     * Each operation is wrapped in a <code>try...catch</code> block that manage
     * and print application exceptions.
     *
     * @param args cmdLine args, for more info check the method
     *             {@link #createArgsParser()}.
     */
    public static void main(String[] args) {
        JODShell shell = new JODShell();

        // Get cmdLine args
        Options options = createArgsParser();
        CommandLine parsedArgs = parseArgs(options,args);
        String configsFile = parsedArgs.getOptionValue(ARG_CONFIGS_FILE,ARGS_DEF_CONFIGS_FILE);
        String jodVer = parsedArgs.getOptionValue(ARG_JOD_VERSION,"");

        // Initialize JOD
        System.out.println("INF: Load JOD Obj.");
        try {
            JOD.Settings settings = FactoryJOD.loadSettings(configsFile,jodVer);
            shell.createJOD(settings,jodVer);
        } catch (JODShell.Exception | JOD.FactoryException e) {
            shell.fatal(e,EXIT_ERROR_CONFIG);
            return;
        }

        // Start JOD
        System.out.println("INF: Start JOD Obj.");
        try {
            shell.startJOD();
        } catch (JODShell.Exception | JOD.RunException e) {
            shell.fatal(e,EXIT_ERROR_START);
            return;
        }

        // Run interactive shell
        System.out.println("INF: Run JOD Shell.");
        try {
            shell.startShell();
        } catch (IOException e) {
            shell.fatal(e,EXIT_ERROR_SHELL);
            return;
        }

        // Stop JOD
        if (shell.jod.status()!=JOD.Status.STOPPED) {
            System.out.println("INF: Stop JOD Obj.");
            try {
                shell.stopJOD();
            } catch (JODShell.Exception | JOD.RunException e) {
                shell.fatal(e, EXIT_ERROR_STOP);
                return;
            }
        } else {
            System.out.println("INF: JOD Obj already stopped.");
        }

        System.out.println("INF: EXIT");
        System.exit(EXIT_OK);
    }

    /**
     * Manage fatal errors, also recursively.
     *
     * @param e exception that thrown error.
     * @param exitCode exit code assigned to the error.
     */
    private void fatal(Throwable e, int exitCode) {
        boolean firstFatal = !fatalThroned;
        fatalThroned = true;

        String msg;
        switch (exitCode) {
            case EXIT_ERROR_CONFIG: msg = String.format("Can't load JOD Object because configuration error: '%s'", e.getMessage()); break;
            case EXIT_ERROR_START: msg = String.format("Can't start JOD Object because startup error: '%s'", e.getMessage()); break;
            case EXIT_ERROR_STOP: msg = String.format("Error on stopping JOD Object because: '%s'", e.getMessage()); break;
            case EXIT_ERROR_SHELL: msg = String.format("Error on JOD Shell execution: '%s'", e.getMessage()); break;
            default: msg = "Unknown error on JOD Shell"; break;
        }


        System.out.println("FAT: " + msg);
        System.err.println();
        System.err.println("######################################");
        e.printStackTrace();
        System.err.println("######################################");
        System.err.println();

        if (firstFatal)
            System.exit(exitCode);
    }


    // JOD mngm

    /**
     * Create new instance of the JOD object.
     *
     * @param settings settings used by the JOD object returned.
     * @param jodVer used to force JOD Object version to create, if empty latest
     *               version is used.
     */
    public void createJOD(JOD.Settings settings, String jodVer) throws JODShell.Exception, JOD.FactoryException {
        if (jod!=null)
            throw new JODShell.Exception("Can't initialize JOD object twice.");

        jod = FactoryJOD.createJOD(settings,jodVer);
    }

    /**
     * Start internal JOD object and all his sub-systems.
     */
    public void startJOD() throws JODShell.Exception, JOD.RunException {
        if (jod==null)
            throw new JODShell.Exception("Can't start JOD object because was not initialized.");

        jod.start();
    }

    /**
     * Stop internal JOD object and all his sub-systems.
     */
    public void stopJOD() throws JODShell.Exception, JOD.RunException {
        if (jod==null)
            throw new JODShell.Exception("Can't stop JOD object because was not initialized.");

        if (jod.status()==JOD.Status.STOPPED || jod.status()==JOD.Status.SHUTDOWN)
            throw new JODShell.Exception("Can't stop JOD object because was is already stopped or it's shouting down.");

        jod.stop();
    }


    // Shell mngm

    /**
     * Start interactive shell.
     */
    public void startShell() throws IOException {
        shell = ShellFactory.createConsoleShell(JODInfo.APP_NAME, JODInfo.APP_NAME_FULL,
                this,
                new CmdsJOD(jod),
                new CmdsJCPClient(jod.getJCPClient()),
                new CmdsJODObjectInfo(jod.getObjectInfo()),
                new CmdsJODExecutorMngr(jod.getObjectStructure(), jod.getExecutor()),
                new CmdsJODStructure(jod.getObjectStructure())
        );
        shell.commandLoop();
    }


    // CmdLine args parser

    /**
     * Shell application accept following cmdLine args:
     * <ul>
     *     <li>
     *         {@value #ARG_CONFIGS_FILE} ({@value #ARG_CONFIGS_FILE_SHORT}):
     *         {@value #ARG_CONFIGS_FILE_DESCR}
     *     </li>
     *     <li>
     *         {@value #ARG_JOD_VERSION} ({@value #ARG_JOD_VERSION_SHORT}):
     *         {@value #ARG_JOD_VERSION_DESCR}
     *     </li>
     * </ul>
     *
     * @return {@link Options} object to use for args parsing.
     */
    private static Options createArgsParser() {
        Options options = new Options();

        Option configsFile = new Option(ARG_CONFIGS_FILE_SHORT, ARG_CONFIGS_FILE, true, ARG_CONFIGS_FILE_DESCR);
        configsFile.setRequired(false);
        configsFile.setType(String.class);
        options.addOption(configsFile);

        Option jodVer = new Option(ARG_JOD_VERSION_SHORT, ARG_JOD_VERSION, true, ARG_JOD_VERSION_DESCR);
        jodVer.setRequired(false);
        jodVer.setType(String.class);
        options.addOption(jodVer);

        return options;
    }

    /**
     * Parse <code>args</code> with <code>options</code>.
     *
     * @param options object to use for args parsing.
     * @param args args to be parsed.
     * @return {@link CommandLine} object to use to get cmdLine args.
     */
    private static CommandLine parseArgs(Options options, String[] args) {
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp(JODInfo.APP_NAME, options);
            System.exit(1);return null;
        }
        return cmd;
    }


    // Exceptions

    /**
     * Exception thrown during JODShell methods execution.
     */
    public static class Exception extends Throwable {
        public Exception(String msg) {
            super(msg);
        }
        public Exception(String msg, Exception e) {
            super(msg,e);
        }
    }
}