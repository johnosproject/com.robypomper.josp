package com.robypomper.josp.defs.core.events;

import com.robypomper.josp.types.josp.AgentType;
import com.robypomper.josp.types.josp.EventType;

import java.util.Date;


/**
 * JOSP Core - Events 2.0
 */
public class Params20 {

    // Events

    public static class Event {

        public long id;
        public EventType type;
        public String srcId;
        public AgentType srcType;
        public Date emittedAt;
        public String phase;
        public String payload;
        public String errorPayload;

    }

}
