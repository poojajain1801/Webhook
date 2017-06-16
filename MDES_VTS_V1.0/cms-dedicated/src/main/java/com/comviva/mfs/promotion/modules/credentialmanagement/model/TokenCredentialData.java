package com.comviva.mfs.promotion.modules.credentialmanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains Card Profile data coming from MDES.
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Setter
@Getter
public class TokenCredentialData {
    /** The card profile for SessionInfo Credential as specified in MasterCard Cloud-Based Payments Card Profile Specification */
    private String cardProfile;

    /** The 128-bit AES key used to encrypt the ICC private keys in the 'cardProfile'. Provided as a 32-byte field,
     * encrypted by a transport key identified by 'kekId' using ECB mode padded with '80' followed by '00' bytes until
     * the end of the block. */
    private String iccKek;

    /** The identifier for the key used to encrypt 'iccKek'. */
    private String kekId;
}
