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

public class APIObjs {
//@formatter:off

    // API info

    public static final String API_NAME = "Objs";
    public static final String API_VER = JCPAPIsVersions.VER_JCP_APIs_2_0;
    public static final String API_PATH = JCPAPIsVersions.PATH_API_BASE + "/" + API_NAME;


    // API's SubGroups

    public static class SubGroupInfo {
        public final static String NAME = "Object's info";
        public final static String DESCR = "Register/get object info, generate object ids";
    }


    // API's methods

    // '{mthdName}
    private static final String MTHD_GENERATEID         = "generate_id";
    private static final String MTHD_REGENERATEID       = "regenerate_id";
    private static final String MTHD_HISTORY            = "history";

    // '/apis/{apiName}/{apiVersion}/{mthdName}
    public static final String FULL_PATH_GENERATEID         = API_PATH + "/" + API_VER + "/" + MTHD_GENERATEID;
    public static final String FULL_PATH_REGENERATEID       = API_PATH + "/" + API_VER + "/" + MTHD_REGENERATEID;
    public static final String FULL_PATH_HISTORY            = API_PATH + "/" + API_VER + "/" + MTHD_HISTORY;


    public static final String HEADER_OBJID = "JOSP-Obj-ID";


    // API's descriptions

    public static final String DESCR_PATH_GENERATEID    = "Generate and return an obj's id for a new object";
    public static final String DESCR_PATH_REGENERATEID  = "Generate and return an obj's id for a existing object";
    public static final String DESCR_PATH_HISTORY       = "Store given statuses as caller object's statuses history";

//@formatter:on
}
