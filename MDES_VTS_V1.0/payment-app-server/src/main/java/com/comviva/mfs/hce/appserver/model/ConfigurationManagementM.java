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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */

@Entity
@Table(name="CONFIGURATION_MANAGEMENT_M")
public class ConfigurationManagementM implements Serializable {

    private static final long serialVersionUID = 1L;

    private String hvtLimit ;

    private Timestamp modifiedOn;

    private String isHvtSupported ;

    private Timestamp createdOn;

    private String status ;

    private String userId;

    @Id
    private String requestId ;

    public ConfigurationManagementM() {
    }

    public String getRequestId() {
        return requestId;
    }

    public String getUserId() {
        return userId;
    }

    public String getHvtLimit() {
        return hvtLimit;
    }

    public String getIsHvtSupported() {
        return isHvtSupported;
    }

    public Timestamp getCreatedOn() {
        return (createdOn);
    }

    public Timestamp getModifiedOn() {
        return (modifiedOn);
    }

    public String getStatus() {
        return status;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setHvtLimit(String hvtLimit) {
        this.hvtLimit = hvtLimit;
    }

    public void setIsHvtSupported(String isHvtSupported) {
        this.isHvtSupported = isHvtSupported;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = (createdOn);
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = (modifiedOn);
    }
}
