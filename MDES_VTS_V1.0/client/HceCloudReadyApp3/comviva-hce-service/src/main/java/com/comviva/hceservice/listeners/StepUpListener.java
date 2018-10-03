package com.comviva.hceservice.listeners;

import com.comviva.hceservice.internalSdkListeners.CommonListener;
import com.comviva.hceservice.responseobject.StepUpRequest;

import java.util.List;

/**
 * Created by amit.randhawa on 22-05-2018.
 */

 public interface StepUpListener extends CommonListener {
     void onRequireAdditionalAuthentication(List<StepUpRequest> stepUpRequests);
}
