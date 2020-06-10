package com.robypomper.josp.jod.objinfo;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.objs.GenerateObjId;
import com.robypomper.josp.jcp.apis.params.objs.RegisterObj;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;


/**
 * Support class for API Objs access to the Object Info generators.
 */
public class JCPObjectInfo extends AbsJCPAPIs {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     */
    public JCPObjectInfo(JCPClient_Object jcpClient, JODSettings_002 settings) {
        super(jcpClient, settings);
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

    public boolean registerOrUpdate(JODObjectInfo objectInfo) throws JCPClient.ConnectionException, JCPClient.RequestException {
        RegisterObj regObjParam = new RegisterObj(objectInfo.getObjName(), objectInfo.getJODVersion(), objectInfo.getOwnerId());
        regObjParam.setModel(objectInfo.getModel());
        regObjParam.setBrand(objectInfo.getBrand());
        regObjParam.setLongDescr(objectInfo.getLongDescr());
        regObjParam.setStructure(objectInfo.readStructureStr());
        return jcpClient.execPostReq(APIObjs.URL_PATH_REGISTER, Boolean.class, regObjParam, true);
    }

    public boolean update(JODObjectInfo objectInfo) {
        System.out.println("WAR: JCPObjectInfo::update() not yet implemented.");
        return false;
    }

}
