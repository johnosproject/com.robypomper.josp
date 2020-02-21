package com.robypomper.build.commons;

/**
 * Utils class for naming management.
 */
public class Naming {

    /**
     * Capitalize given string.
     *
     * @param str string to capitalize
     * @return capitalized string
     */
    static public String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
