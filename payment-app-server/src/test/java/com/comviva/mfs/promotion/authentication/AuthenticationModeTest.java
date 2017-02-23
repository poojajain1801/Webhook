package com.comviva.mfs.promotion.authentication;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by sumit.das on 12/22/2016.
 */
public class AuthenticationModeTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getAuthenticationModeShouldReturnModeOfAuthentication() throws Exception {
        assertThat(AuthenticationMode.NONE.getAuthenticationMode(), is("none"));
        assertThat(AuthenticationMode.JWT.getAuthenticationMode(), is("jwt"));
    }

    @Test
    public void getAuthenticationModeShouldThrowErrorWhenInvalidValidIsProvidedToGetAuthenticationMode() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        AuthenticationMode.valueOf("foo");
    }
}