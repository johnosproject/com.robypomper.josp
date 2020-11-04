package com.robypomper.josp.jsl.objs.history;

import com.robypomper.communication.client.Client;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
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

public class DefaultHistoryCompStatus extends HistoryBase implements HistoryCompStatus {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    private final JSLComponent comp;
    private Map<Integer, StatusHistoryListener> listeners = new HashMap<>();
    private int reqCount = 0;


    // Constructor

    public DefaultHistoryCompStatus(JSLComponent comp, JSLServiceInfo srvInfo) {
        super(comp.getRemoteObject(), srvInfo);
        this.comp = comp;
    }


    // Getters

    protected JSLComponent getComponent() {
        return comp;
    }

    protected JSLRemoteObject getRemote() {
        return comp.getRemoteObject();
    }

    @Override
    public List<JOSPStatusHistory> getStatusHistory(HistoryLimits limits, long timeout) {
        final List<JOSPStatusHistory> result = new ArrayList<>();
        final CountDownLatch countdown = new CountDownLatch(1);
        // register internal listener
        int reqId = registerListener(new StatusHistoryListener() {
            @Override
            public void receivedStatusHistory(List<JOSPStatusHistory> history) {
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
    public void getStatusHistory(HistoryLimits limits, StatusHistoryListener listener) {
        // register listener
        int reqId = registerListener(listener);
        // send
        send(reqId, limits);
    }

    private int registerListener(StatusHistoryListener listener) {
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

    public boolean processHistoryCompStatusMsg(String msg) {
        // Received StatusHistory message
        System.out.println(String.format("Received StatusHistory Message for %s component on %s object", getComponent().getName(), getRemote().getName()));

        String objId;
        String fullSrvId;
        String compPathStr;
        String reqId;
        List<JOSPStatusHistory> statusesHistory;
        try {
            objId = JOSPProtocol_ObjectToService.getHistoryCompStatusMsg_ObjId(msg);
            compPathStr = JOSPProtocol_ObjectToService.getHistoryCompStatusMsg_CompPath(msg);
            reqId = JOSPProtocol_ObjectToService.getHistoryCompStatusMsg_ReqId(msg);
            statusesHistory = JOSPProtocol_ObjectToService.getHistoryCompStatusMsg_HistoryStatus(msg);

        } catch (JOSPProtocol.ParsingException e) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because %s", JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_NAME, e.getMessage()), e);
            return false;
        }

        StatusHistoryListener l = listeners.get(Integer.parseInt(reqId));
        if (l == null) {
            log.warn(Mrk_JOD.JOD_COMM, String.format("Error on processing message %s because no listener expecting '%s' request", JOSPProtocol_ServiceToObject.HISTORY_STATUS_REQ_NAME, reqId));
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                l.receivedStatusHistory(statusesHistory);
            }
        }).start();

        return true;
    }

}
