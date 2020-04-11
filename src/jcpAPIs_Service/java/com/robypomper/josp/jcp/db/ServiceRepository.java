package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ServiceRepository extends JpaRepository<Service, String> {}
