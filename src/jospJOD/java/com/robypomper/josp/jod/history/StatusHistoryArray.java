package com.robypomper.josp.jod.history;

import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.io.File;
import java.io.IOException;

public class StatusHistoryArray extends JavaJSONArrayToFile<JOSPStatusHistory, Long> {

    public StatusHistoryArray(File jsonFile) throws IOException {
        super(jsonFile, JOSPStatusHistory.class);
    }

    @Override
    protected int compareItemIds(Long id1, Long id2) {
        return (int) (id2 - id1);
    }

    @Override
    protected Long getItemId(JOSPStatusHistory value) {
        return value.id;
    }

}
