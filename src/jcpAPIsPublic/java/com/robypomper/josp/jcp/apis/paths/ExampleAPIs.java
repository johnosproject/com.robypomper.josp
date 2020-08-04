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

package com.robypomper.josp.jcp.apis.paths;

import com.robypomper.josp.jcp.info.JCPAPIsVersions;

public class ExampleAPIs {

    // API info

    public static final String API_NAME = "examples";
    public static final String API_VER = JCPAPIsVersions.VER_TEST_2_0;
    public static final String API_PATH = JcpAPI.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupMethods {
        public final static String NAME = "Methods";
        public final static String DESCR = "Examples...";
    }

    public static class SubGroupDB {
        public final static String NAME = "Database";
        public final static String DESCR = "Examples...";
    }

    public static class SubGroupAuthentication {
        public final static String NAME = "Authentication";
        public final static String DESCR = "Examples...";
    }

    public static class SubGroupAuthorization {
        public final static String NAME = "Authorization";
        public final static String DESCR = "Examples...";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_METHODS_GENERIC        = SubGroupMethods.NAME + "";
    private static final String MTHD_DB_DB                  = SubGroupDB.NAME + "/db";
    private static final String MTHD_AUTHENTICATION_GENERIC = SubGroupAuthentication.NAME + "";
    private static final String MTHD_AUTHORIZATION_GENERIC  = SubGroupAuthorization.NAME + "";

    // '/apis/jospgws/{apiVersion}/{mthdName}
    public static final String FULL_METHODS_GENERIC         = API_PATH + "/" + API_VER + "/" + MTHD_METHODS_GENERIC;
    public static final String FULL_DB_DB                   = API_PATH + "/" + API_VER + "/" + MTHD_DB_DB;
    public static final String FULL_AUTHENTICATION_GENERIC  = API_PATH + "/" + API_VER + "/" + MTHD_AUTHENTICATION_GENERIC;
    public static final String FULL_AUTHORIZATION_GENERIC   = API_PATH + "/" + API_VER + "/" + MTHD_AUTHORIZATION_GENERIC;

}
