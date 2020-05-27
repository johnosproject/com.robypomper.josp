package com.robypomper.log;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Mrk_Commons extends Markers {

    //@formatter:off

    // Commons-Components

    protected static final Marker COMM          = MarkerManager.getMarker("COMM").setParents(EXT_COMP);                 // all communication component logs
    // Comm - Server
    public static final Marker COMM_SRV         = MarkerManager.getMarker("COMM_SRV").setParents(COMM);                 // all server component logs
    public static final Marker COMM_SRV_IMPL    = MarkerManager.getMarker("COMM_SRV_IMPL").setParents(COMM);            // all server implementations logs
    // Comm - Client
    public static final Marker COMM_CL          = MarkerManager.getMarker("COMM_CL").setParents(COMM);                  // all client component logs
    public static final Marker COMM_CL_IMPL     = MarkerManager.getMarker("COMM_CL_IMPL").setParents(COMM);             // all client implementations logs
    // SSL Utils, Server and Client
    public static final Marker COMM_SSL_UTILS   = MarkerManager.getMarker("COMM_SSL_UTILS").setParents(COMM);           // all ssl utils logs
    public static final Marker COMM_SSL_CERTSRV = MarkerManager.getMarker("COMM_SSL_CERTSRV").setParents(COMM_SRV_IMPL);// all ssl certificate server logs
    public static final Marker COMM_SSL_SRV     = MarkerManager.getMarker("COMM_SSL_SRV").setParents(COMM);             // all ssl server component logs
    public static final Marker COMM_SSL_CERTCL  = MarkerManager.getMarker("COMM_SSL_CERTCL").setParents(COMM_CL_IMPL);  // all ssl certificate client logs
    public static final Marker COMM_SSL_CL      = MarkerManager.getMarker("COMM_SSL_CL").setParents(COMM);              // all ssl client component logs
    // JCPClient
    public static final Marker COMM_JCPCL       = MarkerManager.getMarker("COMM_JCPCL").setParents(COMM);               // all JOSP Client Platform logs


    // Commons-Components: Discovery

    protected static final Marker DISC          = MarkerManager.getMarker("DISC").setParents(EXT_COMP);                 // all communication component logs
    public static final Marker DISC_IMPL        = MarkerManager.getMarker("DISC_IMPL").setParents(DISC,IMPL_COMP);      // all discovery system implementations logs
    // Disc - Publisher
    public static final Marker DISC_PUB         = MarkerManager.getMarker("DISC_PUB").setParents(DISC);                 // all publisher logs
    public static final Marker DISC_PUB_IMPL    = MarkerManager.getMarker("DISC_PUB_IMPL").setParents(DISC,IMPL_COMP);  // all publisher system implementations logs
    // Disc - Discoverer
    public static final Marker DISC_DISC        = MarkerManager.getMarker("DISC_DISC").setParents(DISC);                // all discoverer logs
    public static final Marker DISC_DISC_IMPL   = MarkerManager.getMarker("DISC_DISC_IMPL").setParents(DISC,IMPL_COMP); // all discoverer system implementations logs

    //@formatter: on
}
