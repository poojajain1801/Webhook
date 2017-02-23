package com.comviva.mfs.promotion.modules.common.sessionmanagement.model;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

/**
 * Response of the session processing.
 * Created by tarkeshwar.v on 2/20/2017.
 */
@Getter
@Setter
public class ProcessSessionResponse {
    private SessionValidationResult sessionValidationResult;
    private JSONObject jsonRequest;
}
