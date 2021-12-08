/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.java;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Utils class to generate and manage {@link Date} instances.
 * <p>
 * This class provide a set of {@link SimpleDateFormat} instances, that can be
 * used to standardize the date formats across different components.<br/>
 * You can use provided SimpleDateFormat instances with the static method
 * <code>getXYFormatter()</code> or use them directly with the commodity methods:
 * <code>formatXY</code>, <code>parseXY</code>, <code>nowXY</code>.
 * <p>
 * By the way, this class expose also methods to extract default
 * {@link SimpleDateFormat}'s patterns for different locales.
 * The <code>getDefaultPattern</code> and <code>getDefaultDatePattern</code>
 * methods allow to extract the default pattern used in the specified
 * {@link Locale}.
 * <p>
 * More over this class provide a set of method to get NOW and EPOCH specific
 * dates. With those methods you can get the correspondent Date instance and
 * string representations.
 * <p>
 * Finally, <code>getDateExact(...)</code>, <code>getDateAltered(...)</code> and
 * <code>getDateExactAltered(...)</code> method allow you to get specific dates
 * relatives to a given one. In other worlds, when you have a Date instance that
 * represent an instant (hours, minutes, seconds...), and you would the 'exact'
 * hour (or day...) of given date. The 'altered' methods adds or remove, specific
 * amount of times to given date.
 */
@SuppressWarnings("unused")
public class JavaDate {

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

    /**
     * Set the UTC time zone to given {@link SimpleDateFormat} and return it.
     *
     * @param dateFormat the {@link SimpleDateFormat} to alter.
     * @return the given {@link SimpleDateFormat} object.
     */
    private static SimpleDateFormat setUTCTimeZone(SimpleDateFormat dateFormat) {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }


    // Locales default patterns

    /**
     * Extract the default {@link SimpleDateFormat} pattern from current
     * {@link Locale} ({@link Locale#getDefault()}).
     *
     * @return the {@link SimpleDateFormat}'s pattern correspondent to the
     * current locale.
     */
    public static String getDefaultPattern() {
        return getDefaultPattern(null);
    }

    /**
     * Extract the default {@link SimpleDateFormat} pattern from given
     * {@link Locale}.
     *
     * @param locale the {@link Locale} to extract the pattern, if null
     *               current locale is used ({@link Locale#getDefault()}).
     * @return the {@link SimpleDateFormat}'s pattern correspondent to the
     * given locale.
     */
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

    /**
     * Extract the date part of the default {@link SimpleDateFormat} pattern
     * from current {@link Locale} ({@link Locale#getDefault()}).
     *
     * @return the date part of the {@link SimpleDateFormat}'s pattern
     * correspondent to the current locale.
     */
    public static String getDefaultDatePattern() {
        return getDefaultDatePattern(null);
    }

    /**
     * Extract the date part of the default {@link SimpleDateFormat} pattern
     * from given {@link Locale}.
     *
     * @param locale the {@link Locale} to extract the pattern, if null
     *               current locale is used ({@link Locale#getDefault()}).
     * @return the date part of the {@link SimpleDateFormat}'s pattern
     * correspondent to the given locale.
     */
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


    // Get Now and Epoch

    /**
     * Return current date time.
     *
     * @return current date time as {@link Date} instance.
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * Return epoch (midnight of 1st january 1970 UTC) date time.
     *
     * @return epoch date time as {@link Date} instance.
     */
    public static Date getEpochDate() {
        return new Date(0L);
    }


    // Date manipulation

    /**
     * Returns current date, resetting the values ​​from the given partition up
     * to milliseconds.
     *
     * @param timePartition the time partition to remove from returned date.
     *                      See {@link Calendar}.
     * @return the exact Date instance.
     * @see #getDateExact(Date, int)
     */
    public static Date getDateExact(int timePartition) {
        return getDateExact(getNowDate(), timePartition);
    }

    /**
     * Returns given date, resetting the values ​​from the given partition up
     * to milliseconds.
     * <p>
     * For example, if given partition is {@link Calendar#DAY_OF_MONTH} then
     * this method return the midnight of current date.
     * <p>
     * More examples:
     * <code>
     * getNow()                                 // Fri Feb 05 18:44:07 CET 2021 with ms
     * getDateExact(Calendar.YEAR)              // Fri Jan 01 00:00:00 CET 2021
     * getDateExact(Calendar.MONTH)             // Mon Feb 01 00:00:00 CET 2021
     * getDateExact(Calendar.DAY_OF_MONTH)      // Fri Feb 05 00:00:00 CET 2021
     * getDateExact(Calendar.HOUR_OF_DAY)       // Fri Feb 05 18:00:00 CET 2021
     * getDateExact(Calendar.MINUTE)            // Fri Feb 05 18:44:00 CET 2021
     * getDateExact(Calendar.SECOND)            // Fri Feb 05 18:44:07 CET 2021 without ms
     * </code>
     *
     * <b>NB:</b> only partitionTime values listed in the example are valid.
     *
     * @param date          the date to make exact.
     * @param timePartition the time partition to remove from returned date.
     *                      See {@link Calendar}.
     * @return the exact Date instance.
     */
    public static Date getDateExact(Date date, int timePartition) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(date);
        if (timePartition == Calendar.YEAR)
            cal.set(Calendar.MONTH, 0);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH)
            cal.set(Calendar.DAY_OF_MONTH, 1);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH || timePartition == Calendar.DAY_OF_MONTH)
            cal.set(Calendar.HOUR_OF_DAY, 0);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH || timePartition == Calendar.DAY_OF_MONTH || timePartition == Calendar.HOUR_OF_DAY)
            cal.set(Calendar.MINUTE, 0);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH || timePartition == Calendar.DAY_OF_MONTH || timePartition == Calendar.HOUR_OF_DAY || timePartition == Calendar.MINUTE)
            cal.set(Calendar.SECOND, 0);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH || timePartition == Calendar.DAY_OF_MONTH || timePartition == Calendar.HOUR_OF_DAY || timePartition == Calendar.MINUTE || timePartition == Calendar.SECOND)
            cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Alter given date, adding <code>count</code> units to given partition
     * time.
     *
     * @param timePartition the time partition to alter. See {@link Calendar}.
     * @param count         the unit to add to timePartition. If it's negative
     *                      number then it will subtracted.
     * @return the altered Date instance.
     * @see #getDateAltered(Date, int, int)
     */
    public static Date getDateAltered(int timePartition, int count) {
        return getDateAltered(getNowDate(), timePartition, count);
    }

    /**
     * Alter given date, adding <code>count</code> units to given partition
     * time.
     * <p>
     * For example, if given partition is {@link Calendar#DAY_OF_MONTH} and
     * <code>count</code> id 1 then it return a Date instance of current time
     * but of tomorrow date. At the same time if <code>count</code> is '-1'
     * it return a yesterday date.
     * <p>
     * More examples:
     * <code>
     * getNow()                                 // Fri Feb 05 18:44:07 CET 2021
     * getDateAltered(Calendar.YEAR,1)          // Fri Jan 01 00:00:00 CET 2022
     * getDateAltered(Calendar.MONTH,1)         // Sat Mar 01 00:00:00 CET 2021
     * getDateAltered(Calendar.DAY_OF_MONTH,1)  // Fri Feb 06 00:00:00 CET 2021
     * getDateAltered(Calendar.HOUR_OF_DAY,1)   // Fri Feb 05 19:00:00 CET 2021
     * getDateAltered(Calendar.MINUTE,1)        // Fri Feb 05 18:45:00 CET 2021
     * getDateAltered(Calendar.SECOND,1)        // Fri Feb 05 18:44:08 CET 2021
     * </code>
     *
     * <b>NB:</b> only partitionTime values listed in the example are valid.
     *
     * @param date          the date to alter.
     * @param timePartition the time partition to alter. See {@link Calendar}.
     * @param count         the unit to add to timePartition. If it's negative
     *                      number then it will subtracted.
     * @return the altered Date instance.
     */
    public static Date getDateAltered(Date date, int timePartition, int count) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(date);
        if (timePartition == Calendar.YEAR)
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + count);
        if (timePartition == Calendar.MONTH)
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + count);
        if (timePartition == Calendar.DAY_OF_MONTH)
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + count);
        if (timePartition == Calendar.HOUR_OF_DAY)
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + count);
        if (timePartition == Calendar.MINUTE)
            cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + count);
        if (timePartition == Calendar.SECOND)
            cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + count);
        return cal.getTime();
    }

    /**
     * Alter and return exact date from current date.
     * <p>
     * This method combine {@link #getDateExact} and {@link #getDateAltered} methods.
     *
     * @param exactTimePartition   the time partition to remove from returned
     *                             date. See {@link Calendar}.
     * @param alteredTimePartition the time partition to alter.
     *                             See {@link Calendar}.
     * @param alteredCount         the unit to add to timePartition. If it's negative
     *                             number then it will subtracted.
     * @return the exact and altered Date instance.
     */
    public static Date getDateExactAltered(int exactTimePartition, int alteredTimePartition, int alteredCount) {
        return getDateAltered(getDateExact(getNowDate(), exactTimePartition), alteredTimePartition, alteredCount);
    }

    /**
     * Alter and return exact date from given date.
     * <p>
     * This method combine {@link #getDateExact} and {@link #getDateAltered} methods.
     *
     * @param exactTimePartition   the time partition to remove from returned
     *                             date. See {@link Calendar}.
     * @param alteredTimePartition the time partition to alter.
     *                             See {@link Calendar}.
     * @param alteredCount         the unit to add to timePartition. If it's negative
     *                             number then it will subtracted.
     * @return the exact and altered Date instance.
     */
    public static Date getDateExactAltered(Date date, int exactTimePartition, int alteredTimePartition, int alteredCount) {
        return getDateAltered(getDateExact(date, exactTimePartition), alteredTimePartition, alteredCount);
    }

}
