package com.robypomper.josp.jod.objinfo;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.objs.GenerateObjId;
import com.robypomper.josp.jcp.apis.params.objs.RegisterObj;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.jod.systems.JODObjectInfo;


/**
 * Support class for API Objs access to the Object Info generators.
 */
public class JCPObjectInfo {

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
     * @param ownerId the owner's User ID.
     * @return the object's Cloud ID.
     */
    public String generateObjIdCloud(String objIdHw, String ownerId) throws JCPClient.RequestException, JCPClient.ConnectionException {
        GenerateObjId params = new GenerateObjId(objIdHw, ownerId);
        return jcpClient.execPostReq(APIObjs.URL_PATH_GENERATEID, String.class, params, true);
    }

    public boolean isRegistered() throws JCPClient.ConnectionException, JCPClient.RequestException {
        return jcpClient.execGetReq(APIObjs.URL_PATH_REGISTER_IS, Boolean.class, true);
    }

    public boolean register(JODObjectInfo objectInfo) throws JCPClient.ConnectionException, JCPClient.RequestException {
        RegisterObj regObjParam = new RegisterObj(objectInfo.getObjName(), objectInfo.getStructureStr());
        regObjParam.setModel(objectInfo.getModel());
        regObjParam.setBrand(objectInfo.getBrand());
        regObjParam.setLongDescr(objectInfo.getLongDescr());
        return jcpClient.execPostReq(APIObjs.URL_PATH_REGISTER, Boolean.class, regObjParam, true);
    }

    public boolean update(JODObjectInfo objectInfo) {
        System.out.println("WAR: JCPObjectInfo::update() not yet implemented.");
        return false;
    }

}
