package com.comviva.mdesapp;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by amit.randhawa on 27-03-2018.
 */

public class CardDetails extends RealmObject {

    @PrimaryKey
    @Required
    private String cardProvisionID;
    private byte[] image;
    private String panEnrollmentId;
    private String cardLast4;




    public String getCardProvisionID() {
        return cardProvisionID;
    }

    public void setCardProvisionID(String cardProvisionID) {
        this.cardProvisionID = cardProvisionID;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getPanEnrollmentId() {
        return panEnrollmentId;
    }

    public void setPanEnrollmentId(String panEnrollmentId) {
        this.panEnrollmentId = panEnrollmentId;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

}
