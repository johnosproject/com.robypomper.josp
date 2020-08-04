/* *****************************************************************************
 * The John Service Library is the software library to connect "software"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **************************************************************************** */

package com.robypomper.josp.jsl.comm;

import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;


/**
 * Interface for JSL Communication system.
 * <p>
 * With this system services can search for local JOD objects and can communicate
 * with cloud JOD objects. This class aim is to create connections and detect
 * when are closed. Then it advise the {@link JSLObjsMngr} system for connections
 * changes.
 */
public interface JSLCommunication {

    // To Object Msg

    // ...


    // From Object Msg

    boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType);


    // Connections access

    /**
     * @return the Gw S2O connection, null if not connected.
     */
    JSLGwS2OClient getCloudConnection();

    /**
     * @return an array containing all local connections.
     */
    List<JSLLocalClient> getAllLocalServers();

    /**
     * Remove given server connection from internal server connections list.
     * <p>
     * This method is called from {@link JSLLocalClient} on server disconnection.
     *
     * @param server the server connection to remove.
     */
    void removeServer(JSLLocalClient server);


    // Mngm methods

    /**
     * @return <code>true</code> if the local search system is active.
     */
    boolean isLocalRunning();

    /**
     * Start local Object's server and publish it.
     */
    void startLocal() throws LocalCommunicationException;

    /**
     * Stop local Object's server and de-publish it, then close all opened connections.
     */
    void stopLocal() throws LocalCommunicationException;

    /**
     * @return <code>true</code> if current JSL library is connected to the Gw S2O.
     */
    boolean isCloudConnected();

    /**
     * Start JOSP Gw S2O Client.
     */
    void connectCloud() throws CloudCommunicationException;

    /**
     * Stop JOSP Gw S2O Client.
     */
    void disconnectCloud();


    // Listeners

    void addListener(CommunicationListener listener);

    void removeListener(CommunicationListener listener);

    interface CommunicationListener {

        void onCloudConnected(JSLCommunication comm);

        void onCloudDisconnected(JSLCommunication comm);

        void onLocalStarted(JSLCommunication comm);

        void onLocalStopped(JSLCommunication comm);

    }


    // Exceptions

    /**
     * Exceptions for local communication errors.
     */
    class LocalCommunicationException extends Throwable {
        public LocalCommunicationException(String msg) {
            super(msg);
        }

        public LocalCommunicationException(String msg, Throwable e) {
            super(msg, e);
        }
    }

    /**
     * Exceptions for cloud communication errors.
     */
    class CloudCommunicationException extends Throwable {
        public CloudCommunicationException(String msg) {
            super(msg);
        }

        public CloudCommunicationException(String msg, Throwable e) {
            super(msg, e);
        }
    }

}
