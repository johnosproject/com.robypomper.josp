package com.robypomper.josp.jcpfe.apis;

import com.robypomper.josp.jcpfe.apis.params.JOSPObjHtml;
import com.robypomper.josp.jcpfe.apis.params.JOSPSrvHtml;
import com.robypomper.josp.jcpfe.apis.params.JOSPUserHtml;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEObjs;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEService;
import com.robypomper.josp.jcpfe.apis.paths.APIJCPFEUser;
import com.robypomper.josp.jcpfe.jsl.JSLSpringService;
import com.robypomper.josp.protocol.JOSPProtocol_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/test/")
public class SingleJSLInstanceTest {

    // Internal vars

    @Autowired
    private JSLSpringService jslService;


    // List

    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String htmlHome(HttpSession session) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("<h1>JCP Front-End</h1>\n");
        responseBuilder.append("<div style=\"float: left;\">\n");

        // Objects
        responseBuilder.append("<p><b>Objects:</b></p>\n");
        responseBuilder.append("<ul>\n");
        for (JOSPObjHtml o : ObjsMngrController.objectsList(session, jslService))
            responseBuilder.append("<li>").append("<a href=\"").append(o.pathSingle).append("\">").append(o.name).append("</a>").append("</li>\n");
        responseBuilder.append("</ul>\n");
        responseBuilder.append(String.format("Go to the <a href=\"%s\">full Objects list</a> (%d objects)\n", APIJCPFEObjs.FULL_PATH_LIST, ObjsMngrController.objectsList(session, jslService).size()));
        responseBuilder.append("<hr>\n");

        // User
        responseBuilder.append("<p><b>Current user:</b></p>\n");
        JOSPUserHtml user = UserController.userDetails(session, jslService);
        responseBuilder.append("<p>");
        responseBuilder.append("User: ").append(user.name).append("(");
        responseBuilder.append(String.format("<a href=\"%s\">details</a>)<br>\n", APIJCPFEUser.FULL_PATH_DETAILS));
        if (!user.isAuthenticated)
            responseBuilder.append(String.format("<a href=\"%s\">Login</a><br>\n", APIJCPFEUser.FULL_PATH_LOGIN));
        else
            responseBuilder.append(String.format("<a href=\"%s\">Logout</a><br>\n", APIJCPFEUser.FULL_PATH_LOGOUT));
        responseBuilder.append("</p>\n");
        responseBuilder.append("<hr>\n");

        // Service
        responseBuilder.append("<p><b>Service:</b></p>\n");
        JOSPSrvHtml srv = ServiceController.serviceDetails(session, jslService);
        responseBuilder.append("<p>");
        responseBuilder.append("Srv: ").append(srv.name).append("(");
        responseBuilder.append(String.format("<a href=\"%s\">details</a>)<br>\n", APIJCPFEService.FULL_PATH_DETAILS));
        responseBuilder.append("ID: ").append(String.format(JOSPProtocol_Service.FULL_SRV_ID_FORMAT, srv.srvId, srv.usrId, srv.instId)).append("<br>\n");
        responseBuilder.append("</p>\n");


        responseBuilder.append("</div>");

        // Login box
        responseBuilder.append("<div style=\"float: right;\">\n");
        if (!user.isAuthenticated) {
            responseBuilder.append("User <b>Anonymous</b><br>");
            responseBuilder.append(String.format("<a href=\"%s\">Login</a><br>\n", APIJCPFEUser.FULL_PATH_LOGIN));
        } else {
            responseBuilder.append(String.format("User <b>%s</b><br>", user.name));
            responseBuilder.append(String.format("<a href=\"%s\">Logout</a><br>\n", APIJCPFEUser.FULL_PATH_LOGOUT));
        }

        responseBuilder.append("</div>\n");


        return responseBuilder.toString();
    }

}
