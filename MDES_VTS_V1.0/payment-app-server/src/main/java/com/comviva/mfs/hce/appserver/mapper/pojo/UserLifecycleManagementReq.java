package com.comviva.mfs.hce.appserver.mapper.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserLifecycleManagementReq {
    private List<String> userIdList;
    private String operation;

    public UserLifecycleManagementReq(List<String> userIdList, String operation) {
        this.userIdList = (userIdList);
        this.operation = operation;
    }

    public UserLifecycleManagementReq() {

    }
}
