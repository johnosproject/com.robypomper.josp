package com.robypomper.josp.states;

import java.util.Arrays;

public class StateException extends Throwable {

    private static final String MSG_1 = "Method '%s' can be executed only on '%s' state(s), but actual state is '%s'";
    private static final String MSG_2 = "Wrong state: %s";
    private static final String MSG_3 = "Wrong state: %s; required '%s' state(s), actual state '%s'";

    public <E> StateException(String method, E expected, E actual) {
        super(String.format(MSG_1, method, expected, actual));
    }

    public <E> StateException(String method, E[] expected, E actual) {
        super(String.format(MSG_1, method, Arrays.toString(expected), actual));
    }

    public <E> StateException(String message) {
        super(String.format(MSG_2, message));
    }

    public <E> StateException(E expected, E actual, String message) {
        super(String.format(MSG_3, message, expected, actual));
    }

    public <E> StateException(E[] expected, E actual, String message) {
        super(String.format(MSG_3, message, Arrays.toString(expected), actual));
    }

}
