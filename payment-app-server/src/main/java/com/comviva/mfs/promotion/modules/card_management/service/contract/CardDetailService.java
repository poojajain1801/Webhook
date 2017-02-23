package com.comviva.mfs.promotion.modules.card_management.service.contract;

import com.comviva.mfs.promotion.modules.card_management.model.*;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface CardDetailService {
    AddCardResponse checkDeviceEligibility(AddCardParm addCardParm);
    AddCardResponse addCard(DigitizationParam digitizationParam);

    Asset getAsset(String assetId);

    ActivateResp activate(ActivateReq activateReq);

}
