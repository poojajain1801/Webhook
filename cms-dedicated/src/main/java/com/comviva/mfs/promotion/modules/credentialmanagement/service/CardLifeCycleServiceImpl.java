package com.comviva.mfs.promotion.modules.credentialmanagement.service;

import com.comviva.mfs.promotion.constants.ConstantErrorCodes;
import com.comviva.mfs.promotion.constants.Constants;
import com.comviva.mfs.promotion.constants.TokenState;
import com.comviva.mfs.promotion.modules.common.tokens.domain.Token;
import com.comviva.mfs.promotion.modules.common.tokens.repository.TokenRepository;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.CardLifeCycleReq;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.CardLifeCycleResp;
import com.comviva.mfs.promotion.modules.credentialmanagement.service.contract.CardLifeCycleService;
import com.comviva.mfs.promotion.util.ArrayUtil;
import com.comviva.mfs.promotion.util.aes.AESUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.Optional;

/**
 * Card Life Cycle Management Services implementation.
 * Created by tarkeshwar.v on 2/9/2017.
 */
@Service
public class CardLifeCycleServiceImpl implements CardLifeCycleService {
    private TokenRepository tokenRepository;

    @Autowired
    public CardLifeCycleServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Prepare Provision response.
     * @param reasonCode    Reason Code as defined by MDES API specification.
     * @return Response
     */
    private CardLifeCycleResp prepareCardLifeCycleManagementErrResp(final int reasonCode) {
        JSONObject resp = new JSONObject();
        resp.put("responseId", "7000000001");
        resp.put("responseHost", Constants.RESPONSE_HOST);
        String encData = null;
        try {
            byte[] bEncData = AESUtil.cipherCcm(resp.toString().getBytes(), Constants.AES_KEY, Constants.CCM_NONCE, true);
            encData = ArrayUtil.getHexString(bEncData);
        } catch (GeneralSecurityException e) {
        }
        return new CardLifeCycleResp(encData, Integer.toString(reasonCode), ConstantErrorCodes.errorCodes.get(reasonCode));
    }

    /**
     * Prepare Provision response.
     * @param reasonCode            Reason Code as defined by MDES API specification.
     * @param reasonDescription     Description
     * @return Response
     */
    private CardLifeCycleResp prepareCardLifeCycleManagementErrResp(final int reasonCode, final String reasonDescription) {
        JSONObject resp = new JSONObject();
        resp.put("responseId", "7000000001");
        resp.put("responseHost", Constants.RESPONSE_HOST);
        String encData = null;
        try {
            byte[] bEncData = AESUtil.cipherCcm(resp.toString().getBytes(), Constants.AES_KEY, Constants.CCM_NONCE, true);
            encData = ArrayUtil.getHexString(bEncData);
        } catch (GeneralSecurityException e) {
        }
        return new CardLifeCycleResp(encData, Integer.toString(reasonCode), reasonDescription);
    }

    @Override
    public CardLifeCycleResp deleteCard(CardLifeCycleReq cardLifeCycleReq) {
        // TODO Validate mobileKeysetId
        // TODO Validate Authentication Code

        // Decrypt encryptedData and recover request data
        JSONObject jsReqData;
        try {
            byte[] bReqData = AESUtil.cipherCcm(ArrayUtil.getByteArray(cardLifeCycleReq.getEncryptedData()),
                    Constants.AES_KEY,
                    Constants.CCM_NONCE,
                    false);

            jsReqData = new JSONObject(new String(bReqData));
        } catch (GeneralSecurityException e) {
            return prepareCardLifeCycleManagementErrResp(ConstantErrorCodes.CRYPTOGRAPHY_ERROR);
        }

        // Validate tokenUniqueReference
        if(!jsReqData.has("tokenUniqueReference") || !jsReqData.has("transactionCredentialsStatus")) {
            return prepareCardLifeCycleManagementErrResp(ConstantErrorCodes.MISSING_REQUIRED_FIELD);
        }
        String tokenUniqueReference = jsReqData.getString("tokenUniqueReference");
        Optional<Token> tokenOptional = tokenRepository.findByTokenUniqueReference(tokenUniqueReference);
        if(!tokenOptional.isPresent()) {
            return prepareCardLifeCycleManagementErrResp(ConstantErrorCodes.INVALID_TOKEN_UNIQUE_REFERENCE);
        }

        // Validate transactionCredentialsStatus
        Token token = tokenOptional.get();
        JSONArray transactionCredentialsStatus = jsReqData.getJSONArray("transactionCredentialsStatus");
        // If token is just added and not activated then transactionCredentialsStatus must be empty
        if(TokenState.NEW.name().equalsIgnoreCase(token.getState())) {
            if(transactionCredentialsStatus.length() != 0) {
                return prepareCardLifeCycleManagementErrResp(ConstantErrorCodes.INVALID_FIELD_VALUE);
            }
        }

        // Delete the SessionInfo
        tokenRepository.delete(token);

        return prepareCardLifeCycleManagementErrResp(ConstantErrorCodes.SC_OK, "SessionInfo deleted successfully");
    }
}
