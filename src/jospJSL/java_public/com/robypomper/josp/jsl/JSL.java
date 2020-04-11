package com.robypomper.josp.jsl;

import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.systems.JSLCommunication;
import com.robypomper.josp.jsl.systems.JSLObjsMngr;
import com.robypomper.josp.jsl.systems.JSLServiceInfo;
import com.robypomper.josp.jsl.systems.JSLUserMngr;

import java.io.File;


/**
 * Main interface for JSL library.
 * <p>
 * This interface define methods to initialize and manage a JSL library. Moreover,
 * it define also JSL support classes, struct and exceptions.
 */
public interface JSL {

    // New instance method

    /**
     * Static method to generate JSL library from <code>settings</code> configs.
     *
     * <b>This method (from {@link JSL} interface) return null object</b>,
     * because JSL sub-classes must re-implement the same method and
     * return sub-class instance.
     *
     * @param settings JSL.Settings containing JSL settings. Sub-classes can
     *                 extend JSL.Settings and used extended class as
     *                 <code>instance</code> param.
     * @return null pointer.
     */
    static JSL instance(Settings settings) {
        return null;
    }


    // JOD implementation version

    /**
     * @return the JSL library implementation version.
     */
    String version();


    // JSL mngm

    /**
     * Starts JSL library and his systems, then connect current JSL library
     * instance to the Gw S2O and to all available local JOD objects.
     *
     * @return <code>true</code> if connected to Gw O2S or if started the local
     * discovery system, or both.
     */
    boolean connect();

    /**
     * Disconnect current JSL library instance from the JCP cloud and from all
     * local JOD objects, then stops JSL and all his systems.
     *
     * @return <code>true</code> if disconnected from Gw O2S and stopped the local
     * discovery system.
     */
    boolean disconnect();

    /**
     * Disconnect and connect again current JSL library instance from the
     * JCP cloud and all local JOD objects.
     * <p>
     * This method also stop and start JSL instance and all his systems.
     *
     * @return <code>true</code> if the JSL library result connected.
     */
    boolean reconnect();

    /**
     * @return the JSL library's status.
     */
    Status status();


    // JSL Systems

    JCPClient_Service getJCPClient();

    JSLServiceInfo getServiceInfo();

    JSLUserMngr getUserMngr();

    JSLObjsMngr getObjsMngr();

    JSLCommunication getCommunication();


    // Support struct and classes

    /**
     * JSL Library statuses.
     */
    enum Status {

        /**
         * JSL library is starting.
         * <p>
         * The method {@link #connect()} was called, when finish the status become
         * {@link #CONNECTED} or {@link #DISCONNECTED} if error occurs.
         */
        CONNECTING,

        /**
         * JSL library is connected and operative.
         * <p>
         * The method {@link #connect()} was called and the JSL library was started
         * successfully.
         */
        CONNECTED,

        /**
         * JSL library is disconnecting.
         * <p>
         * The method {@link #disconnect()} was called, when finish the status
         * become {@link #DISCONNECTED}.
         */
        DISCONNECTING,

        /**
         * JSL library is disconnected.
         * <p>
         * The method {@link #disconnect()} was called and the JSL library was
         * stopped successfully.
         */
        DISCONNECTED,

        /**
         * JSL library is disconnecting and reconnecting.
         * <p>
         * The method {@link #reconnect()} was called, when finish the status
         * become {@link #CONNECTED} or {@link #DISCONNECTED} if error occurs.
         */
        RECONNECTING,

    }


    // Settings class

    /**
     * JSL's settings interface.
     * <p>
     * JSL.Settings implementations can be used by JSL implementations to
     * customize settings for specific JSL implementations.
     */
    interface Settings {

        // New instance method

        /**
         * Static method to generate JSL.Settings object from <code>file</code>
         * configs.
         *
         * <b>This method (from {@link JSL.Settings} interface) return null object</b>,
         * because JSL.Settings sub-classes must re-implement the same method and
         * return sub-class instance.
         *
         * @param file file to be parsed as configs file.
         * @return null pointer.
         */
        static Settings instance(File file) {
            return null;
        }


        // JSL.Settings implementation version

        /**
         * @return the JSL.Settings implementation version.
         */
        String version();


        // JSL settings

        /**
         * JSL required version.
         * <p>
         * The required JSL version is read from configs file (optionally from
         * JSL init method params) and is used to get right {@link JSL} and
         * {@link JSL.Settings} implementations. Then this implementations can
         * expose different JSL's version.
         * <p>
         * To get actually using JSL's version, look at {@link JSL#version()}
         * method.
         *
         * @return the JSL required version.
         */
        String getJSLVersion_Required();

        /**
         * Set JSL required version.
         *
         * @param jslVer     the JSL required version to set.
         * @param storageUpd if true, store given value on configs file and make
         *                   it available at next JSL reboot.
         */
        void setJSLVersion_Required(String jslVer, boolean storageUpd);

    }


    // Exceptions

    /**
     * Exceptions for JSL object connection and disconnection down errors.
     */
    class ConnectionException extends Throwable {
        public ConnectionException(String msg) {
            super(msg);
        }

        public ConnectionException(String msg, Exception e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions for {@link JSL} and {@link JSL.Settings} object creation.
     */
    class FactoryException extends Throwable {
        public FactoryException(String msg) {
            super(msg);
        }

        public FactoryException(String msg, Exception e) {
            super(msg, e);
        }
    }

}