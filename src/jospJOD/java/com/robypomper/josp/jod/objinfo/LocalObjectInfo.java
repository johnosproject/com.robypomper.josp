package com.robypomper.josp.jod.objinfo;

import com.robypomper.java.JavaRandomStrings;

import java.util.Random;

/**
 * Support class for Object Info generation.
 */
public class LocalObjectInfo {

    // Class constants

    private static final int LENGTH = 5;        // 26^5 = 11.881.376 combinations


    // Generator methods

    /**
     * Generate random (and hopefully unique) object's Hardware ID.
     *
     * @return a 5-chars random string.
     */
    public static String generateObjIdHw() {
        return JavaRandomStrings.randomAlfaString(LENGTH);
    }

    /**
     * Generate readable, random (and hopefully unique) object's name.
     *
     * @return a {fruit}_{2Digit} random string.
     */
    public static String generateObjName() {
        return String.format("%s_%02d", JavaRandomStrings.randomFruitsString(), new Random().nextInt(100));
    }

}
