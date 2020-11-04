package com.robypomper.josp.jod.history;

import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.protocol.JOSPStatusHistory;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class StatusHistoryArray extends JavaJSONArrayToFile<JOSPStatusHistory, Long> {

    public StatusHistoryArray(File jsonFile) throws IOException {
        super(jsonFile, JOSPStatusHistory.class);
    }

    @Override
    protected int compareItemIds(Long id1, Long id2) {
        return (int) (id1 - id2);
    }

    @Override
    protected Long getItemId(JOSPStatusHistory value) {
        return value.getId();
    }

    @Override
    protected Date getItemDate(JOSPStatusHistory value) {
        return value.getUpdatedAt();
    }

}
