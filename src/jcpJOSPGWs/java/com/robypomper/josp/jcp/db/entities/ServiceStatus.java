package com.robypomper.josp.jcp.db.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class ServiceStatus {

    @Id
    @Column(nullable = false)
    private String fullId;

    @Column(nullable = false)
    private String srvId;

    @Column(nullable = false)
    private String usrId;

    @Column(nullable = false)
    private String instId;

    //@Column(nullable = false)
    private String version;

    private boolean online = true;


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}
