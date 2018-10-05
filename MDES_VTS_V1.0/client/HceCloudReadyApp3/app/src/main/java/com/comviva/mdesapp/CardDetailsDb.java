package com.comviva.mdesapp;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class CardDetailsDb extends RealmObject {

    @Required
    @PrimaryKey
    private String uniqueCardId;
    private String cardImagePath;


    public String getUniqueCardId() {

        return uniqueCardId;
    }


    public void setUniqueCardId(String uniqueCardId) {

        this.uniqueCardId = uniqueCardId;
    }


    public String getCardImagePath() {

        return cardImagePath;
    }


    public void setCardImagePath(String cardImagePath) {

        this.cardImagePath = cardImagePath;
    }


}
