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

}
