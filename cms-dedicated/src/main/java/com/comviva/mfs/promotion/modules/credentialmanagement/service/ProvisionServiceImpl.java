package com.comviva.mfs.promotion.modules.credentialmanagement.service;

import com.comviva.mfs.promotion.constants.ConstantErrorCodes;
import com.comviva.mfs.promotion.constants.Constants;
import com.comviva.mfs.promotion.constants.TokenState;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.ProvisionRequestMdes;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.ProvisionResponseMdes;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.TokenCredential;
import com.comviva.mfs.promotion.modules.credentialmanagement.service.contract.ProvisionService;
import com.comviva.mfs.promotion.modules.mpamanagement.model.TokenType;
import com.comviva.mfs.promotion.modules.mpamanagement.repository.ApplicationInstanceInfoRepository;
import com.comviva.mfs.promotion.modules.common.paymentappproviders.repository.PaymentAppProviderRepository;
import com.comviva.mfs.promotion.modules.common.tokens.domain.Token;
import com.comviva.mfs.promotion.modules.common.tokens.repository.TokenRepository;
import com.comviva.mfs.promotion.util.ArrayUtil;
import com.comviva.mfs.promotion.util.aes.AESUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;

/**
 * Implementation of ProvisionServiceMobPayApi.
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Service
public class ProvisionServiceImpl implements ProvisionService {
    private ApplicationInstanceInfoRepository mobileEnvRepository;
    private PaymentAppProviderRepository paymentAppProviderRepository;
    private TokenRepository tokenRepository;

    @Autowired
    public ProvisionServiceImpl(ApplicationInstanceInfoRepository mobileEnvRepository,
                                PaymentAppProviderRepository paymentAppProviderRepository,
                                TokenRepository tokenRepository) {
        this.mobileEnvRepository = mobileEnvRepository;
        this.paymentAppProviderRepository = paymentAppProviderRepository;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Prepare Provision response for MDES.
     * @param reasonCode    Reason Code as defined by MDES API specification.
     * @return  Response
     */
    private ProvisionResponseMdes prepareProvisionMdesResp(final int reasonCode) {
        return new ProvisionResponseMdes("123456",
                Constants.RESPONSE_HOST,
                Integer.toString(reasonCode),
                ConstantErrorCodes.errorCodes.get(reasonCode));
    }

    /**
     * Prepare Provision response for MDES.
     * @param reasonCode        Reason Code as defined by MDES API specification.
     * @param reasonDescription Description
     * @return  Response
     */
    private ProvisionResponseMdes prepareProvisionMdesResp(final int reasonCode, final String reasonDescription) {
        return new ProvisionResponseMdes("123456",
                Constants.RESPONSE_HOST,
                Integer.toString(reasonCode),
                reasonDescription);
    }

    @Override
    @Transactional
    public ProvisionResponseMdes provisionMdes(ProvisionRequestMdes provisionRequestMdes) {
        // Validate paymentAppProviderId i.e. Payment App Provider Id is present in authorised app providers list.
        boolean isPayAppProviderValid = paymentAppProviderRepository.findByPaymentAppProviderId(provisionRequestMdes.getPaymentAppProviderId()).isPresent();
        if(!isPayAppProviderValid) {
            return prepareProvisionMdesResp(ConstantErrorCodes.INVALID_PAYMENT_APP_PROVIDER_ID);
        }

        //  Validate paymentAppInstanceId
        String payAppInstId = provisionRequestMdes.getPaymentAppInstanceId();
        boolean isPayAppInstValid = mobileEnvRepository.findByPaymentAppInstId(payAppInstId).isPresent();
        if (!isPayAppInstValid) {
            return prepareProvisionMdesResp(ConstantErrorCodes.PAYMENT_APP_INSTANCE_NOT_REGISTERED);
        }

        // Validate tokenUniqueReference
        String tokenUniqueRef = provisionRequestMdes.getTokenUniqueReference();
        if (tokenUniqueRef.length() > Constants.LEN_TOKEN_UNIQUE_REFERENCE) {
            return prepareProvisionMdesResp(ConstantErrorCodes.INVALID_TOKEN_UNIQUE_REFERENCE);
        }

        //  Validate tokenType. It must be CLOUD only
        String tokenType = provisionRequestMdes.getTokenType();
        if (!TokenType.CLOUD.name().equalsIgnoreCase(tokenType)) {
            return prepareProvisionMdesResp(ConstantErrorCodes.INVALID_TOKEN_TYPE);
        }

        // Fetch tokenCredential
        TokenCredential tokenCredential = provisionRequestMdes.getTokenCredential();
        // TODO For the time being we have taken CCM keys as fixed value
        // TODO Check that ccmKeyId is valid

        // Decrypt encryptedData and recover TokenCredentialData (cardProfile, iccKek and kekId)
        byte[] bEncData = ArrayUtil.getByteArray(tokenCredential.getEncryptedData());
        byte[] bCcmNonce = ArrayUtil.getByteArray(tokenCredential.getCcmNonce());
        byte[] key = Constants.AES_KEY;
        String strTokenCredentialData = null;
        try {
            strTokenCredentialData = new String(AESUtil.cipherCcm(bEncData, key, bCcmNonce, false));
        } catch (GeneralSecurityException e) {
            return prepareProvisionMdesResp(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);
        }

        // Fetch iccKek, decrypt it with kek and recover iccKek
        JSONObject tokenCredentialData = new JSONObject(strTokenCredentialData);
        String iccKek = tokenCredentialData.getString("iccKek");
        String kekId = tokenCredentialData.getString("kekId");
        byte[] encIccKek = ArrayUtil.getByteArray(iccKek);
        // TODO Validate that kekId is valid and then fetch the kay value
        /*byte[] kek = Constants.AES_KEY;
        byte[] iccKek = null;
        try {
            iccKek = AESUtil.cipherECB(encIccKek, kek, AESUtil.Padding.ISO7816_4, false);
        } catch (GeneralSecurityException e) {
            return prepareProvisionMdesResp(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);
        }*/

        JSONObject cardProfile = tokenCredentialData.getJSONObject("cardProfile");
        // Now save cardProfile and iccKek
        tokenRepository.save(new Token("1234", payAppInstId, tokenUniqueRef, tokenType, cardProfile.toString(), iccKek, kekId, TokenState.NEW.name()));

        // Prepare response
        return prepareProvisionMdesResp(ConstantErrorCodes.SC_OK, "Provision Successful");
    }

}
