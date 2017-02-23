package com.comviva.mfs.promotion.authentication;

import com.comviva.mfs.promotion.model.error.ErrorResponse;
import com.newrelic.agent.deps.org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Created by sumit.das on 12/23/2016.
 */
public class AccessDeniedExceptionHandlerTest {
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;

    private AccessDeniedExceptionHandler exceptionHandler;
    private AccessDeniedException exception;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        exceptionHandler = new AccessDeniedExceptionHandler();
        exception = new AccessDeniedException("Access denied!");
    }

    @Test
    public void handleDefaultExceptionShouldReturnFieldErrorDTOWithDefaultMessageWhenAcceptHeaderIsJson() throws Exception {
        when(httpServletRequest.getHeader("Accept")).thenReturn(MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<ErrorResponse> objectResponseEntity = (ResponseEntity<ErrorResponse>) exceptionHandler.handleException(exception, httpServletRequest, httpServletResponse);

        assertThat(objectResponseEntity.getStatusCode().value(), is(HttpServletResponse.SC_FORBIDDEN));
    }

}