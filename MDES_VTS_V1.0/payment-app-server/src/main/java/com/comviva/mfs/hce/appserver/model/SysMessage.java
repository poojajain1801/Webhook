package com.comviva.mfs.hce.appserver.model;

import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the SYS_MESSAGES database table.
 *
 */
@Entity
@Table(name = "SYS_MESSAGES")
@Cacheable(true)
public class SysMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private SysMessagePK id;

    private String bearer;

    // @Column(name="\"MESSAGE\"")
    private String message;

    private String reasonCode ;

    /**
     * Instantiates a new sys message.
     */
    public SysMessage() {
    }

    public SysMessagePK getId() {
        return id;
    }

    public String getBearer() {
        return bearer;
    }

    public String getMessage() {
        return message;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setId(SysMessagePK id) {
        this.id = id;
    }

    public void setBearer(String bearer) {
        this.bearer = bearer;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

}

