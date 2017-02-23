package com.comviva.mfs.promotion.model.error;

import lombok.Getter;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Getter
public class ErrorResponse {
    private final List<ErrorDescription> errorDescriptions;

    public ErrorResponse(List<ErrorDescription> errorDescriptions) {
        this.errorDescriptions = errorDescriptions;
    }

    public static ErrorResponse forErrorCode(String errorCode, MessageSource messageSource, Locale locale) {
        String message = messageSource.getMessage(errorCode, null, locale);
        List<ErrorDescription> errors = Arrays.asList(new ErrorDescription(errorCode, message));
        ErrorResponse errorResponse = new ErrorResponse(errors);
        return errorResponse;
    }
}
