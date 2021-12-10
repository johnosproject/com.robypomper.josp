package com.robypomper.build.java;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Class that provide multiple {@link SimpleDateFormat} instances for commons
 * date patterns.
 * <p>
 * NB: This class is a duplicate of com.robypomper.java.JavaData, because the
 * original class is contained in the JOSP Commons library; that's not a
 * dependency of the Gradle's buildSrc module.
 */
public class JavaDataDefinitions {

    // Default data formatters

    //@formatter:off
    public static String FORMATTER_DATETIME = getDefaultDatePattern() + " HH:mm:ss.SSS";
    public static SimpleDateFormat getDateTimeFormatter() { return new SimpleDateFormat(FORMATTER_DATETIME); }
    public static String formatDateTime(Date date) { return getDateTimeFormatter().format(date); }
    public static Date parseDateTime(String strDate) throws ParseException { return getDateTimeFormatter().parse(strDate); }
    public static String nowDateTime() { return getDateTimeFormatter().format(new Date()); }
    public static SimpleDateFormat getDateTimeFormatterUTC() { return setUTCTimeZone(new SimpleDateFormat(FORMATTER_DATETIME)); }
    public static String formatDateTimeUTC(Date date) { return getDateTimeFormatterUTC().format(date); }
    public static Date parseDateTimeUTC(String strDate) throws ParseException { return getDateTimeFormatterUTC().parse(strDate); }
    public static String nowDateTimeUTC() { return getDateTimeFormatterUTC().format(new Date()); }

    public static String FORMATTER_DATETIME_ORDERED = "yyyyMMdd-HHmmssSSS";
    public static SimpleDateFormat getDateTimeOrderedFormatter() { return new SimpleDateFormat(FORMATTER_DATETIME_ORDERED); }
    public static String formatDateTimeOrdered(Date date) { return getDateTimeOrderedFormatter().format(date); }
    public static Date parseDateTimeOrdered(String strDate) throws ParseException { return getDateTimeOrderedFormatter().parse(strDate); }
    public static String nowDateTimeOrdered() { return getDateTimeOrderedFormatter().format(new Date()); }
    public static SimpleDateFormat getDateTimeOrderedFormatterUTC() { return setUTCTimeZone(new SimpleDateFormat(FORMATTER_DATETIME_ORDERED)); }
    public static String formatDateTimeOrderedUTC(Date date) { return getDateTimeOrderedFormatterUTC().format(date); }
    public static Date parseDateTimeOrderedUTC(String strDate) throws ParseException { return getDateTimeOrderedFormatterUTC().parse(strDate); }
    public static String nowDateTimeOrderedUTC() { return getDateTimeOrderedFormatterUTC().format(new Date()); }

    public static String FORMATTER_DATETIME_COMPACT = "yyyyMMddHHmm";
    public static SimpleDateFormat getDateTimeCompactFormatter() { return new SimpleDateFormat(FORMATTER_DATETIME_COMPACT); }
    public static String formatDateTimeCompact(Date date) { return getDateTimeCompactFormatter().format(date); }
    public static Date parseDateTimeCompact(String strDate) throws ParseException { return getDateTimeCompactFormatter().parse(strDate); }
    public static String nowDateTimeCompact() { return getDateTimeCompactFormatter().format(new Date()); }
    public static SimpleDateFormat getDateTimeCompactFormatterUTC() { return setUTCTimeZone(new SimpleDateFormat(FORMATTER_DATETIME_COMPACT)); }
    public static String formatDateTimeCompactUTC(Date date) { return getDateTimeCompactFormatterUTC().format(date); }
    public static Date parseDateTimeCompactUTC(String strDate) throws ParseException { return getDateTimeCompactFormatterUTC().parse(strDate); }
    public static String nowDateTimeCompactUTC() { return getDateTimeCompactFormatterUTC().format(new Date()); }

    public static String FORMATTER_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static SimpleDateFormat getISO8601Formatter() { return new SimpleDateFormat(FORMATTER_ISO_8601); }
    public static String formatISO8601(Date date) { return getISO8601Formatter().format(date); }
    public static Date parseISO8601(String strDate) throws ParseException { return getISO8601Formatter().parse(strDate); }
    public static String nowISO8601() { return getISO8601Formatter().format(new Date()); }
    public static String FORMATTER_ISO_8601_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static SimpleDateFormat getISO8601FormatterUTC() { return setUTCTimeZone(new SimpleDateFormat(FORMATTER_ISO_8601_UTC)); }
    public static String formatISO8601UTC(Date date) { return getISO8601FormatterUTC().format(date); }
    public static Date parseISO8601UTC(String strDate) throws ParseException { return getISO8601FormatterUTC().parse(strDate); }
    public static String nowISO8601UTC() { return getISO8601FormatterUTC().format(new Date()); }
    //@formatter:on


    // Default data formatters - Alias

    //@formatter:off
    public static String FORMATTER_DATETIME_SERIALIZATION = FORMATTER_ISO_8601;
    public static SimpleDateFormat getDateTimeSerializationFormatter() { return new SimpleDateFormat(FORMATTER_DATETIME_SERIALIZATION); }
    public static String formatDateTimeSerialization(Date date) { return getDateTimeSerializationFormatter().format(date); }
    public static Date parseDateTimeSerialization(String strDate) throws ParseException { return getDateTimeSerializationFormatter().parse(strDate); }
    public static String nowDateTimeSerialization() { return getDateTimeJSONFormatter().format(new Date()); }
    public static String FORMATTER_DATETIME_SERIALIZATION_UTC = FORMATTER_ISO_8601_UTC;
    public static SimpleDateFormat getDateTimeSerializationFormatterUTC() { return setUTCTimeZone(new SimpleDateFormat(FORMATTER_DATETIME_SERIALIZATION_UTC)); }
    public static String formatDateTimeSerializationUTC(Date date) { return getDateTimeSerializationFormatterUTC().format(date); }
    public static Date parseDateTimeSerializationUTC(String strDate) throws ParseException { return getDateTimeSerializationFormatterUTC().parse(strDate); }
    public static String nowDateTimeSerializationUTC() { return getDateTimeJSONFormatterUTC().format(new Date()); }

    public static String FORMATTER_DATETIME_JSON = FORMATTER_ISO_8601;
    public static SimpleDateFormat getDateTimeJSONFormatter() { return new SimpleDateFormat(FORMATTER_DATETIME_JSON); }
    public static String formatDateTimeJSON(Date date) { return getDateTimeJSONFormatter().format(date); }
    public static Date parseDateTimeJSON(String strDate) throws ParseException { return getDateTimeJSONFormatter().parse(strDate); }
    public static String nowDateTimeJSON() { return getDateTimeJSONFormatter().format(new Date()); }
    public static String FORMATTER_DATETIME_JSON_UTC = FORMATTER_ISO_8601_UTC;
    public static SimpleDateFormat getDateTimeJSONFormatterUTC() { return setUTCTimeZone(new SimpleDateFormat(FORMATTER_DATETIME_JSON_UTC)); }
    public static String formatDateTimeJSONUTC(Date date) { return getDateTimeJSONFormatterUTC().format(date); }
    public static Date parseDateTimeJSONUTC(String strDate) throws ParseException { return getDateTimeJSONFormatterUTC().parse(strDate); }
    public static String nowDateTimeJSONUTC() { return getDateTimeJSONFormatterUTC().format(new Date()); }

    public static String DEFAULT_DATETIME_FORMATTER = FORMATTER_DATETIME_ORDERED;
    public static SimpleDateFormat getDateTimeDefaultFormatter() { return new SimpleDateFormat(DEFAULT_DATETIME_FORMATTER); }
    public static String formatDateTimeDefault(Date date) { return getDateTimeDefaultFormatter().format(date); }
    public static Date parseDateTimeDefault(String strDate) throws ParseException { return getDateTimeDefaultFormatter().parse(strDate); }
    public static String nowDateTimeDefault() { return getDateTimeDefaultFormatter().format(new Date()); }
    public static SimpleDateFormat getDateTimeDefaultFormatterUTC() { return setUTCTimeZone(new SimpleDateFormat(DEFAULT_DATETIME_FORMATTER)); }
    public static String formatDateTimeDefaultUTC(Date date) { return getDateTimeDefaultFormatterUTC().format(date); }
    public static Date parseDateTimeDefaultUTC(String strDate) throws ParseException { return getDateTimeDefaultFormatterUTC().parse(strDate); }
    public static String nowDateTimeDefaultUTC() { return getDateTimeDefaultFormatterUTC().format(new Date()); }
    //@formatter:on


    // Static internal utils for data formatters

    private static SimpleDateFormat setUTCTimeZone(SimpleDateFormat dateFormat) {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    public static String getDefaultPattern() {
        return getDefaultPattern(null);
    }

    public static String getDefaultPattern(Locale locale) {
        // set locale as global
        Locale previous = Locale.getDefault();
        if (locale != null) Locale.setDefault(locale);

        // generate new date format with global locale
        SimpleDateFormat sdf = new SimpleDateFormat();

        // reset previous local as global
        if (locale != null) Locale.setDefault(previous);

        return sdf.toPattern();
    }

    public static String getDefaultDatePattern() {
        return getDefaultDatePattern(null);
    }

    public static String getDefaultDatePattern(Locale locale) {
        String localPattern = getDefaultPattern(locale);
        String localDatePattern = null;

        // check pattern splitter
        String patternSplitter = ",";
        if (localPattern.split(patternSplitter).length != 2)
            patternSplitter = " ";

        // search for date patter
        for (String p : localPattern.split(patternSplitter))
            if (p.contains("/")
                    || p.contains("-")
                    || p.contains(".")) {
                localDatePattern = p;
                break;
            }

        // if no date patter was found, fallback on static pattern
        if (localDatePattern == null)
            localDatePattern = "yyyy/MM/dd";

        return localDatePattern;
    }

}
