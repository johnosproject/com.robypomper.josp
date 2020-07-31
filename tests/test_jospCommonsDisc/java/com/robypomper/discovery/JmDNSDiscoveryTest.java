package com.robypomper.discovery;

import com.robypomper.discovery.impl.DiscoveryJmDNS;
import org.junit.jupiter.api.*;

public class JmDNSDiscoveryTest extends DiscoveryTestGeneric {

    // Class constants

    final static int AFTER_JMDNS_START_WAIT_TIME = 1;


    // Test configurations

    @BeforeAll
    static void setUp() {

    }

    @AfterAll
    static void tearDown() {

    }

    @BeforeEach
    public void setUpEach() {
        super.setUpEach();
    }

    @AfterEach
    public void tearDownEach() {
        super.tearDownEach();
    }


    // Discovery system tests

    @Test
    public void JmDNS_StartAndStopTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        if (checkDiscoverySystemSupportOnCurrentOs(DiscoveryJmDNS.IMPL_NAME) && !DiscoveryJmDNS.isStarted())
            startSubDiscovery();
        test_startAndStop(DiscoveryJmDNS.IMPL_NAME);
    }

    @Test
    public void JmDNS_PublishAndDiscoverTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        if (checkDiscoverySystemSupportOnCurrentOs(DiscoveryJmDNS.IMPL_NAME) && !DiscoveryJmDNS.isStarted())
            startSubDiscovery();
        test_PublishAndDiscover(DiscoveryJmDNS.IMPL_NAME);
    }

    @Test
    public void JmDNS_DiscoverAndPublishTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        if (checkDiscoverySystemSupportOnCurrentOs(DiscoveryJmDNS.IMPL_NAME) && !DiscoveryJmDNS.isStarted())
            startSubDiscovery();
        test_DiscoverAndPublish(DiscoveryJmDNS.IMPL_NAME);
    }

    @Test
    public void JmDNS_PublishDiscoverAndPublishTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        if (checkDiscoverySystemSupportOnCurrentOs(DiscoveryJmDNS.IMPL_NAME) && !DiscoveryJmDNS.isStarted())
            startSubDiscovery();
        test_PublishDiscoverAndPublish(DiscoveryJmDNS.IMPL_NAME);
    }

    private void startSubDiscovery() throws InterruptedException {
        DiscoveryJmDNS.startJmDNSSubSystem();
        Thread.sleep(AFTER_JMDNS_START_WAIT_TIME * 1000);
    }

}