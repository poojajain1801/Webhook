package com.comviva.mfs.promotion.config;

import com.comviva.mfs.promotion.authentication.AuthenticationFilter;
import com.comviva.mfs.promotion.authentication.EntryPointUnauthorizedHandler;
import com.comviva.mfs.promotion.authentication.listener.AuthenticationListener;
import com.comviva.mfs.promotion.authentication.listener.SessionFixationProtectionListener;
import com.comviva.mfs.promotion.authentication.strategy.AuthenticationStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by sumit.das on 12/21/2016.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    private static final String[] NO_AUTH_URLS = new String[]{
            "/error", "/", "/static/**", "/assets/**", "/stub/**", "/favicon.ico"
    };

    private final AuthenticationStrategyFactory authenticationStrategyFactory;
    private final EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
    private final SessionFixationProtectionListener sessionFixationProtectionListener;

    @Autowired
    public SecurityConfig(AuthenticationStrategyFactory authenticationStrategyFactory, EntryPointUnauthorizedHandler entryPointUnauthorizedHandler, SessionFixationProtectionListener sessionFixationProtectionListener) {
        this.authenticationStrategyFactory = authenticationStrategyFactory;
        this.entryPointUnauthorizedHandler = entryPointUnauthorizedHandler;
        this.sessionFixationProtectionListener = sessionFixationProtectionListener;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.exceptionHandling().authenticationEntryPoint(this.entryPointUnauthorizedHandler)
                .and().anonymous().and().servletApi().and().headers().cacheControl().and().xssProtection()
                .and().httpStrictTransportSecurity();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                .antMatchers(NO_AUTH_URLS).permitAll()
                .and().authorizeRequests().anyRequest().fullyAuthenticated()
                .and().addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    private AuthenticationFilter authenticationFilter() throws Exception {
        List<AuthenticationListener> authenticationListeners = Arrays.asList(sessionFixationProtectionListener);
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationStrategyFactory, Arrays.asList(NO_AUTH_URLS), authenticationListeners);
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationFilter;
    }
}
