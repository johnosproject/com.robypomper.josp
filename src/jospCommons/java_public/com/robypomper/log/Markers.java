package com.robypomper.log;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Markers {

    //@formatter:off
    // Components
    public static final Marker SUB_COMP = MarkerManager.getMarker("SUB_COMP");      // all sub-components logs (Discovery, Communication...)
    public static final Marker TEST     = MarkerManager.getMarker("TEST");          // all test logs

    // Log types

    public static final Marker METHODS  = MarkerManager.getMarker("METHODS");       // info about methods
    public static final Marker SPACER   = MarkerManager.getMarker("SPACER");        // log spacer and bars


    public static final Marker TEST_METHODS     = MarkerManager.getMarker("TEST_METHODS").setParents(TEST).setParents(METHODS);
    public static final Marker TEST_SPACER      = MarkerManager.getMarker("TEST_SEPARATOR").setParents(TEST).setParents(SPACER);
    //@formatter: on
}
