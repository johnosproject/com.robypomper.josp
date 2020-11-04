package com.robypomper.josp.protocol;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Messaging types to use in Events messaging classes.
 */
public class JOSPStatusHistory {

    private static final String STATUS_HISTORY_REQ_FORMAT = "id:%s;compPath:%s;compType:%s;updatedAt:%s;payload:%s";

    private final long id;
    private final String compPath;
    private final String compType;
    private final Date updatedAt;
    private final String payload;

    @JsonCreator
    public JOSPStatusHistory(@JsonProperty("id") long id,
                             @JsonProperty("compPath") String compPath,
                             @JsonProperty("compType") String compType,
                             @JsonProperty("updatedAt") Date updatedAt,
                             @JsonProperty("payload") String payload) {
        this.id = id;
        this.compPath = compPath;
        this.compType = compType;
        this.updatedAt = updatedAt;
        this.payload = payload;
    }


    // Getters

    public long getId() {
        return id;
    }


    public String getCompPath() {
        return compPath;
    }

    public String getCompType() {
        return compType;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @JsonIgnore
    public String getUpdatedAtStr() {
        return JOSPProtocol.getDateFormatter().format(updatedAt);
    }

    public String getPayload() {
        return payload;
    }


    // Converters

    public static JOSPStatusHistory fromString(String statusesHistoryStr) throws JOSPProtocol.ParsingException {
        String[] statusHistoryStrs = statusesHistoryStr.split(";");
        if (statusHistoryStrs.length != 5)
            throw new JOSPProtocol.ParsingException("Few fields in JOSPStatusHistory string");

        String id = statusHistoryStrs[0].substring(statusHistoryStrs[0].indexOf(":") + 1);
        String compPath = statusHistoryStrs[1].substring(statusHistoryStrs[1].indexOf(":") + 1);
        String compType = statusHistoryStrs[2].substring(statusHistoryStrs[2].indexOf(":") + 1);
        String updatedAt = statusHistoryStrs[3].substring(statusHistoryStrs[3].indexOf(":") + 1);
        String payload = statusHistoryStrs[4].substring(statusHistoryStrs[4].indexOf(":") + 1);

        try {
            return new JOSPStatusHistory(Long.parseLong(id), compPath, compType, JOSPProtocol.getDateFormatter().parse(updatedAt), payload);
        } catch (ParseException e) {
            throw new JOSPProtocol.ParsingException(String.format("Error parsing JOSPStatusHistory fileds: %s", e.getMessage()));
        }
    }

    public static List<JOSPStatusHistory> listFromString(String statusesHistoryStr) throws JOSPProtocol.ParsingException {
        List<JOSPStatusHistory> statuses = new ArrayList<>();

        for (String statusStr : statusesHistoryStr.split("\n"))
            statuses.add(fromString(statusStr));

        return statuses;
    }

    public static String toString(JOSPStatusHistory statusHistory) {
        return String.format(STATUS_HISTORY_REQ_FORMAT, statusHistory.getId(), statusHistory.getCompPath(), statusHistory.getCompType(), JOSPProtocol.getDateFormatter().format(statusHistory.getUpdatedAt()), statusHistory.getPayload());
    }

    public static String toString(List<JOSPStatusHistory> statusesHistory) {
        StringBuilder str = new StringBuilder();
        for (JOSPStatusHistory s : statusesHistory) {
            str.append(toString(s));
            str.append("\n");
        }

        return str.toString();
    }

    public static String logStatuses(List<JOSPStatusHistory> statusesHistory, boolean showCompInfo) {
        StringBuilder str = new StringBuilder();
        if (showCompInfo) {
            str.append("  +-------+--------------------+---------------+------------------------------------------+--------------------------------+\n");
            str.append("  | ID    | Updated At         | CompType      | CompPath                                 | Payload                        |\n");
            //str.append("  | 00000 | 20201029-172422355 | BooleanAction | 1234567890123456789012345678901234567890 | 123456789012345678901234567890 |\n");
            str.append("  +-------+--------------------+---------------+------------------------------------------+--------------------------------+\n");
        } else {
            str.append("  +-------+--------------------+--------------------------------+\n");
            str.append("  | ID    | Updated At         | Payload                        |\n");
            //str.append("  | 00000 | 20201029-172422355 | 123456789012345678901234567890 |\n");
            str.append("  +-------+--------------------+--------------------------------+\n");

        }

        for (JOSPStatusHistory s : statusesHistory) {
            if (showCompInfo) {
                int pathLength = s.getCompPath().length();
                String pathTruncated = pathLength<40 ? s.getCompPath() : "..." + s.getCompPath().substring(pathLength-37);
                str.append(String.format("  | %-5s | %-18s | %-13s | %-40s | %-30s |\n",
                        s.getId(), s.getUpdatedAtStr(), s.getCompType(), pathTruncated, s.getPayload()));
            } else
                str.append(String.format("  | %-5s | %-18s | %-30s |\n",
                        s.getId(), s.getUpdatedAtStr(), s.getPayload()));
        }

        if (showCompInfo)
            str.append("  +-------+--------------------+---------------+------------------------------------------+--------------------------------+\n");
        else
            str.append("  +-------+--------------------+--------------------------------+\n");

        return str.toString();
    }

}
