package com.robypomper.josp.jcp.db.db;

import com.robypomper.josp.jcp.db.entities.Username;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsernameRepository extends JpaRepository<Username, Long> {}