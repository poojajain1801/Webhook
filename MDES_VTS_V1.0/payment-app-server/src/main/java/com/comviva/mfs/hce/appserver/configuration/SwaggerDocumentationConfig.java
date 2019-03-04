package com.comviva.mfs.hce.appserver.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Created by Tanmay.Patel on 12/20/2016.
 */

@Configuration
@EnableSwagger2
@PropertySource("classpath:application.properties")
@ComponentScan
@ConditionalOnProperty(name = "swagger.document.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerDocumentationConfig {
    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.comviva.mfs.hce.appserver"))
                .paths(regex("/api.*"))
                .build();
    }
}
