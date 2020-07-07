package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.Object;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ObjectRepository extends JpaRepository<Object, String> {}
