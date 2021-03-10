/* *****************************************************************************
 * The John Cloud Platform set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.paths;

import com.robypomper.josp.info.JCPAPIsVersions;

public class APIAdmin {
//@formatter:off

    // API info

    public static final String API_NAME = "Admin";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupAdmin {
        public final static String NAME = "JCP Services status methods";
        public final static String DESCR = "Methods to query JCP Services statuses";
    }


    // API's methods

    // '{mthdName}
    public static final String MTHD_JCP_APIS_STATUS_INSTANCE        = "jcp/apis/status/instance";
    public static final String MTHD_JCP_APIS_STATUS_OBJS            = "jcp/apis/status/objs";
    public static final String MTHD_JCP_APIS_STATUS_SRVS            = "jcp/apis/status/srvs";
    public static final String MTHD_JCP_APIS_STATUS_USRS            = "jcp/apis/status/usrs";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_ONLINE             = "jcp/apis/status/exec/online";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_CPU                = "jcp/apis/status/exec/cpu";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_DISKS              = "jcp/apis/status/exec/disks";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_JAVA               = "jcp/apis/status/exec/java";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_JAVA_THS           = "jcp/apis/status/exec/java/threads";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_MEMORY             = "jcp/apis/status/exec/memory";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_NETWORK            = "jcp/apis/status/exec/network";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_NETWORK_INTFS      = "jcp/apis/status/exec/network/intfs";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_OS                 = "jcp/apis/status/exec/os";
    public static final String MTHD_JCP_APIS_STATUS_EXEC_PROCESS            = "jcp/apis/status/exec/process";
    public static final String MTHD_JCP_GWS_STATUS_INSTANCE         = "jcp/gws/status/instance";
    public static final String MTHD_JCP_GWS_STATUS_SERVERS          = "jcp/gws/status/servers";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_ONLINE              = "jcp/gws/status/exec/online";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_CPU                 = "jcp/gws/status/exec/cpu";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_DISKS               = "jcp/gws/status/exec/disks";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_JAVA                = "jcp/gws/status/exec/java";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_JAVA_THS            = "jcp/gws/status/exec/java/threads";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_MEMORY              = "jcp/gws/status/exec/memory";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_NETWORK             = "jcp/gws/status/exec/network";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_NETWORK_INTFS       = "jcp/gws/status/exec/network/intfs";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_OS                  = "jcp/gws/status/exec/os";
    public static final String MTHD_JCP_GWS_STATUS_EXEC_PROCESS             = "jcp/gws/status/exec/process";
    public static final String MTHD_JCP_JSL_WB_STATUS_INSTANCE      = "jcp/jslwb/status/instance";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_ONLINE           = "jcp/jslwb/status/exec/online";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_CPU              = "jcp/jslwb/status/exec/cpu";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_DISKS            = "jcp/jslwb/status/exec/disks";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_JAVA             = "jcp/jslwb/status/exec/java";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_JAVA_THS         = "jcp/jslwb/status/exec/java/threads";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_MEMORY           = "jcp/jslwb/status/exec/memory";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_NETWORK          = "jcp/jslwb/status/exec/network";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_NETWORK_INTFS    = "jcp/jslwb/status/exec/network/intfs";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_OS               = "jcp/jslwb/status/exec/os";
    public static final String MTHD_JCP_JSL_WB_STATUS_EXEC_PROCESS          = "jcp/jslwb/status/exec/process";
    public static final String MTHD_JCP_FE_STATUS_INSTANCE          = "jcp/fe/status/instance";
    public static final String MTHD_JCP_FE_STATUS_EXEC_ONLINE               = "jcp/fe/status/exec/online";
    public static final String MTHD_JCP_FE_STATUS_EXEC_CPU                  = "jcp/fe/status/exec/cpu";
    public static final String MTHD_JCP_FE_STATUS_EXEC_DISKS                = "jcp/fe/status/exec/disks";
    public static final String MTHD_JCP_FE_STATUS_EXEC_JAVA                 = "jcp/fe/status/exec/java";
    public static final String MTHD_JCP_FE_STATUS_EXEC_JAVA_THS             = "jcp/fe/status/exec/java/threads";
    public static final String MTHD_JCP_FE_STATUS_EXEC_MEMORY               = "jcp/fe/status/exec/memory";
    public static final String MTHD_JCP_FE_STATUS_EXEC_NETWORK              = "jcp/fe/status/exec/network";
    public static final String MTHD_JCP_FE_STATUS_EXEC_NETWORK_INTFS        = "jcp/fe/status/exec/network/intfs";
    public static final String MTHD_JCP_FE_STATUS_EXEC_OS                   = "jcp/fe/status/exec/os";
    public static final String MTHD_JCP_FE_STATUS_EXEC_PROCESS              = "jcp/fe/status/exec/process";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_JCP_APIS_STATUS_INSTANCE       = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_INSTANCE;
    public static final String FULL_PATH_JCP_APIS_STATUS_OBJS           = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_OBJS;
    public static final String FULL_PATH_JCP_APIS_STATUS_SRVS           = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_SRVS;
    public static final String FULL_PATH_JCP_APIS_STATUS_USRS           = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_USRS;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_ONLINE            = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_ONLINE;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_CPU               = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_CPU;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_DISKS             = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_DISKS;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA              = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_JAVA;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS          = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_JAVA_THS;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_MEMORY            = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_MEMORY;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK           = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_NETWORK;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS     = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_NETWORK_INTFS;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_OS                = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_OS;
    public static final String FULL_PATH_JCP_APIS_STATUS_EXEC_PROCESS           = API_PATH + "/" + API_VER + "/" + MTHD_JCP_APIS_STATUS_EXEC_PROCESS;
    public static final String FULL_PATH_JCP_GWS_STATUS_INSTANCE        = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_INSTANCE;
    public static final String FULL_PATH_JCP_GWS_STATUS_SERVERS         = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_SERVERS;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_ONLINE             = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_ONLINE;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_CPU                = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_CPU;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_DISKS              = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_DISKS;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_JAVA               = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_JAVA;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_JAVA_THS           = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_JAVA_THS;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_MEMORY             = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_MEMORY;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_NETWORK            = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_NETWORK;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_NETWORK_INTFS      = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_NETWORK_INTFS;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_OS                 = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_OS;
    public static final String FULL_PATH_JCP_GWS_STATUS_EXEC_PROCESS            = API_PATH + "/" + API_VER + "/" + MTHD_JCP_GWS_STATUS_EXEC_PROCESS;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_INSTANCE     = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_INSTANCE;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_ONLINE          = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_ONLINE;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_CPU             = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_CPU;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_DISKS           = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_DISKS;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA            = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_JAVA;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA_THS        = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_JAVA_THS;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_MEMORY          = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_MEMORY;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK         = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_NETWORK;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK_INTFS   = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_NETWORK_INTFS;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_OS              = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_OS;
    public static final String FULL_PATH_JCP_JSL_WB_STATUS_EXEC_PROCESS         = API_PATH + "/" + API_VER + "/" + MTHD_JCP_JSL_WB_STATUS_EXEC_PROCESS;
    public static final String FULL_PATH_JCP_FE_STATUS_INSTANCE         = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_INSTANCE;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_ONLINE              = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_ONLINE;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_CPU                 = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_CPU;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_DISKS               = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_DISKS;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_JAVA                = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_JAVA;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_JAVA_THS            = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_JAVA_THS;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_MEMORY              = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_MEMORY;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_NETWORK             = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_NETWORK;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_NETWORK_INTFS       = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_NETWORK_INTFS;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_OS                  = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_OS;
    public static final String FULL_PATH_JCP_FE_STATUS_EXEC_PROCESS             = API_PATH + "/" + API_VER + "/" + MTHD_JCP_FE_STATUS_EXEC_PROCESS;


    // API's descriptions

    public static final String DESCR_PATH_JCP_APIS_STATUS_INSTANCE       = "Return JCP APIs instance's status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_OBJS           = "Return JCP APIs objects stats";
    public static final String DESCR_PATH_JCP_APIS_STATUS_SRVS           = "Return JCP APIs services stats";
    public static final String DESCR_PATH_JCP_APIS_STATUS_USRS           = "Return JCP APIs users stats";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_ONLINE           = "Return JCP APIs executable's ONLINE status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_CPU              = "Return JCP APIs executable's CPU status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_DISKS            = "Return JCP APIs executable's DISKS status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA             = "Return JCP APIs executable's JAVA status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_JAVA_THS         = "Return JCP APIs executable's JAVA_THS status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_MEMORY           = "Return JCP APIs executable's MEMORY status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK          = "Return JCP APIs executable's NETWORK status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_NETWORK_INTFS    = "Return JCP APIs executable's NETWORK_INTFS status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_OS               = "Return JCP APIs executable's OS status";
    public static final String DESCR_PATH_JCP_APIS_STATUS_EXEC_PROCESS          = "Return JCP APIs executable's PROCESS status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_INSTANCE        = "Return JCP GWs instance's status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_SERVERS         = "Return JCP GWs servers status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_ONLINE           = "Return JCP GWs executable's ONLINE status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_CPU              = "Return JCP GWs executable's CPU status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_DISKS            = "Return JCP GWs executable's DISKS status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_JAVA             = "Return JCP GWs executable's JAVA status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_JAVA_THS         = "Return JCP GWs executable's JAVA_THS status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_MEMORY           = "Return JCP GWs executable's MEMORY status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_NETWORK          = "Return JCP GWs executable's NETWORK status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_NETWORK_INTFS    = "Return JCP GWs executable's NETWORK_INTFS status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_OS               = "Return JCP GWs executable's OS status";
    public static final String DESCR_PATH_JCP_GWS_STATUS_EXEC_PROCESS          = "Return JCP GWs executable's PROCESS status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_INSTANCE     = "Return JCP JSL Web Bridge instance's status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_ONLINE           = "Return JCP JSL Web Bridge executable's ONLINE status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_CPU              = "Return JCP JSL Web Bridge executable's CPU status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_DISKS            = "Return JCP JSL Web Bridge executable's DISKS status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA             = "Return JCP JSL Web Bridge executable's JAVA status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_JAVA_THS         = "Return JCP JSL Web Bridge executable's JAVA_THS status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_MEMORY           = "Return JCP JSL Web Bridge executable's MEMORY status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK          = "Return JCP JSL Web Bridge executable's NETWORK status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_NETWORK_INTFS    = "Return JCP JSL Web Bridge executable's NETWORK_INTFS status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_OS               = "Return JCP JSL Web Bridge executable's OS status";
    public static final String DESCR_PATH_JCP_JSL_WB_STATUS_EXEC_PROCESS          = "Return JCP JSL Web Bridge executable's PROCESS status";
    public static final String DESCR_PATH_JCP_FE_STATUS_INSTANCE         = "Return JCP Front End instance's status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_ONLINE           = "Return JCP Front End executable's ONLINE status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_CPU              = "Return JCP Front End executable's CPU status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_DISKS            = "Return JCP Front End executable's DISKS status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_JAVA             = "Return JCP Front End executable's JAVA status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_JAVA_THS         = "Return JCP Front End executable's JAVA_THS status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_MEMORY           = "Return JCP Front End executable's MEMORY status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_NETWORK          = "Return JCP Front End executable's NETWORK status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_NETWORK_INTFS    = "Return JCP Front End executable's NETWORK_INTFS status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_OS               = "Return JCP Front End executable's OS status";
    public static final String DESCR_PATH_JCP_FE_STATUS_EXEC_PROCESS          = "Return JCP Front End executable's PROCESS status";

//@formatter:on
}
