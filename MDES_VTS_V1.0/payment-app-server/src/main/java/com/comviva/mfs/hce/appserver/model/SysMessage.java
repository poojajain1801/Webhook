package com.comviva.mfs.hce.appserver.model;

import java.io.Serializable;
import javax.persistence.*;

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

    /**
     * Instantiates a new sys message.
     */
    public SysMessage() {
    }

    public SysMessagePK getId() {
        return this.id;
    }

    public void setId(SysMessagePK id) {
        this.id = id;
    }

    public String getBearer() {
        return this.bearer;
    }

    public void setBearer(String bearer) {
        this.bearer = bearer;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}