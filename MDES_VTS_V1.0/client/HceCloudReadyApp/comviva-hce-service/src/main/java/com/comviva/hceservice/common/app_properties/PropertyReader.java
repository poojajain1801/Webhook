package com.comviva.hceservice.common.app_properties;


import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {
    private AssetManager assetManager;
    private static PropertyReader propertyReader;

    private PropertyReader(Context context) {
        assetManager = context.getAssets();
    }

    public static PropertyReader getInstance(Context context) {
        if(propertyReader == null) {
            propertyReader = new PropertyReader(context);
        }
        return propertyReader;
    }

    public String getProperty(String key) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open(PropertyConst.COMVIVA_HCE_PROPERTY_FILE);
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (IOException e) {
        }
        return null;
    }
}
