package com.robypomper.josp.jcp.base.controllers;

import com.robypomper.josp.jcp.defs.base.errors.Params20;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@ControllerAdvice
@RestController
public class ControllerError implements ErrorController {

    // Class constants

    public static final String PATH_ERROR = "/error";


    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(ControllerError.class);


    // Error handlers

    @RequestMapping(value = PATH_ERROR)
    public Object handleError(final HttpServletRequest request) {
        // Error info
        HttpStatus status = generateHttpStatus(request);
        String type = "HTTP_" + status;
        String msg = generateMessage(request, status);
        String details = "";

        // Request info
        String headerAccept = request.getHeader("accept");
        String reqUrl = request.getRequestURI();

        ControllerError.log.warn(String.format("REQUEST ERROR [%d-%s on '%s']: '%s' (%s)'", status.value(), type, reqUrl, msg, request));

        // Prepare response
        if (headerAccept.contains("text/html"))
            return generateHtmlResponse(request.getRequestURI(), status, type, msg, details);

        return generateDefaultResponse(request.getRequestURI(), status, type, msg, details);
    }

    @Override
    public String getErrorPath() {
        return PATH_ERROR;
    }

    // Exception handlers

    @ExceptionHandler(Throwable.class)
    public Object handleException(final HttpServletRequest request, final Throwable ex) {
        // Error info
        HttpStatus status = generateHttpStatus(ex);
        String type = ex != null ? ex.getClass().getSimpleName() : "N/A";
        String msg = ex != null ? ex.getMessage() : "N/A";
        String details = ex != null ? concatenateCauses(ex) : "N/A";

        // Request info
        String headerAccept = request.getHeader("accept");
        String reqUrl = request.getRequestURI();
        if (status == HttpStatus.NOT_FOUND) {
            Object notFound_Path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            if (notFound_Path != null)
                reqUrl = notFound_Path.toString();
        }

        ControllerError.log.warn(String.format("REQUEST ERROR [%d-%s on '%s']: '%s' (%s)'", status.value(), type, reqUrl, msg, request));

        // Prepare response
        if (headerAccept.contains("text/html"))
            return generateHtmlResponse(request.getRequestURI(), status, type, msg, details);

        return generateDefaultResponse(request.getRequestURI(), status, type, msg, details);
    }


    private static HttpStatus generateHttpStatus(Throwable ex) {
        if (ex == null)
            return HttpStatus.INTERNAL_SERVER_ERROR;

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

    private static HttpStatus generateHttpStatus(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode != null)
            return HttpStatus.resolve(Integer.parseInt(statusCode.toString()));

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private static String generateMessage(HttpServletRequest request, HttpStatus status) {
        if (status == HttpStatus.NOT_FOUND) {
            Object notFound_Path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            if (notFound_Path != null)
                return String.format("Path '%s' not found.", notFound_Path);
        } else {
            Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            if (errorMessage != null)
                return errorMessage.toString();
        }

        return "Unknown error";
    }

    private static String concatenateCauses(Throwable ex) {
        if (ex == null)
            return "";

        String current = String.format("    -> (%s) %s", ex.getClass().getSimpleName(), ex.getMessage());
        String next = concatenateCauses(ex.getCause());
        if (!next.isEmpty())
            current += "\n" + next;
        return current;
    }

    private static String generateHtmlResponse(String reqUrl, HttpStatus status, String type, String message, String details) {
        return String.format("<center><h1>Error %d</h1><p>Error <b>%s</b> on request '%s':<br>%s</p></center>", status.value(), type, reqUrl, message);
        //return new ResponseEntity<String>
    }

    private static ResponseEntity<Params20.Error> generateDefaultResponse(String reqUrl, HttpStatus status, String type, String message, String details) {
        Params20.Error error = new Params20.Error(reqUrl, type, status.value(), message, details, new HashMap<>());
        return ResponseEntity.status(status).body(error);
    }

}