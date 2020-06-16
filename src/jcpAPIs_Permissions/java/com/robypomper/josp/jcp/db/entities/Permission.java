package com.robypomper.josp.jcp.db.entities;

import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;


/**
 * Entity class for <code>username</code> table of the <code>jcp_apis</code> database.
 */
@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"objId", "usrId", "srvId"}))
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String objId;

    private String usrId;

    private String srvId;

    @Enumerated(EnumType.STRING)
    private PermissionsTypes.Connection connection;

    @Enumerated(EnumType.STRING)
    private PermissionsTypes.Type type;


    // Extra profile

    //@UpdateTimestamp
    private Date updatedAt;

}

