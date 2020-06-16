package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceStatusRepository extends JpaRepository<ServiceStatus, String> {}
