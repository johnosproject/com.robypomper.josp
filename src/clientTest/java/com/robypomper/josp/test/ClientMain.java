package com.robypomper.josp.test;

import com.robypomper.java.JavaSSLIgnoreChecks;
import javafx.util.Pair;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;


public class ClientMain {

    public static void main(String... args) throws IOException, InterruptedException, ExecutionException, KeyManagementException, NoSuchAlgorithmException, JavaSSLIgnoreChecks.JavaSSLIgnoreChecksException {
        //args = generateJospClientCodeConfigs();

        // Get cmdLine args
        Options options = createArgsParser();
        CommandLine parsedArgs = parseArgs(options,args);
        String authFlow = parsedArgs.getOptionValue(ARG_AUTH_FLOW);
        String env = parsedArgs.getOptionValue(ARG_ENV);
        String cliCred = parsedArgs.getOptionValue(ARG_CLI_CRED);
        int resServer = Integer.parseInt(parsedArgs.getOptionValue(ARG_RES_SRV,"-1"));

        if (parsedArgs.hasOption(ARG_SSL_CHECKS))
            JavaSSLIgnoreChecks.disableSSLChecks(JavaSSLIgnoreChecks.LOCALHOST);

        System.out.println("Client will use following:");
        System.out.println(String.format("  - authFlow: %s", authFlow));
        System.out.println(String.format("  - env: %s", env));
        System.out.println(String.format("  - cliCred: %s", cliCred));

        // Initialize client
        AbsClientFlow authFlowInstance;
        ClientSettings envInstance;
        Pair<String,String> cliCredInstance;

        try {
            cliCredInstance = getCliCred(cliCred);
            envInstance = getEnv(env, cliCredInstance,resServer);
            authFlowInstance = getAuth(authFlow, envInstance);
            System.out.println(String.format("  - resServer: %s", envInstance.getResServerPort()));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);return;
        }

        // Run client
        System.out.println();
        System.out.println("------");
        authFlowInstance.run();
        System.out.println("------");
    }


    // Client initialization

    private static Pair<String, String> getCliCred(String cliCred) {
        if (ClientSettingsJcp.findClientCredentials(cliCred)!=null) return ClientSettingsJcp.findClientCredentials(cliCred);

        throw new IllegalArgumentException(String.format("No client's credentials with name '%s' found, check '%s' arg.", cliCred, ARG_CLI_CRED));
    }

    private static ClientSettings getEnv(String env, Pair<String, String> cliCredInstance, int resServerPort) {
        if (env.compareToIgnoreCase(ClientSettingsJcp.AUTH_REALM)==0) return new ClientSettingsJcp(cliCredInstance,resServerPort);

        throw new IllegalArgumentException(String.format("No env found with name '%s', check '%s' arg (valid values are 'jcp').", env, ARG_ENV));
    }

    private static AbsClientFlow getAuth(String authFlow, ClientSettings envInstance) {
        if (authFlow.compareToIgnoreCase(ClientAuthCodeFlow.AUTH_FLOW_NAME)==0) return new ClientAuthCodeFlow(envInstance);
        if (authFlow.compareToIgnoreCase(ClientClientCredentialFlow.AUTH_FLOW_NAME)==0) return new ClientClientCredentialFlow(envInstance);

        throw new IllegalArgumentException(String.format("No auth flow found with name '%s', check '%s' arg (valid values are '%s' or '%s').", authFlow, ARG_AUTH_FLOW, ClientAuthCodeFlow.AUTH_FLOW_NAME, ClientClientCredentialFlow.AUTH_FLOW_NAME));
    }


    // CmdLine args parser

    private static final String ARG_AUTH_FLOW = "authFlow";
    private static final String ARG_AUTH_FLOW_SHORT = "a";
    private static final String ARG_AUTH_FLOW_DESCR = "specify which authentication flow to use";
    private static final String ARG_ENV = "env";
    private static final String ARG_ENV_SHORT = "e";
    private static final String ARG_ENV_DESCR = "specify which environment settings to use (valid values: test or jcp)";
    private static final String ARG_CLI_CRED = "clientCredentials";
    private static final String ARG_CLI_CRED_SHORT = "c";
    private static final String ARG_CLI_CRED_DESCR = "specify which client credential to use";
    private static final String ARG_RES_SRV = "resourceServerPort";
    private static final String ARG_RES_SRV_SHORT = "r";
    private static final String ARG_RES_SRV_DESCR = "specify which resource server port to use";
    private static final String ARG_SSL_CHECKS = "disable-ssl-checks";
    private static final String ARG_SSL_CHECKS_SHORT = "r";
    private static final String ARG_SSL_CHECKS_DESCR = "inhibits SSL checks";

    private static Options createArgsParser() {
        Options options = new Options();

        Option authFlow = new Option(ARG_AUTH_FLOW_SHORT, ARG_AUTH_FLOW, true, ARG_AUTH_FLOW_DESCR);
        authFlow.setRequired(true);
        authFlow.setType(String.class);
        options.addOption(authFlow);

        Option env = new Option(ARG_ENV_SHORT, ARG_ENV, true, ARG_ENV_DESCR);
        env.setRequired(true);
        env.setType(String.class);
        options.addOption(env);

        Option cliCred = new Option(ARG_CLI_CRED_SHORT, ARG_CLI_CRED, true, ARG_CLI_CRED_DESCR);
        cliCred.setRequired(true);
        options.addOption(cliCred);

        Option resSrv = new Option(ARG_RES_SRV_SHORT, ARG_RES_SRV, true, ARG_RES_SRV_DESCR);
        resSrv.setRequired(false);
        options.addOption(resSrv);

        Option sslChecks = new Option(ARG_SSL_CHECKS_SHORT, ARG_SSL_CHECKS, false, ARG_SSL_CHECKS_DESCR);
        sslChecks.setRequired(false);
        options.addOption(sslChecks);

        return options;
    }

    private static CommandLine parseArgs(Options options, String[] args) {
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            new HelpFormatter().printHelp(ClientMain.class.getSimpleName(), options);
            System.exit(1);return null;
        }
        return cmd;
    }

}
