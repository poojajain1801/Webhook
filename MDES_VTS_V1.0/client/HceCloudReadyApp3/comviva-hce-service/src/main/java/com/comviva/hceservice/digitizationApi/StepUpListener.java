package com.comviva.hceservice.digitizationApi;

import com.comviva.hceservice.util.ResponseListener;
import com.visa.cbp.external.common.StepUpRequest;

import java.util.ArrayList;

/**
 * Created by amit.randhawa on 22-05-2018.
 */

 public interface StepUpListener extends ResponseListener {
     void onRequireAdditionalAuthentication(ArrayList<StepUpRequest> stepUpRequests);
}
