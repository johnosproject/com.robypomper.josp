package com.robypomper.discovery;

import com.robypomper.discovery.impl.DiscoveryAvahi;
import org.junit.jupiter.api.*;

public class AvahiDiscoveryTest extends DiscoveryTestGeneric {

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
    public void Avahi_StartAndStopTest() throws Discover.DiscoveryException, Publisher.PublishException {
        test_startAndStop(DiscoveryAvahi.IMPL_NAME);
    }

    @Test
    public void Avahi_PublishAndDiscoverTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        test_PublishAndDiscover(DiscoveryAvahi.IMPL_NAME);
    }

    @Test
    public void Avahi_DiscoverAndPublishTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        test_DiscoverAndPublish(DiscoveryAvahi.IMPL_NAME);
    }

    @Test
    public void Avahi_PublishDiscoverAndPublishTest() throws Discover.DiscoveryException, Publisher.PublishException, InterruptedException {
        test_PublishDiscoverAndPublish(DiscoveryAvahi.IMPL_NAME);
    }

}