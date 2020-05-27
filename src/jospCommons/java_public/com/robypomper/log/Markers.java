package com.robypomper.log;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Markers {

    //@formatter:off

    // Component's types

    protected static final Marker MAIN_COMP     = MarkerManager.getMarker("MAIN_COMP");     // all main-components logs
    protected static final Marker EXT_COMP      = MarkerManager.getMarker("EXT_COMP");      // all external components logs
    protected static final Marker SUB_COMP      = MarkerManager.getMarker("SUB_COMP");      // all sub-components logs
    protected static final Marker IMPL_COMP     = MarkerManager.getMarker("IMPL_COMP");     // all components implementations logs


    // Log types

    public static final Marker METHODS      = MarkerManager.getMarker("METHODS");       // info about methods
    public static final Marker SPACER       = MarkerManager.getMarker("SPACER");        // log spacer and bars

    //@formatter: on
}
