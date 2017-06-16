package com.comviva.mfs.promotion.config;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ExposedResourceMessageBundleSource extends ReloadableResourceBundleMessageSource {

    public Map getMessages(Locale locale) {
        return getMergedProperties(locale).getProperties().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}