package com.robypomper.josp.jcp.jslwebbridge.controllers.internal.status.buildinfo;


import com.robypomper.jcpJSLWebBridge.BuildInfoJcpJSLWebBridge;
import com.robypomper.josp.jcp.defs.base.internal.status.buildinfo.Params20;
import com.robypomper.josp.jcp.defs.base.internal.status.buildinfo.Paths20;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;


/**
 * JCP All - Status / Build Info 2.0 (JSL Web Bridge)
 */
@RestController(value = Paths20.API_NAME + " " + Paths20.DOCS_NAME)
@Api(tags = Paths20.DOCS_NAME, description = Paths20.DOCS_DESCR)
public class Controller20 extends com.robypomper.josp.jcp.base.controllers.internal.status.buildinfo.Controller20 {

    static Params20.BuildInfo current = Params20.BuildInfo.clone(BuildInfoJcpJSLWebBridge.current);

    @Override
    protected Params20.BuildInfo getInstanceReqSubClass() {
        return current;
    }

}
