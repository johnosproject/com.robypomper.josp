package com.robypomper.java;

import java.util.Date;


/**
 * Utils class to manage and print assertion errors.
 * <p>
 * Assertion errors are intended as error that should not occurs on production
 * execution, but can happen on development or testing process.
 * <p>
 * All errors printed by this class are printed with detailed error info and
 * enclosed in START/END lines to better identify error printed lines.
 * <p>
 * Such as classic <code>assert</code> statement, this assertions can be
 * enabled/disabled with the <code>-enableassertions</code> switch as JVM
 * param.
 */
public class JavaAssertions {

    // Internal vars

    private static final boolean isAssertionDisabled;

    static {
        boolean tmp;
        try {
            assert false;
            tmp = true;

        } catch (AssertionError ignore) {
            tmp = false;
        }

        isAssertionDisabled = tmp;
    }


    // Assertion utils

    /**
     * @return true if assertions are disabled.
     */
    public static boolean isAssertionDisabled() {
        return isAssertionDisabled;
    }


    // Failed assertions

    /**
     * Make and print assertion error.
     *
     * @param e         the exception related with the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertionFailed(Throwable e, T returnVal) {
        makeAssertionFailed(e);
        return returnVal;
    }

    /**
     * Make and print assertion error.
     *
     * @param e the exception related with the assertion error.
     */
    public static void makeAssertionFailed(Throwable e) {
        makeAssertionFailed(e, null);
    }

    /**
     * Make and print assertion error.
     *
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertionFailed(String msg, T returnVal) {
        makeAssertionFailed(msg);
        return returnVal;
    }

    /**
     * Make and print assertion error.
     *
     * @param msg a message to describe the assertion error.
     */
    public static void makeAssertionFailed(String msg) {
        makeAssertionFailed((Throwable) null, msg);
    }

    /**
     * Make and print assertion error.
     *
     * @param e         the exception related with the assertion error.
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertionFailed(Throwable e, String msg, T returnVal) {
        makeAssertionFailed(e, msg);
        return returnVal;
    }

    /**
     * Make and print assertion error.
     *
     * @param e   the exception related with the assertion error.
     * @param msg a message to describe the assertion error.
     */
    public static void makeAssertionFailed(Throwable e, String msg) {
        makeAssertion(false, e, msg);
    }


    // Assertion makers

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertion(boolean condition, String msg, T returnVal) {
        makeAssertion(condition, msg);
        return returnVal;
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param msg       a message to describe the assertion error.
     */
    public static void makeAssertion(boolean condition, String msg) {
        makeAssertion(condition, (Throwable) null, msg);
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     * @param msg       a message to describe the assertion error.
     * @param returnVal value returned by current method.
     * @param <T>       return type of current method.
     * @return the value given as <code>returnVal</code> param.
     */
    public static <T> T makeAssertion(boolean condition, Throwable e, String msg, T returnVal) {
        makeAssertion(condition, e, msg);
        return returnVal;
    }

    /**
     * Make, check and print assertion error.
     *
     * @param condition the assertion error is printed only if condition is false.
     * @param e         the exception related with the assertion error.
     * @param msg       a message to describe the assertion error.
     */
    public static void makeAssertion(boolean condition, Throwable e, String msg) {
        if (condition || isAssertionDisabled()) return;

        print(e, msg);
    }


    // Printers

    private static void print(Throwable extraThrowable, String msg) {
        String s = "";
        s += "ASSERTION ERROR (start)\n";
        if (msg != null)
            s += String.format("- Message:  %s\n", msg);
        s += String.format("- Date:     %s\n", new Date());
        s += String.format("- Thread:   %s\n", Thread.currentThread().getName());
        if (extraThrowable != null) {
            s += "- ExtraEx: ";
            s += JavaThreads.stackTraceToString(extraThrowable).replace("\n", "\n            ").trim() + "\n";
        }
        s += "ASSERTION ERROR (end)";
        System.err.println(s);
        System.err.flush();
    }

}
