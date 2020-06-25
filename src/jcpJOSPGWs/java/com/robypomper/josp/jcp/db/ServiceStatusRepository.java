package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceStatusRepository extends JpaRepository<ServiceStatus, String> {

    List<ServiceStatus> findAll();

    List<ServiceStatus> findBySrvId(String srvId);

    List<ServiceStatus> findByUsrId(String usrId);

    List<ServiceStatus> findBySrvIdAndUsrId(String srvId, String usrId);

}
