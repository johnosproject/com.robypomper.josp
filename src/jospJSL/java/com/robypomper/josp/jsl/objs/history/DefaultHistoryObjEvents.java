package com.robypomper.josp.jsl.objs.history;

import com.robypomper.communication.client.Client;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.protocol.*;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultHistoryObjEvents extends HistoryBase implements HistoryObjEvents {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLRemoteObject obj;
    private final Map<Integer, EventsListener> listeners = new HashMap<>();
    private int reqCount = 0;


    // Constructor

    public DefaultHistoryObjEvents(JSLRemoteObject obj, JSLServiceInfo srvInfo) {
        super(obj, srvInfo);
        this.obj = obj;
    }


    // Getters

    @Override
    public JSLRemoteObject getObject() {
        return obj;
    }

    @Override
    public List<JOSPEvent> getEventsHistory(HistoryLimits limits, long timeout) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        final List<JOSPEvent> result = new ArrayList<>();
        final CountDownLatch countdown = new CountDownLatch(1);
        // register internal listener
        int reqId = registerListener(new EventsListener() {
            @Override
            public void receivedEvents(List<JOSPEvent> history) {
                result.addAll(history);
                countdown.countDown();
            }
        });

        // send
        send(reqId, limits);

        // wait internal listener
        try {
            countdown.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
        }
        if (countdown.getCount() != 0)
            return null;

        return result;
    }

    @Override
    public void getEventsHistory(HistoryLimits limits, EventsListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        // register listener
        int reqId = registerListener(listener);
        // send
        send(reqId, limits);
    }

    private int registerListener(EventsListener listener) {
        listeners.put(reqCount, listener);
        return reqCount++;
    }

    private void send(int reqId, HistoryLimits limits) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission {
        try {
            sendToObjectCloudly(JOSPProtocol_ServiceToObject.HISTORY_EVENTS_REQ_MIN_PERM, JOSPProtocol_ServiceToObject.createHistoryEventsMsg(getServiceInfo().getFullId(), getRemote().getId(), Integer.toString(reqId), limits));

        } catch (Client.ServerNotConnectedException ignore) {
            sendToObjectLocally(JOSPProtocol_ServiceToObject.HISTORY_EVENTS_REQ_MIN_PERM, JOSPProtocol_ServiceToObject.createHistoryEventsMsg(getServiceInfo().getFullId(), getRemote().getId(), Integer.toString(reqId), limits));
        }
    }


    // Processing

    public boolean processHistoryEventsMsg(String msg) {
        // Received StatusHistory message
        System.out.println(String.format("Received Events Message for %s object", getRemote().getName()));

        String reqId;
        List<JOSPEvent> eventsHistory;
        try {
            reqId = JOSPProtocol_ObjectToService.getHistoryEventsMsg_ReqId(msg);
            eventsHistory = JOSPProtocol_ObjectToService.getHistoryEventsMsg_HistoryStatus(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.HISTORY_EVENTS_REQ_NAME, e.getMessage()), e);
            return false;
        }

        EventsListener l = listeners.get(Integer.parseInt(reqId));
        if (l == null) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because no listener expecting '%s' request", JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_NAME, reqId));
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                l.receivedEvents(eventsHistory);
            }
        }).start();

        return true;
    }

}
