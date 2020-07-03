package com.robypomper.josp.jcp.db.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.Date;


@Entity
@Data
public class ObjectStatus {

    @Id
    @Column(nullable = false, unique = true)
    private String objId;

    private boolean online = true;

    @Lob
    private String structure;

    private Date lastStructUpdate = new Date(0);


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}
