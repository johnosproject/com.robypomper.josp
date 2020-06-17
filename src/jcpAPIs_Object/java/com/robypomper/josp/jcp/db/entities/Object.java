package com.robypomper.josp.jcp.db.entities;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;


/**
 * Entity class for <code>object</code> table of the <code>jcp_apis</code> database.
 * <p>
 * This table contains the ids and names of all registered objects.
 * <p>
 * It's populated during object startup when it tries to register him self to the
 * JCP. A the same time, a new record to the {@link ObjectInfo} table with more
 * details on registered object will be created.
 */
@Entity
@Data
public class Object {

    // Mngm

    @Id
    @Column(nullable = false, unique = true)
    private String objId;

    @Column(nullable = false)
    private String name;

    private Boolean active;

    @Column(nullable = false)
    private String version;


    // Details

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ObjectId id;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ObjectInfo info;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ObjectOwner owner;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ObjectStatus status;

}

