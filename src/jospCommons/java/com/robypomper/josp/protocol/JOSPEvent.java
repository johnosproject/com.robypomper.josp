package com.robypomper.josp.protocol;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.robypomper.java.JavaDate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Messaging types to use in Events messaging classes.
 */
public class JOSPEvent {

    /**
     * Available event types.
     */
    public enum SrcType {
        /**
         * Indicate that the event emitter is an object.
         */
        Obj,
        /**
         * Indicate that the event emitter is a service.
         */
        Srv,
        /**
         * Indicate that the event emitter is the JCP APIs.
         */
        APIs,
        /**
         * Indicate that the event emitter is the O2S GW.
         */
        GWs_O2S,
        /**
         * Indicate that the event emitter is the S2O GW.
         */
        GWs_S2O,
    }

    /**
     * Available event types.
     */
    public enum Type {

        /**
         * Events emitted during JOD startup.
         * <p>
         * The JOD startup is split in two phases:
         * <ul>
         *     <li>Start sub-system creation</li>
         *     <li>Startup sub-system</li>
         * </ul>
         * <p>
         * Each phase emits 2 events (start phase and end phase). The second
         * event include the phase's time in milliseconds.
         *
         * <pre>
         *     {"phase": "Start sub-system creation", "instId": "8484"}
         *     ...
         *     {"phase": "End sub-system creation", "time": "1097"}
         *     {"phase": "Startup sub-system"}
         *     ...
         *     {"phase": "Sub-systems started successfully", "time": "228"}
         * </pre>
         */
        JOD_START,
        /**
         * Events emitted during JOD shutdown.
         * <p>
         * The JOD shutdown emits 2 events (start shutdown and end shutdown).
         * The second event include the shutdown's time in milliseconds.
         *
         * <pre>
         *     {"phase": "Stopping sub-system"}
         *     ...
         *     {"phase": "Sub-systems stopped successfully", "time": "180"}
         * </pre>
         */
        JOD_STOP,

        /**
         * Event emitted on JCP connection.
         * <p>
         * When the JOD connects to the JCP APIs, it emits this event. This
         * event is emitted also if connection fails, in this case it contains
         * also the errorPayload.
         *
         * <pre>
         *     {"phase": "JCP Connected", "urlAPIs": "https://localhost:9001/", "ipAPIs": "127.0.0.1", "urlAuth": "https://localhost:8998/", "ipAuth": "127.0.0.1"}
         *
         *     {"phase": "JCP Connection failed", "urlAPIs": "https://localhost:9001/", "ipAPIs": "127.0.0.1", "urlAuth": "https://localhost:8998/", "ipAuth": "127.0.0.1", "error": "{"type": "JCPNotReachableException", "msg": "Error connecting to JCP because 'localhost:9001/apis/Status/2.0/' (APIs's url) not reachable [ConnectException:Connection refused (Connection refused)]", "stack": "[com.robypomper.josp.core.jcpclient.DefaultJCPClient2.checkServerReachability(DefaultJCPClient2.java:249), com.robypomper.josp.core.jcpclient.DefaultJCPClient2.checkServerReachability(DefaultJCPClient2.java:218), com.robypomper.josp.core.jcpclient.DefaultJCPClient2.connect(DefaultJCPClient2.java:135), com.robypomper.josp.core.jcpclient.DefaultJCPClient2$2.run(DefaultJCPClient2.java:426), java.util.TimerThread.mainLoop(Timer.java:555), java.util.TimerThread.run(Timer.java:505)]"}"}
         * </pre>
         */
        JOD_COMM_JCP_CONN,
        /**
         * Event emitted on JCP disconnection.
         * <p>
         * When the JOD disconnect or lost the JCP APIs connection, it emits
         * this event. On JOD shutdown this event is not emitted.
         *
         * <pre>
         *     {"phase": "JCP Disconnected", "urlAPIs": "https://localhost:9001/", "ipAPIs": "127.0.0.1", "urlAuth": "https://localhost:8998/", "ipAuth": "127.0.0.1", "flow": "ClientCred"}
         * </pre>
         */
        JOD_COMM_JCP_DISC,
        /**
         * Not used
         */
        JOD_COMM_JCP_OFFLINE,
        /**
         * Event emitted on JOSP GW O2S connection.
         * <p>
         * When the JOD connects to the JOSP GW, it emits this event. This
         * event is emitted also if connection fails, in this case it contains
         * also the errorPayload.
         *
         * <pre>
         *     {"phase": "Comm Cloud Connected", "connected": "true", "url": "MBP-di-Roberto.station", "ip": "192.168.1.4", "port": "9110", "client": {"id": "SXTTO-TCIJE-CDZYC", "url": "mbp-di-roberto.station", "ip": "192.168.1.4", "port": "65103"}}
         *
         *     TODO
         * </pre>
         */
        JOD_COMM_CLOUD_CONN,
        /**
         * Event emitted on JOSP GW O2S disconnection.
         * <p>
         * When the JOD disconnect or lost the JOSP GW connection, it emits
         * this event.
         *
         * <pre>
         *     {"phase": "Comm Cloud Disconnected", "connected": "false", "url": "MBP-di-Roberto.station", "ip": "192.168.1.4", "port": "9110"}
         * </pre>
         */
        JOD_COMM_CLOUD_DISC,
        /**
         * Event emitted on Local Server startup.
         * <p>
         * When the JOD runs his server for local service connections, it emits
         * this events. The local server startup in two process:
         * <ul>
         *     <li>server initialization and listening</li>
         *     <li>server publication on local network</li>
         * </ul>
         *
         * <pre>
         *     {"phase": "Comm Local Started", "running": "true", "url": "0.0.0.0", "ip": "0.0.0.0", "port": "1234", "clientsCount": "0"}
         *     {"phase": "Comm Local Published", "running": "true", "url": "0.0.0.0", "ip": "0.0.0.0", "port": "1234", "clientsCount": "0"}
         * </pre>
         */
        JOD_COMM_LOC_START,
        /**
         * Event emitted on Local Server startup.
         * <p>
         * When the JOD stops his server for local service connections, it emits
         * this events. The local server stops in two process:
         * <ul>
         *     <li>server shutdown</li>
         *     <li>server de-publication from local network</li>
         * </ul>
         *
         * <pre>
         *     {"phase": "Comm Local Stopped", "running": "false", "port": "1234", "clientsCount": "0"}
         *     {"phase": "Comm Local Hided", "running": "false", "port": "1234", "clientsCount": "0"}
         * </pre>
         */
        JOD_COMM_LOC_STOP,
        /**
         * Event emitted when a Local Service connects to Local Server startup.
         * <p>
         * When a JSL service connects locally to the current JOD instance, JOD
         * emits this events. When the received connection is from an "already
         * connected" service the event's phase is set to 'Local JSL refused
         * connection because already connected service'.
         * <p>
         * In the payload this event contains two clients info:
         * <ul>
         *     <li>connectingCli: info about the client that is connecting to the current JOD local server</li>
         *     <li>serviceCli: info about the first service's client</li>
         * </ul>
         * <p>
         * If the clients are equals, that means this is the first connection from the service (JSL instance).
         *
         * <pre>
         *     {"phase": "Local JSL connected", "connected": "true", "srvId": "test-client-srv", "usrId": "00000-00000-00000", "instanceId": "4730", "clientId": "test-client-srv/00000-00000-00000/4730", "connectingCli": {"id": "test-client-srv/00000-00000-00000/4730", "url": "localhost", "ip": "127.0.0.1", "port": "49170"}, "serviceCli": {"id": "test-client-srv/00000-00000-00000/4730", "url": "localhost", "ip": "127.0.0.1", "port": "49170"}}
         *
         *     ERROR
         *     {"phase": "Local JSL refused connection because already connected service", "connected": "false", "srvId": "test-client-srv", "usrId": "00000-00000-00000", "instanceId": "4730", "clientId": "test-client-srv/00000-00000-00000/4730", "connectingCli": {"id": "test-client-srv/00000-00000-00000/4730", "url": "localhost", "ip": "127.0.0.1", "port": "49172"}, "serviceCli": {"id": "test-client-srv/00000-00000-00000/4730", "url": "localhost", "ip": "127.0.0.1", "port": "49172"}}
         * </pre>
         */
        JOD_COMM_LOC_CONN,
        /**
         * Event emitted when a Local Service disconnects from Local Server startup.
         * <p>
         * When a JSL service disconnect from current JOD instance, JOD
         * emits this events. This event is emitted only when the service lost
         * the connection with the object (no other connections available).
         * <p>
         * In the payload this event contains two clients info:
         * <ul>
         *     <li>disconnectingCli: info about the client that is disconnecting to the current JOD local server</li>
         *     <li>serviceCli: info about the first service's client</li>
         * </ul>
         * <p>
         * If the clients are equals, that means this is the first disconnection from the service (JSL instance).
         *
         * <pre>
         *     {"phase": "Local JSL disconnected", "connected": "false", "srvId": "test-client-srv", "usrId": "00000-00000-00000", "instanceId": "4730", "clientId": "test-client-srv/00000-00000-00000/4730", "disconnectingCli": {"id": "test-client-srv/00000-00000-00000/4730", "url": "localhost", "ip": "127.0.0.1", "port": "49170"}, "serviceCli": {"id": "test-client-srv/00000-00000-00000/4730", "url": "localhost", "ip": "127.0.0.1", "port": "49170"}}
         * </pre>
         */
        JOD_COMM_LOC_DISC,

        /**
         * Event emitted on Permissions load / save / generated.
         * <p>
         * When the JOD instance load, generate or save object's permissions,
         * emits this event.
         *
         * <pre>
         *     {"phase": "Gen permissions on cloud", "count": "1", "objId": "SXTTO-TCIJE-CDZYC"}
         *     {"phase": "Save permissions to file", "count": "1", "objId": "SXTTO-TCIJE-CDZYC"}
         *     {"phase": "Load permissions from file", "count": "1", "objId": "SXTTO-TCIJE-CDZYC"}
         * </pre>
         */
        JOD_PERMS_LOAD,
        /**
         * Not used
         */
        JOD_PERMS_MISSING,
        /**
         * Event emitted when a new permission is added.
         *
         * <pre>
         *     {"add": {    "id": "EYUDPKGHXVYZTUIRZTMX",     "srvId": "#All",     "usrId": "#All",     "connType": "OnlyLocal",     "permType": "Actions"}}
         * </pre>
         */
        JOD_PERMS_ADD,
        /**
         * Event emitted when a permission is updated.
         *
         * <pre>
         *     {"add": {    "id": "EYUDPKGHXVYZTUIRZTMX",     "srvId": "#All",     "usrId": "#All",     "connType": "OnlyLocal",     "permType": "Status"},"rem": {    "id": "EYUDPKGHXVYZTUIRZTMX",     "srvId": "#All",     "usrId": "#All",     "connType": "OnlyLocal",     "permType": "Actions"}}
         * </pre>
         */
        JOD_PERMS_UPD,
        /**
         * Event emitted when a permission is removed.
         *
         * <pre>
         *     {"rem": {    "id": "EYUDPKGHXVYZTUIRZTMX",     "srvId": "#All",     "usrId": "#All",     "connType": "OnlyLocal",     "permType": "Status"}}
         * </pre>
         */
        JOD_PERMS_REM,

        /**
         * Event emitted when the object's structure is loaded.
         * <p>
         * This event is emitted during the JOD startup.
         *
         * <pre>
         *     {"model": "JOD Test Object 2.0", "version": "version", "compsCount": "4"}
         * </pre>
         */
        JOD_STRUCT_LOAD,
        /**
         * Not used
         */
        JOD_STRUCT_UPD,

        /**
         * Event emitted on object info updates.
         * <p>
         * When an object's info is updated, the JOD instance emits this event.
         * This event is emitted for following info:
         * <ul>
         *     <li>objId</li>
         *     <li>objOwner</li>
         * </ul>
         *
         * <pre>
         *     // Set objId on first jod execution
         *     {"info": "objId", newValue": "SXTTO-TCIJE-CDZYC"}
         *
         *     // Re-new objId after objOwner update
         *     {"info": "objOwner", oldValue": "00000-00000-00000", newValue": "3d6bd9ed-8d63-43d6-a465-dbe9029e04c8"}
         *     {"info": "objId", oldValue": "SXTTO-TCIJE-CDZYC", newValue": "SXTTO-NVKJL-VWVFV"}
         * </pre>
         */
        JOD_INFO_UPD,
        /**
         * Event emitted on status updates.
         * <p>
         * When the JOD instance detect a status update, emits this event.
         * This event contains the old and new status value.
         *
         * <pre>
         *     {"comp": "Group that contains status&gt;State On/Off", "name": "State On/Off", "update": "new:true old:false"}
         *     {"comp": "Group that contains status&gt;State 0-50", "name": "State 0-50", "update": "new:5.000000 old:0.000000"}
         * </pre>
         */
        JOD_STATUS_UPD,

        /**
         * Event emitted on action request.
         * <p>
         * When the JOD instance receive an action request, emits this event.
         * This event contains the action requester info.
         *
         * <pre>
         *     {"srvId": "jcp-fe", "usrId": "3d6bd9ed-8d63-43d6-a465-dbe9029e04c8", "connType": "LocalAndCloud", "component": "Light example&gt;Switch", "command": "JOSPBoolean"}
         * </pre>
         */
        JOD_ACTION_REQ,
        /**
         * Event emitted on action execution.
         * <p>
         * When the JOD instance received an action request and execute it,
         * emits this event.
         * This event contains the action requester info.
         *
         * <code>
         * {"srvId": "jcp-fe", "usrId": "3d6bd9ed-8d63-43d6-a465-dbe9029e04c8", "connType": "LocalAndCloud", "component": "Light example&gt;Switch", "command": "JOSPBoolean"}
         * </code>
         */
        JOD_ACTION_EXEC
    }


    // Internal constants

    private static final String EVENTS_REQ_FORMAT = "id:%s;type:%s;srcId:%s;srcType:%s;emittedAt:%s;phase:%s;payload:%s;errorPayload:%s";


    // Internal vars

    private final long id;
    private final Type type;
    private final String srcId;
    private final SrcType srcType;
    private final Date emittedAt;
    private final String phase;
    private final String payload;
    private final String errorPayload;


    // Constructor

    @JsonCreator
    public JOSPEvent(@JsonProperty("id") long id,
                     @JsonProperty("type") Type type,
                     @JsonProperty("srcId") String srcId,
                     @JsonProperty("srcType") SrcType srcType,
                     @JsonProperty("emittedAt") Date emittedAt,
                     @JsonProperty("phase") String phase,
                     @JsonProperty("payload") String payload,
                     @JsonProperty("errorPayload") String errorPayload) {
        this.id = id;
        this.type = type;
        this.srcId = srcId;
        this.srcType = srcType;
        this.emittedAt = emittedAt;
        this.phase = phase;
        this.payload = payload;
        this.errorPayload = errorPayload;
    }


    // Getters

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public String getSrcId() {
        return srcId;
    }

    public SrcType getSrcType() {
        return srcType;
    }

    public Date getEmittedAt() {
        return emittedAt;
    }

    @JsonIgnore
    public String getEmittedAtStr() {
        return JavaDate.DEF_DATE_FORMATTER.format(emittedAt);
    }

    public String getPhase() {
        return phase;
    }

    public String getPayload() {
        return payload;
    }

    public String getErrorPayload() {
        return errorPayload;
    }


    // Converters

    public static String toString(JOSPEvent event) {
        return String.format(EVENTS_REQ_FORMAT, event.getId(), event.getType(), event.getSrcId(), event.getSrcType(), JavaDate.DEF_DATE_FORMATTER.format(event.getEmittedAt()), event.phase, event.getPayload(), event.getErrorPayload());
    }

    public static String toString(List<JOSPEvent> statusesHistory) {
        StringBuilder str = new StringBuilder();
        for (JOSPEvent e : statusesHistory) {
            str.append(toString(e));
            str.append("\n");
        }

        return str.toString();
    }

    public static JOSPEvent fromString(String eventStr) throws JOSPProtocol.ParsingException {
        String[] eventStrs = eventStr.split(";");
        if (eventStrs.length != 8)
            throw new JOSPProtocol.ParsingException("Few fields in JOSPEvent string");

        String id = eventStrs[0].substring(eventStrs[0].indexOf(":") + 1);
        String type = eventStrs[1].substring(eventStrs[1].indexOf(":") + 1);
        String srcId = eventStrs[2].substring(eventStrs[2].indexOf(":") + 1);
        String srcType = eventStrs[3].substring(eventStrs[3].indexOf(":") + 1);
        String emittedAt = eventStrs[4].substring(eventStrs[4].indexOf(":") + 1);
        String phase = eventStrs[5].substring(eventStrs[5].indexOf(":") + 1);
        String payload = eventStrs[6].substring(eventStrs[6].indexOf(":") + 1);
        String errorPayload = eventStrs[7].substring(eventStrs[7].indexOf(":") + 1);

        try {
            return new JOSPEvent(Long.parseLong(id), Type.valueOf(type), srcId, SrcType.valueOf(srcType), JavaDate.DEF_DATE_FORMATTER.parse(emittedAt), phase, payload, errorPayload);

        } catch (ParseException e) {
            throw new JOSPProtocol.ParsingException(String.format("Error parsing JOSPEvent fields: %s", e.getMessage()));
        }
    }

    public static List<JOSPEvent> listFromString(String eventsHistoryStr) throws JOSPProtocol.ParsingException {
        List<JOSPEvent> statuses = new ArrayList<>();

        for (String eventStr : eventsHistoryStr.split("\n"))
            statuses.add(fromString(eventStr));

        return statuses;
    }

    public static String logEvents(List<JOSPEvent> events, boolean showSrcInfo) {
        StringBuilder str = new StringBuilder();
        if (showSrcInfo) {
            str.append("  +-------+--------------------+------------+------------+----------------------+----------------------+------------------------------------------+------------------------------------------+\n");
            str.append("  | ID    | Emitted At         | SrcType    | SrcId      | EventType            | Phase                | Payload                                  | Error                                    |\n");
            //str.append("  | 00000 | 20201029-172422355 | 1234567890 | 1234567890 | 12345678901234567890 | 12345678901234567890 | 1234567890123456789012345678901234567890 | 1234567890123456789012345678901234567890 |\n");
            str.append("  +-------+--------------------+------------+------------+----------------------+----------------------+------------------------------------------+------------------------------------------+\n");
        } else {
            str.append("  +-------+--------------------+----------------------+----------------------+------------------------------------------+------------------------------------------+\n");
            str.append("  | ID    | Emitted At         | EventType            | Phase                | Payload                                  | Error                                    |\n");
            //str.append("  | 00000 | 20201029-172422355 | 12345678901234567890 | 12345678901234567890 | 1234567890123456789012345678901234567890 | 1234567890123456789012345678901234567890 |\n");
            str.append("  +-------+--------------------+----------------------+----------------------+------------------------------------------+------------------------------------------+\n");
        }

        for (JOSPEvent e : events) {
            if (showSrcInfo) {
                int pLength = e.getPayload().length();
                String payloadTruncated = pLength < 30 ? e.getPayload() : "..." + e.getPayload().substring(pLength - 37);
                int peLength = e.getErrorPayload().length();
                String payloadErrorTruncated = pLength < 30 ? e.getErrorPayload() : "..." + e.getErrorPayload().substring(peLength - 37);
                str.append(String.format("  | %-5s | %-18s | %-10s | %-10s | %-10s | %-10s | %-30s | %-30s |\n",
                        e.getId(), e.getEmittedAtStr(), e.getType(), e.getSrcType(), e.getSrcId(), e.getPhase(), payloadTruncated, payloadErrorTruncated));
            } else
                str.append(String.format("  | %-5s | %-18s | %-20s | %-20s | %-30s | %-30s\n",
                        e.getId(), e.getEmittedAtStr(), e.getType(), e.getPhase(), e.getPayload(), e.getErrorPayload()));
        }

        if (showSrcInfo)
            str.append("  +-------+--------------------+---------------+---------------+-------------------------+----------------------+------------------------------------------+------------------------------------------+\n");
        else
            str.append("  +-------+--------------------+-------------------------+----------------------+------------------------------------------+------------------------------------------+\n");

        return str.toString();
    }

}
