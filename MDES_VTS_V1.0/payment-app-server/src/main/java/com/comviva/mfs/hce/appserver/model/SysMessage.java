/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

