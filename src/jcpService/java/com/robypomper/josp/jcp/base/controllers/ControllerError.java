package com.robypomper.josp.jcp.base.controllers;

import com.robypomper.java.JavaThreads;
import com.robypomper.josp.jcp.defs.base.errors.Params20;
import com.robypomper.josp.jcp.defs.jslwebbridge.pub.core.init.Paths20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@ControllerAdvice
public class ControllerError {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ControllerError.class);


    // Exception handlers

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Params20.Error> handleException(final HttpServletRequest request, final Throwable ex) {
        HttpStatus code = exception2HttpStatus(ex);
        return ResponseEntity.status(code).body(createError(code, request, ex));
    }


    // ControllerError utils

    private static String concatenateCauses(Throwable ex) {
        if (ex == null)
            return "";

        String current = String.format("    -> (%s) %s", ex.getClass().getSimpleName(), ex.getMessage());
        String next = concatenateCauses(ex.getCause());
        if (!next.isEmpty())
            current += "\n" + next;
        return current;
    }

    private static Params20.Error createError(HttpStatus code, HttpServletRequest req, Throwable ex) {
        String request = req.getRequestURI();
        String type = ex.getClass().getSimpleName();
        String msg = ex.getMessage();
        String log = concatenateCauses(ex);
        //String log = JavaThreads.stackTraceToString(ex);

        int codeInt = code.value();
        ControllerError.log.warn(String.format("REQUEST ERROR [%d]: '%s'; %s: '%s'", codeInt, request, type, msg));
        if (request.endsWith(Paths20.FULL_PATH_INIT_SSE))
            return null;

        return new Params20.Error(request, type, code.value(), msg, log, new HashMap<>());
    }

    private static HttpStatus exception2HttpStatus(Throwable ex) {
        switch (ex.getClass().getSimpleName()) {

            case "ResponseStatusException":
                return ((ResponseStatusException) ex).getStatus();

            case "RequestRejectedException":
            case "HttpRequestMethodNotSupportedException":
                return HttpStatus.BAD_REQUEST;

            case "GWNotAvailableException":
            case "Exception":
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}