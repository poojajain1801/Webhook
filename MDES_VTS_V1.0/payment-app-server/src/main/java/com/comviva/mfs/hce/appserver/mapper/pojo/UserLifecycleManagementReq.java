package com.comviva.mfs.hce.appserver.mapper.pojo;

public class UserLifecycleManagementReq {
    private String userId;
    private String operation;

    public UserLifecycleManagementReq(String userId, String operation) {
        this.userId = userId;
        this.operation = operation;
    }
    public UserLifecycleManagementReq() {

    }

    public String getUserId() {
        return userId;
    }

    public String getOperation() {
        return operation;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
