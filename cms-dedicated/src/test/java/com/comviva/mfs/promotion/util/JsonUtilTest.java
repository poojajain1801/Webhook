package com.comviva.mfs.promotion.util;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

/**
 * Created by sumit.das on 12/22/2016.
 */
public class JsonUtilTest {

    @Test
    public void toJsonShouldReturnStringWhenAnObjectIsPassed() throws Exception {
        String expectedData = "{'id':'123', 'name':'foo'}";
        Map testData = ImmutableMap.of(
                "id", "123",
                "name", "foo"
        );

        String jsonString = JsonUtil.toJson(testData);

        assertEquals(jsonString, expectedData, true);
    }

    @Test
    public void fromJsonShouldReturnExpectedObjectFromJsonString() throws Exception {
        String testData = "{\"id\":\"123\", \"name\":\"foo\"}";
        Map expectedData = ImmutableMap.of(
                "id", "123",
                "name", "foo"
        );

        Map mapFromJson = JsonUtil.fromJson(testData, Map.class);

        assertThat(mapFromJson, is(expectedData));
    }

    @Test
    public void fromJsonShouldReadInputStreamAndReturnExpectedObject() throws Exception {
        InputStream testData = getInputStream();
        Map expectedData = ImmutableMap.of(
                "id", "123",
                "name", "foo"
        );
        Map mapFromJson = JsonUtil.fromJson(testData, Map.class);

        assertThat(mapFromJson, is(expectedData));
    }

    private InputStream getInputStream() throws IOException {
        File initialFile = new File("src/test/resources/test.json");
        InputStream targetStream = new FileInputStream(initialFile);
        return targetStream;

    }
}