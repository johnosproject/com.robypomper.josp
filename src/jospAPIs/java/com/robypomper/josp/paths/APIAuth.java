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


/**
 * Auth API Paths class definitions.
 * <p>
 * APIs group is used for the access to the auth server APIs, like that one to
 * retrieve user profile.
 * <p>
 * The Auth Service path is used by JOSP components to send auth requests.
 */
public class APIAuth {
//@formatter:off

    // Class constants

    public static final String REALM="jcp";


    // Base

    public static final String PATH_BASE = "";

    public static final String FULL_PATH_BASE   = JcpAPI.PATH_AUTH_BASE + PATH_BASE;


    // Keycloak APIs

    public static final String PATH_USER    = "/admin/realms/" + REALM + "/users";

    public static final String FULL_PATH_USER   = FULL_PATH_BASE + PATH_USER;


    // Auth service

    public static final String PATH_AUTH        = "/realms/" + REALM + "/protocol/openid-connect/auth";
    public static final String PATH_TOKEN       = "/realms/" + REALM + "/protocol/openid-connect/token";

    public static final String FULL_PATH_AUTH       = FULL_PATH_BASE + PATH_AUTH;
    public static final String FULL_PATH_TOKEN      = FULL_PATH_BASE + PATH_TOKEN;

//@formatter:on
}
