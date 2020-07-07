package com.robypomper.josp.jcp.db.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Date;


/**
 * Entity class for <code>user</code> table of the <code>jcp_apis</code> database.
 */
@Entity
@Data
public class User {

    // Mngm

    @Id
    @Column(nullable = false, unique = true)
    private String usrId;

    @Column(nullable = false, unique = true)
    private String username;


    // Details

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserProfile profile;


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}

