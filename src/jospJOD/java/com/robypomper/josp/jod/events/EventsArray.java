package com.robypomper.josp.jod.events;

import com.robypomper.java.JavaJSONArrayToFile;
import com.robypomper.josp.protocol.JOSPEvent;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class EventsArray extends JavaJSONArrayToFile<JOSPEvent, Long> {

    public EventsArray(File jsonFile) throws IOException {
        super(jsonFile, JOSPEvent.class);
    }

    @Override
    protected int compareItemIds(Long id1, Long id2) {
        return (int) (id1 - id2);
    }

    @Override
    protected Long getItemId(JOSPEvent value) {
        return value.getId();
    }

    @Override
    protected Date getItemDate(JOSPEvent value) {
        return value.getEmittedAt();
    }

}
