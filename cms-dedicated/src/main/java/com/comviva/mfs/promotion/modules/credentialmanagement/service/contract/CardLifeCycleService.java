package com.comviva.mfs.promotion.modules.credentialmanagement.service.contract;

import com.comviva.mfs.promotion.modules.credentialmanagement.model.CardLifeCycleReq;
import com.comviva.mfs.promotion.modules.credentialmanagement.model.CardLifeCycleResp;

/**
 * Contains card life cycle management APIs (Delete, Suspend and Resume).
 * Created by tarkeshwar.v on 2/9/2017.
 */
public interface CardLifeCycleService {
    CardLifeCycleResp deleteCard(CardLifeCycleReq cardLifeCycleReq);
}
