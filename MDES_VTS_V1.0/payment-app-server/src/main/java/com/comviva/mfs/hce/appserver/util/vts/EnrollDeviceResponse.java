/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 * <p/>
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 * <p/>
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.util.vts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnrollDeviceResponse {
    /**
     * Conditional if the SDK indicates deviceInitParams are needed.
     * Applicable only to use cases where the SDK is implemented using
     * software-based secure storage.
     */
    private DeviceInitParams deviceInitParams;
    private String requestID;

    /**
     * (Optional) Stable device identification set by wallet provider.
     * Could be computer identifier or ID tied to hardware such as TEE_ID or
     * SE_ID. clientDeviceIDis not needed for Presentation Type = ECOM
     * Format:String. Size: 24 [0-9,A-Z, a-z,-, _]
     */
    private String clientDeviceID;
    /**
     * (Required) VTS-defined unique ID that identifies the wallet provider
     * entity in the system. Provided during on-boarding.
     * Format: String. Size: 36, [A-Z][a-z][0-9,-]
     */
    private String vClientID;
    /**
     * Depending on the scheme selected, more data may be required.
     * This field is required only if channelInfo is specified.
     * Note:
     * This field is critical from an an SDK perspective.
     * Format: String. Size: 36, [A-Z][a-z][0-9,-]
     */
    private String vServerNonce;
}
