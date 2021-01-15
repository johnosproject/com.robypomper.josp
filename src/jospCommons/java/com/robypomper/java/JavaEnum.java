package com.robypomper.java;

import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.Logger;

public class JavaEnum {

    public static class Synchronizable<E> {

        // Internal vars

        private E val;


        // Constructor

        public Synchronizable(E val) {
            this.val = val;
        }


        // Value access

        public void set(E val) {
            this.val = val;
        }

        public E get() {
            return val;
        }


        // Value comparison

        public boolean enumEquals(E obj) {
            return this.val == obj;
        }

        public boolean enumNotEquals(E obj) {
            return !enumEquals(obj);
        }

    }

    public static class SynchronizableState<E> extends Synchronizable<E> {

        // Internal vars

        private Logger log;
        private String logName;


        // Constructor

        public SynchronizableState(E val) {
            this(val, Thread.currentThread().getStackTrace()[2].getClassName().substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.') + 1), null);
        }

        public SynchronizableState(E val, String logName) {
            this(val, logName, null);
        }

        public SynchronizableState(E val, Logger log) {
            this(val, Thread.currentThread().getStackTrace()[2].getClassName().substring(Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf('.') + 1), log);
        }

        public SynchronizableState(E val, String logName, Logger log) {
            super(val);
            this.log = log;
            this.logName = logName;
        }


        // Value access

        public void set(E val) {
            set(log, val);
        }

        public void set(Logger log, E val) {
            if (log != null) {
                StackTraceElement caller = null;
                for (StackTraceElement e : Thread.currentThread().getStackTrace())
                    if (!e.getClassName().equals(Thread.class.getName())
                            && e.getClassName().equals(Thread.class.getName())) {
                        caller = e;
                        break;
                    }
                if (caller == null)
                    log.debug(Mrk_Commons.STATE, String.format("%s state = %s", logName, val));
                else
                    log.debug(Mrk_Commons.STATE, String.format("%s state = %s by %s::%s at line %d", logName, val, caller.getClassName(), caller.getMethodName(), caller.getLineNumber()));
            }
            super.set(val);
        }


        // Setters

        public void setLogName(String logName) {
            this.logName = logName;
        }

    }

}
