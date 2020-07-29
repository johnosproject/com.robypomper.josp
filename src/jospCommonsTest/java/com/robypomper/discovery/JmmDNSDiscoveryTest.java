package com.robypomper.discovery;

import com.robypomper.discovery.impl.DiscoveryJmmDNS;
import org.junit.jupiter.api.*;

public class JmmDNSDiscoveryTest extends DiscoveryTestGeneric {

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
    public void JmmDNS_StartAndStopTest() throws Discover.DiscoveryException, Publisher.PublishException {
        test_startAndStop(DiscoveryJmmDNS.IMPL_NAME);
    }

    @Test
    public void JmmDNS_PublishAndDiscover() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        test_PublishAndDiscover(DiscoveryJmmDNS.IMPL_NAME);
    }

    @Test
    public void JmmDNS_DiscoverAndPublishTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        test_DiscoverAndPublish(DiscoveryJmmDNS.IMPL_NAME);
    }

    @Test
    public void JmmDNS_PublishDiscoverAndPublishTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        test_PublishDiscoverAndPublish(DiscoveryJmmDNS.IMPL_NAME);
    }

}