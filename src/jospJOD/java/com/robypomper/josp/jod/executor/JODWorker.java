package com.robypomper.josp.jod.executor;

import com.robypomper.josp.jod.structure.JODComponent;


/**
 * Basic interface for JOD executor manager worker representations.
 */
public interface JODWorker {

    // Getters

    /**
     * Current JODWorker's name.
     *
     * @return the JODWorker's name.
     */
    String getName();

    /**
     * Current JODWorker's protocol.
     *
     * @return the JODWorker's protocol.
     */
    String getProto();

    /**
     * Current JODWorker's component.
     *
     * @return the JODWorker's component.
     */
    JODComponent getComponent();

    /**
     * @return <code>true</code> if current worker is enabled, <code>false</code>
     * otherwise.
     */
    boolean isEnabled();


    // Exceptions

    /**
     * Exceptions for {@link JODWorker} object creation.
     */
    class FactoryException extends Throwable {
        public FactoryException(String msg) {
            super(msg);
        }

        public FactoryException(String msg, Exception e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions for missing mandatory property or with wrong value from the
     * configs string during the {@link JODWorker} object initialization.
     */
    class MissingPropertyException extends Throwable {
        private static final String MSG_MISSING_PROP = "Mandatory property '%s' for '%s://%s' %s is missing.";

        public MissingPropertyException(String property, String proto, String workerName, String workerType) {
            super(String.format(MSG_MISSING_PROP, property, proto, workerName, workerType));
        }

        public MissingPropertyException(String property, String proto, String workerName, String workerType, Exception e) {
            super(String.format(MSG_MISSING_PROP, property, proto, workerName, workerType), e);
        }
    }

    /**
     * Exceptions for missing mandatory property or with wrong value from the
     * configs string during the {@link JODWorker} object initialization.
     */
    class ParsingPropertyException extends Throwable {
        private static final String MSG_WRONG_VAL = "Property '%s' for '%s://%s' value '%s' not valid ('%s').";

        public ParsingPropertyException(String property, String proto, String workerName, String workerType, String value) {
            super(String.format(MSG_WRONG_VAL, property, proto, workerName, workerType, value));
        }

        public ParsingPropertyException(String property, String proto, String workerName, String workerType, String value, Exception e) {
            super(String.format(MSG_WRONG_VAL, property, proto, workerName, workerType, value), e);
        }
    }

}
