package com.comviva.hceservice.listeners;

import com.comviva.hceservice.internalSdkListeners.CommonListener;
import com.comviva.hceservice.responseobject.cardmetadata.CardMetaData;

public interface GetCardMetaDataListener extends CommonListener {

    void onSuccess(CardMetaData cardMetaData);

}
