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

package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RePersoTokenRequest {
    private String clientAppId;
    private String vProvisionedTokenID;
    private String clientDeviceID;
    private String clientWalletAccountId;
    private String vNotificationID;
    private String fullReperso;

    public String getvProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setvProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public String getvNotificationID() {
        return vNotificationID;
    }

    public void setvNotificationID(String vNotificationID) {
        this.vNotificationID = vNotificationID;
    }

    public String getVProvisionedTokenID() {
        return vProvisionedTokenID;
    }

    public void setVProvisionedTokenID(String vProvisionedTokenID) {
        this.vProvisionedTokenID = vProvisionedTokenID;
    }

    public String getClientDeviceID() {
        return clientDeviceID;
    }

    public void setClientDeviceID(String clientDeviceID) {
        this.clientDeviceID = clientDeviceID;
    }

    public String getClientWalletAccountId() {
        return clientWalletAccountId;
    }

    public void setClientWalletAccountId(String clientWalletAccountId) {
        this.clientWalletAccountId = clientWalletAccountId;
    }

    public String isFullReperso() {
        return fullReperso;
    }

    public void setFullReperso(String fullReperso) {
        this.fullReperso = fullReperso;
    }

    public String getClientAppId() {
        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {
        this.clientAppId = clientAppId;
    }
}
