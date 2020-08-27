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

import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.comm.JODCommunication;
import com.robypomper.josp.jod.comm.JODCommunication_002;
import com.robypomper.josp.jod.comm.JODLocalClientInfo;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.test.mocks.jod.MockJCPClient_Object;
import com.robypomper.josp.test.mocks.jod.MockJODObjectInfo;
import com.robypomper.josp.test.mocks.jod.MockJODPermissions;
import com.robypomper.josp.test.mocks.jsl.MockJCPClient_Service;
import com.robypomper.josp.test.mocks.jsl.MockJSLServiceInfo;
import com.robypomper.josp.test.mocks.jsl.MockJSLUserMngr_002;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JOSPCommunicationIntegration {

    // Class constants

    protected static final String TEST_FILES_PREFIX = "tmp/tests/";
    protected static final String UNIQUE_ID = "123456789";


    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected static int port = 1234;
    JODSettings_002 jodSettings;
    JCPClient_Object jcpClientObj;
    JODObjectInfo objInfo;
    JODPermissions jodPermissions;
    JSLSettings_002 jslSettings;
    JCPClient_Service jcpClientSrv;
    JSLServiceInfo srvInfo;
    JSLUserMngr jslUserMngr;
    JSLObjsMngr jslObjsMngr;

    // Test config

    @BeforeEach
    public void setUp() {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Create test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.mkdirs();

        port += 2;

        // Init JOD Comm params
        jodSettings = new JODSettings_002(getDefaultJODSettings(port));
        jcpClientObj = new MockJCPClient_Object();
        objInfo = new MockJODObjectInfo("objId");
        jodPermissions = new MockJODPermissions();

        // Init JSL Comm params
        jslSettings = new JSLSettings_002(getDefaultJSLSettings());
        jcpClientSrv = new MockJCPClient_Service();
        srvInfo = new MockJSLServiceInfo("srvId/usrId/instId");
        jslUserMngr = new MockJSLUserMngr_002();
        //jslObjsMngr = new MockJSLObjsMngr_002();
        jslObjsMngr = new JSLObjsMngr_002(jslSettings, srvInfo);

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        // Empty test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        for (String s : Objects.requireNonNull(testDirFiles.list())) {
            File currentFile = new File(testDirFiles.getPath(), s);
            //noinspection ResultOfMethodCallIgnored
            currentFile.delete();
        }
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.delete();
    }


    // Publish/Discovery and connect/disconnect

    @Test
    public void testLocalPublishAndDiscoveryFirstJOD() throws JODCommunication.LocalCommunicationException, JODCommunication.CloudCommunicationException, JSLCommunication.LocalCommunicationException, JSLCommunication.CloudCommunicationException, SocketException {
        System.out.println("\nJOD LOCAL COMM START");
        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.startLocal();


        int ntwkIntfs = getNetworkInterfacesCount();
        try {
            Thread.sleep(ntwkIntfs * 1000);
        } catch (InterruptedException ignore) {}

        Assertions.assertEquals(1, getJODLocConnCount(jodComm));
        Assertions.assertEquals(1, getJODLocConnConnectedCount(jodComm));
        Assertions.assertEquals(1, getJSLLocConnCount(jslComm));
        Assertions.assertEquals(1, getJSLLocConnConnectedCount(jslComm));

        System.out.println("\nJOD and JSL LOCAL COM STOP");
        jodComm.stopLocal();
        jslComm.stopLocal();
    }

    @Test
    public void testLocalPublishAndDiscoveryFirstJSL() throws JODCommunication.LocalCommunicationException, JODCommunication.CloudCommunicationException, JSLCommunication.LocalCommunicationException, JSLCommunication.CloudCommunicationException, SocketException {
        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.startLocal();

        System.out.println("\nJOD LOCAL COMM START");
        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, UNIQUE_ID);
        jodComm.startLocal();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {}

        int ntwkIntfs = getNetworkInterfacesCount();
        try {
            Thread.sleep(ntwkIntfs * 1000);
        } catch (InterruptedException ignore) {}

        Assertions.assertEquals(1, getJODLocConnCount(jodComm));
        Assertions.assertEquals(1, getJODLocConnConnectedCount(jodComm));
        Assertions.assertEquals(1, getJSLLocConnCount(jslComm));
        Assertions.assertEquals(1, getJSLLocConnConnectedCount(jslComm));

        System.out.println("\nJOD and JSL LOCAL COM STOP");
        jodComm.stopLocal();
        jslComm.stopLocal();
    }

    @Test
    public void testLocalPublishAndDiscoveryStopJOD() throws JODCommunication.LocalCommunicationException, JODCommunication.CloudCommunicationException, JSLCommunication.LocalCommunicationException, JSLCommunication.CloudCommunicationException, SocketException {
        System.out.println("\nJOD LOCAL COMM START");
        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.startLocal();

        int ntwkIntfs = getNetworkInterfacesCount();
        try {
            Thread.sleep(ntwkIntfs * 1000);
        } catch (InterruptedException ignore) {}

//        for (JODLocalConnection c: jodComm.getAllLocalConnection())
//            System.out.println("#" + c.getClientId() + "\t" + c.getPeerAddress() + "\t" + c.getPeerPort() + "\t" + c.isConnected());
//        for (JSLLocalClient c: jslComm.getAllLocalConnection())
//            System.out.println("@" + c.getClientId() + "\t" + c.getObjId() + "\t" + c.getServerAddr() + "\t" + c.getServerPort() + "\t" + c.isConnected());

        System.out.println("\nJOD LOCAL COM STOP");
        jodComm.stopLocal();


        Assertions.assertEquals(1, getJODLocConnCount(jodComm));
        Assertions.assertEquals(0, getJODLocConnConnectedCount(jodComm));
        Assertions.assertEquals(0, getJSLLocConnCount(jslComm));
        Assertions.assertEquals(0, getJSLLocConnConnectedCount(jslComm));

        System.out.println("\nJSL LOCAL COM STOP");
        jslComm.stopLocal();
    }

    @Test
    public void testLocalPublishAndDiscoveryStopJSL() throws JODCommunication.LocalCommunicationException, JODCommunication.CloudCommunicationException, JSLCommunication.LocalCommunicationException, JSLCommunication.CloudCommunicationException, SocketException {
        System.out.println("\nJOD LOCAL COMM START");
        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.startLocal();

        int ntwkIntfs = getNetworkInterfacesCount();
        try {
            Thread.sleep(ntwkIntfs * 1000);
        } catch (InterruptedException ignore) {}


        System.out.println("\nJSL LOCAL COM STOP");
        jslComm.stopLocal();

//        for (JODLocalConnection c: jodComm.getAllLocalConnection())
//            System.out.println("#" + c.getClientId() + "\t" + c.getPeerAddress() + "\t" + c.getPeerPort() + "\t" + c.isConnected());
//        for (JSLLocalClient c: jslComm.getAllLocalConnection())
//            System.out.println("@" + c.getClientId() + "\t" + c.getObjId() + "\t" + c.getServerAddr() + "\t" + c.getServerPort() + "\t" + c.isConnected());
//
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {}
        Assertions.assertEquals(1, getJODLocConnCount(jodComm));
        Assertions.assertEquals(0, getJODLocConnConnectedCount(jodComm));
        Assertions.assertEquals(0, getJSLLocConnCount(jslComm));
        Assertions.assertEquals(0, getJSLLocConnConnectedCount(jslComm));

        System.out.println("\nJOD LOCAL COM STOP");
        jodComm.stopLocal();
    }


    // Multiple server

    @Test()
    public void testLocalPublishAndDiscoveryTwoJOD() throws JODCommunication.LocalCommunicationException, JODCommunication.CloudCommunicationException, JSLCommunication.LocalCommunicationException, JSLCommunication.CloudCommunicationException {
        System.out.println("\nJOD LOCAL COMM START (1st)");
        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJOD LOCAL COMM START (2nd)");
        port += 2;
        JODSettings_002 jodSettings2 = new JODSettings_002(getDefaultJODSettings(port));
        JODObjectInfo objInfo2 = new MockJODObjectInfo("objId_2");
        JODCommunication jodComm2 = new JODCommunication_002(jodSettings2, objInfo2, jcpClientObj, jodPermissions, UNIQUE_ID + "bis");
        jodComm2.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr, UNIQUE_ID + "srv");
        jslComm.startLocal();

        int ntwkIntfs = 1;//getNetworkInterfacesCount();
        try {
            Thread.sleep(ntwkIntfs * 1000);
            if (ntwkIntfs * 2 > getJSLLocConnCount(jslComm)) {
                int extraSecs = (ntwkIntfs * 2 - getJSLLocConnCount(jslComm));
                System.out.printf("\nEXTRA TIME (%s)%n", extraSecs);
                Thread.sleep(extraSecs * 1000);
            }
        } catch (InterruptedException ignore) {}

//        for (JSLLocalClient c : jslComm.getAllLocalServers())
//            System.out.println(c.getClientId() + "\t" + c.getObjId() + "\t" + c.getServerAddr() + "\t" + c.getServerPort() + "\t" + c.isConnected());

//        Assertions.assertEquals(ntwkIntfs, getJODLocConnCount(jodComm));
//        Assertions.assertEquals(ntwkIntfs, getJODLocConnConnectedCount(jodComm));
//        Assertions.assertEquals(ntwkIntfs, getJODLocConnCount(jodComm2));
//        Assertions.assertEquals(ntwkIntfs, getJODLocConnConnectedCount(jodComm2));
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignore) {}
        Assertions.assertEquals(2, getJSLLocConnCount(jslComm));
        Assertions.assertEquals(2, getJSLLocConnConnectedCount(jslComm));

        System.out.println("\nJOD and JSL LOCAL COM STOP");
        jodComm.stopLocal();
        jodComm2.stopLocal();
        jslComm.stopLocal();
    }


    private int getJODLocConnCount(JODCommunication jodComm) {
        return jodComm.getAllLocalClientsInfo().size();
    }

    private int getJODLocConnConnectedCount(JODCommunication jodComm) {
        int count = 0;
        for (JODLocalClientInfo conn : jodComm.getAllLocalClientsInfo())
            if (conn.isConnected())
                count++;
        return count;
    }

    private int getJSLLocConnCount(JSLCommunication jslComm) {
        return jslComm.getAllLocalServers().size();
    }

    private int getJSLLocConnConnectedCount(JSLCommunication jslComm) {
        int count = 0;
        for (JSLLocalClient conn : jslComm.getAllLocalServers())
            if (conn.isConnected())
                count++;
        return count;
    }

    private int getNetworkInterfacesCount() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        int count = 0;
        while (interfaces.hasMoreElements()) {
            interfaces.nextElement();
            count++;
        }
        return count;
    }


    private Map<String, Object> getDefaultJODSettings(int port) {
        Map<String, Object> properties = new HashMap<>();

        // Comm's paths
        properties.put(JODSettings_002.JODCOMM_LOCAL_ENABLED, "true");
        properties.put(JODSettings_002.JODCOMM_LOCAL_PORT, Integer.toString(port));
        properties.put(JODSettings_002.JODCOMM_CLOUD_ENABLED, "false");
        return properties;
    }

    private Map<String, Object> getDefaultJSLSettings() {
        Map<String, Object> properties = new HashMap<>();

        // Comm's paths
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, "true");
        properties.put(JSLSettings_002.JSLCOMM_CLOUD_ENABLED, "false");
        return properties;
    }

}