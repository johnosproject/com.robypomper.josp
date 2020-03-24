package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, String> {

    List<Permission> findByObjId(String objId);

    List<Permission> findBySrvId(String objId);

    List<Permission> findByUsrId(String objId);

}
