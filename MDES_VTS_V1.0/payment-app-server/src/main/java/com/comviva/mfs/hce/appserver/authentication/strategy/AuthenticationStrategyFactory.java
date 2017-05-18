package com.comviva.mfs.hce.appserver.authentication.strategy;

import com.comviva.mfs.hce.appserver.authentication.AuthenticationMode;
import com.comviva.mfs.hce.appserver.exception.UnsupportedAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by sumit.das on 12/22/2016.
 */
@Component
public class AuthenticationStrategyFactory {
    private final Environment environment;

    @Autowired
    public AuthenticationStrategyFactory(Environment environment) {
        this.environment = environment;
    }

    public AuthenticationStrategy create() {
        String configuredAuthenticationMode = environment.getProperty("authentication.mode", AuthenticationMode.NONE.getAuthenticationMode());
        switch (configuredAuthenticationMode) {
            case "none":
                return new NoAuthenticationStrategy();
            case "jwt":
                return new JwtAuthenticationStrategy(environment);
            default:
                throw new UnsupportedAuthenticationException(String.format("Authentication Mode [%s] is not supported", configuredAuthenticationMode));
        }
    }
}
