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

package com.robypomper.josp.params.usrs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.josp.consts.JOSP;
import lombok.Data;

/**
 * Messaging class to transmit user's username and id.
 */
@Data
public class UsrName {

    // Params

    public final String usrId;

    public final String username;

    public final boolean authenticated;

    public final boolean admin;

    public final boolean maker;

    public final boolean developer;


    // Constructor

    @JsonCreator
    public UsrName(@JsonProperty("usrId") String usrId,
                   @JsonProperty("username") String username,
                   @JsonProperty("isAuthenticated") boolean isAuthenticated,
                   @JsonProperty("isAdmin") boolean isAdmin,
                   @JsonProperty("isMaker") boolean isMaker,
                   @JsonProperty("isDeveloper") boolean isDeveloper) {
        this.usrId = usrId;
        this.username = username;
        this.authenticated = isAuthenticated;
        this.admin = isAdmin;
        this.maker = isMaker;
        this.developer = isDeveloper;
    }


    public static UsrName ANONYMOUS = new UsrName(JOSP.ANONYMOUS_ID.toString(),JOSP.ANONYMOUS_USERNAME.toString(),false,false,false,false);

    public static UsrName fromLocal(String usrId, String usrName) {
        return new UsrName(usrId,usrName,false,false,false,false);
    }


    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();

        UsrName usr = ANONYMOUS;

        try {

            String jsonInString2 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(usr);
            System.out.println(jsonInString2);

            UsrName usr2 = mapper.readValue(jsonInString2, UsrName.class);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }

    }

}