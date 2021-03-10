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

import com.robypomper.java.JavaString;
import com.robypomper.josp.info.JCPAPIsVersions;
import com.robypomper.josp.jcp.db.apis.EventDBService;
import com.robypomper.josp.jcp.db.apis.entities.Event;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.paths.APIEvents;
import com.robypomper.josp.paths.APIObjs;
import com.robypomper.josp.paths.APISrvs;
import com.robypomper.josp.protocol.JOSPEvent;
import com.robypomper.josp.protocol.JOSPPerm;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.security.RolesAllowed;
import java.util.List;


/**
 * Base JCP API Events controller...
 */
@SuppressWarnings("unused")
@RestController
@Api(tags = {APIEvents.SubGroupEvent.NAME})
public class APIEventsController {

    // Internal vars

    @Autowired
    private EventDBService eventService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIEvents() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIEvents.SubGroupEvent.NAME, APIEvents.SubGroupEvent.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIEvents.API_NAME, APIEvents.API_VER, JCPAPIsVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods

    @PostMapping(path = APIEvents.FULL_PATH_OBJECT)
    @ApiOperation(value = APIEvents.DESCR_PATH_OBJECT,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Object's events", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<Event>> postObjectEvents(@RequestHeader(APIObjs.HEADER_OBJID) String objId,
                                                        @RequestBody List<JOSPEvent> events) {
        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        //eventService.add(Event.newObjEvent(client.getClientId(), JOSPEvent.EventType.ConnectToCloud));
        for (JOSPEvent e : events)
            eventService.add(Event.fromJOSPEvent(e));

        return getObjectEventsById(objId);
    }

    @GetMapping(path = APIEvents.FULL_PATH_OBJECT)
    @ApiOperation(value = APIEvents.DESCR_PATH_OBJECTg,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Object's events", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<Event>> getObjectEvents(@RequestHeader(APIObjs.HEADER_OBJID) String objId) {
        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        return getObjectEventsById(objId);
    }

    @GetMapping(path = APIEvents.FULL_PATH_GET_OBJECT)
    @ApiOperation(value = APIEvents.DESCR_PATH_GET_OBJECT,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Object's events", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory param 'objId' or it's invalid full service id."),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<Event>> getObjectEventsById(@PathVariable("objId") String objId) {
        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", APIObjs.HEADER_OBJID));

        if (!isObjectId(objId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given objId '%s' is not a valid object id.", objId));

        return ResponseEntity.ok(eventService.findBySrcId(objId));
    }


    // Methods Objects Last

    @GetMapping(path = APIEvents.FULL_PATH_OBJECT_LAST)
    @ApiOperation(value = APIEvents.DESCR_PATH_OBJECT_LAST,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Last object's event", response = Event.class),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
            @ApiResponse(code = 404, message = "No events found for specified object"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Event> getObjectLastEvent(@RequestHeader(APIObjs.HEADER_OBJID) String objId) {
        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        return getObjectLastEventById(objId);
    }

    @GetMapping(path = APIEvents.FULL_PATH_GET_OBJECT_LAST)
    @ApiOperation(value = APIEvents.DESCR_PATH_GET_OBJECT_LAST,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Last object's event", response = Event.class),
            @ApiResponse(code = 400, message = "Missing mandatory param 'objId' or it's invalid object id."),
            @ApiResponse(code = 404, message = "No events found for specified object"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<Event> getObjectLastEventById(@PathVariable("objId") String objId) {
        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", APIObjs.HEADER_OBJID));

        if (!isObjectId(objId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given objId '%s' is not a valid object id.", objId));

        List<Event> events = eventService.findBySrcId(objId);
        if (events.size() < 1)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No events found for specified object.");

        return ResponseEntity.ok(events.get(0));
    }


    // Methods Objects Type

    @GetMapping(path = APIEvents.FULL_PATH_OBJECT_BY_TYPE)
    @ApiOperation(value = APIEvents.DESCR_PATH_OBJECT_BY_TYPE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Object's events of specified type", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<Event>> getObjectTypeEvent(
            @RequestHeader(APIObjs.HEADER_OBJID) String objId,
            @PathVariable("type") JOSPEvent.Type type) {
        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APIObjs.HEADER_OBJID));

        return getObjectTypeEventById(objId, type);
    }

    @GetMapping(path = APIEvents.FULL_PATH_GET_OBJECT_BY_TYPE)
    @ApiOperation(value = APIEvents.DESCR_PATH_GET_OBJECT_BY_TYPE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_OBJ_SWAGGER,
                            description = SwaggerConfigurer.ROLE_OBJ_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Object's events of specified type", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory param 'objId' or it's invalid object id."),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<Event>> getObjectTypeEventById(
            @PathVariable("objId") String objId,
            @PathVariable("type") JOSPEvent.Type type) {
        if (objId == null || objId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", APIObjs.HEADER_OBJID));

        if (!isObjectId(objId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given objId '%s' is not a valid object id.", objId));

        return ResponseEntity.ok(eventService.findBySrcIdAndEvnType(objId, type));
    }


    // Methods Service

    @PostMapping(path = APIEvents.FULL_PATH_SERVICE)
    @ApiOperation(value = APIEvents.DESCR_PATH_SERVICE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_OBJ,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service's events", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APIObjs.HEADER_OBJID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_OBJ)
    public ResponseEntity<List<Event>> postSERVICEEvents(@RequestHeader(APISrvs.HEADER_SRVID) String srvId,
                                                         @RequestBody List<JOSPEvent> events) {
        if (srvId == null || srvId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APISrvs.HEADER_SRVID));

        //eventService.add(Event.newObjEvent(client.getClientId(), JOSPEvent.EventType.ConnectToCloud));
        for (JOSPEvent e : events)
            eventService.add(Event.fromJOSPEvent(e));

        return getObjectEventsById(srvId);
    }

    @GetMapping(path = APIEvents.FULL_PATH_SERVICE)              //  /service    -> FULL_PATH_GET_SERVICE
    @ApiOperation(value = APIEvents.DESCR_PATH_SERVICEg,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service's events", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APISrvs.HEADER_SRVID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<List<Event>> getServiceEvents(@RequestHeader(APISrvs.HEADER_SRVID) String srvId) {
        if (srvId == null || srvId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APISrvs.HEADER_SRVID));

        return getServiceEventsById(srvId);
    }

    @GetMapping(path = APIEvents.FULL_PATH_GET_SERVICE)
    @ApiOperation(value = APIEvents.DESCR_PATH_GET_SERVICE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service's events", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory param 'fullSrvId' or it's invalid full service id."),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<List<Event>> getServiceEventsById(@PathVariable("fullSrvId") String srvId) {
        if (srvId == null || srvId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", APISrvs.HEADER_SRVID));

        String srvIdReceived = srvId;
        srvId = srvId.replace('@', '/');
        if (!isServiceId(srvId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given srvId '%s' is not a valid service id (@ replaced with /).", srvIdReceived));

        return ResponseEntity.ok(eventService.findBySrcId(srvId));
    }


    // Methods Service Last

    @GetMapping(path = APIEvents.FULL_PATH_SERVICE_LAST)
    @ApiOperation(value = APIEvents.DESCR_PATH_SERVICE_LAST,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Last service's event", response = Event.class),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APISrvs.HEADER_SRVID),
            @ApiResponse(code = 404, message = "No events found for specified service"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<Event> getServiceLastEvents(@RequestHeader(APISrvs.HEADER_SRVID) String srvId) {
        if (srvId == null || srvId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APISrvs.HEADER_SRVID));

        return getServiceLastEventsById(srvId);
    }

    @GetMapping(path = APIEvents.FULL_PATH_GET_SERVICE_LAST)
    @ApiOperation(value = APIEvents.DESCR_PATH_GET_SERVICE_LAST,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Last service's event", response = Event.class),
            @ApiResponse(code = 400, message = "Missing mandatory param 'fullSrvId' or it's invalid full service id."),
            @ApiResponse(code = 404, message = "No events found for specified service"),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<Event> getServiceLastEventsById(@PathVariable("fullSrvId") String srvId) {
        if (srvId == null || srvId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", APISrvs.HEADER_SRVID));

        String srvIdReceived = srvId;
        srvId = srvId.replace('@', '/');
        if (!isServiceId(srvId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given srvId '%s' is not a valid service id (@ replaced with /).", srvIdReceived));

        List<Event> events = eventService.findBySrcId(srvId);
        if (events.size() < 1)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No events found for specified service.");

        return ResponseEntity.ok(events.get(0));
    }


    // Methods Service Type

    @GetMapping(path = APIEvents.FULL_PATH_SERVICE_BY_TYPE)
    @ApiOperation(value = APIEvents.DESCR_PATH_SERVICE_BY_TYPE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service's events of specified event type", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory header " + APISrvs.HEADER_SRVID),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<List<Event>> getServiceTypeEvents(
            @RequestHeader(APISrvs.HEADER_SRVID) String srvId,
            @PathVariable("type") JOSPEvent.Type type) {
        if (srvId == null || srvId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", APISrvs.HEADER_SRVID));

        return getServiceTypeEventsById(srvId, type);
    }

    @GetMapping(path = APIEvents.FULL_PATH_GET_SERVICE_BY_TYPE)
    @ApiOperation(value = APIEvents.DESCR_PATH_GET_SERVICE_BY_TYPE,
            authorizations = @Authorization(
                    value = SwaggerConfigurer.OAUTH_FLOW_DEF_SRV,
                    scopes = @AuthorizationScope(
                            scope = SwaggerConfigurer.ROLE_SRV_SWAGGER,
                            description = SwaggerConfigurer.ROLE_SRV_DESC
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service's events of specified event type", response = Event.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Missing mandatory param 'fullSrvId' or it's invalid full service id."),
    })
    @RolesAllowed(SwaggerConfigurer.ROLE_SRV)
    public ResponseEntity<List<Event>> getServiceTypeEventsById(
            @PathVariable("fullSrvId") String srvId,
            @PathVariable("type") JOSPEvent.Type type) {
        if (srvId == null || srvId.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory param '%s'.", APISrvs.HEADER_SRVID));

        String srvIdReceived = srvId;
        srvId = srvId.replace('@', '/');
        if (!isServiceId(srvId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Given srvId '%s' is not a valid service id (@ replaced with /).", srvIdReceived));

        return ResponseEntity.ok(eventService.findBySrcIdAndEvnType(srvId, type));
    }


    // SrcId type checker

    private boolean isObjectId(String srcId) {
        // IUWUH-EHCEF-JZZCF
        return srcId.length() == JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString().length()
                && JavaString.occurrenceCount(srcId, "-") == 2;
    }

    private boolean isServiceId(String srcId) {
        // jcp-fe/00000-00000-00000/5464 = length29
        // jcp-fe/67de275c-1861-4e86-8e03-2ef643c8a592/9606 = length48
        return (srcId.length() == 29 || srcId.length() == 48)
                && JavaString.occurrenceCount(srcId, "/") == 2;
    }


}
