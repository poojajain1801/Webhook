package com.comviva.mfs.hce.appserver.util.vts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceInitParams {
    /**
     * (Conditional) Server-side nonce created during Enroll Device.
     * Depending on the scheme selected, more data may be required.
     * This field is required only if channelInfo is specified.
     * Note:
     * This field is critical from an an SDK perspective.
     * Format: String. Size: 36, [A-Z][a-z][0-9,-]
     */
    private String vServerNonce;
}
