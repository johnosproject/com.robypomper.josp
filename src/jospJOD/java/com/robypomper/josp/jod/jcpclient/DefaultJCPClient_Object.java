package com.robypomper.josp.jod.jcpclient;

import com.robypomper.josp.core.jcpclient.DefaultJCPConfigs;
import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.core.jcpclient.JCPClient_CliCredFlow;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jod.JOD_002;


/**
 * Object default implementation of {@link JCPClient} interface.
 * <p>
 * This class initialize a JCPClient object that can be used by Objects.
 */
public class DefaultJCPClient_Object extends JCPClient_CliCredFlow
        implements JCPClient_Object {

    // Constructor

    /**
     * Object JCPClient constructor, it setup the client for Object's requests.
     * <p>
     * Moreover this constructor disable SSL checks and bypass the
     * {@link com.robypomper.josp.core.jcpclient.AbsJCPClient} autoConnect
     * mechanism.
     *
     * @param settings the JOD settings.
     */
    public DefaultJCPClient_Object(JOD_002.Settings settings) throws ConnectionException {
        super(new DefaultJCPConfigs(settings.getJCPId(),
                        settings.getJCPSecret(),
                        "openid",
                        "",
                        settings.getJCPUrl(),
                        "jcp"),
                false);
        disableSSLChecks();
        if (settings.getJCPConnect())
            tryConnect();
    }

    // Headers default values setters

    @Override
    public void setObjectId(String objId) {
        if (objId != null)
            addDefaultHeader(APIObjs.HEADER_OBJID, objId);
        else
            removeDefaultHeader(APIObjs.HEADER_OBJID);
    }

}
