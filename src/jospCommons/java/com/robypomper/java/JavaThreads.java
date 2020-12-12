package com.robypomper.java;

public class JavaThreads {

    public static boolean softSleep(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public static boolean isInStackOverflow() {
        return isInStackOverflow(null, null, -1);
    }

    public static boolean isInStackOverflow(int callsLimit) {
        return isInStackOverflow(null, null, callsLimit);
    }

    public static boolean isInStackOverflow(String clazz, String method, int callsLimit) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        int callerIdx = 0;
        while (stack[callerIdx].getMethodName().equalsIgnoreCase("getStackTrace")
                || stack[callerIdx].getMethodName().equalsIgnoreCase("isInStackOverflow"))
            callerIdx++;

        if (clazz == null)
            clazz = stack[callerIdx].getClassName();
        if (method == null)
            method = stack[callerIdx].getMethodName();
        if (callsLimit < 1)
            callsLimit = 1;

        int callsCount = 0;
        for (StackTraceElement el : stack) {
            if (el.getClassName().equalsIgnoreCase(clazz)
                    && el.getMethodName().equalsIgnoreCase(method)) {
                callsCount++;
                if (callsCount > callsLimit)
                    return true;
            }
        }

        return false;
    }

}
