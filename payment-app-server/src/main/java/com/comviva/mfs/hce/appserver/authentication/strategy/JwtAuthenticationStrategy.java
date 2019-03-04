package com.comviva.mfs.hce.appserver.authentication.strategy;

import com.comviva.mfs.hce.appserver.authentication.User;
import com.comviva.mfs.hce.appserver.exception.AuthenticationFailedException;
import com.comviva.mfs.hce.appserver.util.common.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by sumit.das on 12/21/2016.
 */
@Configuration
public class JwtAuthenticationStrategy implements AuthenticationStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationStrategy.class);
    public static final String AUTHORIZATION_HEADER = "X-AUTH-TOKEN";

    private final Environment environment;

    @Autowired
    public JwtAuthenticationStrategy(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Authentication authenticate(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            return null;
        }

        final String jwt = authHeader.substring(7);
        if (jwt.isEmpty()) {
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && (isBlank(jwt) || Objects.equals(authentication.getCredentials(), jwt))) {
            return authentication;
        } else {
            User user = getAuthenticatedUserFromToken(jwt);
            return user == null ? null : new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());
        }
    }

    private User getAuthenticatedUserFromToken(String token) {
        if (isBlank(token)) {
            LOGGER.debug("Can not find a token for authenticating user");
            return null;
        }
        try {
            String jwtSecretKey = environment.getProperty("authentication.jwt.secret");
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecretKey.getBytes(Charsets.toCharset(Constants.DEFAULT_ENCODING.getValue())))
                    .parseClaimsJws(token)
                    .getBody();
            return User.fromMap(claims);
        } catch (JwtException e) {
            LOGGER.error(String.format("Invalid JWT token : %s", token), e);
            throw new AuthenticationFailedException(e);
        }
    }
}
