package com.robypomper.josp.jod.comm;

import com.robypomper.communication.client.Client;
import com.robypomper.communication.server.Server;
import com.robypomper.josp.jod.executor.JODExecutorMngr;
import com.robypomper.josp.jod.objinfo.JODObjectInfo;
import com.robypomper.josp.jod.permissions.JODPermissions;
import com.robypomper.josp.jod.structure.JODState;
import com.robypomper.josp.jod.structure.JODStateUpdate;
import com.robypomper.josp.jod.structure.JODStructure;
import com.robypomper.josp.jsl.comm.JSLCommunication;
import com.robypomper.josp.jsl.comm.JSLGwS2OClient;
import com.robypomper.josp.jsl.comm.JSLLocalClient;
import com.robypomper.josp.jsl.objs.JSLObjsMngr;
import com.robypomper.josp.jsl.srvinfo.JSLServiceInfo;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.log.Mrk_Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.Objects;

public class JSLLocalClientTest {

    // Class constants

    protected static final String TEST_FILES_PREFIX = "tmp/tests/";
    protected static final String KS_FILE = String.format("%s.p12", JSLLocalClientTest.class.getSimpleName());
    protected static final String PUB_CERT_PATH = String.format("%s.crt", JSLLocalClientTest.class.getSimpleName());
    protected static final String JSL_PUB_CERT_PATH = TEST_FILES_PREFIX + PUB_CERT_PATH;
    protected static final InetAddress LOCALHOST = InetAddress.getLoopbackAddress();


    // Internal vars

    protected static Logger log = LogManager.getLogger();
    protected static JODLocalServer jodServer;
    protected static int port = 1234;


    // Test config

    @BeforeEach
    public void setUp() throws Server.ListeningException {
        log.debug(Mrk_Test.TEST_SPACER, "########## ########## ########## ########## ##########");
        log.debug(Mrk_Test.TEST_METHODS, "setUp");

        // Create test dir
        File testDirFiles = new File(TEST_FILES_PREFIX);
        //noinspection ResultOfMethodCallIgnored
        testDirFiles.mkdirs();

        port++;

        // Start server
        jodServer = new JODLocalServer(new MockJODCommunication(), new MockJODObjectInfo(), new MockJODPermissions(), port,
                TEST_FILES_PREFIX + "jodSr-" + PUB_CERT_PATH);
        jodServer.start();

        log.debug(Mrk_Test.TEST_METHODS, "test");
    }

    @AfterEach
    public void tearDown() {
        // Stop JOD servers
        if (jodServer.isRunning())
            jodServer.stop();

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


    // Connect and disconnect

    @Test
    public void testLocalConnectAndDisconnect() throws Client.ConnectionException {
        System.out.println("\nJSL LOCAL CLIENT CONNECT");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                JSL_PUB_CERT_PATH);
        client.connect();
        Assertions.assertTrue(client.isConnected());
        Assertions.assertEquals(jodServer.getServerId(), client.getObjId());

        System.out.println("\nJSL LOCAL CLIENT DISCONNECT");
        client.disconnect();
        Assertions.assertFalse(client.isConnected());
    }

    @Test
    public void testLocalConnectAndServerStop() throws Client.ConnectionException {
        System.out.println("\nJSL LOCAL CLIENT CONNECT");
        JSLLocalClient client = new JSLLocalClient(new MockJSLCommunication(), "srvId/usrId/instId", LOCALHOST, port,
                JSL_PUB_CERT_PATH);
        client.connect();
        Assertions.assertTrue(client.isConnected());

        Assertions.assertEquals(jodServer.getServerId(), client.getObjId());

        System.out.println("\nJOD LOCAL SERVER STOP");
        jodServer.stop();
        Assertions.assertFalse(client.isConnected());
    }


    // Mockup classes

    public static class MockJODObjectInfo implements JODObjectInfo {

        @Override
        public void setSystems(JODStructure structure, JODExecutorMngr executor, JODCommunication comm, JODPermissions permissions) {}

        @Override
        public String getJODVersion() {
            return "test version";
        }

        @Override
        public String getObjId() {
            return "test objId";
        }

        @Override
        public String getObjName() {
            return "test objName";
        }

        @Override
        public void setObjName(String newName) {}

        @Override
        public String getOwnerId() {
            return "test ownerId";
        }

        @Override
        public String getStructurePath() {
            return "test structPath";
        }

        @Override
        public String readStructureStr() {
            return "test structStrJOD";
        }

        @Override
        public String getStructForJSL() throws JODStructure.ParsingException {
            return "test structStrJSL";
        }

        @Override
        public String getPermsForJSL() throws JODStructure.ParsingException {
            return null;
        }

        @Override
        public String getBrand() {
            return "test brand";
        }

        @Override
        public String getModel() {
            return "test model";
        }

        @Override
        public String getLongDescr() {
            return "test longDescr";
        }

        @Override
        public String getPermissionsPath() {
            return "test permsPath";
        }

        @Override
        public String readPermissionsStr() {
            return "test permsStr";
        }

        @Override
        public void startAutoRefresh() {}

        @Override
        public void stopAutoRefresh() {}

        @Override
        public void syncObjInfo() {}

        @Override
        public void regenerateObjId() {}

    }

    public static class MockJODPermissions implements JODPermissions {

        @Override
        public void setCommunication(JODCommunication comm) throws JODStructure.CommunicationSetException {

        }

        @Override
        public void syncObjPermissions() {

        }

        @Override
        public List<JOSPPerm> getPermissions() {
            return null;
        }

        @Override
        public boolean checkPermission(String srvId, String usrId, JOSPPerm.Type minReqPerm, JOSPPerm.Connection connType) {
            return false;
        }

        @Override
        public JOSPPerm.Type getServicePermission(String srvId, String usrId, JOSPPerm.Connection connType) {
            return null;
        }

        @Override
        public boolean addPermissions(String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
            return false;
        }

        @Override
        public boolean updPermissions(String permId, String srvId, String usrId, JOSPPerm.Type type, JOSPPerm.Connection connection) {
            return false;
        }

        @Override
        public boolean remPermissions(String permId) {
            return false;
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
        public void startAutoRefresh() {

        }

        @Override
        public void stopAutoRefresh() {

        }

        @Override
        public void regeneratePermissions() throws PermissionsFileException {

        }
    }

    public static class MockJODCommunication implements JODCommunication {

        @Override
        public boolean sendToServices(String msg, JOSPPerm.Type minPermReq) {
            return false;
        }

        @Override
        public boolean sendToCloud(String msg) throws CloudNotConnected {
            return false;
        }

        @Override
        public boolean sendToSingleLocalService(JODLocalClientInfo locConn, String msg, JOSPPerm.Type minReqPerm) throws ServiceNotConnected {
            return false;
        }

        @Override
        public void sendObjectUpdMsg(JODState component, JODStateUpdate update) {

        }

        @Override
        public void syncObject() {}

        @Override
        public boolean processFromServiceMsg(String msg, JOSPPerm.Connection connType) {
            return false;
        }

        @Override
        public JODGwO2SClient getGwO2SClient() {
            return null;
        }

        @Override
        public List<JODLocalClientInfo> getAllLocalClientsInfo() {
            return null;
        }

        @Override
        public boolean isLocalRunning() {
            return false;
        }

        @Override
        public void startLocal() {

        }

        @Override
        public void stopLocal() {

        }

        @Override
        public boolean isCloudConnected() {
            return false;
        }

        @Override
        public void connectCloud() {

        }

        @Override
        public void disconnectCloud() {

        }

        @Override
        public void setStructure(JODStructure structure) {

        }

        @Override
        public JODStructure getStructure() {
            return null;
        }
    }

    public static class MockJSLCommunication implements JSLCommunication {

        @Override
        public boolean processFromObjectMsg(String msg, JOSPPerm.Connection connType) {
            return false;
        }

        @Override
        public JSLGwS2OClient getCloudConnection() {
            return null;
        }

        @Override
        public List<JSLLocalClient> getAllLocalServers() {
            return null;
        }

        @Override
        public void removeServer(JSLLocalClient server) {

        }

        @Override
        public boolean isLocalRunning() {
            return false;
        }

        @Override
        public void startLocal() {

        }

        @Override
        public void stopLocal() {

        }

        @Override
        public boolean isCloudConnected() {
            return false;
        }

        @Override
        public void connectCloud() {

        }

        @Override
        public void disconnectCloud() {

        }

        @Override
        public void addListener(CommunicationListener listener) {

        }

        @Override
        public void removeListener(CommunicationListener listener) {

        }

    }

    public static class MockJSLServiceInfo implements JSLServiceInfo {

        private final String fullId;

        public MockJSLServiceInfo(String fullId) {
            this.fullId = fullId;
        }

        @Override
        public void setSystems(JSLUserMngr user, JSLObjsMngr objs) {

        }

        @Override
        public void setCommunication(JSLCommunication comm) {

        }

        @Override
        public String getSrvId() {
            return null;
        }

        @Override
        public String getSrvName() {
            return null;
        }

        @Override
        public boolean isUserLogged() {
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

        @Override
        public String getInstanceId() {
            return null;
        }

        @Override
        public String getFullId() {
            return fullId;
        }

        @Override
        public int getConnectedObjectsCount() {
            return 0;
        }

        @Override
        public int getKnownObjectsCount() {
            return 0;
        }

        @Override
        public void startAutoRefresh() {

        }

        @Override
        public void stopAutoRefresh() {

        }
    }

}
