package com.comviva.mfs.promotion.authentication.strategy;

import com.comviva.mfs.promotion.authentication.User;
import com.comviva.mfs.promotion.builder.UserBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sumit.das on 12/23/2016.
 */
public class NoAuthenticationStrategyTest {

    @Mock
    HttpServletRequest httpServletRequest;

    private NoAuthenticationStrategy noAuthenticationStrategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        noAuthenticationStrategy = new NoAuthenticationStrategy();
    }

    @Test
    public void authenticateShouldReturnUserWithDetails() throws Exception {
        User user = new UserBuilder().anonymousUser().build();
        UsernamePasswordAuthenticationToken existingAuthentication = new UsernamePasswordAuthenticationToken(user, null,user.getAuthorities());

        Authentication authentication = noAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication, is(existingAuthentication));

    }

    @Test
    public void authenticateShouldReturnSameAuthenticationWhenCalledMultipleTimes() throws Exception {
        Authentication authentication1 = noAuthenticationStrategy.authenticate(httpServletRequest);
        Authentication authentication2 = noAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication2, equalTo(authentication1));
    }

}