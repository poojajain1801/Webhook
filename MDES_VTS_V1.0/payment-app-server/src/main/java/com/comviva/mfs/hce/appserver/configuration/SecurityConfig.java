/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 * <p/>
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 * <p/>
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.configuration;


import com.comviva.mfs.hce.appserver.authentication.AuthenticationFilter;
import com.comviva.mfs.hce.appserver.authentication.EntryPointUnauthorizedHandler;
import com.comviva.mfs.hce.appserver.authentication.listener.AuthenticationListener;
import com.comviva.mfs.hce.appserver.authentication.listener.SessionFixationProtectionListener;
import com.comviva.mfs.hce.appserver.authentication.strategy.AuthenticationStrategyFactory;
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
