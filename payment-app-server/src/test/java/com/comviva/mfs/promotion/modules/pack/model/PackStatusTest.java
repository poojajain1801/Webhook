package com.comviva.mfs.promotion.modules.pack.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by sumit.das on 12/26/2016.
 */
public class PackStatusTest {
    @Test
    public void getStatusShouldReturnValueForAStatus() throws Exception {
        assertThat(PackStatus.ACTIVE.getStatus(), is("active"));
        assertThat(PackStatus.INACTIVE.getStatus(), is("inactive"));
    }
}