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

import com.robypomper.discovery.impl.DiscoveryAvahi;
import com.robypomper.discovery.impl.DiscoveryDNSSD;
import com.robypomper.discovery.impl.DiscoveryJmDNS;
import com.robypomper.discovery.impl.DiscoveryJmmDNS;


/**
 * Provide methods to create {@link Publisher} and {@link Discover} instances.
 * <p>
 * Available implementations list:
 * <ul>
 *     <li>{@value DiscoveryAvahi#IMPL_NAME}: use the Avahi cmd line program</li>
 *     <li>{@value DiscoveryJmDNS#IMPL_NAME}: use the a JmDNS instances for each network interface</li>
 *     <li>{@value DiscoveryJmmDNS#IMPL_NAME}: use the JmmDNS instance for all network interfaces</li>
 * </ul>
 */
public class DiscoverySystemFactory {

    // Class constants

    public static final String IMPL_NAME_AUTO = "Auto";


    /**
     * Create and return a {@link Publisher} instance of class depending on
     * given <code>implementation</code> param.
     *
     * @param implementation the string identify the implementation required.
     * @param srvType        string containing the service type ("_http._tcp").
     * @param srvName        string containing the service name.
     * @param srvPort        string containing the service port.
     * @return instance of {@link Publisher}.
     */
    public static Publisher createPublisher(String implementation, String srvType, String srvName, int srvPort) throws Publisher.PublishException {
        return createPublisher(implementation, srvType, srvName, srvPort, "");
    }

    /**
     * Create and return a {@link Publisher} instance of class depending on
     * given <code>implementation</code> param.
     *
     * @param implementation the string identify the implementation required.
     * @param srvType        string containing the service type ("_http._tcp").
     * @param srvName        string containing the service name.
     * @param srvPort        string containing the service port.
     * @param extraText      string containing extra text related to service to publish.
     * @return instance of {@link Publisher}.
     */
    public static Publisher createPublisher(String implementation, String srvType, String srvName, int srvPort, String extraText) throws Publisher.PublishException {
        if (IMPL_NAME_AUTO.equalsIgnoreCase(implementation))
            implementation = detectAutoImplementation();

        if (DiscoveryAvahi.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoveryAvahi.Publisher(srvType, srvName, srvPort, extraText);
        if (DiscoveryJmDNS.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoveryJmDNS.Publisher(srvType, srvName, srvPort, extraText);
        if (DiscoveryJmmDNS.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoveryJmmDNS.Publisher(srvType, srvName, srvPort, extraText);
        if (DiscoveryDNSSD.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoveryDNSSD.Publisher(srvType, srvName, srvPort, extraText);

        throw new Publisher.PublishException(String.format("ERR: can't find '%s' AbsPublisher implementation", implementation));
    }

    /**
     * Create and return a {@link Discover} instance of class depending on
     * given <code>implementation</code> param.
     *
     * @param implementation the string identify the implementation required.
     * @param srvType        string containing the service type ("_http._tcp") to listen.
     * @return instance of {@link Discover}.
     */
    public static Discover createDiscover(String implementation, String srvType) throws Discover.DiscoveryException {
        if (IMPL_NAME_AUTO.equalsIgnoreCase(implementation))
            implementation = detectAutoImplementation();

        if (DiscoveryAvahi.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoveryAvahi.Discover(srvType);
        if (DiscoveryJmDNS.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoveryJmDNS.Discover(srvType);
        if (DiscoveryJmmDNS.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoveryJmmDNS.Discover(srvType);
        if (DiscoveryDNSSD.IMPL_NAME.equalsIgnoreCase(implementation))
            return new DiscoveryDNSSD.Discover(srvType);

        throw new Discover.DiscoveryException(String.format("ERR: can't find '%s' AbsDiscovery implementation", implementation));
    }

    private static String detectAutoImplementation() {
        if (DiscoveryAvahi.isAvailable())
            return DiscoveryAvahi.IMPL_NAME;
        if (DiscoveryDNSSD.isAvailable())
            return DiscoveryDNSSD.IMPL_NAME;
        return DiscoveryJmDNS.IMPL_NAME;
    }

}
