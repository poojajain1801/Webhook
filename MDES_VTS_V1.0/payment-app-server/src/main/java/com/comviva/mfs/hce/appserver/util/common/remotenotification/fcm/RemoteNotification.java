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
package com.comviva.mfs.hce.appserver.util.common.remotenotification.fcm;

/**
 * Remote Notification Service.
 * Created by tarkeshwar.v on 2/14/2017.
 */
public interface RemoteNotification {
    /** Server's registration key with RNS server */
    String SERVER_KEY = "AAAAZ7NvXtc:APA91bHaOfXFRoMHidhBFmgDHDLB0jqF_VucWIAhEEePGO8AtQhWLh42Y2c1v7SHb3C44NZqMurD7C3u2Mu5qImr6G_y0uhCTfUY5gZoVk4-wIRtVYuHSAczm-UZ8abdNzr6gLF41rq-";

    String KEY_NOTIFICATION_TYPE = "notificationType";
    String TYPE_TDS_REGISTRATION_NOTIFICATION = "notificationTdsRegistration";
    String TYPE_TDS_NOTIFICATION = "notificationTds";

    String VERSION = "1.0";
    /** Expiry duration for session code */
    int RNS_SESSION_EXPIRY_DURATION_IN_MONTH = 1;
    /** The number of seconds after which the remote management session code will expire after first use */
    int RNS_SESSION_VALIDITY_DURATION_IN_SECONDS = 3600;

    enum PENDING_ACTION {
        REGISTER,
        REQUEST_SESSION,
        PROVISION,
        NOTIFY_PROVISION_RESULT,
        CHANGE_PIN,
        DELETE,
        REPLENISH,
        GET_TASK_STATUS,
        RESET_MOBILE_PIN
    }

    /**
     * Send notification message to the target device.
     * @param rnsPostData           Remote Notification data to be posted
     * @return  Response
     */
    RnsResponse sendRns(byte[] rnsPostData);
}
