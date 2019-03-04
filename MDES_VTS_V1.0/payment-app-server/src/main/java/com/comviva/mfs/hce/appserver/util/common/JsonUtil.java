/*******************************************************************************
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package com.comviva.mfs.hce.appserver.util.common;

import com.comviva.mfs.hce.appserver.exception.ApplicationException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.hibernate.internal.util.collections.ArrayHelper.toList;

public class JsonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtil() {
    }

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toJson(Object obj, SerializationFeature... serializationFeatures) {
        try {
            ObjectWriter writer = OBJECT_MAPPER.writer().withFeatures(serializationFeatures);
            return writer.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("Exception occurred during converting an object to Json", e);
            throw new ApplicationException(e);
        }
    }

    public static <T> T fromJson(String string, Class<T> valueType) {
        try {
            return StringUtils.isBlank(string) ? null : OBJECT_MAPPER.readValue(string, valueType);
        } catch (IOException e) {
            LOGGER.error("Exception occurred during creating object from json string", e);
            throw new ApplicationException(e);
        }
    }

    public static <T> T fromJson(InputStream inputStream, Class<T> valueType) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, valueType);
        } catch (IOException e) {
            LOGGER.error("Exception occurred during creating object from input stream", e);
            throw new ApplicationException(e);
        }
    }

    public static Map<String,Object> jsonStringToHashMap(String response)
    {
        HashMap<String,Object> result =null;
        try {

            result =   new ObjectMapper().readValue(response, HashMap.class);
        } catch (IOException e) {
            LOGGER.error("Exception Occured" +e);
        }
        return result;
    }

    public static Map<String, Object> jsonToMap(JSONObject object) throws JSONException {

        if (null == object){
            LOGGER.error("Null object passed");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static ArrayList<Object> toList(JSONArray array) throws JSONException {
        ArrayList<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = jsonToMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}

