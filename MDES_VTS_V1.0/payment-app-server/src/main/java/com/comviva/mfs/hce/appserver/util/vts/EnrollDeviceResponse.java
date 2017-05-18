package com.comviva.mfs.hce.appserver.util.vts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
