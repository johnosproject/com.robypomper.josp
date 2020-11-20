package com.robypomper.josp.jcp.fe.apis;

import com.github.scribejava.core.model.Verb;
import com.robypomper.cloud.apis.CloudStatusControllerBase;
import com.robypomper.josp.params.cloud.CloudStatus;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.params.admin.JCPCloudStatus;
import com.robypomper.josp.paths.APICloudStatus;
import com.robypomper.josp.jcp.paths.fe.APIJCPFECloudStatus;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
//@Api(tags = {APIJCPFECloudStatus.SubGroupState.NAME})
public class CloudStatusAPIsController extends CloudStatusControllerBase {

    // Internal vars

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private JSLSpringService jslService;

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS)
    public ResponseEntity<JCPCloudStatus.JCPAPI> getJCPAPIsReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            JCPCloudStatus.JCPAPI jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_JCPAPIS, JCPCloudStatus.JCPAPI.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS_PROCESS)
    public ResponseEntity<CloudStatus.Process> getJCPAPIs_ProcessReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            CloudStatus.Process jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_PROCESS, CloudStatus.Process.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS_JAVA)
    public ResponseEntity<CloudStatus.Java> getJCPAPIs_JavaReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            CloudStatus.Java jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_JAVA, CloudStatus.Java.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS_JAVA_THREADS)
    public ResponseEntity<List<CloudStatus.JavaThread>> getJCPAPIs_JavaThreadsReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            List<CloudStatus.JavaThread> jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_JAVA_THREADS, List.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS_OS)
    public ResponseEntity<CloudStatus.Os> getJCPAPIs_OsReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            CloudStatus.Os jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_OS, CloudStatus.Os.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS_CPU)
    public ResponseEntity<CloudStatus.CPU> getJCPAPIs_CPUReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            CloudStatus.CPU jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_CPU, CloudStatus.CPU.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS_MEMORY)
    public ResponseEntity<CloudStatus.Memory> getJCPAPIs_MemoryReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            CloudStatus.Memory jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_MEMORY, CloudStatus.Memory.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS_DISKS)
    public ResponseEntity<CloudStatus.Disks> getJCPAPIs_DisksReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            CloudStatus.Disks jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_DISKS, CloudStatus.Disks.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS_NETWORK)
    public ResponseEntity<CloudStatus.Network> getJCPAPIs_NetworkReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            CloudStatus.Network jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_NETWORK, CloudStatus.Network.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPAPIS_NETWORK_INTFS)
    public ResponseEntity<List<CloudStatus.NetworkIntf>> getJCPAPIs_NetworkIntfReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            List<CloudStatus.NetworkIntf> jcpAPIsResponse = jcpClient.execReq(Verb.GET, APICloudStatus.FULL_PATH_STATE_JAVA_THREADS, List.class, jcpClient.isSecured());
            return ResponseEntity.ok(jcpAPIsResponse);

        } catch (JCPClient2.ConnectionException | JCPClient2.AuthenticationException | JCPClient2.RequestException | JCPClient2.ResponseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    private void checkAdmin() {
        try {
            if (!jslService.get(httpSession).getUserMngr().isAdmin())
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only Admin user can access to this request");
        } catch (JSLSpringService.JSLSpringException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't get JSL instance associated to this session");
        }
    }
}
