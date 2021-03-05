package com.robypomper.josp.jcp.params.jslwb;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class JSLStatus {

    public final String sessionId;
    public final boolean isJSLInit;

    public JSLStatus(String sessionId, boolean isJSLInit) {
        this.sessionId = sessionId;
        this.isJSLInit = isJSLInit;
    }

}
