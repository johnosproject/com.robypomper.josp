package com.robypomper.josp.jod.jcpclient;

import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.paths.APIObjs;


/**
 * Main JCP client interface for Objects.
 */
public interface JCPClient_Object extends JCPClient2 {

    // Headers default values setters

    /**
     * When set the objId will used as {@value APIObjs#HEADER_OBJID} header
     * value for each request send to the server.
     *
     * @param objId the current object id, or null to reset it.
     */
    void setObjectId(String objId);

}
