package com.example.tutorial.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class TutorialErrorHandler extends ResponseEntityExceptionHandler {
    @Autowired
    private ObjectMapper mapper;

    private static final Map<Class<? extends Exception>, HttpStatus> exceptionToStatusMap = new HashMap<>();

    static {
        exceptionToStatusMap.put(InvalidDataException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(IllegalArgumentException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(IncorrectParameterException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(PropertyReferenceException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(ValidationException.class, HttpStatus.BAD_REQUEST);
        exceptionToStatusMap.put(AuthenticationException.class, HttpStatus.UNAUTHORIZED);
        exceptionToStatusMap.put(AccessDeniedException.class, HttpStatus.FORBIDDEN);
        exceptionToStatusMap.put(ItemNotFoundException.class, HttpStatus.NOT_FOUND);
    }

    public static HttpStatus exceptionToStatus(Exception exception) {
        if (exception instanceof AuthenticationException) {
            return exceptionToStatusMap.getOrDefault(AuthenticationException.class, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return exceptionToStatusMap.getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public void handle(Exception exception, HttpServletResponse response) throws IOException {
        log.error("Error: ", exception);
        if (!response.isCommitted()) {
            TutorialErrorResponse exceptionResponse = new TutorialErrorResponse(exceptionToStatus(exception), exception.getMessage());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(exceptionResponse.getStatus());
            mapper.writeValue(response.getWriter(), exceptionResponse);
        }
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(statusCode)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }
        return new ResponseEntity<>(new TutorialErrorResponse((HttpStatus) statusCode, ex), headers, statusCode);
    }
}
