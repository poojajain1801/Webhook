package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by rishikesh.kumar on 03-08-2018.
 */
@Getter
@Setter
public class GetTaskStatusReq {
    private String paymentAppInstanceId ;
    private String taskId ;

    public GetTaskStatusReq(String paymentAppInstanceId, String taskId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
        this.taskId = taskId;
    }
}
