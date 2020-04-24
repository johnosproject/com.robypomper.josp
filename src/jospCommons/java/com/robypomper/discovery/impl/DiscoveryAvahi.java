package com.robypomper.discovery.impl;

import com.robypomper.discovery.AbsDiscover;
import com.robypomper.discovery.AbsPublisher;
import com.robypomper.discovery.LogDiscovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementations of the discovery system based on Avahi.
 */
public class DiscoveryAvahi {

    // Class constants

    public static final String IMPL_NAME = "Avahi";


    // Publisher and discover implementations

    /**
     * Avahi publisher.
     */
    public static class Publisher extends AbsPublisher {


        // Internal vars

        public Process avahiProcess = null;


        // Constructor

        /**
         * Default constructor.
         *
         * @param srvType   the service type to publish.
         * @param srvName   the service name to publish.
         * @param srvPort   the service port to publish.
         * @param extraText string containing extra text related to service to publish.
         */
        public Publisher(String srvType, String srvName, int srvPort, String extraText) {
            super(srvType, srvName, srvPort, extraText);
        }


        // Publication mngm

        /**
         * {@inheritDoc}
         */
        @Override
        public void publish(boolean waitForPublished) throws PublishException {
            LogDiscovery.logPub(String.format("INF: publishing service '%s'", getServiceName()));
            if (!setIsPublishing(true)) {return;}

            // Init discovery system for service publication checks
            startAutoDiscovery(IMPL_NAME);

            // Publish service with avahi cmd
            try {
                String cmd = String.format("avahi-publish -s %s %s. %d %s", getServiceName(), getServiceType(), getServicePort(), getServiceExtraText());
                avahiProcess = Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                throw new PublishException(String.format("ERR: can't publish service '%s' because %s", getServiceName(), e.getMessage()));
            }

            // Wait for service published
            if (waitForPublished)
                waitServicePublication();

            setIsPublishing(false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void hide(boolean waitForDepublished) throws PublishException {
            LogDiscovery.logPub(String.format("INF: hiding service '%s'", getServiceName()));
            if (!setIsDepublishing(true)) {return;}

            // Stop avahi cmd used to publish the service
            avahiProcess.destroy();
            avahiProcess = null;

            // Wait for service de-publication
            if (waitForDepublished)
                waitServiceDepublication();

            // Stop and destroy discovery system for service publication checks
            stopAutoDiscovery();

            setIsDepublishing(false);
        }

    }

    /**
     * Avahi discover.
     */
    public static class Discover extends AbsDiscover {

        // Internal vars

        private Thread avahiThread = null;
        private Process avahiProcess = null;
        private List<String> knownServices = new ArrayList<>();
        private IOException avahiStartException = null;
        private boolean isShutingDown = false;


        // Constructor

        /**
         * Default constructor.
         *
         * @param srvType the service type to looking for.
         */
        public Discover(String srvType) {
            super(srvType);
        }


        // Discovery mngm

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRunning() {
            return avahiThread != null && avahiThread.isAlive();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() throws DiscoveryException {
            if (isRunning()) {
                LogDiscovery.logPub(String.format("WAR: can't start discovery services '%s' because already started", getServiceType()));
                return;
            }
            LogDiscovery.logPub(String.format("INF: start discovery services '%s'", getServiceType()));

            // Start AvahiRunnable thread
            avahiThread = new Thread(new AvahiRunnable());
            avahiThread.start();

            // wait some time, to catch startup error on AvahiRunnable thread
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {}

            // Check if was throw exception on AvahiRunnable thread
            if (avahiStartException != null)
                throw new DiscoveryException(String.format("ERR: can't start discovery services '%s' because %s", getServiceType(), avahiStartException.getMessage()), avahiStartException);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() throws DiscoveryException {
            if (!isRunning()) {
                LogDiscovery.logPub(String.format("WAR: can't stop discovery services '%s' because already stopped", getServiceType()));
                return;
            }
            LogDiscovery.logDisc(String.format("INF: stop discovery services '%s'", getServiceType()));

            isShutingDown = true;

            // Stop avahi process (cmd line executor)
            try {
                avahiProcess.destroy();
                avahiProcess.waitFor();
                avahiProcess = null;
            } catch (InterruptedException ignored) {
                throw new DiscoveryException(String.format("ERR: can't stop discovery system '%s' process service type because %s", getServiceType(), avahiStartException.getMessage()), avahiStartException);
            }

            try {
                avahiThread.join(1000);
            } catch (InterruptedException e) {
                throw new DiscoveryException(String.format("ERR: can't stop discovery system '%s' thread because %s", getServiceType(), avahiStartException.getMessage()), avahiStartException);
            }
            avahiThread = null;
        }


        /**
         * Runnable implementation for read and parsing <code>avahi-browse</code>
         * command output.
         */
        private class AvahiRunnable implements Runnable {
            @Override
            public void run() {
                try {
                    String cmd = String.format("avahi-browse -pr %s.", getServiceType());
                    LogDiscovery.logSubSystem(String.format("DEB: starting process '%s'", cmd));
                    avahiProcess = Runtime.getRuntime().exec(cmd);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(avahiProcess.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {

                        if (line.startsWith("+;")) {
                            LogDiscovery.logSubSystem(String.format("DEB: detected service string '%s'", line));

                        } else if (line.startsWith("=;")) {
                            LogDiscovery.logSubSystem(String.format("DEB: resolved service string '%s'", line));
                            //=;docker0;IPv4;JOD_Gw_Test_\040\123PROTOCOL_NAME\125\041-1625630496;_josp._tcp;local;roby-hp.local;172.17.0.1;42175;"robypomper"
                            //=;lo;IPv4;SrvName;_http._tcp;local;localhost;127.0.0.1;1234;"robypomper"

                            String[] fields = line.split(";");
                            String intf = fields[1];
                            String proto = fields[2];
                            String name = fields[3];
                            String type = fields[4];
                            String extra = fields[9];
                            String nameUnique = name + "@" + intf + "/" + proto;
                            InetAddress inetAddr = InetAddress.getByName(fields[7]);
                            int port = Integer.parseInt(fields[8]);

                            if (!knownServices.contains(nameUnique)) {
                                emitOnServiceDiscovered(type, name, inetAddr, port, extra);
                                knownServices.add(nameUnique);
                            }

                        } else if (line.startsWith("-;")) {
                            LogDiscovery.logSubSystem(String.format("DEB: lost service string '%s'", line));
                            //-;wlo1;IPv6;JOD_Gw_Test_\040\123PROTOCOL_NAME\125\041-1769875233;_josp._tcp;local
                            //-;lo;IPv4;SrvName;_http._tcp;local

                            String[] fields = line.split(";");
                            String intf = fields[1];
                            String proto = fields[2];
                            String name = fields[3];
                            String type = fields[4];
                            String nameUnique = name + "@" + intf + "/" + proto;

                            if (knownServices.contains(nameUnique)) {
                                emitOnServiceLost(type, name);
                                knownServices.remove(nameUnique);
                            }
                        }
                    }

                    LogDiscovery.logSubSystem(String.format("DEB: terminating process '%s'", cmd));

                } catch (IOException e) {

                    if (isShutingDown)
                        return; // stop() method called

                    avahiStartException = e;
                    LogDiscovery.logDisc(String.format("ERR: IOException during discovering service '%s'.", getServiceType()));
                }
            }
        }

    }

}
