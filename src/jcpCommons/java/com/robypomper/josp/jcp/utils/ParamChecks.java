package com.robypomper.josp.jcp.utils;

import com.robypomper.josp.jcp.paths.apis.JCPAPIsGWRegistration;
import com.robypomper.josp.paths.APIObjs;
import com.robypomper.josp.paths.APISrvs;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ParamChecks {

    public static void checkObjId(Logger log, String objId) {
        checkHeaderParam(log, APIObjs.HEADER_OBJID, objId);
    }

    public static void checkSrvId(Logger log, String srvId) {
        checkHeaderParam(log, APISrvs.HEADER_SRVID, srvId);
    }

    public static void checkGwId(Logger log, String gwId) throws ResponseStatusException {
        checkHeaderParam(log, JCPAPIsGWRegistration.HEADER_JCPGWID, gwId);
    }

    public static void checkHeaderParam(Logger log, String headerName, String value) {
        if (value == null || value.isEmpty()) {
            log.trace(String.format("Error executing request because header '%s' not set", headerName));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Missing mandatory header '%s'.", headerName));
        }
    }

}
