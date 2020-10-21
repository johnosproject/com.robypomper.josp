package com.robypomper.josp.jcpfe.apis.params;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.robypomper.josp.jsl.user.JSLUserMngr;

@JsonAutoDetect
public class JOSPUserHtml {

    public final String id;
    public final String name;
    public final boolean isAuthenticated;
    public final boolean isAdmin;
    public final boolean isMaker;
    public final boolean isDeveloper;

    public JOSPUserHtml(JSLUserMngr jslUsrMngr) {
        this.id = jslUsrMngr.getUserId();
        this.name = jslUsrMngr.getUsername();
        this.isAuthenticated = jslUsrMngr.isUserAuthenticated();
        this.isAdmin = jslUsrMngr.isAdmin();
        this.isMaker = jslUsrMngr.isMaker();
        this.isDeveloper = jslUsrMngr.isDeveloper();
    }

}
