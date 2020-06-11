package com.robypomper.josp.jcp.db.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
// key srvId, usrId, instId
public class ServiceStatus {

    @Id
    @Column(nullable = false)
    private String srvId;

    @Id
    @Column(nullable = false)
    private String usrId;

    @Id
    @Column(nullable = false)
    private String instId;

    @Column(nullable = false)
    private String version;


    private boolean online = true;

}
