package com.robypomper.josp.jcp.db.entities;

import com.robypomper.josp.protocol.JOSPPerm;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;


/**
 * Entity class for <code>username</code> table of the <code>jcp_apis</code> database.
 */
@Entity
@Data
public class Permission {

    @Id
    private String id;

    private String objId;

    private String usrId;

    private String srvId;

    @Enumerated(EnumType.STRING)
    private JOSPPerm.Connection connection;

    @Enumerated(EnumType.STRING)
    private JOSPPerm.Type type;

    private Date permissionUpdatedAt;


    // Extra profile

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}

