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

package com.robypomper.josp.jsl.objs;

import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.structure.JSLAction;
import com.robypomper.josp.jsl.objs.structure.JSLActionParams;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;
import com.robypomper.josp.protocol.JOSPPerm;

import java.util.List;


/**
 * JOD Object representation for JSL library.
 */
public interface JSLRemoteObject {

    // Object's info

    /**
     * @return the object's id.
     */
    String getId();

    /**
     * @return the object's name.
     */
    String getName();

    /**
     * Send a request to set the object's name.
     */
    void setName(String newName) throws ObjectNotConnected, MissingPermission;

    /**
     * @return the object's structure.
     */
    JSLRoot getStructure();

    /**
     * @return the object's owner Id.
     */
    String getOwnerId();

    /**
     * Send a request to set the object's owner id.
     */
    void setOwnerId(String newOwnerId) throws ObjectNotConnected, MissingPermission;

    /**
     * @return the object's JOD version.
     */
    String getJODVersion();

    /**
     * @return the object's model.
     */
    String getModel();

    /**
     * @return the object's brand.
     */
    String getBrand();

    /**
     * @return the object's long description.
     */
    String getLongDescr();

    /**
     * @return object's permissions.
     */
    List<JOSPPerm> getPerms();

    /**
     * @return the service's permission to access to object via given connection
     * type.
     */
    JOSPPerm.Type getServicePerm(JOSPPerm.Connection connType);

    void addPerm(String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws ObjectNotConnected, MissingPermission;

    void updPerm(String permId, String srvId, String usrId, JOSPPerm.Type permType, JOSPPerm.Connection connType) throws ObjectNotConnected, MissingPermission;

    void remPerm(String permId) throws ObjectNotConnected, MissingPermission;


    // Object's communication

    JSLCommunication getCommunication();

    /**
     * @return <code>true</code> if the objects is reachable via JCP or direct
     * communication.
     */
    boolean isConnected();

    /**
     * @return <code>true</code> if the objects is reachable via JCP.
     */
    boolean isCloudConnected();

    /**
     * @return <code>true</code> if and only if there at least is a valid and
     * open connection to the object.
     */
    boolean isLocalConnected();

    /**
     * Add given client as communication channel between this JSL object representation
     * and corresponding JOD object.
     * <p>
     * This method checks if it's the first client set for current object
     * representation then send object's presentation requests (objectInfo and
     * objectStructure).
     * <p>
     * If there is already a connected client, then this method disconnect given
     * one. Ther must be at least one connected client, others are used as
     * backups.
     *
     * @param localClient the client connected with corresponding JOD object.
     */
    void addLocalClient(JSLLocalClient localClient);

    void removeLocalClient(JSLLocalClient localClient);

    /**
     * Method called when a client disconnect and reconnect (same JOD IP address
     * but different ports) and update internal client list.
     *
     * @param oldClient the old client info.
     * @param newClient the updated client info.
     */
    void replaceLocalClient(JSLLocalClient oldClient, JSLLocalClient newClient);

    /**
     * @return an array containing all available local connections to the object.
     * if the object is disconnected or works only via cloud, then the
     * returned array will be empty.
     */
    List<JSLLocalClient> getLocalClients();

    /**
     * @return the client connected (if any) to the corresponding JOD object.
     */
    JSLLocalClient getConnectedLocalClient();


    // To Object Msg

    void sendObjectCmdMsg(JSLAction component, JSLActionParams command) throws ObjectNotConnected, MissingPermission;


    // From Object Msg

    boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) throws Throwable;


    // Listeners connections

    void addListener(RemoteObjectConnListener listener);

    void removeListener(RemoteObjectConnListener listener);

    interface RemoteObjectConnListener {

        void onLocalConnected(JSLRemoteObject obj, JSLLocalClient localClient);

        void onLocalDisconnected(JSLRemoteObject obj, JSLLocalClient localClient);

        void onCloudConnected(JSLRemoteObject obj);

        void onCloudDisconnected(JSLRemoteObject obj);

    }


    // Listeners info

    void addListener(RemoteObjectInfoListener listener);

    void removeListener(RemoteObjectInfoListener listener);

    interface RemoteObjectInfoListener {

        void onNameChanged(JSLRemoteObject obj, String newName, String oldName);

        void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId);

        void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion);

        void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel);

        void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand);

        void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr);

        void onStructureChanged(JSLRemoteObject obj, JSLRoot newRoot);

        void onPermissionsChanged(JSLRemoteObject obj, List<JOSPPerm> newPerms, List<JOSPPerm> oldPerms);

        void onServicePermChanged(JSLRemoteObject obj, JOSPPerm.Connection connType, JOSPPerm.Type newPermType, JOSPPerm.Type oldPermType);

    }


    // Exceptions

    /**
     * Exceptions thrown when accessing to a not connected object.
     */
    class ObjectNotConnected extends Throwable {
        private static final String MSG = "Can't access to '%s' object because not connected.";

        public ObjectNotConnected(JSLRemoteObject obj) {
            super(String.format(MSG, obj.getId()));
        }

        public ObjectNotConnected(JSLRemoteObject obj, Throwable t) {
            super(String.format(MSG, obj.getId()), t);
        }
    }

    /**
     * Exceptions thrown when accessing to a not connected object.
     */
    class MissingPermission extends Throwable {
        private static final String MSG = "Can't access to '%s' object because missing permission (required: %s; actual: %s; msg: '%s').";

        public MissingPermission(JSLRemoteObject obj, JOSPPerm.Connection onlyLocal, JOSPPerm.Type permType, JOSPPerm.Type minReqPerm, String msg) {
            super(String.format(MSG, obj.getId(), minReqPerm, permType, msg.substring(0, msg.indexOf('\n'))));
        }
    }

    /**
     * Exceptions thrown when accessing to a not connected object.
     */
    class ComponentInitException extends Throwable {
        private static final String MSG = "Error for object '%s' on %s.";

        public ComponentInitException(JSLRemoteObject obj, String msg) {
            super(String.format(MSG, obj.getId(), msg));
        }

        public ComponentInitException(JSLRemoteObject obj, String msg, Throwable t) {
            super(String.format(MSG, obj.getId(), msg), t);
        }
    }

    /**
     * Exceptions for structure parsing, file load... errors.
     */
    class ParsingException extends Throwable {
        private static final String MSG_WRAPPER = "Error for object '%s' on %s.";
        private static final String MSG = "(@line: %d; col: %d)";

        public ParsingException(JSLRemoteObject obj, String msg) {
            super(String.format(MSG_WRAPPER, obj.getId(), msg));
        }

        public ParsingException(JSLRemoteObject obj, String msg, Throwable e) {
            super(String.format(MSG_WRAPPER, obj.getId(), msg), e);
        }

        public ParsingException(JSLRemoteObject obj, String msg, Throwable e, int line, int col) {
            super(String.format(MSG_WRAPPER, obj.getId(), msg + String.format(MSG, line, col)));
        }
    }

    /**
     * Exception thrown when the structure initialization try to generate an
     * unknown component type.
     */
    class ParsingUnknownTypeException extends ParsingException {
        private static final String MSG = "Unknown type '%s' for '%s' JOD Component.";

        public ParsingUnknownTypeException(JSLRemoteObject obj, String compType, String compName) {
            super(obj, String.format(MSG, compType, compName));
        }
    }

}