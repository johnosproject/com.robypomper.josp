package com.robypomper.build.commons;

import org.gradle.api.JavaVersion;

public class JavaVersionUtils {

    public final static JavaVersion CURRENT_JAVA_VERSION = JavaVersion.current();

    public static boolean currentGreaterEqualsThan9() {
        String currVer = System.getProperty("java.version");
        return currVer.startsWith("9") || currVer.startsWith("1.9")
                || currVer.startsWith("10")
                || currVer.startsWith("11")
                || currVer.startsWith("12")
                || currVer.startsWith("13")
                || currVer.startsWith("14");
    }

}
