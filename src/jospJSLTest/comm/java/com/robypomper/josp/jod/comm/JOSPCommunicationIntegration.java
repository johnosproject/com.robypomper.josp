package com.robypomper.josp.jod.comm;

import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jod.JOD_002;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.systems.JODCommunication;
import com.robypomper.josp.jod.systems.JODCommunication_002;
import com.robypomper.josp.jod.systems.JODExecutorMngr;
import com.robypomper.josp.jod.systems.JODObjectInfo;
import com.robypomper.josp.jod.systems.JODPermissions;
import com.robypomper.josp.jod.systems.JODStructure;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import com.robypomper.josp.jsl.systems.JSLCommunication;
import com.robypomper.josp.jsl.systems.JSLCommunication_002;
import com.robypomper.josp.jsl.systems.JSLObjsMngr;
import com.robypomper.josp.jsl.systems.JSLObjsMngr_002;
import com.robypomper.josp.jsl.systems.JSLServiceInfo;
import com.robypomper.josp.jsl.systems.JSLUserMngr;
import com.robypomper.log.Markers;
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
    JOD_002.Settings jodSettings;
    JCPClient_Object jcpClientObj;
    JODObjectInfo objInfo;
    JODPermissions jodPermissions;
    JSL_002.Settings jslSettings;
    JCPClient_Service jcpClientSrv;
    JSLServiceInfo srvInfo;
    JSLUserMngr jslUserMngr;
    JSLObjsMngr jslObjsMngr;

    // Test config

    @BeforeEach
    public void setUp() {
        log.debug(Markers.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Markers.TEST_METHODS, "setUp");

        // Create test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.mkdirs();

        port += 2;

        // Init JOD Comm params
        jodSettings = new JOD_002.Settings(getDefaultJODSettings(port));
        jcpClientObj = new MockJCPClient_Object();
        objInfo = new MockJODObjectInfo("objId");
        jodPermissions = new MockJODPermissions_002();

        // Init JSL Comm params
        jslSettings = new JSL_002.Settings(getDefaultJSLSettings());
        jcpClientSrv = new MockJCPClient_Service();
        srvInfo = new JSLLocalClientTest.MockJSLServiceInfo("srvId/usrId/instId");
        jslUserMngr = new MockJSLUserMngr_002();
        //jslObjsMngr = new MockJSLObjsMngr_002();
        jslObjsMngr = new JSLObjsMngr_002(jslSettings, srvInfo);

        log.debug(Markers.TEST_METHODS, "test");
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
    public void testLocalPublishAndDiscoveryFirstJOD() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException {
        System.out.println("\nJOD LOCAL COMM START");
        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr);
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
    public void testLocalPublishAndDiscoveryFirstJSL() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException {
        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr);
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
    public void testLocalPublishAndDiscoveryStopJOD() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException {
        System.out.println("\nJOD LOCAL COMM START");
        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr);
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
    public void testLocalPublishAndDiscoveryStopJSL() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException {
        System.out.println("\nJOD LOCAL COMM START");
        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr);
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
    public void testLocalPublishAndDiscoveryTwoJOD() throws JODCommunication.LocalCommunicationException, JSLCommunication.LocalCommunicationException, SocketException {
        System.out.println("\nJOD LOCAL COMM START (1st)");
        JODCommunication jodComm = new JODCommunication_002(jodSettings, objInfo, jcpClientObj, jodPermissions, UNIQUE_ID);
        jodComm.startLocal();

        System.out.println("\nJOD LOCAL COMM START (2nd)");
        port += 2;
        JOD_002.Settings jodSettings2 = new JOD_002.Settings(getDefaultJODSettings(port));
        JODObjectInfo objInfo2 = new MockJODObjectInfo("objId_2");
        JODCommunication jodComm2 = new JODCommunication_002(jodSettings2, objInfo2, jcpClientObj, jodPermissions, UNIQUE_ID + "bis");
        jodComm2.startLocal();

        System.out.println("\nJSL LOCAL COMM START");
        JSLCommunication jslComm = new JSLCommunication_002(jslSettings, srvInfo, jcpClientSrv, jslUserMngr, jslObjsMngr);
        jslComm.startLocal();

        int ntwkIntfs = getNetworkInterfacesCount();
        try {
            Thread.sleep(ntwkIntfs * 1000);
            if (ntwkIntfs * 2 > getJSLLocConnCount(jslComm)) {
                int extraSecs = (ntwkIntfs * 2 - getJSLLocConnCount(jslComm));
                System.out.println(String.format("\nEXTRA TIME (%s)", extraSecs));
                Thread.sleep(extraSecs * 1000);
            }
        } catch (InterruptedException ignore) {}

        for (JSLLocalClient c : jslComm.getAllLocalClients())
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
        return jslComm.getAllLocalClients().size();
    }

    private int getJSLLocConnConnectedCount(JSLCommunication jslComm) {
        int count = 0;
        for (JSLLocalClient conn : jslComm.getAllLocalClients())
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


    private Map<String, String> getDefaultJODSettings(int port) {
        Map<String, String> properties = new HashMap<>();

        // Comm's paths
        properties.put(JOD_002.Settings.JODCOMM_LOCAL_ENABLED, "true");
        properties.put(JOD_002.Settings.JODCOMM_LOCAL_PORT, Integer.toString(port));
        properties.put(JOD_002.Settings.JODCOMM_LOCAL_KS_FILE, String.format(TEST_FILES_PREFIX + "server-%d.p12", port));
        properties.put(JOD_002.Settings.JODCOMM_LOCAL_CERT, String.format(TEST_FILES_PREFIX + "server-%d.crt", port));
        properties.put(JOD_002.Settings.JODCOMM_CLOUD_ENABLED, "false");
        properties.put(JOD_002.Settings.JODCOMM_CLOUD_CERT, TEST_FILES_PREFIX + "clientJODCloud.crt");
        properties.put(JOD_002.Settings.JODCOMM_CLOUD_CERT_REMOTE, "src/jcpJOSPGWs/certs/cloud/public/mainServer@GwObjService.crt");
        return properties;
    }

    private Map<String, String> getDefaultJSLSettings() {
        Map<String, String> properties = new HashMap<>();

        // Comm's paths
        properties.put(JSL_002.Settings.JSLCOMM_LOCAL_ENABLED, "true");
        properties.put(JSL_002.Settings.JSLCOMM_LOCAL_KS_FILE, TEST_FILES_PREFIX + "client.p12");
        properties.put(JSL_002.Settings.JSLCOMM_CLOUD_ENABLED, "false");
        properties.put(JSL_002.Settings.JSLCOMM_CLOUD_CERT_REMOTE, "src/jcpJOSPGWs/certs/cloud/public/mainServer@GwObjService.crt");
        properties.put(JSL_002.Settings.JSLCOMM_CLOUD_CERT, TEST_FILES_PREFIX + "clientJSLCloud.crt");
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
        public boolean resetOwnerId() {
            return false;
        }

        @Override
        public void startAutoRefresh() {

        }

        @Override
        public void stopAutoRefresh() {

        }
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
        public boolean setRefreshToken(String refreshToken) {
            return false;
        }

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
        public String getOwnerId() {
            return null;
        }

        @Override
        public String getStructureStr() {
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
        public String getPermissionsStr() {
            return null;
        }

        @Override
        public void startAutoRefresh() {

        }

        @Override
        public void stopAutoRefresh() {

        }

    }

}
