package com.robypomper.log;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Mrk_JOD extends Markers {

    //@formatter:off

    // John Object Daemon

    protected static final Marker JOD               = MarkerManager.getMarker("JOD");
    public static final Marker JOD_MAIN             = MarkerManager.getMarker("JOD_MAIN").setParents(JOD,MAIN_COMP);
    // JOD - Info
    public static final Marker JOD_INFO             = MarkerManager.getMarker("JOD_INFO").setParents(JOD,MAIN_COMP);
    // JOD - Executor
    public static final Marker JOD_EXEC             = MarkerManager.getMarker("JOD_EXEC").setParents(JOD,MAIN_COMP);
    public static final Marker JOD_EXEC_SUB         = MarkerManager.getMarker("JOD_EXEC_SUB").setParents(JOD,SUB_COMP);
    public static final Marker JOD_EXEC_IMPL        = MarkerManager.getMarker("JOD_EXEC_IMPL").setParents(JOD,IMPL_COMP);
    // JOD - Structure
    public static final Marker JOD_STRU             = MarkerManager.getMarker("JOD_STRU").setParents(JOD,MAIN_COMP);
    public static final Marker JOD_STRU_SUB         = MarkerManager.getMarker("JOD_STRU_SUB").setParents(JOD,SUB_COMP);
    // JOD - Permission
    public static final Marker JOD_PERM             = MarkerManager.getMarker("JOD_PERM").setParents(JOD,MAIN_COMP);
    // JOD - Comm
    public static final Marker JOD_COMM             = MarkerManager.getMarker("JOD_COMM").setParents(JOD,MAIN_COMP);
    public static final Marker JOD_COMM_SUB         = MarkerManager.getMarker("JOD_COMM_SUB").setParents(JOD,SUB_COMP);
    public static final Marker JOD_COMM_JCPCL       = MarkerManager.getMarker("JOD_COMM_JCPCL").setParents(JOD, Mrk_Commons.COMM_JCPCL);
    // JOD - Shell
    public static final Marker JOD_SHELL            = MarkerManager.getMarker("JOD_SHELL").setParents(JOD,MAIN_COMP);
    public static final Marker JOD_SHELL_SUB        = MarkerManager.getMarker("JOD_SHELL_SUB").setParents(JOD,SUB_COMP);

    //@formatter: on
}
