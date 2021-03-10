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

public class APIPermissions {
//@formatter:off

    // API info

    public static final String API_NAME = "Perms";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupObjPerm {
        public final static String NAME = "Object's permissions";
        public final static String DESCR = "Manage objects permissions";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_OBJGENERATE     = "obj/{strategy}";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_OBJGENERATE    = API_PATH + "/" + API_VER + "/" + MTHD_OBJGENERATE;


    // API's descriptions

    public static final String DESCR_PATH_OBJGENERATE   = "Generate object's permissions";

//@formatter:on
}
