package com.robypomper.josp.jcp.db.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;


/**
 * Entity class for <code>object_info</code> table of the <code>jcp_apis</code> database.
 * <p>
 * This table contains the detailed info of all registered objects. This table
 * act as support/details table for {@link Object} table that contains only the
 * user id and his name for fastest access.
 * <p>
 * It's populated during object startup when it tries to register him self to the
 * JCP.
 */
@Entity
@Data
public class ObjectInfo {

    // Mngm

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Profile

    private String brand = "";

    private String model = "";

    private String longDescr = "";

    @Lob
    private String structure = "";


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}

