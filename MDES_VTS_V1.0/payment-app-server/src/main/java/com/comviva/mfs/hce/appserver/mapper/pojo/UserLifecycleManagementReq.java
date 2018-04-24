package com.comviva.mfs.hce.appserver.mapper.pojo;

import java.util.List;

public class UserLifecycleManagementReq {
    private List<String> userIdList;
    private String operation;

    public UserLifecycleManagementReq(List<String> userIdList, String operation) {
        this.userIdList = userIdList;
        this.operation = operation;
    }

    public UserLifecycleManagementReq() {

    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public String getOperation() {
        return operation;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
