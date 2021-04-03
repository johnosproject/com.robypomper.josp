package com.robypomper.josp.defs.core.objects;

import java.util.Date;


/**
 * JOSP Core - Objects 2.0
 */
public class Params20 {

    // Generator

    public static class GenerateObjId {

        public String objIdHw;
        public String ownerId;

    }


    // History

    public static class HistoryStatus {

        public long id;
        public String compPath;
        public String compType;
        public Date updatedAt;
        public String payload;

    }

}
