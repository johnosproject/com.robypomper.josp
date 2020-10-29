/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jod;

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.events.JODEvents;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.history.JODHistory;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;

import java.io.File;
import java.util.Map;


/**
 * Main interface for JOD object.
 * <p>
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
     * @return the JOSP JOD agent's implementation version.
     */
    String version();


    /**
     * @return the list of supported JOSP Protocol versions.
     */
    String[] versionsJOSPProtocol();


    /**
     * @return the list of supported JCP APIs versions.
     */
    String[] versionsJCPAPIs();


    // JOD mngm

    /**
     * Start current JOD object and all his systems.
     * <p>
     * Update the JOD object's status {@link #status()}.
     *
     * @throws RunException thrown if errors occurs on JOD object start.
     */
    void start() throws RunException;

    /**
     * Stop current JOD object and all his systems.
     * <p>
     * Update the JOD object's status {@link #status()}.
     *
     * @throws RunException thrown if errors occurs on JOD object stop.
     */
    void stop() throws RunException;

    /**
     * Stop then restart current JOD object and all his systems.
     * <p>
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

    JODEvents getEvents();

    JODHistory getHistory();


    // Support struct and classes

    /**
     * JOD Objects statuses.
     */
    enum Status {
        /**
         * JOD object is starting.
         * <p>
         * The method {@link #start()} was called, when finish the status become
         * {@link #RUNNING} or {@link #STOPPED} if error occurs.
         */
        STARTING,

        /**
         * JOD object is running.
         * <p>
         * The method {@link #start()} was called and the JOD object was started
         * successfully.
         */
        RUNNING,

        /**
         * JOD object is shouting down.
         * <p>
         * The method {@link #stop()} was called, when finish the status become
         * {@link #STOPPED}.
         */
        SHUTDOWN,

        /**
         * JOD object is stopped.
         * <p>
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
     * <p>
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

        /**
         * Static method to generate JOD.Settings object from <code>file</code>
         * configs.
         *
         * <b>This method (from {@link JOD.Settings} interface) return null object</b>,
         * because JOD.Settings sub-classes must re-implement the same method and
         * return sub-class instance.
         *
         * @param properties map containing the properties to set as JOD configurations.
         * @return null pointer.
         */
        static Settings instance(Map<String, Object> properties) {
            return null;
        }

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
            super(msg, e);
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
            super(msg, e);
        }
    }

}
