package com.robypomper.josp.jcpfe.apis;

import com.robypomper.josp.jcpfe.apis.paths.APIJCPFESSEUpdater;
import com.robypomper.josp.jcpfe.jsl.JSLSpringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
//@Api(tags = {APIJCPFESSEUpdater.SubGroupState.NAME})
public class SSEUpdaterController {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;


    @GetMapping(APIJCPFESSEUpdater.FULL_PATH_INIT)
    public SseEmitter streamSseMvc(HttpSession session,
                                   CsrfToken token) {
        SseEmitter emitter;
        try {
            emitter = jslService.newEmitter(session);

        } catch (JSLSpringService.JSLSpringException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

        SseEmitter.SseEventBuilder event = SseEmitter.event()
                .data("csrf:" + token.getToken() + ";" +
                        "header:" + token.getHeaderName())
                .id(String.valueOf(0));
        try {
            emitter.send(event);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }

        return emitter;
    }


}
