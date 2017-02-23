package com.comviva.mfs.promotion.modules.pack.model;

import com.comviva.mfs.promotion.exception.InvalidValueException;

/**
 * Created by sumit.das on 12/23/2016.
 */
public enum PackStatus {
    ACTIVE("active"), INACTIVE("inactive");

    private final String status;

    PackStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static PackStatus getPackStatus(String status) {
        try {
            return PackStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidValueException(e.getMessage(), e);
        }
    }
}
