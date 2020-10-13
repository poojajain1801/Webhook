package com.comviva.mfs.hce.appserver.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the SYS_MESSAGES database table.
 * 
 */
@Embeddable
public class SysMessagePK implements Serializable {
    // default serial version id, required for serializable classes.
    private static final long serialVersionUID = 1L;

    @Column(name = "MESSAGE_CODE")
    private String messageCode;

    @Column(name = "LANGUAGE_CODE")
    private String languageCode;

    /**
     * Instantiates a new sys message pk.
     */
    public SysMessagePK() {
    }

    public String getMessageCode() {
        return this.messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

}