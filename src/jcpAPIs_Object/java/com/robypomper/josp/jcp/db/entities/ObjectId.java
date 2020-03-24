package com.robypomper.josp.jcp.db.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;


/**
 * Entity class for <code>object_id</code> table of the <code>jcp_apis</code> database.
 * <p>
 * This table contains all ids assigned to objects and guarantee his uniqueness.
 * <p>
 * New record is added when an object requests the generation of an object id.
 * The generated object id is associated to object's hardware id and the his
 * owner id. So when same object/owner requests for the object id generation,
 * the same object id will always returned.
 */
@Entity
@Data
@Table(indexes = {@Index(columnList = "objIdHw,usrId")})
public class ObjectId {

    // Mngm

    @Id
    @Column(nullable = false, unique = true)
    private String objId;

    @Column(nullable = false)
    private String objIdHw;

    @Column(nullable = false)
    private String usrId;


    // Extra profile

    @CreationTimestamp
    private Date createdAt;
}

