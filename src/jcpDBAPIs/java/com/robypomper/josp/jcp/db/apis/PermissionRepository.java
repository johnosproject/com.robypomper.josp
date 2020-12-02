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

package com.robypomper.josp.jcp.db.apis;

import com.robypomper.josp.jcp.db.apis.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, String> {

    List<Permission> findByObjId(String objId);

    /**
     * <pre>
     * 	SELECT * FROM permission
     * 	WHERE connection='__CONN__'
     * 	  AND (
     * 	    type IN ('__TYPES[0]__', '__TYPES[1]__', '...')
     * 	  )
     * 	  AND obj_id='__OBJID__'
     * </pre>
     */
    @Query(value = "SELECT * FROM permission\n" +
            "WHERE connection= :conn\n" +
            "  AND (\n" +
            "       type IN :types\n" +
            "      )\n" +
            "  AND obj_id= :objId\n",
            nativeQuery = true)
    List<Permission> findForObj(@Param("conn") String conn,
                                @Param("types") String[] types,
                                @Param("objId") String objId);

    /**
     * <pre>
     * 	SELECT permission.*
     * 	FROM (
     * 		SELECT *
     * 		FROM (
     * 			SELECT * FROM permission
     * 			WHERE connection='__CONN__'
     * 			  AND (
     * 				   type IN ('__TYPES[0]__', '__TYPES[1]__', '...')
     * 				  )
     * 		) as permission_type
     * 		WHERE (srv_id='#All' OR srv_id='__SRVID__')
     * 	) as permission
     * 	LEFT JOIN object_owner ON permission.obj_id=object_owner.obj_id
     * 	WHERE (usr_id='__USRID__')
     * 	   OR (usr_id='#Owner' AND owner_id='__USRID__')
     * 	   OR (usr_id='#Owner' AND owner_id='00000-00000-00000')
     * </pre>
     */
    @Query(value = "SELECT permission.*\n" +
            "FROM (\n" +
            "     SELECT *\n" +
            "     FROM (\n" +
            "         SELECT * FROM permission\n" +
            "         WHERE connection= :conn\n" +
            "           AND (\n" +
            "                type IN :types\n" +
            "               )\n" +
            "     ) as permission_type\n" +
            "     WHERE (srv_id='#All' OR srv_id= :srvId)\n" +
            " ) as permission\n" +
            " LEFT JOIN object_owner " +
            "        ON permission.obj_id=object_owner.obj_id\n" +
            " WHERE (usr_id= :usrId)\n" +
            "    OR (usr_id='#Owner' AND owner_id= :usrId)\n" +
            "    OR (usr_id='#Owner' AND owner_id='00000-00000-00000')\n",
            nativeQuery = true)
    List<Permission> findForSrv(@Param("conn") String conn,
                                @Param("types") String[] types,
                                @Param("srvId") String srvId,
                                @Param("usrId") String usrId);

}
