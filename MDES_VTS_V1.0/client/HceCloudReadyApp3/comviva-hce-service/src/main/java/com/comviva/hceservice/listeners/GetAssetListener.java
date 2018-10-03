package com.comviva.hceservice.listeners;

import com.comviva.hceservice.internalSdkListeners.CommonListener;
import com.comviva.hceservice.responseobject.contentguid.ContentGuid;

public interface GetAssetListener extends CommonListener {
    /**
     *
     */

    void onCompleted(ContentGuid contentGuid);

}
