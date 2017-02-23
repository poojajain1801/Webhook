package com.comviva.mfs.promotion.authentication.strategy;

import com.comviva.mfs.promotion.authentication.AuthenticationMode;
import com.comviva.mfs.promotion.exception.UnsupportedAuthenticationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sumit.das on 12/23/2016.
 */
public class AuthenticationStrategyFactoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Environment environment;

    private AuthenticationStrategyFactory authenticationStrategyFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        authenticationStrategyFactory = new AuthenticationStrategyFactory(environment);
    }

    @Test
    public void createShouldReturnNoAuthenticationStrategyWhenTheAuthenticationModeIsNone() throws Exception {
        when(environment.getProperty("authentication.mode", AuthenticationMode.NONE.getAuthenticationMode())).thenReturn("none");

        AuthenticationStrategy authenticationStrategy = authenticationStrategyFactory.create();

        assertThat(authenticationStrategy, instanceOf(NoAuthenticationStrategy.class));
    }

    @Test
    public void createShouldReturnJWTStrategyAuthenticationWhenTheAuthenticationModeIsJWT() throws Exception {
        when(environment.getProperty("authentication.mode", AuthenticationMode.NONE.getAuthenticationMode())).thenReturn("jwt");

        AuthenticationStrategy authenticationStrategy = authenticationStrategyFactory.create();

        assertThat(authenticationStrategy, instanceOf(JwtAuthenticationStrategy.class));
    }


    @Test
    public void createShouldThrowExceptionIsSomethingOtherThanNoneAndJwtIsConfigured() throws Exception {
        when(environment.getProperty("authentication.mode", AuthenticationMode.NONE.getAuthenticationMode())).thenReturn("foo");
        expectedException.expect(UnsupportedAuthenticationException.class);

        authenticationStrategyFactory.create();
    }
}