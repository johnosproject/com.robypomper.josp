package com.robypomper.josp.jod.objinfo;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;


/**
 * Support class for API Objs access to the Object Info generators.
 */
public class JCPObjectInfo {

    // Class constants

    private final String URL_OBJINFO = "https://localhost:9001/apis/object/ver/objinfo";
    private final String URL_OBJINFO_GENERATE = URL_OBJINFO + "/generate";


    // Internal vars

    private final JCPClient_Object jcpClient;


    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public JCPObjectInfo(JCPClient_Object jcpClient) {
        this.jcpClient = jcpClient;
    }


    // Generator methods

    /**
     * Generate and return a valid Object's Cloud ID.
     *
     * @param objIdHw the object's Hardware ID.
     * @param usrId   the owner's User ID.
     * @return the object's Cloud ID.
     */
    public String generateObjIdCloud(String objIdHw, String usrId) throws JCPClient.RequestException, JCPClient.ConnectionException {
        ArgsGenerateObjId params = new ArgsGenerateObjId();
        params.objIdHw = objIdHw;
        params.usrId = usrId;
        return jcpClient.execPostReq(URL_OBJINFO_GENERATE + "/obj_id", String.class, params, true);
    }


    // Messaging classes

    /**
     * Messaging class used by {@link #generateObjIdCloud(String, String)} method.
     */
    private static class ArgsGenerateObjId {
        public String objIdHw;
        public String usrId;
    }

}
