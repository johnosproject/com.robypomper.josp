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

package com.robypomper.josp.jcp.gws.controllers;

import com.robypomper.comm.trustmanagers.AbsCustomTrustManager;
import com.robypomper.java.JavaJKS;
import com.robypomper.josp.jcp.gws.gw.GWAbs;
import com.robypomper.josp.jcp.gws.services.GWServiceO2S;
import com.robypomper.josp.jcp.gws.services.GWServiceS2O;
import com.robypomper.josp.jcp.info.JCPGWsVersions;
import com.robypomper.josp.jcp.paths.gws.JCPGWsAccessInfo;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jcp.utils.ParamChecks;
import com.robypomper.josp.params.jospgws.*;
import com.robypomper.josp.paths.APIObjs;
import com.robypomper.josp.paths.APISrvs;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

/**
 * API JCP APIs controller.
 * <p>
 * This controller expose methods called by JCP APIs to add clients certificate
 * to current JCP GWs.
 */
@RestController
@Api(tags = {JCPGWsAccessInfo.SubGroupGWs.NAME})
public class JCPGWsAccessInfoController {

    // Internal vars

    private static final Logger log = LoggerFactory.getLogger(JCPGWsAccessInfoController.class);
    @Autowired
    private GWServiceO2S gwO2SService;
    @Autowired
    private GWServiceS2O gwS2OService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_JCPGWsAccessInfo() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(JCPGWsAccessInfo.SubGroupGWs.NAME, JCPGWsAccessInfo.SubGroupGWs.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(JCPGWsAccessInfo.API_NAME, JCPGWsAccessInfo.API_VER, JCPGWsVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    @PostMapping(path = JCPGWsAccessInfo.FULL_PATH_O2S_ACCESS)
    @ApiOperation(value = JCPGWsAccessInfo.DESCR_PATH_O2S_ACCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = O2SAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
            @ApiResponse(code = 500, message = "Error adding client certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<O2SAccessInfo> postO2SAccess(
            @RequestHeader(APIObjs.HEADER_OBJID)
                    String objId,
            @RequestBody
                    O2SAccessRequest accessRequest) {

        ParamChecks.checkObjId(log, objId);

        Certificate clientCertificate = generateCertificate(objId, accessRequest.getObjCertificate(), "objCertificate");
        String certId = String.format("%s/%s", objId, accessRequest.instanceId);
        registerCertificate(gwO2SService.get(), objId, certId, clientCertificate);
        log.trace(String.format("Registered certificate for Object '%s'", objId));

        return ResponseEntity.ok((O2SAccessInfo) getAccessInfo(gwO2SService.get()));
    }


    @PostMapping(path = JCPGWsAccessInfo.FULL_PATH_S2O_ACCESS)
    @ApiOperation(value = JCPGWsAccessInfo.DESCR_PATH_S2O_ACCESS,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_JCP,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_JCP_SWAGGER,
                            description = SwaggerConfigurer.ROLE_JCP_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = S2OAccessInfo.class),
            @ApiResponse(code = 401, message = "User not authenticated"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APISrvs.HEADER_SRVID),
            @ApiResponse(code = 500, message = "Error adding client certificate")
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_JCP)
    public ResponseEntity<S2OAccessInfo> postS2OAccess(
            @RequestHeader(APISrvs.HEADER_SRVID)
                    String srvId,
            @RequestBody
                    S2OAccessRequest accessRequest) {

        ParamChecks.checkSrvId(log, srvId);

        Certificate clientCertificate = generateCertificate(srvId, accessRequest.getSrvCertificate(), "srvCertificate");
        String certId = String.format("%s/%s", srvId, accessRequest.instanceId);
        registerCertificate(gwS2OService.get(), srvId, certId, clientCertificate);
        log.trace(String.format("Registered certificate for Service '%s'", srvId));

        return ResponseEntity.ok((S2OAccessInfo) getAccessInfo(gwS2OService.get()));
    }


    // Utils

    private static Certificate generateCertificate(String clientId, byte[] certificateBytes, String paramName) {
        try {
            return JavaJKS.loadCertificateFromBytes(certificateBytes);

        } catch (JavaJKS.LoadingException e) {
            log.trace(String.format("Error parsing client '%s' certificate", clientId));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Body param %s contains invalid value.", paramName));
        }
    }

    private static void registerCertificate(GWAbs gwService, String clientId, String certId, Certificate certificate) {
        try {
            gwService.addClientCertificate(certId, certificate);

        } catch (AbsCustomTrustManager.UpdateException e) {
            log.trace(String.format("Error registering client '%s' certificate '%s' on GW server '%s'", clientId, certId, gwService.getId()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error registering client certificate '%s' on GW server '%s'.", certId, gwService.getId()));
        }
    }

    private static AccessInfo getAccessInfo(GWAbs gwService) {
        try {
            return gwService.getAccessInfo();

        } catch (CertificateEncodingException e) {
            log.trace(String.format("Error retrieve gw server '%s' certificate", gwService.getId()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error getting GW server '%s' certificate.", gwService.getId()));
        }
    }

}