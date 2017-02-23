package com.comviva.mfs.promotion.authentication;

import com.comviva.mfs.promotion.authentication.listener.AuthenticationListener;
import com.comviva.mfs.promotion.authentication.strategy.AuthenticationStrategy;
import com.comviva.mfs.promotion.authentication.strategy.AuthenticationStrategyFactory;
import com.comviva.mfs.promotion.authentication.strategy.JwtAuthenticationStrategy;
import com.comviva.mfs.promotion.builder.UserBuilder;
import com.comviva.mfs.promotion.exception.AuthenticationFailedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sumit.das on 12/23/2016.
 */
public class AuthenticationFilterTest {

    @Mock
    AuthenticationStrategyFactory authenticationStrategyFactory;

    @Mock
    AuthenticationStrategy authenticationStrategy;

    @Mock
    JwtAuthenticationStrategy jwtAuthenticationStrategy;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @Mock
    AuthenticationListener authenticationListener1;

    @Mock
    AuthenticationListener authenticationListener2;


    private AuthenticationFilter authenticationFilter;
    private User user1;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        List<String> noAuthUrls = Arrays.asList("/css/**");
        authenticationFilter = new AuthenticationFilter(authenticationStrategyFactory, noAuthUrls, Arrays.asList(authenticationListener1, authenticationListener2));
        when(request.getServletPath()).thenReturn("");
        when(request.getPathInfo()).thenReturn("/");
        when(authenticationStrategyFactory.create()).thenReturn(authenticationStrategy);
        user1 = new UserBuilder().withStubData().build();
        SecurityContextHolder.clearContext();
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void doFilterShouldSkipAuthenticationAndContinueFilterChainForNoAuthUrl() throws Exception {
        when(request.getPathInfo()).thenReturn("/css/style.css");

        authenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyZeroInteractions(authenticationStrategyFactory, authenticationStrategy);
        verifyZeroInteractions(authenticationListener1, authenticationListener2);
    }

    @Test
    public void doFilterShouldSetAuthenticationInSecurityContextAsPerAuthStrategyAndShouldUnsetAuthorizationOnceRequestIsProceces() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user1, "t1");
        when(authenticationStrategy.authenticate(request)).thenReturn(authentication);

        authenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication(), is(nullValue()));
    }


    @Test
    public void doFilterShouldSetResponseResponseStatusAsUnauthorized() throws Exception {
        when(authenticationStrategy.authenticate(request)).thenThrow(AuthenticationFailedException.class);

        authenticationFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void doFilterShouldCallAuthListenersOnAuthentication() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user1, "t1");
        when(authenticationStrategy.authenticate(request)).thenReturn(authentication);

        authenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}