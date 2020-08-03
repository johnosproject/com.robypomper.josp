package com.robypomper.java;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class JavaFormatter {

    // Independent comparison for Boolean

    public static final List<String> TRUE_ALIASES = Arrays.asList("TRUE", "1", "ON", "HIGH", "OPEN", "FULL");


    // Independent locale formatter for Double

    public static final Locale LOCALE_POINT_DEC = Locale.US;
    public static final Locale LOCALE_COMMA_DEC = Locale.ITALY;
    public static final Locale LOCALE_STANDARD = LOCALE_POINT_DEC;


    // Boolean convert methods

    public static boolean strToBoolean(String s) {
        return TRUE_ALIASES.contains(s);
    }


    // Double convert methods

    public static String doubleToStr(double d) {
        return String.format(LOCALE_STANDARD, "%f", d);
    }

    public static String doubleToStr_Point(double d) {
        return String.format(LOCALE_POINT_DEC, "%f", d);
    }

    public static String doubleToStr_Comma(double d) {
        return String.format(LOCALE_COMMA_DEC, "%f", d);
    }

    public static Double strToDouble(String s) {
        try {
            try {
                try {
                    return (Double) (NumberFormat.getInstance(LOCALE_POINT_DEC)).parse(s);

                } catch (ClassCastException e) {
                    return (Double) (NumberFormat.getInstance(LOCALE_COMMA_DEC)).parse(s);
                }

            } catch (ClassCastException e) {
                return (NumberFormat.getInstance(LOCALE_POINT_DEC)).parse(s).doubleValue();
            }

        } catch (ParseException e) {
            return null;
        }
    }

}
