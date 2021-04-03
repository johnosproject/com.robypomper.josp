package com.robypomper.josp.test.mocks.jod;

import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.clients.JCPAPIsClientObj;
import com.robypomper.josp.jod.events.JODEvents;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.types.josp.EventType;

import java.util.List;

public class MockJODEvents implements JODEvents {

    @Override
    public void setJCPClient(JCPAPIsClientObj jcpClient) {
    }

    @Override
    public void register(EventType type, String phase, String payload) {
    }

    @Override
    public void register(EventType type, String phase, String payload, Throwable error) {
    }

    @Override
    public List<JOSPEvent> getHistoryEvents(HistoryLimits limits) {
        return null;
    }

    @Override
    public List<JOSPEvent> filterHistoryEvents(HistoryLimits limits, JavaJSONArrayToFile.Filter<JOSPEvent> filter) {
        return null;
    }

    @Override
    public void startCloudSync() {
    }

    @Override
    public void stopCloudSync() {
    }

    @Override
    public void storeCache() {
    }

}
