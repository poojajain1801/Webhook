package com.comviva.mfs.promotion.util;

import java.io.*;

/**
 * Contains utility method to serialize or deserialize object.
 * Created by tarkeshwar.v on 2/16/2017.
 */
public class SerializationUtil {
    /**
     * Serializes a given Object into a byte array.
     * @param obj   Object to be serialized
     * @return  Byte Array containing serialized object
     */
    public static byte[] serializeObject(Object obj) {
        byte[] serializedObj = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            serializedObj = bos.toByteArray();
        }  catch (IOException e) {
        } finally {
            try {
                bos.close();
            } catch (IOException ex) { }
        }
        return serializedObj;
    }

    /**
     * Deserializes a given byte stream into Object.
     * @param serializedObj Byte array
     * @return Recovered Object
     */
    public static Object deserialize(byte[] serializedObj) {
        Object deserializedObj =  null;
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedObj);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            deserializedObj = in.readObject();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) { }
        }
        return deserializedObj;
    }

}
