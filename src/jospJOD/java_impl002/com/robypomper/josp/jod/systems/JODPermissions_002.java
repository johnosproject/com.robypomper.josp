package com.robypomper.josp.jod.systems;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.JOD;
import com.robypomper.josp.jod.permissions.JODService;
import com.robypomper.josp.jod.permissions.JODUser;

/**
 * ToDo: doc JODPermissions_002
 */
public class JODPermissions_002 implements JODPermissions {

    public JODPermissions_002(JOD.Settings settings, JODObjectInfo objInfo, JCPClient jcpClient) {
        System.out.println("DEB: JODPermissions_002()");
    }

    @Override
    public boolean canExecuteAction(JODService service, JODUser user) {
        System.out.println("DEB: JODPermissions_002::canExecuteAction()");
        return false;
    }

    @Override
    public boolean canSendUpdate(JODService service, JODUser user) {
        System.out.println("DEB: JODPermissions_002::canSendUpdate()");
        return false;
    }

    @Override
    public void startAutoRefresh() {
        System.out.println("DEB: JODPermissions_002::startAutoRefresh()");
    }

    @Override
    public void stopAutoRefresh() {
        System.out.println("DEB: JODPermissions_002::stopAutoRefresh()");
    }

    @Override
    public void loadPermissionsFromJCP() {
        System.out.println("DEB: JODPermissions_002::loadPermissionsFromJCP()");
    }

    @Override
    public void loadPermissionsFromFile() {
        System.out.println("DEB: JODPermissions_002::loadPermissionsFromFile()");
    }
}
