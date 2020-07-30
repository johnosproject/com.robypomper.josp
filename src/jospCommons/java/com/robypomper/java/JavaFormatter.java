package com.robypomper.java;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class JavaFormatter {


    // Independent locale formatter

    public static final Locale LOCALE_STANDARD = Locale.US;
    public static final Locale LOCALE_POINT_DEC = Locale.US;
    public static final Locale LOCALE_COMMA_DEC = Locale.ITALY;

    public static String doubleToStr(double d) {
        return String.format(LOCALE_STANDARD, "%f", d);
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
