package com.robypomper.josp.jcp.defs.jslwebbridge.internal.status;

import com.robypomper.josp.types.RESTItemList;

import java.util.Date;
import java.util.List;


/**
 * JCP JSL Web Bridge - Status 2.0
 */
public class Params20 {

    // Index

    public static class Index {

        public final String urlSessions = Paths20.FULL_PATH_STATUS_SESSIONS;

    }


    // Sessions

    public static class Sessions {

        public List<RESTItemList> sessionsList;

    }

    public static class Session {

        public String id;
        public String name;
        public Date createdAt;
        public Date lastAccessedAt;
        public int maxInactiveInterval; // seconds

    }

}
