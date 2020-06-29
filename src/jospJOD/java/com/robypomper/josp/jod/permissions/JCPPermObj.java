package com.robypomper.josp.jod.permissions;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jcp.apis.paths.APIPermissions;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;
import com.robypomper.josp.protocol.JOSPPermissions;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<ObjPermission> generatePermissionsFromJCP() throws JCPClient.ConnectionException, JCPClient.RequestException {
        JOSPPermissions.GenerateStrategy strategy = locSettings.getPermissionsGenerationStrategy();
        ObjPermission[] objPermArray = jcpClient.execGetReq(APIPermissions.URL_PATH_OBJGENERATE + "/" + strategy, ObjPermission[].class, true);
        return Arrays.asList(objPermArray);
    }

    /**
     * Send local permission to cloud, merge them with cloud stored permission
     * and the return merged list.
     *
     * @param localPermissions local object's permission list.
     * @return updated permission list.
     */
    public List<ObjPermission> refreshPermissionsFromJCP(List<ObjPermission> localPermissions) throws JCPClient.ConnectionException, JCPClient.RequestException {
        ObjPermission[] objPermArray = jcpClient.execPostReq(APIPermissions.URL_PATH_OBJMERGE, ObjPermission[].class, localPermissions, true);
        return new ArrayList<>(Arrays.asList(objPermArray));
    }

}
