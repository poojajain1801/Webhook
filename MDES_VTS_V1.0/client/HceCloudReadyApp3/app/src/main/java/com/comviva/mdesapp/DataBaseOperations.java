package com.comviva.mdesapp;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by amit.randhawa on 28-03-2018.
 */

public class DataBaseOperations {

    private static DataBaseOperations dataBaseOperations;
    private static Realm myRealm;
    private Context context;

    public  DataBaseOperations(Context context)
    {
        this.context = context;
    }

    public void addCardData(CardDetails cardDetails)
    {
        myRealm = Realm.getDefaultInstance();
        myRealm.beginTransaction();
        CardDetails cardDetailsRealm = myRealm.createObject(CardDetails.class,cardDetails);
        myRealm.commitTransaction();
        myRealm.close();
    }


    public void deleteCardData(String provisionID)
    {
        myRealm = Realm.getDefaultInstance();
        RealmResults<CardDetails> cardDetailsRealmResults = myRealm.where(CardDetails.class).equalTo("cardProvisionID", provisionID).findAll();
        myRealm.beginTransaction();
        cardDetailsRealmResults.remove(0);
        myRealm.commitTransaction();
        myRealm.close();
    }

    public CardDetails searchCard(String provisionID)
    {
        myRealm = Realm.getDefaultInstance();
        RealmResults<CardDetails> realmResults = myRealm.where(CardDetails.class).equalTo("cardProvisionID",provisionID).findAll();
        myRealm.beginTransaction();
        myRealm.commitTransaction();
        CardDetails cardDetails = realmResults.get(0);
        myRealm.close();
        return  cardDetails;
    }

    public ArrayList<CardDetails> getAllCardDetails()
    {
        myRealm = Realm.getDefaultInstance();
        ArrayList<CardDetails> cardDetailsArrayList = new ArrayList<>();
        RealmResults<CardDetails> realmResults = myRealm.where(CardDetails.class).findAll();
        myRealm.beginTransaction();
        for (int i = 0; i < realmResults.size(); i++) {
            cardDetailsArrayList.add(realmResults.get(i));
        }
        myRealm.close();
        return cardDetailsArrayList;
    }
}
