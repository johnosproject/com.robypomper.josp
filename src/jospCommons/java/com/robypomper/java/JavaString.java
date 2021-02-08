package com.robypomper.java;

/**
 * String utils class.
 */
public class JavaString {

    /**
     * Count occurrences of <code>subStr</code> in <code>str</code>.
     *
     * @param str    the String to check.
     * @param subStr the String to looking for.
     * @return the number of occurrences.
     */
    public static int occurrenceCount(String str, String subStr) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {

            lastIndex = str.indexOf(subStr, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += subStr.length();
            }
        }

        return count;
    }

}
