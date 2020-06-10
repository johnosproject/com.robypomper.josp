package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.UserName;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsernameRepository extends JpaRepository<UserName, Long> {}