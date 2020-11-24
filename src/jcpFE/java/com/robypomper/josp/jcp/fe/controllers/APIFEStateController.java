package com.robypomper.josp.jcp.fe.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcp.fe.HTMLUtils;
import com.robypomper.josp.jcp.paths.fe.APIJCPFEObjs;
import com.robypomper.josp.jcp.paths.fe.APIJCPFEState;
import com.robypomper.josp.jcp.paths.fe.APIJCPFEStructure;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.protocol.HistoryLimits;
import com.robypomper.josp.protocol.JOSPStatusHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.List;


@RestController
//@Api(tags = {APIJCPFEState.SubGroupState.NAME})
public class APIFEStateController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;


    // Boolean

    @GetMapping(path = APIJCPFEState.FULL_PATH_BOOL)
    public ResponseEntity<Boolean> jsonBool(HttpSession session,
                                            @PathVariable("obj_id") String objId,
                                            @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLBooleanAction.class);
        return ResponseEntity.ok(comp.getState());
    }

    @GetMapping(path = APIJCPFEState.FULL_PATH_BOOL, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlBool(HttpSession session,
                           @PathVariable("obj_id") String objId,
                           @PathVariable("comp_path") String compPath) {
        Boolean value = jsonBool(session, objId, compPath).getBody();
        if (value == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' component's value on '%s' object.", compPath, objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(value,
                    String.format("%s Object's %s state", jslService.getComp(jslService.getHttp(session), objId, compPath, JSLBooleanAction.class).getName(), jslService.getObj(jslService.getHttp(session), objId).getName()),
                    String.format("<a href=\"%s\">Object</a>\n<a href=\"%s\">Object's structure</a>\n<a href=\"%s\">Component</a>", APIJCPFEObjs.FULL_PATH_DETAILS(objId), APIJCPFEStructure.FULL_PATH_STRUCT(objId), APIJCPFEStructure.FULL_PATH_COMP(objId, compPath)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's permissions on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // Range

    @GetMapping(path = APIJCPFEState.FULL_PATH_RANGE)
    public ResponseEntity<Double> jsonRange(HttpSession session,
                                            @PathVariable("obj_id") String objId,
                                            @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);
        return ResponseEntity.ok(comp.getState());
    }

    @GetMapping(path = APIJCPFEState.FULL_PATH_RANGE, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRange(HttpSession session,
                            @PathVariable("obj_id") String objId,
                            @PathVariable("comp_path") String compPath) {
        Double value = jsonRange(session, objId, compPath).getBody();
        if (value == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' component's value on '%s' object.", compPath, objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(value,
                    String.format("%s Object's %s state", jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class).getName(), jslService.getObj(jslService.getHttp(session), objId).getName()),
                    String.format("<a href=\"%s\">Object</a>\n<a href=\"%s\">Object's structure</a>\n<a href=\"%s\">Component</a>", APIJCPFEObjs.FULL_PATH_DETAILS(objId), APIJCPFEStructure.FULL_PATH_STRUCT(objId), APIJCPFEStructure.FULL_PATH_COMP(objId, compPath)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's permissions on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // History

    @GetMapping(path = APIJCPFEState.FULL_STATUS_HISTORY)
    public ResponseEntity<List<JOSPStatusHistory>> jsonStatusHistory(HttpSession session,
                                                                     @PathVariable("obj_id") String objId,
                                                                     @PathVariable("comp_path") String compPath,
                                                                     HistoryLimits limits) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);
        JSLComponent comp = obj.getStruct().getComponent(compPath);
        try {
            return ResponseEntity.ok(obj.getStruct().getComponentHistory(comp, limits, 20));
        } catch (JSLRemoteObject.ObjectNotConnected objectNotConnected) {
            objectNotConnected.printStackTrace();
        } catch (JSLRemoteObject.MissingPermission missingPermission) {
            missingPermission.printStackTrace();
        }
        return null;
    }

}
