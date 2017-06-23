package com.comviva.mfs.promotion.exception.handlers;

import com.comviva.mfs.promotion.exception.DataConversionException;
import com.comviva.mfs.promotion.exception.ValidationException;
import com.comviva.mfs.promotion.mapper.ErrorDescriptionMapper;
import com.comviva.mfs.promotion.model.error.ErrorDescription;
import com.comviva.mfs.promotion.model.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    private final ErrorDescriptionMapper errorDescriptionMapper;

    @Autowired
    public ValidationExceptionHandler(ErrorDescriptionMapper errorDescriptionMapper) {
        this.errorDescriptionMapper = errorDescriptionMapper;
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException validationException, WebRequest request) {
        LOGGER.debug("ValidationException: ", validationException);
        List<ErrorDescription> errorDescriptions = errorDescriptionMapper.map(validationException.getErrors(), request.getLocale());
        return ResponseEntity.unprocessableEntity().body(new ErrorResponse(errorDescriptions));
    }

    @ExceptionHandler(DataConversionException.class)
    public ResponseEntity<Object> handleDataConversionException(DataConversionException dataConversionException, WebRequest request) {
        LOGGER.debug("DataConversionException: ", dataConversionException);
        List<ErrorDescription> errorDescriptions = errorDescriptionMapper.map(dataConversionException.getErrors(), request.getLocale());
        return ResponseEntity.badRequest().body(new ErrorResponse(errorDescriptions));
    }
}

