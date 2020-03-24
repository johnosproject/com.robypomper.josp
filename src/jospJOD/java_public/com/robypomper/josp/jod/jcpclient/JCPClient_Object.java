package com.robypomper.josp.jod.jcpclient;

import com.robypomper.josp.core.jcpclient.JCPClient;


/**
 * Main JCP client interface for Objects.
 */
public interface JCPClient_Object extends JCPClient {

    /**
     * When set the objId will used as header value for each request send to the
     * server.
     *
     * @param objId the current object id, or null to reset it.
     */
    void setObjectId(String objId);

}
