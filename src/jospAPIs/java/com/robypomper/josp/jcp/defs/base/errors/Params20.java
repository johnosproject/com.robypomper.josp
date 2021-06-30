package com.robypomper.josp.jcp.defs.base.errors;

import java.util.Map;


/**
 * JCP All - Errors 2.0
 */
public class Params20 {

    // Error

    public static class Error {

        public final String request;            // Request url that thrown the exception
        public final String type;               // Error type (customizable by developers)
        public final int code;                  // HTTP error code (4XX)
        public final String message;            // Customized message to display
        public final String log;                // Customized message for developers
        public final Map<String, String> params; // Optional log's params key-value

        public Error(String request, String type, int code, String msg, String log, Map<String, String> params) {
            this.request = request;
            this.type = type;
            this.code = code;
            this.message = msg;
            this.log = log;
            this.params = params;
        }

    }

}
