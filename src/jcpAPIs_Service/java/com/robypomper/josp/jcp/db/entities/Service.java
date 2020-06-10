package com.robypomper.josp.jcp.db.entities;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;


/**
 * Entity class for <code>service</code> table of the <code>jcp_apis</code> database.
 */
@Entity
@Data
public class Service {

    // Mngm

    @Id
    @Column(nullable = false, unique = true)
    private String srvId;

    @Column(nullable = false, unique = true)
    private String srvName;


    // Details

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ObjectInfo details;

}

