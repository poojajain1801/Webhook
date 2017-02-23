package com.comviva.mfs.promotion.modules.credentialmanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains the card profile representing the SessionInfo Credential to be provisioned to the Mobile Payment App
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Setter
@Getter
public class TokenCredential {
    /** Contains the encrypted TokenCredentialData object. */
    private String encryptedData;

    /** The nonce used to CCM encrypt the SessionInfo Credential data. */
    private String ccmNonce;

    /** The identifier of the key used to CCM encrypt the SessionInfo Credential data. */
    private String ccmKeyId;

    /** The message authentication code computed over the data that was encrypted. */
    private String ccmMac;
}
