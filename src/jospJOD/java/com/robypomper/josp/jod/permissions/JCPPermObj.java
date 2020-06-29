package com.robypomper.josp.jod.permissions;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.paths.APIPermissions;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.protocol.JOSPPerm;
import com.robypomper.josp.protocol.JOSPProtocol;

import java.util.List;


/**
 * Support class for API Perm access to the object's permissions.
 */
public class JCPPermObj extends AbsJCPAPIs {

    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient the JCP client.
     * @param settings  the JOD settings.
     */
    public JCPPermObj(JCPClient_Object jcpClient, JODSettings_002 settings) {
        super(jcpClient, settings);
    }


    // Generator methods

    /**
     * Request to the JCP a generic object's permission list.
     * <p>
     * The list generated can be different depending on generation strategy request.
     *
     * @return a valid permission list.
     */
    public List<JOSPPerm> generatePermissionsFromJCP() throws JCPClient.ConnectionException, JCPClient.RequestException {
        JOSPPerm.GenerateStrategy strategy = locSettings.getPermissionsGenerationStrategy();
        String permsStr = jcpClient.execGetReq(APIPermissions.URL_PATH_OBJGENERATE + "/" + strategy, String.class, true);
        try {
            return JOSPPerm.listFromString(permsStr);
        } catch (JOSPProtocol.ParsingException e) {
            throw new JCPClient.RequestException(String.format("Can't parse JOSPPerm list from returned string '%s'", permsStr));
        }
    }

    /**
     * Send local permission to cloud, merge them with cloud stored permission
     * and the return merged list.
     *
     * @param localPermsStr local object's permission list.
     * @return updated permission list.
     */
    public List<JOSPPerm> refreshPermissionsFromJCP(String localPermsStr) throws JCPClient.ConnectionException, JCPClient.RequestException {
        String permsStr = jcpClient.execPostReq(APIPermissions.URL_PATH_OBJMERGE, String.class, localPermsStr, true);
        try {
            return JOSPPerm.listFromString(permsStr);
        } catch (JOSPProtocol.ParsingException e) {
            throw new JCPClient.RequestException(String.format("Can't parse JOSPPerm list from returned string '%s'", permsStr));
        }
    }

}
