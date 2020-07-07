package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.ObjectId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ObjectIdRepository extends JpaRepository<ObjectId, String> {

    Optional<ObjectId> findByObjIdHwAndUsrId(String objIdHw, String usrId);

}
