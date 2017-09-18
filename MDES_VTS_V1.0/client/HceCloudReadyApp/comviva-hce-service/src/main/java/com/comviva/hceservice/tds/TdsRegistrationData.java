package com.comviva.hceservice.tds;


public class TdsRegistrationData {
    private String tokenUniqueReference;
    private String tdsRegistrationCode1;
    private String authenticationCode;
    private String tdsUrl;

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

    public String getTdsRegistrationCode1() {
        return tdsRegistrationCode1;
    }

    public void setTdsRegistrationCode1(String tdsRegistrationCode1) {
        this.tdsRegistrationCode1 = tdsRegistrationCode1;
    }

    public String getAuthenticationCode() {
        return authenticationCode;
    }

    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public String getTdsUrl() {
        return tdsUrl;
    }

    public void setTdsUrl(String tdsUrl) {
        this.tdsUrl = tdsUrl;
    }
}
