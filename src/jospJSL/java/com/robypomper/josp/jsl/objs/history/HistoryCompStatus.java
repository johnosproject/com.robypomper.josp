package com.robypomper.josp.jsl.objs.history;

import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.util.List;

public interface HistoryCompStatus {

    List<JOSPStatusHistory> getStatusHistory(HistoryLimits limits, long timeout);

    void getStatusHistory(HistoryLimits limits, StatusHistoryListener listener);

    interface StatusHistoryListener {

        void receivedStatusHistory(List<JOSPStatusHistory> history);

    }

}
