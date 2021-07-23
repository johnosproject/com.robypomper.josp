/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
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
 ******************************************************************************/

package com.robypomper.josp.test.mocks.jod;

import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;

public class MockJODObjectInfo implements JODObjectInfo {

    private final String objId;

    public MockJODObjectInfo() {
        objId = "";
    }

    public MockJODObjectInfo(String objId) {
        this.objId = objId;
    }

    @Override
    public void setSystems(JODStructure structure, JODExecutorMngr executor, JODCommunication comm, JODPermissions permissions) {
    }

    @Override
    public String getJODVersion() {
        return null;
    }

    @Override
    public String getObjId() {
        return objId;
    }

    @Override
    public String getObjName() {
        return null;
    }

    @Override
    public void setObjName(String newName) {
    }

    @Override
    public String getOwnerId() {
        return null;
    }

    @Override
    public void setOwnerId(String ownerId) {

    }

    @Override
    public void resetOwnerId() {

    }

    @Override
    public String getFullId() {
        return null;
    }

    @Override
    public String getStructurePath() {
        return null;
    }

    @Override
    public String readStructureStr() {
        return null;
    }

    @Override
    public String getStructForJSL() {
        return null;
    }

    @Override
    public String getBrand() {
        return null;
    }

    @Override
    public String getModel() {
        return null;
    }

    @Override
    public String getLongDescr() {
        return null;
    }

    @Override
    public void startAutoRefresh() {
    }

    @Override
    public void stopAutoRefresh() {
    }

    @Override
    public void syncObjInfo() {
    }

}
