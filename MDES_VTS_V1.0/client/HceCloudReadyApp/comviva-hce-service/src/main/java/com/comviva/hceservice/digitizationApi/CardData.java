package com.comviva.hceservice.digitizationApi;

/**
 * Created by tarkeshwar.v on 8/19/2017.
 */

public class CardData {
    private String guid;
    private ContentType contentType;
    private Content content;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

}
