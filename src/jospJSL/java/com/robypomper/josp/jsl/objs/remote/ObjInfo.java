package com.robypomper.josp.jsl.objs.remote;

import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLRoot;

public interface ObjInfo {

    // Getters / Setters

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
     * <p>
     * Send SetObjectName request to represented object.
     */
    void setName(String newName) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

    /**
     * @return the object's owner Id.
     */
    String getOwnerId();

    /**
     * Send a request to set the object's owner id.
     * <p>
     * Send SetObjectOwnerId request to represented object.
     */
    void setOwnerId(String newOwnerId) throws JSLRemoteObject.ObjectNotConnected, JSLRemoteObject.MissingPermission;

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


    // Listeners

    void addListener(RemoteObjectInfoListener listener);

    void removeListener(RemoteObjectInfoListener listener);

    interface RemoteObjectInfoListener {

        void onNameChanged(JSLRemoteObject obj, String newName, String oldName);

        void onOwnerIdChanged(JSLRemoteObject obj, String newOwnerId, String oldOwnerId);

        void onJODVersionChanged(JSLRemoteObject obj, String newJODVersion, String oldJODVersion);

        void onModelChanged(JSLRemoteObject obj, String newModel, String oldModel);

        void onBrandChanged(JSLRemoteObject obj, String newBrand, String oldBrand);

        void onLongDescrChanged(JSLRemoteObject obj, String newLongDescr, String oldLongDescr);

    }

}
