package com.comviva.mfs.hce.appserver.mapper;

import com.comviva.mfs.hce.appserver.mapper.error.ErrorDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by charu.sharma on 12/29/2016.
 */
@Component
public class ErrorDescriptionMapper {
    private final MessageSource messageSource;

    @Autowired
    public ErrorDescriptionMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public List<ErrorDescription> map(Errors errors, Locale locale) {
        return errors.getAllErrors().stream().map(error -> {
            String field = error instanceof FieldError ? ((FieldError) error).getField() : "";
            String message = messageSource.getMessage(error, locale);
            String errorCode = Arrays.asList(error.getCodes()).stream().filter(code -> !code.contains("[")).findFirst().orElse(error.getCode());
            return new ErrorDescription(field, errorCode, message);
        }).collect(Collectors.toList());
    }
}
