package com.comviva.mfs.promotion.modules.mobilepaymentapi.Constants;

/**
 * Created by Tanmay.Patel on 4/21/2017.
 */
public  class Reqconstants {
    public static final String rephineshRequest =  "{\n" +
            "\"requestId\" : \"500000000001\",\n" +
            "\"tokenUniqueReference\" : \"DWSPMC000000000132d72d4fcb2f4136a0532d3093ff1a45\",\n" +
            "\"transactionCredentialsStatus\" : [\n" +
            "{\n" +
            "\"atc\" : 1,\n" +
            "\"status\" : \"UNUSED_DISCARDED\",\n" +
            "\"timestamp\" : \"2015-03-02T09:00:00Z\"\n" +
            "},\n" +
            "{\n" +
            "\"atc\" : 2,\n" +
            "\"status\" : \"USED_FOR_CONTACTLESS\",\n" +
            "\"timestamp\" : \"2015-03-05T11:45:04Z\"\n" +
            "},\n" +
            "{\n" +
            "\"atc\" : 3,\n" +
            "\"status\" : \"USED_FOR_DSRP\",\n" +
            "\"timestamp\" : \"2015-03-05T11:50:55Z\"\n" +
            "},\n" +
            "{\n" +
            "\"atc\" : 4,\n" +
            "\"status\" : \"UNUSED_ACTIVE\",\n" +
            "\"timestamp\" : \"2015-03-06T03:30:15Z\"\n" +
            "}\n" +
            "]\n" +
            "}";
    public static final String setPinReq = "{\n" +
            "\"requestId\" : \"6000000001\",\n" +
            "\"taskId\" : \"123456\",\n" +
            "\"newMobilePin\" :   \"4456782198765432109876827382028F\"\n" +
            "}";
    public static final String changeMobilePin = "{\n" +
            "\"requestId\" : \"6000000001\",\n" +
            "\"taskId\" : \"123456\",\n" +
            "\"currentMobilePin\":\"4412342198765432109876827382028F\",\n" +
            "\"newMobilePin\" :   \"4456782198765432109876827382028F\"\n" +
            "}";

}
