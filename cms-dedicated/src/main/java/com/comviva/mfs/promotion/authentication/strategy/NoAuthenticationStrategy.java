package com.comviva.mfs.promotion.authentication.strategy;

import com.comviva.mfs.promotion.authentication.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class NoAuthenticationStrategy implements AuthenticationStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoAuthenticationStrategy.class);

    @Override
    public Authentication authenticate(HttpServletRequest httpRequest) {
        LOGGER.debug("Authentication is not enabled for this request. Logged in as anonymous user");
        User user = User.anonymousUser();
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}
