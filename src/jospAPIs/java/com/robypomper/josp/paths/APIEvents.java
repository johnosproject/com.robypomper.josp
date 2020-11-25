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

public class APIEvents {
//@formatter:off

    // API info

    public static final String API_NAME = "Events";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupEvent {
        public final static String NAME = "Objects and service's events";
        public final static String DESCR = "Register and get events...";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_OBJECT                 = "object";
    private static final String MTHD_GET_OBJECT             = "object/{objId}";
    private static final String MTHD_OBJECT_LAST            = "object/last";
    private static final String MTHD_GET_OBJECT_LAST        = "object/{objId}/last";
    private static final String MTHD_OBJECT_BY_TYPE         = "object/type/{type}";
    private static final String MTHD_GET_OBJECT_BY_TYPE     = "object/{objId}/type/{type}";
    private static final String MTHD_SERVICE                = "service";
    private static final String MTHD_GET_SERVICE            = "service/{fullSrvId}";
    private static final String MTHD_SERVICE_LAST           = "service/last";
    private static final String MTHD_GET_SERVICE_LAST       = "service/{fullSrvId}/last";
    private static final String MTHD_SERVICE_BY_TYPE        = "service/type/{type}";
    private static final String MTHD_GET_SERVICE_BY_TYPE    = "service/{fullSrvId}/type/{type}";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_OBJECT                 = API_PATH + "/" + API_VER + "/" + MTHD_OBJECT;
    public static final String FULL_PATH_GET_OBJECT             = API_PATH + "/" + API_VER + "/" + MTHD_GET_OBJECT;
    public static final String FULL_PATH_OBJECT_LAST            = API_PATH + "/" + API_VER + "/" + MTHD_OBJECT_LAST;
    public static final String FULL_PATH_GET_OBJECT_LAST        = API_PATH + "/" + API_VER + "/" + MTHD_GET_OBJECT_LAST;
    public static final String FULL_PATH_OBJECT_BY_TYPE         = API_PATH + "/" + API_VER + "/" + MTHD_OBJECT_BY_TYPE;
    public static final String FULL_PATH_GET_OBJECT_BY_TYPE     = API_PATH + "/" + API_VER + "/" + MTHD_GET_OBJECT_BY_TYPE;
    public static final String FULL_PATH_SERVICE                = API_PATH + "/" + API_VER + "/" + MTHD_SERVICE;
    public static final String FULL_PATH_GET_SERVICE            = API_PATH + "/" + API_VER + "/" + MTHD_GET_SERVICE;
    public static final String FULL_PATH_SERVICE_LAST           = API_PATH + "/" + API_VER + "/" + MTHD_SERVICE_LAST;
    public static final String FULL_PATH_GET_SERVICE_LAST       = API_PATH + "/" + API_VER + "/" + MTHD_GET_SERVICE_LAST;
    public static final String FULL_PATH_SERVICE_BY_TYPE        = API_PATH + "/" + API_VER + "/" + MTHD_SERVICE_BY_TYPE;
    public static final String FULL_PATH_GET_SERVICE_BY_TYPE    = API_PATH + "/" + API_VER + "/" + MTHD_GET_SERVICE_BY_TYPE;

//@formatter:on
}
