package com.robypomper.josp.jsl.comm;

import com.github.scribejava.core.model.Verb;
import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jcp.apis.params.jospgws.S2OAccessInfo;
import com.robypomper.josp.jcp.apis.params.jospgws.S2OAccessRequest;
import com.robypomper.josp.jcp.apis.paths.APIJOSPGWs;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.jcpclient.AbsJCPAPIs;
import com.robypomper.josp.jsl.jcpclient.JCPClient_Service;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;


/**
 * Support class for API JOSP GWs for service's requests.
 */
public class JCPCommSrv extends AbsJCPAPIs {

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
    public JCPCommSrv(JCPClient_Service jcpClient, JSLSettings_002 settings, String instanceId) {
        super(jcpClient, settings);
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
    public S2OAccessInfo getS2OAccessInfo(Certificate clietnPublicCertificate) throws CertificateEncodingException, JCPClient2.ConnectionException, JCPClient2.AuthenticationException, JCPClient2.ResponseException, JCPClient2.RequestException {
        return jcpClient.execReq(Verb.POST, APIJOSPGWs.FULL_PATH_S2O_ACCESS, S2OAccessInfo.class, new S2OAccessRequest(instanceId, clietnPublicCertificate.getEncoded()), true);
    }

}
