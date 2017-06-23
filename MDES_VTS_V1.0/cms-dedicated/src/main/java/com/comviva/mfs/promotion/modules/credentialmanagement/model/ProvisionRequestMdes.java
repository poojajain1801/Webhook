package com.comviva.mfs.promotion.modules.credentialmanagement.model;

import com.comviva.mfs.promotion.model.CmsDRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * Provisioning request
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Getter
@Setter
public class ProvisionRequestMdes extends CmsDRequest {
    /** Globally unique identifier for the Wallet Provider, as assigned by MDES.
     * Commonly known as the Wallet Identifier. */
    private String paymentAppProviderId;

    /** Identifier for the Payment App, unique per app as assigned by MasterCard for this Payment App. */
    private String paymentAppId;

    /** Identifier for the specific Mobile Payment App instance, unique across a given Wallet Identifier.
     * This value cannot be changed after digitization */
    private String paymentAppInstanceId;

    /** Globally unique identifier for the SessionInfo, as assigned by MDES */
    private String tokenUniqueReference;

    /** The type of SessionInfo requested */
    private String tokenType;

    /** Unique identifier for this task as assigned by MDES */
    private String taskId;

    /** Contains the card profile representing the SessionInfo Credential to be provisioned to the Mobile Payment App */
    private TokenCredential tokenCredential;

    public ProvisionRequestMdes(String requestId,
                                String responseHost,
                                String paymentAppProviderId,
                                String paymentAppId,
                                String paymentAppInstanceId,
                                String tokenUniqueReference,
                                String tokenType,
                                String taskId,
                                TokenCredential tokenCredential) {
        super(requestId, responseHost);
        this.paymentAppProviderId = paymentAppProviderId;
        this.paymentAppId = paymentAppId;
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.tokenUniqueReference = tokenUniqueReference;
        this.tokenType = tokenType;
        this.taskId = taskId;
        this.tokenCredential = tokenCredential;
    }

    public ProvisionRequestMdes() {
    }
}
