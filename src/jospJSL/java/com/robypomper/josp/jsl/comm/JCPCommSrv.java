package com.robypomper.josp.jsl.comm;

import com.robypomper.josp.core.jcpclient.JCPClient;
import com.robypomper.josp.jcp.apis.params.jospgws.S2OAccessInfo;
import com.robypomper.josp.jcp.apis.params.jospgws.S2OAccessRequest;
import com.robypomper.josp.jcp.apis.paths.APIJOSPGWs;
import com.robypomper.josp.jsl.JSL_002;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


/**
 * Support class for API JOSP GWs for service's requests.
 */
public class JCPCommSrv {

    // Internal vars

    private final JCPClient_Service jcpClient;
    private final JSL_002.Settings settings;
    private final String instanceId;


    // Constructor

    /**
     * Default constructor.
     *
     * @param jcpClient  the JCP client.
     * @param settings   the JOD settings.
     * @param instanceId the JOD instance id.
     */
    public JCPCommSrv(JCPClient_Service jcpClient, JSL_002.Settings settings, String instanceId) {
        this.jcpClient = jcpClient;
        this.settings = settings;
        this.instanceId = instanceId;
    }


    // Generator methods

    /**
     * Request to the JCP service's access info for Gateway S2O connection.
     * <p>
     * Service send his public certificate and instance id to the GW S2O and
     * the JCP respond with the GW S2O's address, port and public certificate.
     *
     * @return the GW S2O access info.
     */
    public S2OAccessInfo getS2OAccessInfo(Certificate clietnPublicCertificate) throws JCPClient.ConnectionException, JCPClient.RequestException, CertificateEncodingException {
        return jcpClient.execPostReq(APIJOSPGWs.URL_PATH_S2O_ACCESS, S2OAccessInfo.class, new S2OAccessRequest(instanceId, clietnPublicCertificate.getEncoded()), true);
    }

}