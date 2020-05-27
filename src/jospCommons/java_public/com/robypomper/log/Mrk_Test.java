package com.robypomper.log;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Mrk_Test extends Markers {

    //@formatter:off

    // Tests

    public static final Marker TEST             = MarkerManager.getMarker("TEST");          // all test logs
    public static final Marker TEST_METHODS     = MarkerManager.getMarker("TEST_METHODS").setParents(TEST,METHODS);
    public static final Marker TEST_SPACER      = MarkerManager.getMarker("TEST_SPACER").setParents(TEST,SPACER);

    //@formatter: on
}
