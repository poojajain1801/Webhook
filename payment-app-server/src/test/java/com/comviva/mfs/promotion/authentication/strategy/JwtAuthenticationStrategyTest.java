package com.comviva.mfs.promotion.authentication.strategy;

import com.comviva.mfs.promotion.authentication.User;
import com.comviva.mfs.promotion.builder.UserBuilder;
import com.comviva.mfs.promotion.exception.AuthenticationFailedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.comviva.mfs.promotion.authentication.strategy.JwtAuthenticationStrategy.AUTHORIZATION_HEADER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by sumit.das on 12/22/2016.
 */
public class JwtAuthenticationStrategyTest {
    public static final String KEY = "Yd5bFKbdvUWPS4uasRB4UE62Mnh9Jb4ablSNZkYJ5dXnjYluNCOL1F0Wf+PuO0Jm3Jq50s9teyT0DXZKzgSgkA==";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    Environment environment;

    private JwtAuthenticationStrategy jwtAuthenticationStrategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        jwtAuthenticationStrategy = new JwtAuthenticationStrategy(environment);

        when(environment.getProperty("authentication.jwt.secret")).thenReturn(KEY);
        SecurityContextHolder.clearContext();
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void authenticateShouldReturnNullWhenAuthorizationIsNull() throws Exception {
        when(httpServletRequest.getParameter(AUTHORIZATION_HEADER)).thenReturn(null);

        Authentication authentication = jwtAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication, is(nullValue()));
    }

    @Test
    public void authenticateShouldReturnNullWhenAuthorizationIsEmpty() throws Exception {
        when(httpServletRequest.getParameter(AUTHORIZATION_HEADER)).thenReturn("");

        Authentication authentication = jwtAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication, is(nullValue()));
    }

    @Test
    public void authenticateShouldReturnUserWithDetailsReadFromJWT() throws Exception {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", "8989-9090");
        claims.put("username", "jane.doe");
        claims.put("displayname", "Jane Doe");
        claims.put("permissions", Arrays.asList("BUY", "SUBSCRIBE"));
        String jwt = generateJWT(claims);
        String authorizationHeader = String.format("Bearer:%s", jwt);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(authorizationHeader);

        Authentication authenticatedUser = jwtAuthenticationStrategy.authenticate(httpServletRequest);

/*        assertThat(authenticatedUser, not(nullValue()));
        assertThat(authenticatedUser.getId(), is("8989-9090"));
        assertThat(authenticatedUser.getUsername(), is("jane.doe"));
        assertThat(authenticatedUser.getDisplayName(), is("Jane Doe"));
        assertThat(authenticatedUser.getAuthorities(), is(AuthorityUtils.createAuthorityList("BUY", "SUBSCRIBE")));*/
    }

    private String generateJWT(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, KEY.getBytes()).compact();
    }

    @Test
    public void authenticateShouldReturnNullWhenJWTIsInInvalidFormat() throws Exception {
        when(httpServletRequest.getParameter(AUTHORIZATION_HEADER)).thenReturn("invalid.token");

        Authentication authentication = jwtAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication, is(nullValue()));
    }

    @Test
    public void authenticateShouldCreateNewAuthenticationIfExistsAndSameTokenIsPassedInRequest() throws Exception {
        User user1 = new UserBuilder().withStubData().build();
        String user1Token = generateJWT(user1.toClaimDetails());
        UsernamePasswordAuthenticationToken existingAuthentication = new UsernamePasswordAuthenticationToken(user1, user1Token);
        String authorizationHeader = String.format("Bearer:%s", user1Token);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(authorizationHeader);
        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

        Authentication authentication = jwtAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication, is(existingAuthentication));
    }

    @Test
    public void authenticateShouldReturnNewAuthenticationIfExistsAndDifferentJWTIsPassedInRequest() throws Exception {
        User user1 = new UserBuilder().withStubData().setUsername("john.doe").build();
        String user1JWT = generateJWT(user1.toClaimDetails());
        UsernamePasswordAuthenticationToken user1Authentication = new UsernamePasswordAuthenticationToken(user1, user1JWT);
        User user2 = new UserBuilder().withStubData().setUsername("foo.bar").build();
        String user2JWT = generateJWT(user2.toClaimDetails());
        String authorizationHeader = String.format("Bearer:%s", user2JWT);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(authorizationHeader);
        SecurityContextHolder.getContext().setAuthentication(user1Authentication);

        Authentication authentication = jwtAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication, not(user1Authentication));
        User user = (User) authentication.getPrincipal();
        assertThat(user.getUsername(), is(user2.getUsername()));
    }

    @Test
    public void authenticateShouldReturnNullWhenTokenIsExpired() throws Exception {
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS512, KEY.getBytes())
                .setExpiration(DateUtils.addSeconds(new Date(), -1))
                .compact();
        String authHeader=String.format("Bearer:%s", token);
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(authHeader);
        expectedException.expect(AuthenticationFailedException.class);

        Authentication authentication = jwtAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication, is(nullValue()));
    }

    @Test
    public void authenticateShouldReturnNullWhenHeaderDoesNotContainAnyAuthorizationAmount() throws Exception {
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        Authentication authentication = jwtAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication, is(nullValue()));
    }


    @Test
    public void authenticateShouldReturnNullWhenHeaderDoesNotContainJWTInformation() throws Exception {
        String authHeader=String.format("Bearer:");
        when(httpServletRequest.getHeader(AUTHORIZATION_HEADER)).thenReturn(authHeader);

        Authentication authentication = jwtAuthenticationStrategy.authenticate(httpServletRequest);

        assertThat(authentication, is(nullValue()));
    }
}