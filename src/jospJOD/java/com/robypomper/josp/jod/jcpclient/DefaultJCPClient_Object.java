package com.robypomper.josp.jod.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPClient2;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jod.JODSettings_002;


/**
 * Object default implementation of {@link DefaultJCPClient2} class.
 * <p>
 * This class initialize a JCPClient object that can be used by Objects.
 */
public class DefaultJCPClient_Object extends DefaultJCPClient2 implements JCPClient_Object {

    // Constructor

    public DefaultJCPClient_Object(JODSettings_002 settings) {
        super(settings.getJCPId(),
                settings.getJCPSecret(),
                settings.getJCPUrlAPIs(),
                settings.getJCPUrlAuth(),
                "openid",
                "",
                "jcp",
                settings.getJCPRefreshTime());
    }


    // Headers default values setters

    @Override
    public void setObjectId(String objId) {
        if (objId != null && !objId.isEmpty())
            addDefaultHeader(APIObjs.HEADER_OBJID, objId);
        else
            removeDefaultHeader(APIObjs.HEADER_OBJID);
    }

}
