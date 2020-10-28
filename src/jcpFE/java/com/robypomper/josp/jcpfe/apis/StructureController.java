package com.robypomper.josp.jcpfe.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robypomper.josp.jcpfe.HTMLUtils;
import com.robypomper.josp.jcpfe.apis.params.*;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEObjs;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEStructure;
import com.robypomper.josp.jcpfe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.objs.structure.JSLContainer;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanState;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeState;
import com.robypomper.josp.protocol.JOSPPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@SuppressWarnings("unused")
@RestController
//@Api(tags = {APIJCPFEStructure.SubGroupStructure.NAME})
public class StructureController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;


    // Obj's structure

    @GetMapping(path = APIJCPFEStructure.FULL_PATH_STRUCT)
    public ResponseEntity<JOSPStructHtml> jsonObjectStructure(HttpSession session,
                                                              @PathVariable("obj_id") String objId) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (!jslService.serviceCanPerm(obj, JOSPPerm.Type.Status))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Access denied to current user/service on access '%s' object's structure.", objId));

        return ResponseEntity.ok(new JOSPStructHtml(obj.getStruct().getStructure(), true));
    }

    @GetMapping(path = APIJCPFEStructure.FULL_PATH_STRUCT, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectStructure(HttpSession session,
                                      @PathVariable("obj_id") String objId) {
        JOSPStructHtml structHtml = jsonObjectStructure(session, objId).getBody();
        if (structHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' object's structure.", objId));

        try {
            return HTMLUtils.toHTMLFormattedJSON(structHtml,
                    String.format("%s Object's Structure", jslService.getObj(jslService.getHttp(session), objId).getName()),
                    String.format("<a href=\"%s\">Object</a>", APIJCPFEObjs.FULL_PATH_DETAILS(objId)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's structure on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // Obj's compos

    @GetMapping(path = APIJCPFEStructure.FULL_PATH_COMP)
    public ResponseEntity<JOSPComponentHtml> jsonObjectComponent(HttpSession session,
                                                                 @PathVariable("obj_id") String objId,
                                                                 @PathVariable("comp_path") String compPath) {
        JSLRemoteObject obj = jslService.getObj(jslService.getHttp(session), objId);

        // Check permission (Preventive)
        if (!jslService.serviceCanPerm(obj, JOSPPerm.Type.Status))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Access denied to current user/service on access '%s' object's structure.", objId));

        JSLComponent comp = compPath.equals("-") ? obj.getStruct().getStructure() : jslService.getComp(jslService.getHttp(session), objId, compPath, JSLComponent.class);
        return ResponseEntity.ok(generateJOSPComponentHtml(comp));
    }

    @GetMapping(path = APIJCPFEStructure.FULL_PATH_COMP, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlObjectComponent(HttpSession session,
                                      @PathVariable("obj_id") String objId,
                                      @PathVariable("comp_path") String compPath) {
        JOSPComponentHtml compHtml = jsonObjectComponent(session, objId, compPath).getBody();
        if (compHtml == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error on get '%s' object's component '%s'.", objId, compPath));

        try {
            return HTMLUtils.toHTMLFormattedJSON(compHtml,
                    String.format("%s Object's Component %s", jslService.getObj(jslService.getHttp(session), objId).getName(), compPath),
                    String.format("<a href=\"%s\">Object</a>\n<a href=\"%s\">Object's structure</a>", APIJCPFEObjs.FULL_PATH_DETAILS(objId), APIJCPFEStructure.FULL_PATH_STRUCT(objId)));

        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Error get '%s' object's structure on formatting response (%s).", objId, e.getMessage()), e);
        }
    }


    // Shared structures


    // Shared structure support methods

    public static List<JOSPComponentHtml> convert(Collection<JSLComponent> subComps) {
        List<JOSPComponentHtml> subCompsHtml = new ArrayList<>();
        for (JSLComponent sc : subComps)
            subCompsHtml.add(generateJOSPComponentHtml(sc));
        return subCompsHtml;
    }

    private static JOSPComponentHtml generateJOSPComponentHtml(JSLComponent comp) {
        if (comp instanceof JSLContainer)
            return new JOSPContainerHtml((JSLContainer) comp, true);

        // Actions
        if (comp instanceof JSLBooleanAction)
            return new JOSPBooleanActionHtml((JSLBooleanAction) comp);
        if (comp instanceof JSLRangeAction)
            return new JOSPRangeActionHtml((JSLRangeAction) comp);

        // States
        if (comp instanceof JSLBooleanState)
            return new JOSPBooleanStateHtml((JSLBooleanState) comp);
        if (comp instanceof JSLRangeState)
            return new JOSPRangeStateHtml((JSLRangeState) comp);

        return new JOSPComponentHtml(comp);
    }

}
