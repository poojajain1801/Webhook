package com.comviva.mfs.promotion.model.error;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.MapUtils;
import org.junit.Test;

import java.util.Map;

import static org.apache.commons.collections4.MapUtils.getString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by sumit.das on 12/24/2016.
 */
public class ErrorDescriptionTest {
    @Test
    public void toMapShouldReturnErrorDescription() throws Exception {
        ErrorDescription errorDescription = new ErrorDescriptionBuilder()
                .setMessage("Name can not be empty field")
                .setField("login.username")
                .setCode("login.emptyName")
                .build();

        Map map = errorDescription.toMap();

        assertThat(getString(map, "field"), is("login.username"));
        assertThat(getString(map, "code"), is("login.emptyName"));
        assertThat(getString(map, "message"), is("Name can not be empty field"));
    }

    @Test
    public void toMapShouldNotIncludeFieldDetailsWhenFieldIsSetToNull() throws Exception {
        ErrorDescription errorDescription = new ErrorDescriptionBuilder()
                .stubData()
                .setField(null)
                .build();

        Map map = errorDescription.toMap();

        assertThat(map.containsKey("field"), is(false));
    }
}