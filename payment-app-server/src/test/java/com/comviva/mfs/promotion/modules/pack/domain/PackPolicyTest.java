package com.comviva.mfs.promotion.modules.pack.domain;

import com.comviva.mfs.promotion.builder.PackPolicyBuilder;
import com.comviva.mfs.promotion.model.error.PropertyErrors;
import com.comviva.mfs.promotion.modules.pack.model.Pack;
import com.comviva.mfs.promotion.modules.pack.model.PackConfiguration;
import com.comviva.mfs.promotion.modules.pack.model.PackStatus;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.validation.Errors;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by sumit.das on 12/27/2016.
 */
public class PackPolicyTest {
    @Test
    public void validateShouldReturnZeroErrorsForValidData() throws Exception {
        PackPolicy packPolicy = new PackPolicyBuilder().setId("1").setType("Promo Pack").setPacks(Arrays.asList()).build();

        Errors errors = packPolicy.validate(new PackConfiguration());

        assertThat(errors.hasErrors(), is(false));
    }

    @Test
    public void validateShouldThrowErrorWhenPackPolicyDataIsImproper() throws Exception {
        Pack pack = Pack.builder().packId("123").packName(null).status(PackStatus.ACTIVE).startDate(DateTime.parse("2015-08-09").toDate()).build();
        PackPolicy packPolicy = new PackPolicyBuilder().setId("111").setType(null).setPacks(Arrays.asList(pack)).build();

        PropertyErrors errors = packPolicy.validate(new PackConfiguration());

        assertThat(errors.getErrorCount(), CoreMatchers.is(2));
        assertThat(Arrays.asList(errors.getFieldError("type").getCodes()), hasItem("required.type"));
        assertThat(Arrays.asList(errors.getFieldError("packs[0].packName").getCodes()), hasItem("required.packName"));
    }
}