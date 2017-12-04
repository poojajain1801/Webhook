package com.comviva.mfs.hce.appserver.authentication;

import com.comviva.mfs.hce.appserver.authentication.listener.AuthenticationListener;
import com.comviva.mfs.hce.appserver.authentication.strategy.AuthenticationStrategy;
import com.comviva.mfs.hce.appserver.authentication.strategy.AuthenticationStrategyFactory;
import com.comviva.mfs.hce.appserver.exception.AuthenticationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);
    private final AuthenticationStrategyFactory authenticationStrategyFactory;
    private final List<AuthenticationListener> authenticationListeners;

    public AuthenticationFilter(AuthenticationStrategyFactory authenticationStrategyFactory, List<String> noAuthUrls, List<AuthenticationListener> authenticationListeners) {
        this.authenticationStrategyFactory = authenticationStrategyFactory;
        this.authenticationListeners = authenticationListeners;
        this.setRequiresAuthenticationRequestMatcher(new NegatedRequestMatcher(new OrRequestMatcher(noAuthUrls.stream().map(AntPathRequestMatcher::new).collect(Collectors.toList()))));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            if (requiresAuthentication(httpRequest, httpResponse)) {
                Authentication previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
                AuthenticationStrategy authenticationStrategy = authenticationStrategyFactory.create();
                LOGGER.debug("Authenticating user with strategy : {} for url {}", authenticationStrategy, httpRequest.getRequestURI());
                Authentication newAuthentication = authenticationStrategy.authenticate(httpRequest);
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
                for (AuthenticationListener authenticationListener : authenticationListeners) {
                    authenticationListener.onAuthentication(previousAuthentication, newAuthentication, httpRequest);
                }
            }
            chain.doFilter(request, response);
            SecurityContextHolder.getContext().setAuthentication(null);
        } catch (AuthenticationFailedException e) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}