package com.robypomper.discovery;

import com.robypomper.discovery.impl.DiscoveryDNSSD;
import org.junit.jupiter.api.*;

public class DNSSDDiscoveryTest extends DiscoveryTestGeneric {

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
    public void DNSSD_StartAndStopTest() throws Discover.DiscoveryException, Publisher.PublishException {
        test_startAndStop(DiscoveryDNSSD.IMPL_NAME);
    }

    @Test
    public void DNSSD_PublishAndDiscoverTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        test_PublishAndDiscover(DiscoveryDNSSD.IMPL_NAME);
    }

    @Test
    public void DNSSD_DiscoverAndPublishTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        test_DiscoverAndPublish(DiscoveryDNSSD.IMPL_NAME);
    }

    @Test
    public void DNSSD_PublishDiscoverAndPublishTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        test_PublishDiscoverAndPublish(DiscoveryDNSSD.IMPL_NAME);
    }

}