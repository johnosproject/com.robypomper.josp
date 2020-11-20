package com.robypomper.josp.jcp.apis.jcp;

import com.robypomper.cloud.apis.CloudStatusControllerBase;
import com.robypomper.josp.params.admin.JCPCloudStatus;
import com.robypomper.josp.paths.APIMngr;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.UserDBService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@Api(tags = {APIMngr.SubGroupObjs.NAME})
public class MngmObjsController extends CloudStatusControllerBase {

    @Autowired
    private ObjectDBService objDB;
    @Autowired
    private ServiceDBService srvDB;
    @Autowired
    private UserDBService usrDB;

    @GetMapping(path = APIMngr.FULL_PATH_MNGM_OBJS)
    @ApiOperation(value = "Return JCP APIs's Objects info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Objects info and stats", response = JCPCloudStatus.JCPObjects.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPCloudStatus.JCPObjects> getJCPAPIsReq() {
        JCPCloudStatus.JCPObjects jcpStatus = new JCPCloudStatus.JCPObjects();

        jcpStatus.objectsCount = objDB.count();
        jcpStatus.objectsOnlineCount = objDB.countOnline();
        jcpStatus.objectsOfflineCount = objDB.countOffline();
        jcpStatus.objectsActiveCount = objDB.countActive();
        jcpStatus.objectsInactiveCount = objDB.countInactive();
        jcpStatus.objectsOwnersCount = objDB.countOwners();

        return ResponseEntity.ok(jcpStatus);
    }

}
