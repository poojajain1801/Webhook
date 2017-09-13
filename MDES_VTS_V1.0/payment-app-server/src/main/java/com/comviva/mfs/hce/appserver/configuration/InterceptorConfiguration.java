package com.comviva.mfs.hce.appserver.configuration;

import com.comviva.mfs.hce.appserver.interceptors.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by shadab.ali on 29-08-2017.
 */
@Component
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    RequestInterceptor requestInterceptor;

    @Override
public void addInterceptors(InterceptorRegistry registry) {

  registry.addInterceptor(requestInterceptor);
}
}
