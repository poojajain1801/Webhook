package com.comviva.mfs.promotion.modules.mpamanagement.model;

public class RnsInfo {
    private final String rnsRegistrationId;

    public RnsInfo(String rnsRegistrationId) {
        this.rnsRegistrationId = rnsRegistrationId;
    }

    public RnsInfo() {
        this(null);
    }

    public String getRnsRegistrationId() {
        return rnsRegistrationId;
    }

}
