package com.robypomper.josp.jcp.db.entities;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;


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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private UserProfile profile;

}

