package com.robypomper.discovery.impl;

import com.robypomper.discovery.AbsDiscover;
import com.robypomper.discovery.AbsPublisher;
import com.robypomper.discovery.DiscoveryService;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * Implementations of the discovery system based on DNSSD.
 */
public class DiscoveryDNSSD {

    // Class constants

    public static final String IMPL_NAME = "DNSSD";


    // Internal vars

    private static final Logger log = LogManager.getLogger();


    // Publisher and discover implementations

    /**
     * DNSSD publisher.
     */
    public static class Publisher extends AbsPublisher {

        // Internal vars

        public Process dnssdProcess = null;


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
            log.debug(Mrk_Commons.DISC_PUB_IMPL, String.format("Initialized DNSSD::Publisher instance for '%s:%s' service on port '%d'", srvType, srvName, srvPort));
        }


        // Publication mngm

        /**
         * {@inheritDoc}
         */
        @Override
        public void publish(boolean waitForPublished) throws PublishException {
            log.info(Mrk_Commons.DISC_PUB_IMPL, String.format("Publish service '%s' on '%d'", getServiceName(), getServicePort()));
            if (!setIsPublishing(true)) return;

            // Init discovery system for service publication checks
            startAutoDiscovery(IMPL_NAME);

            // Publish service with dns-sd cmd
            try {
                String[] cmdArray = new String[]{"dns-sd", "-R", getServiceName(), getServiceType(), ".", Integer.toString(getServicePort()), getServiceExtraText()};
                log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec dns-sd publisher cmd '%s'", String.join(" ", cmdArray)));
                // dns-sd -R "Discovery Service Test" _test._tcp. . 5555
                //Registering Service Discovery Service Test._test._tcp. port 5555
                //DATE: ---Thu 23 Jul 2020---
                //14:22:12.481  ...STARTING...
                //14:22:13.182  Got a reply for service Discovery Service Test._test._tcp.local.: Name now registered and active

                dnssdProcess = Runtime.getRuntime().exec(cmdArray);

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
            log.info(Mrk_Commons.DISC_PUB_IMPL, String.format("Hide service '%s'", getServiceName()));
            if (!setIsDepublishing(true)) return;

            // Stop dnssd cmd used to publish the service
            dnssdProcess.destroy();
            try {
                dnssdProcess.waitFor(1, TimeUnit.SECONDS);
            } catch (InterruptedException ignore) {
            }
            dnssdProcess = null;

            // CAN'T LISTEN FOR SERVICE DEPUBLICATION <- This fail on DNSSDDiscoveryTest.DNSSD_PublishDiscoverAndPublishTest
            // Wait for service de-publication
            //if (waitForDepublished)
            //    waitServiceDepublication();

            // Stop and destroy discovery system for service publication checks
            stopAutoDiscovery();

            setIsDepublishing(false);
        }

    }

    /**
     * DNSSD discover.
     */
    public static class Discover extends AbsDiscover {

        // Internal vars

        private Thread dnssdBrowseThread = null;
        private Process dnssdBrowseProcess = null;
        private IOException dnssdBrowseStartException = null;
        private boolean isShuttingDown = false;


        // Constructor

        /**
         * Default constructor.
         *
         * @param srvType the service type to looking for.
         */
        public Discover(String srvType) {
            super(srvType);
            log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Initialized DNSSD::Discover instance for '%s' service type", srvType));
        }


        // Discovery mngm

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRunning() {
            return dnssdBrowseThread != null && dnssdBrowseThread.isAlive();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void start() throws DiscoveryException {
            if (isRunning()) {
                log.warn(Mrk_Commons.DISC_DISC_IMPL, String.format("Can't start discovery services '%s' because already started", getServiceType()));
                return;
            }
            log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Start discovery services '%s'", getServiceType()));

            // Start DNSSD Browse Runnable thread
            isShuttingDown = false;
            dnssdBrowseThread = new Thread(new DNSSDBrowseRunnable());
            dnssdBrowseThread.setName(DNSSDBrowseRunnable.class.getSimpleName());
            log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Starting thread dnssd browser for '%s' service's type", getServiceType()));
            dnssdBrowseThread.start();

            // wait some time, to catch startup error on DNSSD Browse Runnable thread
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignore) {
            }

            // Check if was throw exception on DNSSD Browse Runnable thread
            if (dnssdBrowseStartException != null)
                throw new DiscoveryException(String.format("ERR: can't start discovery services '%s' because %s", getServiceType(), dnssdBrowseStartException.getMessage()), dnssdBrowseStartException);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void stop() throws DiscoveryException {
            if (!isRunning()) {
                log.warn(Mrk_Commons.DISC_DISC_IMPL, String.format("Can't stop discovery services '%s' because already stopped", getServiceType()));
                return;
            }
            log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Stop discovery services '%s'", getServiceType()));

            isShuttingDown = true;

            // Stop DNSSD Browse Runnable process (cmd line executor)
            if (dnssdBrowseProcess != null)
                try {
                    dnssdBrowseProcess.destroy();
                    dnssdBrowseProcess.waitFor();
                    dnssdBrowseProcess = null;

                } catch (InterruptedException ignored) {
                    throw new DiscoveryException(String.format("ERR: can't stop discovery system '%s' process service type because %s", getServiceType(), dnssdBrowseStartException.getMessage()), dnssdBrowseStartException);
                }

            // Wait DNSSD Browse Runnable thread
            try {
                dnssdBrowseThread.join(1000);
                log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Thread avhai browse '%s' stopped", dnssdBrowseThread.getName()));

            } catch (InterruptedException e) {
                throw new DiscoveryException(String.format("ERR: can't stop discovery system '%s' thread because %s", getServiceType(), dnssdBrowseStartException.getMessage()), dnssdBrowseStartException);
            }
            dnssdBrowseThread = null;

            deregisterAllServices();
        }

        private void resolveViaZ(String srvName, String srvIntf) throws IOException {
            String cmd = String.format("dns-sd -Z %s.", getServiceType());
            log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec dns-sd resolver cmd '%s'", cmd));
            Process dnssdResolveProcess = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(dnssdResolveProcess.getInputStream()));
            log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Thread dnssd resolver '%s' for '%s' service started with command '%s'", Thread.currentThread().getName(), srvName, cmd));

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.startsWith(getServiceType())) {
                    log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Resolved service string '%s'", line));
                    //_josp2._tcp                                     PTR     Star_apple_78-6499._josp2._tcp
                    //Star_apple_78-6499._josp2._tcp                  SRV     0 0 1234 fe80-0-0-0-c41-51f0-dc6-78ee-en0.local. ; Replace with unicast FQDN of target host
                    //Star_apple_78-6499._josp2._tcp                  TXT     "6499"
                    //
                    //_josp2._tcp                                     PTR     My\032Test._josp2._tcp
                    //My\032Test._josp2._tcp                          SRV     0 0 515 MacBook-Pro-di-Roberto.local. ; Replace with unicast FQDN of target host
                    //My\032Test._josp2._tcp                          TXT     "pdl=application/postscript"

                    String[] fields_l1 = line.split("\\s+");
                    line = reader.readLine();
                    String[] fields_l2 = line.split("\\s+");
                    line = reader.readLine();
                    String[] fields_l3 = line.split("\\s+");

                    String intf_Extracted = fields_l1[8].substring(0, fields_l1[8].length() - 1);
                    if (!intf_Extracted.equalsIgnoreCase(srvIntf))
                        continue;

                    String proto = "N/A";
                    //String name = AbsPublisher.getServiceNameDecoded(fields_l1[2].substring(0, fields_l1[2].indexOf(getServiceType()) - 1));
                    String name = fields_l1[2].substring(0, fields_l1[2].indexOf(getServiceType()) - 1).replaceAll("\\\\032", " ");
                    String type = fields_l1[0];
                    String extra = fields_l3[2];
                    InetAddress inetAddr = InetAddress.getByName(fields_l2[5]);
                    int port = Integer.parseInt(fields_l2[4]);

                    DiscoveryService discSrv = new DiscoveryService(name, type, srvIntf, proto, inetAddr, port, extra);
                    log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Discover service '%s'", discSrv));
                    registerService(discSrv);

                    dnssdResolveProcess.destroy();
                    return;
                }
            }
        }

        private void resolveViaL(String srvName, String srvIntf) throws IOException {
            String[] cmdArray = new String[]{"dns-sd", "-L", srvName, getServiceType()};
            log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec dns-sd resolver cmd '%s'", String.join(" ", cmdArray)));
            Process dnssdResolveProcess = Runtime.getRuntime().exec(cmdArray);
            BufferedReader reader = new BufferedReader(new InputStreamReader(dnssdResolveProcess.getInputStream()));
            log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Thread dnssd resolver '%s' for '%s' service started with command '%s'", Thread.currentThread().getName(), srvName, String.join(" ", cmdArray)));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("Lookup ") && line.contains(AbsPublisher.getServiceNameEncoded_DNSSD(srvName))) {
                    log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Resolved service string '%s'", line));
                    //Lookup Pinco Pallo Example (DNSSD - PubDisc&Pub)._discTest._tcp.local
                    //DATE: ---Mon 27 Jul 2020---
                    //15:40:09.200  ...STARTING...
                    //15:40:09.201  Pinco\032Pallo\032Example\032(DNSSD\032-\032PubDisc&Pub)._discTest._tcp.local. can be reached at MacBook-Pro-di-Roberto.local.:5555 (interface 1) Flags: 1
                    //19:09:32.256  Pinco\032Pallo\032Example\032B._discTest._tcp.local. can be reached at MacBook-Pro-di-Roberto.local.:5555 (interface 4)

                    String[] fields_l1 = line.trim().split("\\s+");

                    String intf_Extracted = fields_l1[8].substring(0, fields_l1[8].length() - 1);
                    if (!intf_Extracted.equalsIgnoreCase(srvIntf))
                        continue;
                    String proto = "N/A";
                    String name = AbsPublisher.getServiceNameDecoded_DNSSD(fields_l1[1].substring(0, fields_l1[1].indexOf(getServiceType()) - 1));
                    String type = getServiceType();
                    String extra = "";//fields_l3[2];
                    InetAddress inetAddr = InetAddress.getByName(fields_l1[6].substring(0, fields_l1[6].indexOf(":")));
                    int port = Integer.parseInt(fields_l1[6].substring(fields_l1[6].indexOf(":") + 1));

                    DiscoveryService discSrv = new DiscoveryService(name, type, intf_Extracted, proto, inetAddr, port, extra);
                    log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Discover service '%s'", discSrv));
                    registerService(discSrv);

                    dnssdResolveProcess.destroy();
                }
            }
        }

        /**
         * Runnable implementation for read and parsing <code>dns-sd</code>
         * command output.
         */
        private class DNSSDBrowseRunnable implements Runnable {
            @Override
            public void run() {
                try {
                    String cmd = String.format("dns-sd -B %s", getServiceType());
                    dnssdBrowseProcess = Runtime.getRuntime().exec(cmd);
                    log.trace(Mrk_Commons.DISC_PUB_IMPL, String.format("Exec dns-sd discovery cmd '%s'", cmd));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(dnssdBrowseProcess.getInputStream()));
                    log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Thread dnssd browser '%s' started with command '%s'", Thread.currentThread().getName(), cmd));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] fields = line.trim().split("\\s+");
                        List<String> fieldsStrs = Arrays.asList(fields);
                        boolean added = fields[1].equalsIgnoreCase("Add");
                        boolean removed = fields[1].equalsIgnoreCase("Rmv");

                        if (added) {
                            log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Added service string '%s'", line));
                            ////Browsing for _discTest._tcp
                            ////DATE: ---Thu 23 Jul 2020---
                            ////16:59:23.941  ...STARTING...
                            ////Timestamp     A/R    Flags  if Domain               Service Type         Instance Name
                            //17:01:29.571  Add        2   4 local.               _discTest._tcp.      Pinco Pallo Example
                            //17:01:29.808  Add        2   1 local.               _discTest._tcp.      Pinco Pallo Example

                            String intf = fields[3];
                            StringBuilder name = new StringBuilder();
                            ListIterator<String> iterator = fieldsStrs.listIterator(6);
                            while (iterator.hasNext())
                                name.append(iterator.next()).append(' ');

                            // Exec Resolution
                            try {
                                //resolveViaZ(name.toString().trim(), intf);
                                resolveViaL(name.toString().trim(), intf);

                            } catch (IOException e) {
                                if (isShuttingDown)
                                    return; // stop() method called

                                log.error(Mrk_Commons.DISC_DISC_IMPL, String.format("Thrown IOException during resolve service '%s'.", getServiceType()));
                            }


                        } else if (removed) {
                            log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Removed service string '%s'", line));
                            ////Browsing for _discTest._tcp
                            ////DATE: ---Thu 23 Jul 2020---
                            ////16:59:23.941  ...STARTING...
                            ////Timestamp     A/R    Flags  if Domain               Service Type         Instance Name
                            //17:01:41.087  Rmv        0   4 local.               _discTest._tcp.      Pinco Pallo Example
                            //17:01:45.335  Rmv        0   1 local.               _discTest._tcp.      Pinco Pallo Example

                            String intf = fields[3];
                            String type = fields[5];
                            StringBuilder nameB = new StringBuilder();
                            ListIterator<String> iterator = fieldsStrs.listIterator(6);
                            while (iterator.hasNext())
                                nameB.append(iterator.next()).append(' ');
                            String name = nameB.toString().trim();

                            DiscoveryService lostSrv = new DiscoveryService(name, type, intf, null, null, null, null);
                            log.info(Mrk_Commons.DISC_DISC_IMPL, String.format("Lost service '%s'", lostSrv));
                            deregisterService(lostSrv);
                        }
                    }

                    log.debug(Mrk_Commons.DISC_DISC_IMPL, String.format("Terminating thread dnssd browser '%s'", Thread.currentThread().getName()));

                } catch (IOException e) {

                    if (isShuttingDown)
                        return; // stop() method called

                    dnssdBrowseStartException = e;
                    log.error(Mrk_Commons.DISC_DISC_IMPL, String.format("Thrown IOException during discovering service '%s'.", getServiceType()));
                }

                log.trace(Mrk_Commons.DISC_DISC_IMPL, String.format("Thread dnssd browser '%s' terminated", Thread.currentThread().getName()));
            }
        }

    }

}
