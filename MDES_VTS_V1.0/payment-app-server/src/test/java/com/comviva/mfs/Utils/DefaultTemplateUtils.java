package com.comviva.mfs.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Random;

/**
 * Created by Tanmaya.Patel.
 *
 */
public class DefaultTemplateUtils {

    private static String strUpperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String strLowerChars = "abcdefghijklmnopqrstuvwxyz";

    private static Random rand = null;


    public static Map<String, Object> buildRequest(String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = DefaultTemplateUtils.class.getResourceAsStream(fileName);
        try {
            return mapper.readValue(is, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String randomString(int len) {
        String str = "";
        for(int i = 0; i < len; ++i) {
            str = str + randomChar(strUpperChars + strLowerChars);
        }
        return str;
    }
    public static char randomChar(String str) {

        if(rand == null) {
            rand = new Random();
        }

int i = rand.nextInt(str.length());
        return str.charAt(i);
    }

}
