package com.comviva.mfs.promotion.util;

import com.comviva.mfs.hce.appserver.util.common.Constants;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by sumit.das on 12/21/2016.
 */
public class ConstantsTest {

    @Test
    public void getValueShouldProvideValueConstantValue(){
        assertThat(Constants.DEFAULT_ENCODING.getValue(), is("UTF-8"));
    }

}