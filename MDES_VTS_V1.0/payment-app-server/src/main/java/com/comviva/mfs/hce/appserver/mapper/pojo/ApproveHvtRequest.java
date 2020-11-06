package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by rishikesh.kumar on 08-04-2019.
 */
@Getter
@Setter
@ToString
public class ApproveHvtRequest {

    private String userName ;
    private String requestId ;
    private String decision ;

    public ApproveHvtRequest(String userName, String requestId, String decision) {
        this.userName = userName;
        this.requestId = requestId;
        this.decision = decision;
    }

    public ApproveHvtRequest() {
    }
}
