package com.robypomper.josp.jcpfe.apis.params;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.robypomper.josp.jsl.user.JSLUserMngr;

@JsonAutoDetect
public class JOSPUserHtml {

    public final String id;
    public final String name;
    public final boolean isAuthenticated;

    public JOSPUserHtml(JSLUserMngr jslUsrMngr) {
        this.id = jslUsrMngr.getUserId();
        this.name = jslUsrMngr.getUsername();
        this.isAuthenticated = jslUsrMngr.isUserAuthenticated();
    }

}
