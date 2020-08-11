package com.robypomper.josp.jcpfe.apis;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jcpfe.HTMLUtils;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEAction;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEStructure;
import com.robypomper.josp.jcpfe.jsl.JSLSpringService;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;


@RestController
//@Api(tags = {APIJCPFEAction.SubGroupAction.NAME})
public class ActionsController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;


    // Boolean

    @GetMapping(path = APIJCPFEAction.FULL_PATH_BOOL_SWITCH)
    public ResponseEntity<Boolean> jsonBoolSwitch(@PathVariable("obj_id") String objId,
                                                  @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = jslService.getComp(objId, compPath, JSLBooleanAction.class);

        try {
            comp.execSwitch();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_BOOL_SWITCH, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlBoolSwitch(HttpServletRequest request,
                                 @PathVariable("obj_id") String objId,
                                 @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonBoolSwitch(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_BOOL_TRUE)
    public ResponseEntity<Boolean> jsonBoolTrue(@PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = jslService.getComp(objId, compPath, JSLBooleanAction.class);

        try {

            comp.execSetTrue();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send action commands to '%s' component of '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_BOOL_TRUE, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlBoolTrue(HttpServletRequest request,
                               @PathVariable("obj_id") String objId,
                               @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonBoolTrue(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_BOOL_FALSE)
    public ResponseEntity<Boolean> jsonBoolFalse(@PathVariable("obj_id") String objId,
                                                 @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = jslService.getComp(objId, compPath, JSLBooleanAction.class);

        try {

            comp.execSetFalse();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send action commands to '%s' component of '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_BOOL_FALSE, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlBoolFalse(HttpServletRequest request,
                                @PathVariable("obj_id") String objId,
                                @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonBoolFalse(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }


    // Range actions

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_SET, produces = MediaType.TEXT_HTML_VALUE)
    public String formObjectOwner(@PathVariable("obj_id") String objId,
                                  @PathVariable("comp_path") String compPath) {
        // ONLY HTML

        return "<form id = \"form_id\" method=\"post\">\n" +
                "    <input type=\"text\" id=\"val\" name=\"val\" value=\"" + jslService.getComp(objId, compPath, JSLRangeAction.class).getState() + "\">\n" +
                "    <input type=\"hidden\" id=\"origin_url\" name=\"origin_url\">\n" +
                "    <input type=\"submit\" value=\"Set\">\n" +
                "</form>\n" +
                "<script>" +
                "    document.getElementById(\"origin_url\").value = document.referrer" +
                "</script>";
    }

    @PostMapping(path = APIJCPFEAction.FULL_PATH_RANGE_SET)
    public ResponseEntity<Boolean> jsonRangeSet_POST(@PathVariable("obj_id") String objId,
                                                     @PathVariable("comp_path") String compPath,
                                                     @RequestParam("val") String val) {
        return jsonRangeSet(objId, compPath, val);
    }

    @PostMapping(path = APIJCPFEAction.FULL_PATH_RANGE_SET, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRangeSet_POST(HttpServletRequest request,
                                    @PathVariable("obj_id") String objId,
                                    @PathVariable("comp_path") String compPath,
                                    @RequestParam("val") String val,
                                    @RequestParam("origin_url") String originUrl) {
        return htmlRangeSet(request, objId, compPath, val, originUrl);
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_SETg)
    public ResponseEntity<Boolean> jsonRangeSet(@PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath,
                                                @PathVariable("val") String val) {
        Double dVal = JavaFormatter.strToDouble(val);
        if (dVal == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request param 'val' can't be cast to double (%s), action '%s' on '%s' object not executed.", val, compPath, objId));

        JSLRangeAction comp = jslService.getComp(objId, compPath, JSLRangeAction.class);

        try {
            comp.execSetValue(dVal);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_SETg, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRangeSet(HttpServletRequest request,
                               @PathVariable("obj_id") String objId,
                               @PathVariable("comp_path") String compPath,
                               @PathVariable("val") String val,
                               @RequestParam("origin_url") String originUrl) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonRangeSet(objId, compPath, val).getBody();
        //return HTMLFormatter.redirectBackAndReturn(request, success);
        // This is different to other htmlRange/ActionXXX because the value is set via an intermediate GET HTTP page
        if (originUrl == null)
            originUrl = APIJCPFEStructure.FULL_PATH_STRUCT(objId);
        return HTMLUtils.redirectAndReturn(originUrl, success);
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_INC)
    public ResponseEntity<Boolean> jsonRangeInc(@PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(objId, compPath, JSLRangeAction.class);

        try {
            comp.execIncrease();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_INC, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRangeInc(HttpServletRequest request,
                               @PathVariable("obj_id") String objId,
                               @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonRangeInc(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_DEC)
    public ResponseEntity<Boolean> jsonRangeDec(@PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(objId, compPath, JSLRangeAction.class);

        try {
            comp.execDecrease();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_DEC, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRangeDec(HttpServletRequest request,
                               @PathVariable("obj_id") String objId,
                               @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonRangeDec(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_MAX)
    public ResponseEntity<Boolean> jsonRangeMax(@PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(objId, compPath, JSLRangeAction.class);

        try {
            comp.execSetMax();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_MAX, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRangeMax(HttpServletRequest request,
                               @PathVariable("obj_id") String objId,
                               @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonRangeMax(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_MIN)
    public ResponseEntity<Boolean> jsonRangeMin(@PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(objId, compPath, JSLRangeAction.class);

        try {
            comp.execSetMin();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_MIN, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRangeMin(HttpServletRequest request,
                               @PathVariable("obj_id") String objId,
                               @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonRangeMin(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_1_2)
    public ResponseEntity<Boolean> jsonRange1_2(@PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(objId, compPath, JSLRangeAction.class);

        try {
            double half = comp.getMin() + ((comp.getMax() - comp.getMin()) / 2);
            comp.execSetValue(half);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_1_2, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRange1_2(HttpServletRequest request,
                               @PathVariable("obj_id") String objId,
                               @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonRange1_2(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_1_3)
    public ResponseEntity<Boolean> jsonRange1_3(@PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(objId, compPath, JSLRangeAction.class);

        try {
            double fist_third = comp.getMin() + ((comp.getMax() - comp.getMin()) / 3);
            comp.execSetValue(fist_third);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_1_3, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRange1_3(HttpServletRequest request,
                               @PathVariable("obj_id") String objId,
                               @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonRange1_3(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }


    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_2_3)
    public ResponseEntity<Boolean> jsonRange2_3(@PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(objId, compPath, JSLRangeAction.class);

        try {
            double second_third = comp.getMin() + ((comp.getMax() - comp.getMin()) / 3) + ((comp.getMax() - comp.getMin()) / 3);
            comp.execSetValue(second_third);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIJCPFEAction.FULL_PATH_RANGE_2_3, produces = MediaType.TEXT_HTML_VALUE)
    public String htmlRange2_3(HttpServletRequest request,
                               @PathVariable("obj_id") String objId,
                               @PathVariable("comp_path") String compPath) {
        @SuppressWarnings("ConstantConditions") boolean success = jsonRange2_3(objId, compPath).getBody();
        return HTMLUtils.redirectBackAndReturn(request, success);
    }

}
