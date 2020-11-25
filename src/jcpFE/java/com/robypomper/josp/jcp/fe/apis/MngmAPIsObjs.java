package com.robypomper.josp.jcp.fe.apis;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.params.admin.JCPCloudStatus;
import com.robypomper.josp.paths.APIMngr;
import com.robypomper.josp.jcp.paths.fe.APIJCPAPIMngm;
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

@RestController
//@Api(tags = {APIJCPFECloudStatus.SubGroupState.NAME})
public class MngmAPIsObjs {

    // Internal vars

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private JSLSpringService jslService;

    @GetMapping(path = APIJCPAPIMngm.FULL_PATH_MNGM_OBJS)
    public ResponseEntity<JCPCloudStatus.JCPObjects> getJCPAPIs_ObjsReq() {
        checkAdmin();

        try {
            JSL current = jslService.getHttp(httpSession);
            JCPClient_Service jcpClient = current.getJCPClient();
            JCPCloudStatus.JCPObjects jcpAPIsResponse = jcpClient.execReq(Verb.GET, APIMngr.FULL_PATH_MNGM_OBJS, JCPCloudStatus.JCPObjects.class,jcpClient.isSecured());
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
