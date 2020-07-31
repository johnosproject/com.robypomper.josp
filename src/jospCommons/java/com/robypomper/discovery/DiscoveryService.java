package com.robypomper.discovery;

import java.net.InetAddress;
import java.util.List;


public class DiscoveryService {

    // Public final vars

    public final String name;
    public final String type;
    public final String intf;
    public final String proto;
    public final InetAddress address;
    public final Integer port;
    public final String extra;


    // Constructor

    public DiscoveryService(String name, String type, String intf) {
        this(name, type, intf, null, null, null, null);
    }

    public DiscoveryService(String name, String type, String intf, String proto, InetAddress address, Integer port, String extra) {
        this.name = name;
        this.type = type;
        this.intf = intf;
        this.proto = proto;
        this.address = address;
        this.port = port;
        this.extra = extra;
    }


    // Services comparison methods

    public boolean alreadyIn(List<DiscoveryService> services) {
        return extractFrom(services) != null;
    }

    public DiscoveryService extractFrom(List<DiscoveryService> services) {
        String currType = type.endsWith(".") ? type.substring(0, type.length() - 1) : type;
        currType = currType.endsWith("local") ? currType.substring(0, currType.length() - "local".length()) : currType;
        currType = currType.endsWith(".") ? currType.substring(0, currType.length() - 1) : currType;

        for (DiscoveryService other : services) {
            if (
                    other.name.equalsIgnoreCase(name)
                            && other.type.startsWith(currType)
                            && other.intf.equalsIgnoreCase(intf)
                            && (proto == null || other.proto.equalsIgnoreCase(proto))
                            && (address == null || other.address.getHostAddress().equalsIgnoreCase(address.getHostAddress()))
                            && (port == null || other.port.equals(port))
                            && (extra == null || other.extra.equalsIgnoreCase(extra))
            )
                return other;
        }
        return null;
    }


    // To String override

    @Override
    public String toString() {
        return name + "@" + intf + "//" + address + ":" + port;
    }

}
