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

package com.robypomper.josp.protocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.robypomper.java.JavaDate;

import java.util.Calendar;
import java.util.Date;

public class HistoryLimits {

    // Default HistoryLimits

    public static final HistoryLimits NO_LIMITS = new HistoryLimits();
    public static final HistoryLimits LATEST_10 = new HistoryLimits();
    public static final HistoryLimits ANCIENT_10 = new HistoryLimits();
    public static final HistoryLimits LAST_HOUR = new HistoryLimits();
    public static final HistoryLimits PAST_HOUR = new HistoryLimits();
    private static final String LATEST_JSON_FORMATTER = "{ \"latestCount\":\"%d\" }";

    // Internal consts
    private static final String ANCIENT_JSON_FORMATTER = "{ \"ancientCount\":\"%d\" }";
    private static final String ID_RANGE_JSON_FORMATTER_A = "\"fromID\":\"%d\"";
    private static final String ID_RANGE_JSON_FORMATTER_B = ", \"toID\":\"%d\"";
    private static final String DATE_RANGE_JSON_FORMATTER_A = "\"fromDate\":\"%d\"";
    private static final String DATE_RANGE_JSON_FORMATTER_B = ", \"toDate\":\"%d\"";

    static {
        LATEST_10.latestCount = 10;
        ANCIENT_10.ancientCount = 10;
        LAST_HOUR.fromDate = JavaDate.getDateExact(Calendar.HOUR_OF_DAY);
        LAST_HOUR.fromDate = JavaDate.getDateAltered(JavaDate.getNowDate(), Calendar.HOUR_OF_DAY, -1);
        PAST_HOUR.fromDate = JavaDate.getDateExactAltered(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY, -1);
        PAST_HOUR.toDate = JavaDate.getDateExact(Calendar.HOUR_OF_DAY);
    }


    // Internal vars

    private long latestCount = -1;
    private long ancientCount = -1;
    private long fromID = -1;
    private long toID = -1;
    private Date fromDate = null;
    private Date toDate = null;


    // Constructor

    @JsonCreator
    public HistoryLimits(@JsonProperty("latestCount") Long latestCount,
                         @JsonProperty("ancientCount") Long ancientCount,
                         @JsonProperty("fromID") Long fromID,
                         @JsonProperty("toID") Long toID,
                         @JsonProperty("fromDate") Date fromDate,
                         @JsonProperty("toDate") Date toDate) {
        if (latestCount != null) this.latestCount = latestCount;
        if (ancientCount != null) this.ancientCount = ancientCount;
        if (fromID != null) this.fromID = fromID;
        if (toID != null) this.toID = toID;
        if (fromDate != null) this.fromDate = fromDate;
        if (toDate != null) this.toDate = toDate;
    }


    // HistoryLimits type checks

    public static HistoryLimits fromString(String limitsStr) throws JOSPProtocol.ParsingException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(limitsStr, HistoryLimits.class);

        } catch (JsonProcessingException e) {
            throw new JOSPProtocol.ParsingException(String.format("Can't deserialize '%s' string to HistoryLimits.", limitsStr), e);
        }
    }

    public static String toString(HistoryLimits limits) {

        // Jackson method, not working! It put all fields in the serialized output
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return mapper.writeValueAsString(limits);
//
//        } catch (JsonProcessingException e) {
//            throw new JOSPProtocol.ParsingException(String.format("Can't serialize '%s' HistoryLimits to string.", limits),e);
//        }


        if (limits.isLatestCount())
            return String.format(LATEST_JSON_FORMATTER, limits.latestCount);

        if (limits.isAncientCount())
            return String.format(ANCIENT_JSON_FORMATTER, limits.ancientCount);

        if (limits.isIDRange()) {
            String s = String.format(ID_RANGE_JSON_FORMATTER_A, limits.fromID);
            if (limits.toID != -1)
                s += String.format(ID_RANGE_JSON_FORMATTER_B, limits.toID);
            return "{" + s + "}";
        }

        if (limits.isDateRange()) {
            String s = String.format(DATE_RANGE_JSON_FORMATTER_A, limits.fromDate.getTime());
            if (limits.toDate != null)
                s += String.format(DATE_RANGE_JSON_FORMATTER_B, limits.toDate.getTime());
            return "{" + s + "}";
        }

        return "{}";
    }

    @JsonIgnore
    public boolean isLatestCount() {
        return latestCount != -1;
    }

    @JsonIgnore
    public boolean isAncientCount() {
        return ancientCount != -1;
    }


    // Getters

    @JsonIgnore
    public boolean isIDRange() {
        return fromID != -1 || toID != -1;
    }

    @JsonIgnore
    public boolean isDateRange() {
        return fromDate != null || toDate != null;
    }

    @JsonIgnore
    public Long getLatestCount() {
        return latestCount != -1 ? latestCount : null;
    }

    @JsonIgnore
    public Long getAncientCount() {
        return ancientCount != -1 ? ancientCount : null;
    }

    @JsonIgnore
    public Long getFromId() {
        return fromID != -1 ? fromID : null;
    }

    @JsonIgnore
    public Long getToId() {
        return toID != -1 ? toID : null;
    }

    @JsonIgnore
    public Date getFromDate() {
        return fromDate;
    }


    // Converters

    @JsonIgnore
    public Date getToDate() {
        return toDate;
    }

    @Override
    public String toString() {
        if (isLatestCount())
            return "latestCount: " + latestCount + "";

        if (isAncientCount())
            return "ancientCount: " + ancientCount + "";

        if (isIDRange()) {
            String s = "fromID: " + fromID + "";
            if (toID != -1)
                s += "; toID: " + toID + "";
            return s;
        }

        if (isDateRange()) {
            String s = "fromDate: " + JavaDate.DEF_DATE_FORMATTER.format(fromDate) + "";
            if (toDate != null)
                s += "; toDate: " + JavaDate.DEF_DATE_FORMATTER.format(toDate) + "";
            return s;
        }

        return "NoLimits";
    }


//    public static void main(String[] args) {
//
//        HistoryLimits hs;
//        hs = HistoryLimits.NO_LIMITS;
//        test(hs,"NO_LIMITS");
//
//        hs = HistoryLimits.LATEST_10;
//        test(hs,"LATEST_10");
//
//        hs = HistoryLimits.ANCIENT_10;
//        test(hs,"ANCIENT_10");
//
//        hs = HistoryLimits.LAST_HOUR;
//        test(hs,"LAST_HOUR");
//
//        hs = HistoryLimits.PAST_HOUR;
//        test(hs,"PAST_HOUR");
//
//    }
//
//    private static void test(HistoryLimits limitsToTest, String testname) {
//        System.out.println("Test: " + testname);
//
//        try {
//            String limitsStr = HistoryLimits.toString(limitsToTest);
//            System.out.println("- toString: " + limitsStr);
//            HistoryLimits limits = HistoryLimits.fromString(limitsStr);
//            System.out.println("- fromString: " + limits);
//        } catch (JOSPProtocol.ParsingException e) {
//            System.err.println("Error on test '" + testname + "': " + e.getMessage());
//            System.err.println("              '" + testname + "': " + e.getCause().getMessage());
//        }
//    }

}
