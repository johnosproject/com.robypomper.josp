package com.robypomper.discovery;

import com.robypomper.discovery.impl.DiscoveryAvahi;
import com.robypomper.discovery.impl.DiscoveryDNSSD;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.discovery.impl.DiscoveryJmmDNS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class DiscoveryTestGeneric {

    // Class constants

    //Registering Service `Pinco.Pallo._tcp.Example` port _discTest._tcp. TXT . 5555
    final static String DISC_SRV_NAME = "Pinco Pallo Example (%s - %s)";
    final static int DISC_SRV_PORT = 5555;
    final static String DISC_SRV_TYPE = "_discTest._tcp";


    // Internal vars

    protected static Logger log = LogManager.getLogger();

    Publisher pub;
    Publisher pub2;
    Discover disc;
    TestDiscoverListener listener;
    TestDiscoverListener listenerBis;


    // Test configurations

    @BeforeAll
    static void setUp() {

    }

    @AfterAll
    static void tearDown() {

    }

    @BeforeEach
    public void setUpEach() {
    }

    @AfterEach
    public void tearDownEach() {
        System.out.println("\n\n\n########################                    ciao\n\n\n\n");
        try {
            if (pub != null && pub.isPublishedPartially()) pub.hide(true);
            pub = null;
        } catch (Publisher.PublishException ignore) {
        }
        try {
            if (pub2 != null && pub2.isPublishedPartially()) pub2.hide(true);
            pub2 = null;
        } catch (Publisher.PublishException ignore) {
        }

        try {
            if (disc != null && disc.isRunning()) disc.stop();
            disc = null;
        } catch (Discover.DiscoveryException ignore) {
        }
    }


    // Generic implementation tests

    public void test_startAndStop(String discoveryImplName) throws Discover.DiscoveryException, Publisher.PublishException {
        if (!checkDiscoverySystemSupportOnCurrentOs(discoveryImplName)) {
            System.out.println("\nStart And Stop TEST (" + discoveryImplName + ") - NOT SUPPORTED on current OS (" + currentOs() + ")");
            return;
        }

        System.out.println("\nCREATE PUBLISHER and DISCOVER (" + discoveryImplName + ")");
        pub = DiscoverySystemFactory.createPublisher(discoveryImplName, DISC_SRV_TYPE, String.format(DISC_SRV_NAME, discoveryImplName, "Start&Stop"), DISC_SRV_PORT);
        disc = DiscoverySystemFactory.createDiscover(discoveryImplName, DISC_SRV_TYPE);

        System.out.println("\nSTART AND STOP DISCOVER (" + discoveryImplName + ")");
        start(disc);
        stop(disc);

        System.out.println("\nSTART AND STOP PUBLISHER (" + discoveryImplName + ")");
        pub(pub);
        hide(pub);
    }

    public void test_PublishAndDiscover(String discoveryImplName) throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        if (!checkDiscoverySystemSupportOnCurrentOs(discoveryImplName)) {
            System.out.println("\nPublish And Discovery TEST (" + discoveryImplName + ") - NOT SUPPORTED on current OS (" + currentOs() + ")");
            return;
        }

        System.out.println("\nCREATE PUBLISHER and DISCOVER (" + discoveryImplName + ")");
        String srvName = String.format(DISC_SRV_NAME, discoveryImplName, "Pub&Disc");
        pub = DiscoverySystemFactory.createPublisher(discoveryImplName, DISC_SRV_TYPE, srvName, DISC_SRV_PORT);
        disc = DiscoverySystemFactory.createDiscover(discoveryImplName, DISC_SRV_TYPE);
        listener = new TestDiscoverListener(srvName);
        disc.addListener(listener);

        //System.out.println(String.format("DIS: %s", Integer.toHexString(disc.hashCode())));
        //System.out.println(String.format("PUB: %s", Integer.toHexString(pub.hashCode())));

        System.out.println("\nPUBLISH AND START DISCOVERER");
        pub(pub);
        start(disc);

        System.out.println("\nWAIT FOR SERVICE DISCOVERY");
        System.out.println(new Date());
        waitDiscover(listener);
        System.out.println(new Date());

        //for (DiscoveryService s : new ArrayList<>(disc.getServicesDiscovered()))
        //    System.out.println(Integer.toHexString(disc.hashCode()) + " DIS: " + s);
        //if (pub.getInternalDiscovered()!=null)
        //    for (DiscoveryService s : new ArrayList<>(pub.getInternalDiscovered().getServicesDiscovered()))
        //        System.out.println(Integer.toHexString(pub.getInternalDiscovered().hashCode()) + " PUB: " + s);

        System.out.println("\nDE-PUBLISH");
        hide(pub);

        System.out.println("\nWAIT FOR SERVICE LOST");
        System.out.println(new Date());
        waitLost(listener);
        System.out.println(new Date());

        //for (DiscoveryService s : new ArrayList<>(disc.getServicesDiscovered()))
        //    System.out.println(Integer.toHexString(disc.hashCode()) + " DIS: " + s);
        //if (pub.getInternalDiscovered()!=null)
        //    for (DiscoveryService s : new ArrayList<>(pub.getInternalDiscovered().getServicesDiscovered()))
        //        System.out.println(Integer.toHexString(pub.getInternalDiscovered().hashCode()) + " PUB: " + s);

        System.out.println("\nSTOP DISCOVERER");
        stop(disc);

        //for (DiscoveryService s : new ArrayList<>(disc.getServicesDiscovered()))
        //    System.out.println(Integer.toHexString(disc.hashCode()) + " DIS: " + s);
        //if (pub.getInternalDiscovered()!=null)
        //    for (DiscoveryService s : new ArrayList<>(pub.getInternalDiscovered().getServicesDiscovered()))
        //        System.out.println(Integer.toHexString(pub.getInternalDiscovered().hashCode()) + " PUB: " + s);
    }

    public void test_DiscoverAndPublish(String discoveryImplName) throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        if (!checkDiscoverySystemSupportOnCurrentOs(discoveryImplName)) {
            System.out.println("\nPublish And Discovery TEST (" + discoveryImplName + ") - NOT SUPPORTED on current OS (" + currentOs() + ")");
            return;
        }

        System.out.println("\nCREATE PUBLISHER and DISCOVER (" + discoveryImplName + ")");
        String srvName = String.format(DISC_SRV_NAME, discoveryImplName, "Disc&Pub");
        pub = DiscoverySystemFactory.createPublisher(discoveryImplName, DISC_SRV_TYPE, srvName, DISC_SRV_PORT);
        disc = DiscoverySystemFactory.createDiscover(discoveryImplName, DISC_SRV_TYPE);
        listener = new TestDiscoverListener(srvName);
        disc.addListener(listener);

        System.out.println("\nSTART DISCOVERER AND PUBLISH");
        start(disc);
        pub(pub);

        System.out.println("\nWAIT FOR SERVICE DISCOVERY");
        System.out.println(new Date());
        waitDiscover(listener);
        System.out.println(new Date());

        System.out.println("\nSTOP DISCOVERER");
        stop(disc);

        System.out.println("\nWAIT FOR SERVICE LOST");
        System.out.println(new Date());
        waitLost(listener);
        System.out.println(new Date());

        System.out.println("\nDE-PUBLISH");
        hide(pub);
    }

    public void test_PublishDiscoverAndPublish(String discoveryImplName) throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        if (!checkDiscoverySystemSupportOnCurrentOs(discoveryImplName)) {
            System.out.println("\nPublish, Discovery And Publish TEST (" + discoveryImplName + ") - NOT SUPPORTED on current OS (" + currentOs() + ")");
            return;
        }

        System.out.println("\nCREATE PUBLISHER and DISCOVER (" + discoveryImplName + ")");
        String srvName = String.format(DISC_SRV_NAME, discoveryImplName, "PubDisc&Pub");
        pub = DiscoverySystemFactory.createPublisher(discoveryImplName, DISC_SRV_TYPE, srvName, DISC_SRV_PORT);
        disc = DiscoverySystemFactory.createDiscover(discoveryImplName, DISC_SRV_TYPE);
        listener = new TestDiscoverListener(srvName);
        disc.addListener(listener);

        pub(pub);
        start(disc);

        System.out.println(String.format("DIS: %s", Integer.toHexString(disc.hashCode())));
        System.out.println(String.format("PUB: %s", Integer.toHexString(pub.getInternalDiscovered().hashCode())));

        System.out.println("\nWAIT FOR SERVICE DISCOVERY");
        System.out.println(new Date());
        waitDiscover(listener);
        System.out.println(new Date());

        System.out.println("\nCREATE AND PUBLISH 2nd SERVICE");
        String srvName2 = "222-" + String.format(DISC_SRV_NAME, discoveryImplName, "PubDisc&Pub") + "-222";
        listenerBis = new TestDiscoverListener(srvName2);
        disc.addListener(listenerBis);
        pub2 = DiscoverySystemFactory.createPublisher(discoveryImplName, DISC_SRV_TYPE, srvName2, DISC_SRV_PORT);
        pub(pub2);

        System.out.println(String.format("PU2: %s", Integer.toHexString(pub2.getInternalDiscovered().hashCode())));

        System.out.println("\nWAIT FOR 2nd SERVICE DISCOVERY");
        System.out.println(new Date());
        waitDiscover(listenerBis);
        System.out.println(new Date());

        //Thread.sleep(1000*10);

        System.out.println("\nDE-PUBLISH 2nd SERVICE");
        hide(pub2);

        System.out.println("\nWAIT FOR 2nd SERVICE LOST");
        System.out.println(new Date());
        waitLost(listenerBis);
        System.out.println(new Date());

        System.out.println("\nDE-PUBLISH SERVICE");
        hide(pub);

        //Thread.sleep(1000*10);

        System.out.println("\nWAIT FOR SERVICE LOST");
        System.out.println(new Date());
        waitLost(listener);
        System.out.println(new Date());

        System.out.println("\nSTOP DISCOVERER");
        stop(disc);
    }


    // Operations with assertions

    public static void pub(Publisher pub) throws Publisher.PublishException {
        //Assertions.assertAll(() -> pub.publish(true));
        pub.publish(true);
        Assertions.assertTrue(pub.isPublishedPartially());
    }

    public static void hide(Publisher pub) throws Publisher.PublishException {
        pub.hide(true);
        Assertions.assertFalse(pub.isPublishedFully());
    }

    public static void start(Discover disc) throws Discover.DiscoveryException {
        disc.start();
        Assertions.assertTrue(disc.isRunning());
    }

    public static void stop(Discover disc) throws Discover.DiscoveryException {
        disc.stop();
        Assertions.assertFalse(disc.isRunning());
    }

    public static void waitDiscover(TestDiscoverListener l) throws InterruptedException {
        Assertions.assertTrue(l.onServiceDiscovered.await(10, TimeUnit.SECONDS));
        l.onServiceDiscovered = new CountDownLatch(1);
    }

    public static void waitLost(TestDiscoverListener l) throws InterruptedException {
        Assertions.assertTrue(l.onServiceLost.await(10, TimeUnit.SECONDS));
        l.onServiceLost = new CountDownLatch(1);
    }


    // OS Support checks methods

    protected static boolean checkDiscoverySystemSupportOnCurrentOs(String discoveryImplName) {
        if (DiscoveryJmDNS.IMPL_NAME.equalsIgnoreCase(discoveryImplName)) {
            return true;

        } else if (DiscoveryJmmDNS.IMPL_NAME.equalsIgnoreCase(discoveryImplName)) {
            return true;

        } else if (DiscoveryAvahi.IMPL_NAME.equalsIgnoreCase(discoveryImplName)) {
            return isLinux();

        } else if (DiscoveryDNSSD.IMPL_NAME.equalsIgnoreCase(discoveryImplName)) {
            return isMac();

        }

        return false;
    }

    private static String currentOs() {
        return System.getProperty("os.name");
    }

    private static boolean isLinux() {
        String os = currentOs().toUpperCase(Locale.ENGLISH);
        return (os.contains("NIX") || os.contains("NUX") || os.contains("AIX"));
    }

    private static boolean isMac() {
        String os = currentOs().toUpperCase(Locale.ENGLISH);
        return (os.toUpperCase(Locale.ENGLISH)).contains("MAC");
    }

    private static boolean isWin() {
        String os = currentOs().toUpperCase(Locale.ENGLISH);
        return (os.contains("WIN"));
    }

    private static boolean isSolaris() {
        String os = currentOs().toUpperCase(Locale.ENGLISH);
        return (os.contains("SUNOS"));
    }

}