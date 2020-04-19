package com.robypomper.discovery;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Utils class for logging discovery messages.
 * <p>
 * It can be enabled or disabled changing the 'LOG' constants
 * {@link ConstantsDiscovery} class.
 * <p>
 * This class print log message from all classes in the
 * {@link com.robypomper.discovery} package.
 */
public class LogDiscovery {

    /**
     * Print generic discovery messages logs.
     *
     * @param str message to be printed.
     */
    public static void log(String str) {
        if (!ConstantsDiscovery.LOG_ENABLED)
            return;

        print(str);
    }

    /**
     * Print publication messages logs.
     *
     * @param str message to be printed.
     */
    public static void logPub(String str) {
        if (!ConstantsDiscovery.LOG_PUB_ENABLED)
            return;

        log(str);
    }

    /**
     * Print discovery messages logs.
     *
     * @param str message to be printed.
     */
    public static void logDisc(String str) {
        if (!ConstantsDiscovery.LOG_DISC_ENABLED)
            return;

        log(str);
    }

    /**
     * Print discovery sub-system messages logs.
     *
     * @param str message to be printed.
     */
    public static void logSubSystem(String str) {
        if (!ConstantsDiscovery.LOG_SUB_ENABLED)
            return;

        log(str);
    }

    /**
     * Method that actually print the log message.
     *
     * @param str message to be printed.
     */
    private static void print(String str) {
        String timestamp = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS").format(new Date());
        String threadName = Thread.currentThread().getName();
        System.out.println(String.format("\t\t\t\tDISC\t[%s] [%-6s] %s", timestamp, threadName.substring(0, Math.min(threadName.length(), 6)), str));
    }

}
