package com.comviva.hceservice.common.app_properties;


import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read property value from property file.
 */
public class PropertyReader {
    private AssetManager assetManager;
    private static PropertyReader propertyReader;

    private PropertyReader(Context context) {
        assetManager = context.getAssets();
    }

    /**
     * Creates singleton instance of this class.
     * @param context   Current Application Contect
     * @return Instance of PropertyReader
     */
    public static PropertyReader getInstance(Context context) {
        if(propertyReader == null) {
            propertyReader = new PropertyReader(context);
        }
        return propertyReader;
    }

    /**
     * Reads property value from property file.
     * @param key   Key to fetch value.
     * @return Value of given key
     */
    public String getProperty(String key , String fileName) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = assetManager.open(fileName);
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (IOException e) {
        }
        return null;
    }

    public String getToastMessage(String key) {
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
