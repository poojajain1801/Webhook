package com.comviva.mfs.promotion.modules.pack.model;

import com.comviva.mfs.promotion.model.error.PropertyErrors;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by sumit.das on 12/27/2016.
 */
public class PackTest {
    private Date startDate;
    private Date endDate;

    @Before
    public void setUp() throws Exception {
        startDate = DateTime.parse("2015-08-09").toDate();
        endDate = DateTime.parse("2015-08-30").toDate();
    }

    @Test
    public void toMapShouldReturnMapWhichContainAllPackDetails() throws Exception {
        Pack pack = Pack.builder().packId("123").packName("my pack").status(PackStatus.ACTIVE).startDate(startDate).endDate(endDate).build();

        Map packMap = pack.toMap();

        assertThat(packMap.get("packId").toString(), is("123"));
        assertThat(packMap.get("packName").toString(), is("my pack"));
        assertThat(packMap.get("status").toString(), is("active"));
        assertThat(packMap.get("startDate").toString(), is("2015-08-09"));
        assertThat(packMap.get("endDate"), is("2015-08-30"));
    }

    @Test
    public void fromMapShouldReturnPackFromMapData() throws Exception {
        Map<String, String> packMap = ImmutableMap.of(
                "packId", "123",
                "packName", "my pack",
                "status", "active",
                "startDate", "2015-08-09",
                "endDate", "2015-08-30"
        );

        Pack pack = Pack.fromMap(packMap);

        assertThat(pack.getPackId(), is("123"));
        assertThat(pack.getPackName(), is("my pack"));
        assertThat(pack.getStatus(), is(PackStatus.ACTIVE));
        assertThat(pack.getStartDate(), is(startDate));
        assertThat(pack.getEndDate(), is(endDate));
    }

    @Test
    public void validateShouldNotThrowErrorWhenPackDataIsProper() throws Exception {
        Pack pack = Pack.builder().packId("123").packName("my pack").status(PackStatus.ACTIVE).startDate(startDate).endDate(endDate).build();

        PropertyErrors errors = pack.validate(new PackConfiguration());

        assertThat(errors.getErrorCount(), is(0));
    }

    @Test
    public void validateShouldThrowErrorWhenPackDataIsImproper() throws Exception {
        Pack pack = Pack.builder().packId("123").packName(null).status(null).startDate(null).endDate(endDate).build();

        PropertyErrors errors = pack.validate(new PackConfiguration());

        assertThat(errors.getErrorCount(), is(3));
        assertThat(Arrays.asList(errors.getFieldError("packName").getCodes()), hasItem("required.packName"));
        assertThat(Arrays.asList(errors.getFieldError("status").getCodes()), hasItem("required.status"));
        assertThat(Arrays.asList(errors.getFieldError("startDate").getCodes()), hasItem("required.startDate"));
    }

}