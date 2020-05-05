package com.robypomper.josp.jod;

import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.systems.JODCommunication;
import com.robypomper.josp.jod.systems.JODExecutorMngr;
import com.robypomper.josp.jod.systems.JODObjectInfo;
import com.robypomper.josp.jod.systems.JODPermissions;
import com.robypomper.josp.jod.systems.JODStructure;

import java.io.File;


/**
 * Main interface for JOD object.
 *
 * This interface define methods to initialize and manage a JOD object. Moreover,
 * it define also JOD support classes, struct and exceptions.
 */
public interface JOD {

    // New instance method

    /**
     * Static method to generate JOD object from <code>settings</code> configs.
     *
     * <b>This method (from {@link JOD} interface) return null object</b>,
     * because JOD sub-classes must re-implement the same method and
     * return sub-class instance.
     *
     * @param settings JOD.Settings containing JOD settings. Sub-classes can
     *                 extend JOD.Settings and and used extended class as
     *                 <code>instance</code> param.
     * @return null pointer.
     */
    static JOD instance(Settings settings) {
        return null;
    }


    // JOD implementation version

    /**
     * @return the JOD object implementation version.
     */
    String version();


    // JOD mngm

    /**
     * Start current JOD object and all his systems.
     *
     * Update the JOD object's status {@link #status()}.
     *
     * @throws RunException thrown if errors occurs on JOD object start.
     */
    void start() throws RunException;

    /**
     * Stop current JOD object and all his systems.
     *
     * Update the JOD object's status {@link #status()}.
     *
     * @throws RunException thrown if errors occurs on JOD object stop.
     */
    void stop() throws RunException;

    /**
     * Stop then restart current JOD object and all his systems.
     *
     * Update the JOD object's status {@link #status()}.
     *
     * @throws RunException thrown if errors occurs on JOD object stop and start.
     */
    void restart() throws RunException;

    /**
     * @return the JOD object's status.
     */
    Status status();


    // JOD Systems

    JCPClient_Object getJCPClient();
    JODObjectInfo getObjectInfo();
    JODStructure getObjectStructure();
    JODCommunication getCommunication();
    JODExecutorMngr getExecutor();
    JODPermissions getPermission();


    // Support struct and classes

    /**
     * JOD Objects statuses.
     */
    enum Status {
        /**
         * JOD object is starting.
         *
         * The method {@link #start()} was called, when finish the status become
         * {@link #RUNNING} or {@link #STOPPED} if error occurs.
         */
        STARTING,

        /**
         * JOD object is running.
         *
         * The method {@link #start()} was called and the JOD object was started
         * successfully.
         */
        RUNNING,

        /**
         * JOD object is shouting down.
         *
         * The method {@link #stop()} was called, when finish the status become
         * {@link #STOPPED}.
         */
        SHUTDOWN,

        /**
         * JOD object is stopped.
         *
         * The method {@link #stop()} was called and the JOD object was stopped
         * successfully.
         */
        STOPPED,

        /**
         * JOD object is shouting down and restarted.
         * <p>
         * The method {@link #restart()} was called, when finish the status become
         * {@link #RUNNING} or {@link #STOPPED} if error occurs.
         */
        REBOOTING
    }

    /**
     * JOD's settings interface.
     *
     * JOD.Settings implementations can be used by JOD implementations to
     * customize settings for specific JOD implementations.
     */
    interface Settings {

        // New instance method

        /**
         * Static method to generate JOD.Settings object from <code>file</code>
         * configs.
         *
         * <b>This method (from {@link JOD.Settings} interface) return null object</b>,
         * because JOD.Settings sub-classes must re-implement the same method and
         * return sub-class instance.
         *
         * @param file file to be parsed as configs file.
         * @return null pointer.
         */
        static Settings instance(File file) {
            return null;
        }


        // JOD.Settings implementation version

        /**
         * @return the JOD.Settings implementation version.
         */
        String version();


        // JOD settings

        /**
         * JOD required version.
         *
         * The required JOD version is read from configs file (optionally from
         * cmdLine args) and is used to get right {@link JOD} and
         * {@link JOD.Settings} implementations. Then this implementations can
         * expose different JOD's version.
         *
         * To get actually using JOD's version, look at {@link JOD#version()}
         * method.
         *
         * @return the JOD required version.
         */
        String getJODVersion_Required();

        /**
         * Set JOD required version.
         *
         * @param jodVer the JOD required version to set.
         * @param storageUpd if true, store given value on configs file and make
         *                   it available at next JOD reboot.
         */
        void setJODVersion_Required(String jodVer, boolean storageUpd);

    }


    // Exceptions

    /**
     * Exceptions for JOD object startup and shout down errors.
     */
    class RunException extends Throwable {
        public RunException(String msg) {
            super(msg);
        }
        public RunException(String msg, Exception e) {
            super(msg,e);
        }
    }

    /**
     * Exceptions for {@link JOD} and {@link JOD.Settings} object creation.
     */
    class FactoryException extends Throwable {
        public FactoryException(String msg) {
            super(msg);
        }
        public FactoryException(String msg, Exception e) {
            super(msg,e);
        }
    }

}
