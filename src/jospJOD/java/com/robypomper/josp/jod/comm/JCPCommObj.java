package com.robypomper.josp.jod.comm;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.jospgws.O2SAccessInfo;
import com.robypomper.josp.jcp.apis.params.jospgws.O2SAccessRequest;
import com.robypomper.josp.jcp.apis.paths.APIJOSPGWs;
import com.robypomper.josp.jod.JODSettings_002;
import com.robypomper.josp.jod.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jod.jcpclient.JCPClient_Object;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


/**
 * Support class for API JOSP GWs for object's requests.
 */
public class JCPCommObj extends AbsJCPAPIs {

    // Internal vars

    private final String instanceId;


    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient  the JCP client.
     * @param settings   the JOD settings.
     * @param instanceId the JOD instance id.
     */
    public JCPCommObj(JCPClient_Object jcpClient, JODSettings_002 settings, String instanceId) {
        super(jcpClient, settings);
        this.instanceId = instanceId;
    }


    // Generator methods

    /**
     * Request to the JCP object's access info for Gateway O2S connection.
     * <p>
     * Object send his public certificate and instance id to the GW O2S and
     * the JCP respond with the GW O2S's address, port and public certificate.
     *
     * @return the GW O2S access info.
     */
    public O2SAccessInfo getO2SAccessInfo(Certificate clietnPublicCertificate) throws JCPClient.ConnectionException, JCPClient.RequestException, CertificateEncodingException {
        return jcpClient.execPostReq(APIJOSPGWs.URL_PATH_O2S_ACCESS, O2SAccessInfo.class, new O2SAccessRequest(instanceId, clietnPublicCertificate.getEncoded()), true);
    }

}
