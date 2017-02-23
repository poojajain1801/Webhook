package com.comviva.mfs.promotion.modules.device_management.service.contract;

import java.io.InputStream;

/**
 * Created by Tanmay.Patel on 1/20/2017.
 */
public interface HttpClint {
    String postHttpRequest(byte[] requestData,String url);
    String convertStreamToString (InputStream inStream)throws Exception;
}
