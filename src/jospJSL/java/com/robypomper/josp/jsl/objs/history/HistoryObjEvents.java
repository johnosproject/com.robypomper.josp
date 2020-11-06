package com.robypomper.josp.jsl.objs.history;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;

import java.util.List;

public interface HistoryObjEvents {

    JSLRemoteObject getObject();

    List<JOSPEvent> getEventsHistory(HistoryLimits limits, long timeout) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    void getEventsHistory(HistoryLimits limits, EventsListener listener) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    interface EventsListener {

        void receivedEvents(List<JOSPEvent> history);

    }

}
