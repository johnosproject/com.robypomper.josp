package com.robypomper.josp.jcp.fe.apis;

import com.robypomper.cloud.apis.CloudStatusControllerBase;
import com.robypomper.josp.params.cloud.CloudStatus;
import com.robypomper.josp.jcp.params.fe.JOSPCloudStatus;
import com.robypomper.josp.jcp.paths.fe.APIJCPFECloudStatus;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jcp.fe.security.HttpSessionCollector;
import com.robypomper.josp.jsl.JSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
//@Api(tags = {APIJCPFECloudStatus.SubGroupState.NAME})
public class CloudStatusController extends CloudStatusControllerBase {

    // Internal vars

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private JSLSpringService jslService;

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_PROCESS)
    public ResponseEntity<CloudStatus.Process> getProcessReq() {
        checkAdmin();
        return ResponseEntity.ok(getProcess());
    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JAVA)
    public ResponseEntity<CloudStatus.Java> getJavaReq() {
        checkAdmin();
        return ResponseEntity.ok(getJava());
    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JAVA_THREADS)
    public ResponseEntity<List<CloudStatus.JavaThread>> getJavaThreadsReq() {
        checkAdmin();
        return ResponseEntity.ok(getJavaThreads());
    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_OS)
    public ResponseEntity<CloudStatus.Os> getOsReq() {
        checkAdmin();
        return ResponseEntity.ok(getOs());
    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_CPU)
    public ResponseEntity<CloudStatus.CPU> getCPUReq() {
        checkAdmin();
        return ResponseEntity.ok(getCPU());
    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_MEMORY)
    public ResponseEntity<CloudStatus.Memory> getMemoryReq() {
        checkAdmin();
        return ResponseEntity.ok(getMemory());
    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_DISKS)
    public ResponseEntity<CloudStatus.Disks> getDisksReq() {
        checkAdmin();
        return ResponseEntity.ok(getDisks());
    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_NETWORK)
    public ResponseEntity<CloudStatus.Network> getNetworkReq() {
        checkAdmin();
        return ResponseEntity.ok(getNetwork());
    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_NETWORK_INTFS)
    public ResponseEntity<List<CloudStatus.NetworkIntf>> getNetworkIntfReq() {
        checkAdmin();
        return ResponseEntity.ok(getNetworkIntfs());
    }


    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPFE)
    public ResponseEntity<JOSPCloudStatus.JCPFE> getJCPFEReq() {
        checkAdmin();
        JSL current = jslService.getHttp(httpSession);
        Collection<HttpSession> sessions = HttpSessionCollector.getAllSessions().values();
        List<JSL> all = new ArrayList<>();
        for (HttpSession s : sessions)
            all.add(jslService.getHttp(s));
        return ResponseEntity.ok(new JOSPCloudStatus.JCPFE(current,all,sessions));
    }

    @GetMapping(path = APIJCPFECloudStatus.FULL_PATH_STATE_JCPFE_INST)
    public ResponseEntity<List<JOSPCloudStatus.JSLInstance>> getJCPFE_InstancesReq() {
        checkAdmin();

        Collection<HttpSession> sessions = HttpSessionCollector.getAllSessions().values();
        List<JSL> all = new ArrayList<>();
        for (HttpSession s : sessions)
            all.add(jslService.getHttp(s));

        List<JOSPCloudStatus.JSLInstance> jslInstances = new ArrayList<>();
        for (JSL jsl : all)
            jslInstances.add(new JOSPCloudStatus.JSLInstance(jsl));

        return ResponseEntity.ok(jslInstances);
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
