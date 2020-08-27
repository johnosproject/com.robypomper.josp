/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

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