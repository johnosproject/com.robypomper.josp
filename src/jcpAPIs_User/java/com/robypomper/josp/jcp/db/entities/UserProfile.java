package com.robypomper.josp.jcp.db.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;


/**
 * Entity class for <code>user</code> table of the <code>jcp_apis</code> database.
 */
@Entity
@Data
public class UserProfile {

    // Mngm

    @Id
    @Column(nullable = false, unique = true)
    private String usrId;


    // Profile

    private String email = "";

    private String name = "";

    private String surname = "";


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}
