package com.robypomper.josp.jcp.db.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;


/**
 * Entity class for <code>service_details</code> table of the <code>jcp_apis</code> database.
 */
@Entity
@Data
public class ServiceDetails {

    // Mngm

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Developer

    private String email = "";

    private String web = "";

    private String company = "";


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
