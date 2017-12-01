package com.comviva.hceservice.common;

/**
 * Created by tarkeshwar.v on 5/29/2017.
 */

public class RmPendingTask {
    private String taskId;
    private String tokenUniqueReference;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }
}
