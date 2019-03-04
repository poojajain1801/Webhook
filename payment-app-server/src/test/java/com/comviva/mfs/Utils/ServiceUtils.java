package com.comviva.mfs.Utils;

import com.comviva.mfs.hce.appserver.util.mdes.DeviceRegistrationMdes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by girish.desai on 4/30/2016.
 */
public class ServiceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUtils.class);
    private static MockMvcRequestSpecification request;
    private static String endPoint;

    public static void serviceInit(String endPoint) {
        ServiceUtils.endPoint = endPoint;
    }

    public static void assertResponse(Map mapResponse, String value) {
        assertNotNull(value);
       // assertNotNull(mapResponse.get("response"));
        assertThat(mapResponse.get("responseCode"), equalTo(value));
    }

   /* public static void assertStatusResponse(Map mapResponse, Integer value) {
        assertNotNull(value);
        assertThat(mapResponse.get(JSONConst.STATUS), equalTo(value));
    }*/

    public static Map assertPOSTService(String command, Map mapRequest, String value) {
        Map response = servicePOSTResponse(command, mapRequest);
        assertResponse(response, value);
        return response;
    }

   /* public static Map assertPOSTService(String command, Map mapRequest, Integer status) {
        Map response = servicePOSTResponse(command, mapRequest);
        assertStatusResponse(response, status);
        return response;
    }
*/
    public static Map assertPUTService(String command, Map mapRequest, String value) {
        Map response = servicePUTResponse(command, mapRequest);
        assertResponse(response, value);
        return response;
    }

    public static Map assertGETService(String command, String key1, String key2, String input1, String input2, String value) {
        Map response = serviceGETResponse(command, key1, key2, input1, input2);
        assertResponse(response, value);
        return response;
    }

    public static Map assertGETService(String command, String key1, String key2, String key3,
                                       String input1, String input2, String input3, String value) {
        Map response = serviceGETResponse(command, key1, key2, key3, input1, input2, input3);
        assertResponse(response, value);
        return response;
    }

    public static Map assertGETService(String command, String key, String input, String value) {
        Map response = serviceGETResponse(command, key, input);
        assertResponse(response, value);
        return response;
    }

    public static Map assertGETService(String command, String value) {
        Map response = serviceGETResponse(command, null, null);
        assertResponse(response, value);
        return response;
    }

    public static Map assertDELETEService(String command, String key, String input, String value) {
        Map response = serviceDELETEResponse(command, key, input);
        assertResponse(response, value);
        return response;
    }

    public static Map assertDELETEService(String command, String key1, String key2, String input1, String input2, String value) {
        Map response = serviceDELETEResponse(command, key1, key2, input1, input2);
        assertResponse(response, value);
        return response;
    }

    public static Map servicePOSTResponse(String command, Map mapRequest) {
        request = given().contentType(ContentType.JSON);
        Map mapResponse = request.body(toJson(mapRequest)).log().all()
                .post(endPoint + "/" + command).getBody().as(Map.class);
        return mapResponse;
    }

    public static Map servicePUTResponse(String command, Map mapRequest) {
        request = given().contentType(ContentType.JSON);
        Map mapResponse = request
                .body(toJson(mapRequest))
                .put(endPoint + "/" + command).getBody().as(Map.class);
        return mapResponse;
    }

    public static Map serviceGETResponse(String command, String key, String value) {
        if (null == key) {
            return given().accept(ContentType.JSON).get(endPoint + "/" + command).getBody().as(Map.class);
        }
        return given().accept(ContentType.JSON).get(endPoint + "/" + command + "?" + key + "=" + value).getBody().as(Map.class);
    }

    public static Map serviceGETResponse(String command, String key1, String key2, String input1, String input2) {
        return given().accept(ContentType.JSON).get(endPoint + "/" + command + "?" + key1 + "=" + input1 + "&" + key2 + "=" + input2).getBody().as(Map.class);
    }

    public static Map serviceGETResponse(String command, String key1, String key2, String key3, String input1, String input2, String input3) {
        return given().accept(ContentType.JSON).get(endPoint + "/" + command + "?" + key1 + "=" + input1 + "&" + key2 + "=" + input2 + "&" + key3 + "=" + input3).getBody().as(Map.class);
    }

    public static Map serviceDELETEResponse(String command, String key, String value) {
        return given().accept(ContentType.JSON).delete(endPoint + "/" + command + "?" + key + "=" + value).getBody().as(Map.class);
    }

    public static Map serviceDELETEResponse(String command, String key1, String key2, String input1, String input2) {
        return given().accept(ContentType.JSON).delete(endPoint + "/" + command + "?" + key1 + "=" + input1 + "&" + key2 + "=" + input2).getBody().as(Map.class);
    }

    public static int serviceGETValidateBadRequest(String command, String key, String value) {
        return given().accept(ContentType.JSON).get(endPoint + "/" + command + "?" + key + "=" + value).getStatusCode();
    }

    public static String toJson(Object obj, SerializationFeature... serializationFeatures) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectWriter e = objectMapper.writer().withFeatures(serializationFeatures);
            return e.writeValueAsString(obj);
        }catch (JsonProcessingException var3) {
            LOGGER.error("Exception occured",var3);
            return null;
        }
    }
}
