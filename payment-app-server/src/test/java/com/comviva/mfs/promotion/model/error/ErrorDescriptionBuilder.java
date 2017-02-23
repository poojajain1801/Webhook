package com.comviva.mfs.promotion.model.error;

public class ErrorDescriptionBuilder {
    private String field = null;
    private String code;
    private String message;

    public ErrorDescriptionBuilder setField(String field) {
        this.field = field;
        return this;
    }

    public ErrorDescriptionBuilder setCode(String code) {
        this.code = code;
        return this;
    }

    public ErrorDescriptionBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public ErrorDescriptionBuilder stubData(){
        setCode("200");
        setField("login.userName");
        setMessage("Can not be empty");
        return this;
    }

    public ErrorDescription build() {
        return new ErrorDescription(field, code, message);
    }
}