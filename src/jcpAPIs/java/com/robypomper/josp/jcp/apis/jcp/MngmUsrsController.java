package com.robypomper.josp.jcp.apis.jcp;

import com.robypomper.josp.params.jcp.JCPAPIsStatus;
import com.robypomper.josp.paths.APIMngr;
import com.robypomper.josp.jcp.db.apis.ObjectDBService;
import com.robypomper.josp.jcp.db.apis.ServiceDBService;
import com.robypomper.josp.jcp.db.apis.UserDBService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;

import javax.annotation.security.RolesAllowed;

@RestController
@Api(tags = {APIMngr.SubGroupUsrs.NAME})
public class MngmUsrsController {

    @Autowired
    private ObjectDBService objDB;
    @Autowired
    private ServiceDBService srvDB;
    @Autowired
    private UserDBService usrDB;

    @GetMapping(path = APIMngr.FULL_PATH_MNGM_USRS)
    @ApiOperation(value = "Return JCP APIs Users info and stats",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_MNG,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_MNG_SWAGGER,
                            description = SwaggerConfigurer.ROLE_MNG_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "JCP APIs's Users info and stats", response = JCPAPIsStatus.Users.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 403, message = "Only Admin user can access to this request"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_MNG)
    public ResponseEntity<JCPAPIsStatus.Users> getJCPAPIsReq() {
        JCPAPIsStatus.Users jcpStatus = new JCPAPIsStatus.Users();

        jcpStatus.count = usrDB.count();

        return ResponseEntity.ok(jcpStatus);
    }

}
