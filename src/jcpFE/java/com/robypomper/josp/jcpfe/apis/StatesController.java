package com.robypomper.josp.jcpfe.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcpfe.HTMLUtils;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEObjs;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEState;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEStructure;
import com.robypomper.josp.jcpfe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
//@Api(tags = {APIJCPFEState.SubGroupState.NAME})
public class StatesController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;


    // Boolean

    @GetMapping(path = APIJCPFEState.FULL_PATH_BOOL)
    public ResponseEntity<Boolean> jsonBool(@PathVariable("obj_id") String objId,
                                            @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = jslService.getComp(objId, compPath, JSLBooleanAction.class);
        return ResponseEntity.ok(comp.getState());
    }

    @GetMapping(path = APIJCPFEState.FULL_PATH_BOOL, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlBool(@PathVariable("obj_id") String objId,
                           @PathVariable("comp_path") String compPath) {
        Boolean value = jsonBool(objId, compPath).getBody();
        if (value == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' component's value on '%s' object.", compPath, objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(value,
                    String.format("%s Object's %s state", jslService.getComp(objId, compPath, JSLBooleanAction.class).getName(), jslService.getObj(objId).getName()),
                    String.format("<a href=\"%s\">Object</a>\n<a href=\"/test/structure/%s/\">Object's structure</a>\n<a href=\"%s\">Component</a>", APIJCPFEObjs.FULL_PATH_DETAILS(objId), APIJCPFEStructure.FULL_PATH_STRUCT(objId), APIJCPFEStructure.FULL_PATH_COMP(objId, compPath)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's permissions on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // Range

    @GetMapping(path = APIJCPFEState.FULL_PATH_RANGE)
    public ResponseEntity<Double> jsonRange(@PathVariable("obj_id") String objId,
                                            @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(objId, compPath, JSLRangeAction.class);
        return ResponseEntity.ok(comp.getState());
    }

    @GetMapping(path = APIJCPFEState.FULL_PATH_RANGE, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRange(@PathVariable("obj_id") String objId,
                            @PathVariable("comp_path") String compPath) {
        Double value = jsonRange(objId, compPath).getBody();
        if (value == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' component's value on '%s' object.", compPath, objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(value,
                    String.format("%s Object's %s state", jslService.getComp(objId, compPath, JSLRangeAction.class).getName(), jslService.getObj(objId).getName()),
                    String.format("<a href=\"%s\">Object</a>\n<a href=\"/test/structure/%s/\">Object's structure</a>\n<a href=\"%s\">Component</a>", APIJCPFEObjs.FULL_PATH_DETAILS(objId), APIJCPFEStructure.FULL_PATH_STRUCT(objId), APIJCPFEStructure.FULL_PATH_COMP(objId, compPath)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's permissions on formatting response (%s).", objId, e.getMessage()), e);
        }
    }

}
