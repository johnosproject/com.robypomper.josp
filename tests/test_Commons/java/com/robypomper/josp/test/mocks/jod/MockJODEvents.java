package com.robypomper.josp.test.mocks.jod;

import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.jod.events.JODEvents;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPEvent;

import java.io.IOException;
import java.util.List;

public class MockJODEvents implements JODEvents {

    @Override
    public void setJCPClient(JCPClient_Object jcpClient) {
    }

    @Override
    public void register(JOSPEvent.Type type, String phase, String payload) {
    }

    @Override
    public void register(JOSPEvent.Type type, String phase, String payload, Throwable error) {
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
    public void storeCache() throws IOException {
    }

}
