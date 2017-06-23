/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.mastercard.mobile_api.utils.json;

import com.mastercard.mobile_api.bytes.ByteArray;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Generic utility class to convert to and fro from JSON.
 *
 * @param <T> Class for which we need to perform serialization or deserialization
 */
public class JsonUtils<T> {
    /**
     * Reference type for which we are performing this JSON to and fro operation
     */
    private Class classType;

    public JsonUtils(Class aClass) {
        classType = aClass;
    }

    /**
     * Convert byte array representation of JSON string to instance type.
     *
     * @param content bytes representation of JSON string which needs to convert to required
     *                object type
     * @return Reference type represented by 'T'
     */
    public T valueOf(byte[] content) {
        Reader bfReader = new InputStreamReader(new ByteArrayInputStream(content));
        return new JSONDeserializer<T>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .use(byte.class, new ByteObjectFactory())
                .deserialize(bfReader, classType);
    }

    /**
     * Get Json data of given instance
     *
     * @param instance Instance which needs to convert into json
     * @return Object to json string conversion.
     */
    public String toJsonString(T instance) {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        // ByteArray serialization
        serializer.transform(new com.mastercard.mobile_api.utils.json.ByteArrayTransformer(),
                             ByteArray.class);
        serializer.transform(new ByteTransformer(), byte.class);
        // Skip null values
        serializer.transform(new com.mastercard.mobile_api.utils.json.SuppressNullTransformer(),
                             void.class);
        return serializer.serialize(instance);
    }

    /**
     * Convert JSON representation of string array to String array
     *
     * @param json JSON representation of String array
     * @return String array.
     */
    public static String[] deserializeStringArray(String json) {
        String[] objects = json.substring(json.indexOf("[") + 1)
                               .substring(0, json.lastIndexOf("]") - 1).split("\\},\\{");

        objects[0] += "}";
        for (int i = 1; i < objects.length - 1; i++) {
            objects[i] = "{" + objects[i];
            objects[i] += "}";
        }

        objects[objects.length - 1] = "{" + objects[objects.length - 1];

        if (objects.length == 1) {
            objects[0] = objects[0].substring(1, objects[0].length() - 1);
        }

        return objects;
    }

    /**
     * Get Json data of given instance
     *
     * @param object   Instance which needs to convert into json
     * @param rootName required root name for the resulting JSON
     * @return equivalent json string
     */
    public static String serializeObjectWithByteArray(Object object, String rootName) {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        // ByteArray serialization
        serializer.transform(new com.mastercard.mobile_api.utils.json.ByteArrayTransformer(),
                             ByteArray.class);
        serializer.transform(new ByteTransformer(), Byte.class);
        serializer.rootName(rootName);
        return serializer.serialize(object);
    }

}
