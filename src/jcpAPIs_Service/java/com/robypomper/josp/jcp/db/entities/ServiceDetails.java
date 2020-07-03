package com.robypomper.josp.jcp.db.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
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
    @Column(nullable = false, unique = true)
    private String srvId;


    // Developer

    private String email = "";

    private String web = "";

    private String company = "";


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

}
