/* *****************************************************************************
 * The John Cloud Platform set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.josp.jcp.apis.controllers;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.josp.jcp.gw.JOSPGWsO2SService;
import com.robypomper.josp.jcp.gw.JOSPGWsS2OService;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.params.jospgws.O2SAccessInfo;
import com.robypomper.josp.params.jospgws.O2SAccessRequest;
import com.robypomper.josp.params.jospgws.S2OAccessInfo;
import com.robypomper.josp.params.jospgws.S2OAccessRequest;
import com.robypomper.josp.paths.APIGWs;
import com.robypomper.josp.paths.APIObjs;
import com.robypomper.josp.paths.APISrvs;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.security.RolesAllowed;
import java.net.InetAddress;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

/**
 * API Permission Objs's controller.
 * <p>
 * This controller expose methods used by the JOD Objects and JSL Services to
 * manage their JOSP GWs connections.
 */
@RestController
@Api(tags = {APIGWs.SubGroupGWs.NAME})
public class APIGWsController {

    // Internal vars

    private static final Logger log = LogManager.getLogger();
    @Autowired
    private JOSPGWsO2SService gwO2SService;
    @Autowired
    private JOSPGWsS2OService gwS2OService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIGWs() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIGWs.SubGroupGWs.NAME, APIGWs.SubGroupGWs.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIGWs.API_NAME, APIGWs.API_VER, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    @PostMapping(path = APIGWs.FULL_PATH_O2S_ACCESS)
    @ApiOperation(value = "Set object's certificate and request JOSPGw O2S access info",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = O2SAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
            @ApiResponse(code = 500, message = "Error adding client certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<O2SAccessInfo> postO2SAccess(
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId,
            @RequestBody
                    O2SAccessRequest accessRequest) {

        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        Certificate clientCertificate = null;
        try {
            clientCertificate = UtilsJKS.loadCertificateFromBytes(accessRequest.objCertificate);
        } catch (UtilsJKS.LoadingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body param objCertificate contains invalid value.");
        }

        if (!gwO2SService.addClientCertificate(objId + accessRequest.instanceId, clientCertificate))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error registering object '%s'.", objId));

        InetAddress gwAddress = gwO2SService.getPublicAddress(objId);
        int gwPort = gwO2SService.getPort(objId);
        byte[] gwCert = null;
        try {
            gwCert = gwO2SService.getPublicCertificate(objId).getEncoded();
        } catch (CertificateEncodingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error returning current JOSP Gw certificate.");
        }

        O2SAccessInfo o2sAccessInfo = new O2SAccessInfo(gwAddress, gwPort, gwCert);
        return ResponseEntity.ok(o2sAccessInfo);
    }


    @PostMapping(path = APIGWs.FULL_PATH_S2O_ACCESS)
    @ApiOperation(value = "Set service's certificate and request JOSPGw S2O access info",
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = S2OAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APISrvs.HEADER_SRVID),
            @ApiResponse(code = 500, message = "Error adding client certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<S2OAccessInfo> postS2OAccess(
            @RequestHeader(APISrvs.HEADER_SRVID)
                    String srvId,
            @RequestBody
                    S2OAccessRequest accessRequest) {

        if (srvId == null || srvId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APISrvs.HEADER_SRVID));

        Certificate clientCertificate = null;
        try {
            clientCertificate = UtilsJKS.loadCertificateFromBytes(accessRequest.srvCertificate);
        } catch (UtilsJKS.LoadingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body param srvCertificate contains invalid value.");
        }

        if (!gwS2OService.addClientCertificate(srvId + accessRequest.instanceId, clientCertificate))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error registering service '%s'.", srvId));

        InetAddress gwAddress = gwS2OService.getPublicAddress(srvId);
        int gwPort = gwS2OService.getPort(srvId);
        byte[] gwCert = null;
        try {
            gwCert = gwS2OService.getPublicCertificate(srvId).getEncoded();
        } catch (CertificateEncodingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error returning current JOSP Gw certificate.");
        }

        S2OAccessInfo s2oAccessInfo = new S2OAccessInfo(gwAddress, gwPort, gwCert);
        return ResponseEntity.ok(s2oAccessInfo);
    }

}
