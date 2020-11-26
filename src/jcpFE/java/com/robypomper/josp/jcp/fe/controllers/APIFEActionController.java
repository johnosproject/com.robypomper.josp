package com.robypomper.josp.jcp.fe.controllers;

import com.robypomper.java.JavaFormatter;
import com.robypomper.josp.jcp.fe.HTMLUtils;
import com.robypomper.josp.jcp.fe.jsl.JSLSpringService;
import com.robypomper.josp.jcp.info.JCPFEVersions;
import com.robypomper.josp.jcp.params.fe.JOSPObjHtml;
import com.robypomper.josp.jcp.paths.fe.APIFEAction;
import com.robypomper.josp.jcp.paths.fe.APIFEStruct;
import com.robypomper.josp.jcp.service.docs.SwaggerConfigurer;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLBooleanAction;
import com.robypomper.josp.jsl.objs.structure.pillars.JSLRangeAction;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.spring.web.plugins.Docket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@SuppressWarnings("unused")
@RestController
@Api(tags = {APIFEAction.SubGroupAction.NAME})
public class APIFEActionController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;
    @Autowired
    private SwaggerConfigurer swagger;


    // Docs configs

    @Bean
    public Docket swaggerConfig_APIFEAction() {
        SwaggerConfigurer.APISubGroup[] sg = new SwaggerConfigurer.APISubGroup[1];
        sg[0] = new SwaggerConfigurer.APISubGroup(APIFEAction.SubGroupAction.NAME, APIFEAction.SubGroupAction.DESCR);
        return SwaggerConfigurer.createAPIsGroup(new SwaggerConfigurer.APIGroup(APIFEAction.API_NAME, APIFEAction.API_VER, JCPFEVersions.API_NAME, sg), swagger.getUrlBaseAuth());
    }


    // Methods - Boolean

    @GetMapping(path = APIFEAction.FULL_PATH_BOOL_SWITCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBoolSwitch(@ApiIgnore HttpSession session,
                                                  @PathVariable("obj_id") String objId,
                                                  @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLBooleanAction.class);

        try {
            comp.execSwitch();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIFEAction.FULL_PATH_BOOL_TRUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBoolTrue(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLBooleanAction.class);

        try {

            comp.execSetTrue();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send action commands to '%s' component of '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIFEAction.FULL_PATH_BOOL_FALSE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonBoolFalse(@ApiIgnore HttpSession session,
                                                 @PathVariable("obj_id") String objId,
                                                 @PathVariable("comp_path") String compPath) {
        JSLBooleanAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLBooleanAction.class);

        try {

            comp.execSetFalse();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send action commands to '%s' component of '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }


    // Methods - Range

    @GetMapping(path = APIFEAction.FULL_PATH_RANGE_SET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public String jospRangeSet_Error(@ApiIgnore HttpSession session,
                                     @PathVariable("obj_id") String objId,
                                     @PathVariable("comp_path") String compPath) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing 'val' parameter in GET request");
    }

    @PostMapping(path = APIFEAction.FULL_PATH_RANGE_SET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeSet_POST(@ApiIgnore HttpSession session,
                                                     @PathVariable("obj_id") String objId,
                                                     @PathVariable("comp_path") String compPath,
                                                     @RequestParam("val") String val) {
        return jsonRangeSet(session, objId, compPath, val);
    }

    @GetMapping(path = APIFEAction.FULL_PATH_RANGE_SETg, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeSet(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath,
                                                @PathVariable("val") String val) {
        Double dVal = JavaFormatter.strToDouble(val);
        if (dVal == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Request param 'val' can't be cast to double (%s), action '%s' on '%s' object not executed.", val, compPath, objId));

        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);

        try {
            comp.execSetValue(dVal);
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIFEAction.FULL_PATH_RANGE_INC, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "&&Description&&")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeInc(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);

        try {
            comp.execIncrease();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIFEAction.FULL_PATH_RANGE_DEC, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeDec(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);

        try {
            comp.execDecrease();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIFEAction.FULL_PATH_RANGE_MAX, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeMax(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);

        try {
            comp.execSetMax();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIFEAction.FULL_PATH_RANGE_MIN, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRangeMin(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);

        try {
            comp.execSetMin();
            return ResponseEntity.ok(true);

        } catch (JSLRemoteObject.MissingPermission e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Permission denied to current user/service on send '%s' action commands to '%s' object.", compPath, objId), e);

        } catch (JSLRemoteObject.ObjectNotConnected e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Can't send '%s' action commands because '%s' object is not connected.", compPath, objId), e);
        }
    }

    @GetMapping(path = APIFEAction.FULL_PATH_RANGE_1_2, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRange1_2(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);

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

    @GetMapping(path = APIFEAction.FULL_PATH_RANGE_1_3, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRange1_3(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);

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

    @GetMapping(path = APIFEAction.FULL_PATH_RANGE_2_3, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Method worked successfully", response = JOSPObjHtml.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User not authenticated")
    })
    public ResponseEntity<Boolean> jsonRange2_3(@ApiIgnore HttpSession session,
                                                @PathVariable("obj_id") String objId,
                                                @PathVariable("comp_path") String compPath) {
        JSLRangeAction comp = jslService.getComp(jslService.getHttp(session), objId, compPath, JSLRangeAction.class);

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

}
