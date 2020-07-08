package com.robypomper.josp.jcp.db;

import com.robypomper.josp.jcp.db.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> {}
