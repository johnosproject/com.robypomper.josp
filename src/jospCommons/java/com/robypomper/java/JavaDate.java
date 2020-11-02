package com.robypomper.java;

import com.robypomper.josp.protocol.JOSPProtocol;

import java.util.Calendar;
import java.util.Date;

public class JavaDate {

    /**
     * Usage:
     * <code>
     * System.out.println(getDateExact(Calendar.YEAR));
     * System.out.println(getDateExact(Calendar.MONTH));
     * System.out.println(getDateExact(Calendar.DAY_OF_MONTH));
     * System.out.println(getDateExact(Calendar.HOUR_OF_DAY));
     * System.out.println(getDateExact(Calendar.MINUTE));
     * System.out.println(getDateExact(Calendar.SECOND));
     * </code>
     * <p>
     * Prints:
     * <code>
     * Tue Dec 31 00:00:00 CET 2019
     * Wed Sep 30 00:00:00 CEST 2020
     * Thu Oct 29 00:00:00 CET 2020
     * Thu Oct 29 12:00:00 CET 2020
     * Thu Oct 29 12:13:00 CET 2020
     * Thu Oct 29 12:13:53 CET 2020
     * </code>
     *
     * @param timePartition
     * @return
     */
    public static Date getDateExact(int timePartition) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(JOSPProtocol.getNowDate());
        if (timePartition == Calendar.YEAR)
            cal.set(Calendar.MONTH, 0);
        if (timePartition == Calendar.YEAR || timePartition == Calendar.MONTH)
            cal.set(Calendar.DAY_OF_MONTH, 0);
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
     * Usage:
     * <code>
     * Date d = JOSPProtocol.getNowDate();
     * System.out.println(getDateAltered(d,Calendar.YEAR,1));
     * System.out.println(getDateAltered(d,Calendar.MONTH,1));
     * System.out.println(getDateAltered(d,Calendar.DAY_OF_MONTH,1));
     * System.out.println(getDateAltered(d,Calendar.HOUR_OF_DAY,1));
     * System.out.println(getDateAltered(d,Calendar.MINUTE,1));
     * System.out.println(getDateAltered(d,Calendar.SECOND,1));
     * </code>
     * <p>
     * Prints:
     * <code>
     * Fri Oct 29 12:23:23 CEST 2021
     * Sun Nov 29 12:23:23 CET 2020
     * Fri Oct 30 12:23:23 CET 2020
     * Thu Oct 29 13:23:23 CET 2020
     * Thu Oct 29 12:24:23 CET 2020
     * Thu Oct 29 12:23:24 CET 2020
     * </code>
     *
     * @param date
     * @param timePartition
     * @param count
     * @return
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

    public static Date getDateExactAltered(int exactTimePartition, int alteredTimePartition, int alteredCount) {
        return getDateAltered(getDateExact(exactTimePartition), alteredTimePartition, alteredCount);
    }

}
