package com.robypomper.josp.jsl;

import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import com.robypomper.josp.jsl.shell.CmdsJCPClient;
import com.robypomper.josp.jsl.shell.CmdsJSLCommunication;
import com.robypomper.josp.jsl.shell.CmdsJSLObjsMngr;
import com.robypomper.josp.jsl.shell.CmdsJSLServiceInfo;
import com.robypomper.josp.jsl.shell.CmdsJSL;
import com.robypomper.josp.jsl.shell.CmdsJSLUserMngr;
import com.robypomper.josp.jsl.info.JSLInfo;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 * Runnable class for JSL Shell.
 * <p>
 * For more details on accepted cmdLine params check the method {@link #createArgsParser()}.
 */
public class JSLShell {

    // Internal vars

    private JSL jsl;
    private Shell shell;
    private boolean fatalThrown = false;


    // CmdLine Args

    private static final String ARG_CONFIGS_FILE = "configs";
    private static final String ARG_CONFIGS_FILE_SHORT = "c";
    private static final String ARG_CONFIGS_FILE_DESCR = "specify JSL config file path (default: jsl.yml)";
    private static final String ARGS_DEF_CONFIGS_FILE = "jsl.yml";
    private static final String ARG_JSL_VERSION = "jsl-version";
    private static final String ARG_JSL_VERSION_SHORT = "v";
    private static final String ARG_JSL_VERSION_DESCR = "specify which JSL version to use (default: jsl.version property from JSL config file)";


    // Exit codes

    private static final int EXIT_OK = 0;
    private static final int EXIT_ERROR_CONFIG = -1;
    private static final int EXIT_ERROR_START = -2;
    private static final int EXIT_ERROR_STOP = -3;
    private static final int EXIT_ERROR_SHELL = -4;


    // Main method

    /**
     * Initialize a Shell object and execute it.
     * <p>
     * Each operation is wrapped in a <code>try...catch</code> block that manage
     * and print application exceptions.
     *
     * @param args cmdLine args, for more info check the method
     *             {@link #createArgsParser()}.
     */
    public static void main(String[] args) {
        JSLShell shell = new JSLShell();

        // Get cmdLine args
        Options options = createArgsParser();
        CommandLine parsedArgs = parseArgs(options, args);
        String configsFile = parsedArgs.getOptionValue(ARG_CONFIGS_FILE, ARGS_DEF_CONFIGS_FILE);
        String jslVer = parsedArgs.getOptionValue(ARG_JSL_VERSION, "");

        // Initialize JSL
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: Init JSL Lib.");
        try {
            JSL.Settings settings = FactoryJSL.loadSettings(configsFile, jslVer);
            shell.createJSL(settings, jslVer);
        } catch (Exception | JSL.FactoryException e) {
            shell.fatal(e, EXIT_ERROR_CONFIG);
            return;
        }

        // Connect JSL
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: Connect JSL Lib.");
        try {
            shell.connectJSL();
        } catch (Exception e) {
            shell.fatal(e, EXIT_ERROR_START);
            return;
        }

        // Run interactive shell
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: Run JSL Shell.");
        try {
            shell.startShell(shell.jsl.getServiceInfo().getSrvName());
        } catch (IOException e) {
            shell.fatal(e, EXIT_ERROR_SHELL);
            return;
        }

        // Disconnect JSL
        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        if (shell.jsl.status() != JSL.Status.DISCONNECTED) {
            System.out.println("INF: Disconnect JSL Lib.");
            try {
                shell.disconnectJSL();
            } catch (Exception e) {
                shell.fatal(e, EXIT_ERROR_STOP);
                return;
            }
        } else {
            System.out.println("INF: JSL Lib already disconnected.");
        }

        System.out.println("######### ######### ######### ######### ######### ######### ######### ######### ");
        System.out.println("INF: EXIT");
        System.exit(EXIT_OK);
    }

    /**
     * Manage fatal errors, also recursively.
     *
     * @param e        exception that thrown error.
     * @param exitCode exit code assigned to the error.
     */
    private void fatal(Throwable e, int exitCode) {
        boolean firstFatal = !fatalThrown;
        fatalThrown = true;

        String msg;
        switch (exitCode) {
            case EXIT_ERROR_CONFIG:
                msg = String.format("Can't load JSL Object because configuration error: '%s'", e.getMessage());
                break;
            case EXIT_ERROR_START:
                msg = String.format("Can't start JSL Object because startup error: '%s'", e.getMessage());
                break;
            case EXIT_ERROR_STOP:
                msg = String.format("Error on stopping JSL Object because: '%s'", e.getMessage());
                break;
            case EXIT_ERROR_SHELL:
                msg = String.format("Error on JSL Shell execution: '%s'", e.getMessage());
                break;
            default:
                msg = "Unknown error on JSL Shell";
                break;
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


    // JSL mngm

    /**
     * Create new instance of the JSL library.
     *
     * @param settings settings used by the JSL object returned.
     * @param jslVer   used to force JSL Object version to create, if empty latest
     *                 version is used.
     */
    public void createJSL(JSL.Settings settings, String jslVer) throws Exception, JSL.FactoryException {
        if (jsl != null)
            throw new Exception("Can't initialize JSL object twice.");

        jsl = FactoryJSL.createJSL(settings, jslVer);
    }

    /**
     * Connect internal JSL library and start all his sub-systems.
     */
    public void connectJSL() throws Exception {
        if (jsl == null)
            throw new Exception("Can't connect JSL object because was not initialized.");

        if (jsl.status() != JSL.Status.DISCONNECTED)
            throw new Exception("Can't stop JSL object because was is already connected or it's starting up.");

        jsl.connect();
    }

    /**
     * Disconnect internal JSL library and stops all his sub-systems.
     */
    public void disconnectJSL() throws Exception {
        if (jsl == null)
            throw new Exception("Can't disconnect JSL object because was not initialized.");

        if (jsl.status() != JSL.Status.CONNECTED)
            throw new Exception("Can't stop JSL object because was is already disconnected or it's shouting down.");

        jsl.disconnect();
    }


    // Shell mngm

    /**
     * Start interactive shell.
     */
    public void startShell(String objName) throws IOException {
        shell = ShellFactory.createConsoleShell(JSLInfo.APP_NAME + "-" + objName, JSLInfo.APP_NAME_FULL,
                this,
                new CmdsJSL(jsl),
                new CmdsJCPClient(jsl.getJCPClient()),
                new CmdsJSLServiceInfo(jsl.getServiceInfo())
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
     *         {@value #ARG_JSL_VERSION} ({@value #ARG_JSL_VERSION_SHORT}):
     *         {@value #ARG_JSL_VERSION_DESCR}
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

        Option jslVer = new Option(ARG_JSL_VERSION_SHORT, ARG_JSL_VERSION, true, ARG_JSL_VERSION_DESCR);
        jslVer.setRequired(false);
        jslVer.setType(String.class);
        options.addOption(jslVer);

        return options;
    }

    /**
     * Parse <code>args</code> with <code>options</code>.
     *
     * @param options object to use for args parsing.
     * @param args    args to be parsed.
     * @return {@link CommandLine} object to use to get cmdLine args.
     */
    private static CommandLine parseArgs(Options options, String[] args) {
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp(JSLInfo.APP_NAME, options);
            System.exit(1);
            return null;
        }
        return cmd;
    }


    // Exceptions

    /**
     * Exception thrown during JSLShell methods execution.
     */
    public static class Exception extends Throwable {
        public Exception(String msg) {
            super(msg);
        }

        public Exception(String msg, Exception e) {
            super(msg, e);
        }
    }

}
