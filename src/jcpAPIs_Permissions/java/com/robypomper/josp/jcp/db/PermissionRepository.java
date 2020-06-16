package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, String> {

    List<Permission> findByObjId(String objId);

    List<Permission> findBySrvId(String objId);

    List<Permission> findByUsrId(String objId);

    /**
     * <pre>
     * 	SELECT permission.obj_id
     * 	FROM (
     * 		SELECT *
     * 		FROM (
     * 			SELECT * FROM jcp_apis.permission
     * 			WHERE connection='__CONN__'
     * 			  AND (
     * 				   type IN ('__TYPES[0]__', '__TYPES[1]__', '...')
     * 				  )
     * 		) as permission_type
     * 		WHERE (srv_id='#All' OR srv_id='__SRVID__')
     * 	) as permission
     * 	LEFT JOIN jcp_apis.object_owner ON permission.obj_id=object_owner.obj_id
     * 	WHERE (usr_id='__USRID__')
     * 	   OR (usr_id='#Owner' AND owner_id='__USRID__')
     * 	   OR (usr_id='#Owner' AND owner_id='00000-00000-00000')
     * </pre>
     */
    @Query(value = "   SELECT permission.*\n" +
            "   FROM (\n" +
            "        SELECT *\n" +
            "        FROM (\n" +
            "            SELECT * FROM jcp_apis.permission\n" +
            "            WHERE connection= :conn\n" +
            "              AND (\n" +
            "                   type IN :types\n" +
            "                  )\n" +
            "        ) as permission_type\n" +
            "        WHERE (srv_id='#All' OR srv_id= :srvId)\n" +
            "    ) as permission\n" +
            "    LEFT JOIN jcp_apis.object_owner " +
            "           ON permission.obj_id=object_owner.obj_id\n" +
            "    WHERE (usr_id= :usrId)\n" +
            "       OR (usr_id='#Owner' AND owner_id= :usrId)\n" +
            "       OR (usr_id='#Owner' AND owner_id='00000-00000-00000')\n",
            nativeQuery = true)
    List<Permission> findForSrv(@Param("conn") String conn,
                                @Param("types") String[] types,
                                @Param("srvId") String srvId,
                                @Param("usrId") String usrId);

}
