package com.robypomper.josp.jod.comm;

import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLCommunication_002;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.objs.JSLObjsMngr_002;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
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
import java.util.List;
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
        jodPermissions = new MockJODPermissions_002();

        // Init JSL Comm params
        jslSettings = new JSLSettings_002(getDefaultJSLSettings());
        jcpClientSrv = new MockJCPClient_Service();
        srvInfo = new JSLLocalClientTest.MockJSLServiceInfo("srvId/usrId/instId");
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
        Assertions.assertEquals(ntwkIntfs, getJSLLocConnCount(jslComm));
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
        Assertions.assertEquals(ntwkIntfs, getJSLLocConnCount(jslComm));
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
        Assertions.assertEquals(ntwkIntfs, getJSLLocConnCount(jslComm));
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
//        switchThread();
        Assertions.assertEquals(1, getJODLocConnCount(jodComm));
        Assertions.assertEquals(0, getJODLocConnConnectedCount(jodComm));
        Assertions.assertEquals(ntwkIntfs, getJSLLocConnCount(jslComm));
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

        int ntwkIntfs = getNetworkInterfacesCount();
        try {
            Thread.sleep(ntwkIntfs * 1000);
            if (ntwkIntfs * 2 > getJSLLocConnCount(jslComm)) {
                int extraSecs = (ntwkIntfs * 2 - getJSLLocConnCount(jslComm));
                System.out.printf("\nEXTRA TIME (%s)%n", extraSecs);
                Thread.sleep(extraSecs * 1000);
            }
        } catch (InterruptedException ignore) {}

        for (JSLLocalClient c : jslComm.getAllLocalServers())
            System.out.println(c.getClientId() + "\t" + c.getObjId() + "\t" + c.getServerAddr() + "\t" + c.getServerPort() + "\t" + c.isConnected());

//        Assertions.assertEquals(ntwkIntfs, getJODLocConnCount(jodComm));
//        Assertions.assertEquals(ntwkIntfs, getJODLocConnConnectedCount(jodComm));
//        Assertions.assertEquals(ntwkIntfs, getJODLocConnCount(jodComm2));
//        Assertions.assertEquals(ntwkIntfs, getJODLocConnConnectedCount(jodComm2));
        Assertions.assertEquals(ntwkIntfs * 2, getJSLLocConnCount(jslComm));
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
        properties.put(JODSettings_002.JODCOMM_LOCAL_KS_FILE, String.format(TEST_FILES_PREFIX + "server-%d.p12", port));
        properties.put(JODSettings_002.JODCOMM_LOCAL_CERT, String.format(TEST_FILES_PREFIX + "server-%d.crt", port));
        properties.put(JODSettings_002.JODCOMM_CLOUD_ENABLED, "false");
        properties.put(JODSettings_002.JODCOMM_CLOUD_CERT, TEST_FILES_PREFIX + "clientJODCloud.crt");
        properties.put(JODSettings_002.JODCOMM_CLOUD_CERT_REMOTE, "src/jcpJOSPGWs/certs/cloud/public/mainServer@GwObjService.crt");
        return properties;
    }

    private Map<String, Object> getDefaultJSLSettings() {
        Map<String, Object> properties = new HashMap<>();

        // Comm's paths
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, "true");
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_KS_FILE, TEST_FILES_PREFIX + "client.p12");
        properties.put(JSLSettings_002.JSLCOMM_CLOUD_ENABLED, "false");
        properties.put(JSLSettings_002.JSLCOMM_CLOUD_CERT_REMOTE, "src/jcpJOSPGWs/certs/cloud/public/mainServer@GwObjService.crt");
        properties.put(JSLSettings_002.JSLCOMM_CLOUD_CERT, TEST_FILES_PREFIX + "clientJSLCloud.crt");
        return properties;
    }

    private static class MockJCPClient_Object implements JCPClient_Object {
        public MockJCPClient_Object() {}

        @Override
        public void setObjectId(String objId) {

        }

        @Override
        public boolean isConnected() {
            return false;
        }

        @Override
        public void connect() {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public void tryConnect() {

        }

        @Override
        public void tryDisconnect() {

        }

        @Override
        public void addConnectListener(ConnectListener listener) {}

        @Override
        public void removeConnectListener(ConnectListener listener) {}

        @Override
        public void addDisconnectListener(DisconnectListener listener) {}

        @Override
        public void removeDisconnectListener(DisconnectListener listener) {}

        @Override
        public void execGetReq(String url, boolean secure) {

        }

        @Override
        public void execGetReq(String url, Map<String, String> params, boolean secure) {

        }

        @Override
        public <T> T execGetReq(String url, Class<T> reqObject, boolean secure) {
            return null;
        }

        @Override
        public <T> T execGetReq(String url, Class<T> reqObject, Map<String, String> params, boolean secure) {
            return null;
        }

        @Override
        public void execPostReq(String url, boolean secure) {

        }

        @Override
        public void execPostReq(String url, Object param, boolean secure) {

        }

        @Override
        public <T> T execPostReq(String url, Class<T> reqObject, boolean secure) {
            return null;
        }

        @Override
        public <T> T execPostReq(String url, Class<T> reqObject, Object param, boolean secure) {
            return null;
        }

        @Override
        public void execDeleteReq(String url, boolean secure) {

        }

        @Override
        public void execDeleteReq(String url, Object param, boolean secure) {

        }

        @Override
        public <T> T execDeleteReq(String url, Class<T> reqObject, boolean secure) {
            return null;
        }

        @Override
        public <T> T execDeleteReq(String url, Class<T> reqObject, Object param, boolean secure) {
            return null;
        }
    }

    private static class MockJODPermissions_002 implements JODPermissions {
        @Override
        public boolean canExecuteAction(String srvId, String usrId, PermissionsTypes.Connection connection) {
            return false;
        }

        @Override
        public boolean canSendLocalUpdate(String srvId, String usrId) {
            return false;
        }

        @Override
        public boolean canActAsLocalCoOwner(String srvId, String usrId) {
            return false;
        }

        @Override
        public void syncObjPermissions() {

        }

        @Override
        public List<ObjPermission> getPermissions() {
            return null;
        }

        @Override
        public boolean addPermissions(String usrId, String srvId, PermissionsTypes.Connection connection, PermissionsTypes.Type type) {
            return false;
        }

        @Override
        public boolean deletePermissions(String usrId, String srvId) {
            return false;
        }

        @Override
        public String getOwnerId() {
            return null;
        }

        @Override
        public void setOwnerId(String ownerId) { }

        @Override
        public void resetOwnerId() {}

        @Override
        public void startAutoRefresh() {

        }

        @Override
        public void stopAutoRefresh() {

        }

        @Override
        public void regeneratePermissions() {}
    }

    private static class MockJCPClient_Service implements JCPClient_Service {
        @Override
        public void setServiceId(String srvId) {

        }

        @Override
        public void setUserId(String usrId) {

        }

        @Override
        public boolean isCliCredFlowEnabled() {
            return false;
        }

        @Override
        public boolean isAuthCodeFlowEnabled() {
            return false;
        }

        @Override
        public String getLoginUrl() {
            return null;
        }

        @Override
        public boolean setLoginCode(String code) {
            return false;
        }

        @Override
        public String getRefreshToken() {
            return null;
        }

        @Override
        public void setRefreshToken(String refreshToken) {}

        @Override
        public boolean userLogout() {
            return false;
        }

        @Override
        public void setLoginManager(LoginManager loginMngr) {

        }

        @Override
        public boolean isConnected() {
            return false;
        }

        @Override
        public void connect() {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public void tryConnect() {

        }

        @Override
        public void tryDisconnect() {

        }

        @Override
        public void addConnectListener(ConnectListener listener) {}

        @Override
        public void removeConnectListener(ConnectListener listener) {}

        @Override
        public void addDisconnectListener(DisconnectListener listener) {}

        @Override
        public void removeDisconnectListener(DisconnectListener listener) {}

        @Override
        public void execGetReq(String url, boolean secure) {

        }

        @Override
        public void execGetReq(String url, Map<String, String> params, boolean secure) {

        }

        @Override
        public <T> T execGetReq(String url, Class<T> reqObject, boolean secure) {
            return null;
        }

        @Override
        public <T> T execGetReq(String url, Class<T> reqObject, Map<String, String> params, boolean secure) {
            return null;
        }

        @Override
        public void execPostReq(String url, boolean secure) {

        }

        @Override
        public void execPostReq(String url, Object param, boolean secure) {

        }

        @Override
        public <T> T execPostReq(String url, Class<T> reqObject, boolean secure) {
            return null;
        }

        @Override
        public <T> T execPostReq(String url, Class<T> reqObject, Object param, boolean secure) {
            return null;
        }

        @Override
        public void execDeleteReq(String url, boolean secure) {

        }

        @Override
        public void execDeleteReq(String url, Object param, boolean secure) {

        }

        @Override
        public <T> T execDeleteReq(String url, Class<T> reqObject, boolean secure) {
            return null;
        }

        @Override
        public <T> T execDeleteReq(String url, Class<T> reqObject, Object param, boolean secure) {
            return null;
        }
    }

    private static class MockJSLUserMngr_002 implements JSLUserMngr {
        @Override
        public boolean isUserAuthenticated() {
            return false;
        }

        @Override
        public String getUserId() {
            return null;
        }

        @Override
        public String getUsername() {
            return null;
        }
    }

    public static class MockJODObjectInfo implements JODObjectInfo {

        private final String objId;

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
        public void setObjName(String newName) {}

        @Override
        public String getOwnerId() {
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
        public String getPermissionsPath() {
            return null;
        }

        @Override
        public String readPermissionsStr() {
            return null;
        }

        @Override
        public void startAutoRefresh() {

        }

        @Override
        public void stopAutoRefresh() {

        }

        @Override
        public void syncObjInfo() {}

        @Override
        public void regenerateObjId() {}

    }

}
