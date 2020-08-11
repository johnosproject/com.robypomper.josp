package com.robypomper.josp.jcpfe.jsl;

import com.robypomper.josp.core.jcpclient.JCPClient2;
import com.robypomper.josp.jsl.FactoryJSL;
import com.robypomper.josp.jsl.JSL;
import com.robypomper.josp.jsl.JSLSettings_002;
import com.robypomper.josp.jsl.objs.JSLRemoteObject;
import com.robypomper.josp.jsl.objs.structure.DefaultJSLComponentPath;
import com.robypomper.josp.jsl.objs.structure.JSLComponent;
import com.robypomper.josp.jsl.user.JSLUserMngr;
import com.robypomper.josp.protocol.JOSPPerm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JSLSpringService {

    // Internal vars

    private final String jslVersion;
    private final String urlAPIs;
    private final String urlAuth;
    private final String clientId;
    private final String clientSecret;
    private final String clientCallback;
    private final String srvId;
    private final String srvName = "jcpFrontEnd";
    private final String locCommEnabled = "false";


    // Constructor

    public JSLSpringService(@Value("${jsl.version:2.0.0}") String jslVersion,
                            @Value("${" + JSLSettings_002.JCP_URL_APIS + ":" + JSLSettings_002.JCP_URL_DEF_APIS + "}") String urlAPIs,
                            @Value("${" + JSLSettings_002.JCP_URL_AUTH + ":" + JSLSettings_002.JCP_URL_AUTH + "}") String urlAuth,
                            @Value("${" + JSLSettings_002.JCP_CLIENT_ID + ":}") String clientId,
                            @Value("${" + JSLSettings_002.JCP_CLIENT_SECRET + ":}") String clientSecret,
                            @Value("${" + JSLSettings_002.JCP_CLIENT_CALLBACK + ":}") String clientCallback,
                            @Value("${" + JSLSettings_002.JSLSRV_ID + ":}") String srvId) {
        if (clientId.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP FrontEnd", JSLSettings_002.JCP_CLIENT_ID));
        if (clientSecret.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP FrontEnd", JSLSettings_002.JCP_CLIENT_SECRET));
        if (clientCallback.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP FrontEnd", JSLSettings_002.JCP_CLIENT_CALLBACK));
        if (srvId.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP FrontEnd", JSLSettings_002.JSLSRV_ID));

        this.jslVersion = jslVersion;
        this.urlAPIs = urlAPIs;
        this.urlAuth = urlAuth;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientCallback = clientCallback;
        this.srvId = srvId;
    }


    // Sessions and JSLs

    public JSL getHttp(HttpSession session) {
        try {
            return get(session);

        } catch (JSLSpringService.JSLSpringException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can not access to JSL instance for session '%s'.", session.getId()), e);
        }
    }

    public JSL get(HttpSession session) throws JSLSpringException {
        if (session.getAttribute("JSL-Instance") != null)
            return (JSL) session.getAttribute("JSL-Instance");

        try {
            return registerSessions(session);

        } catch (JSL.FactoryException | JSL.ConnectException e) {
            throw new JSLSpringException(String.format("Error creating JSL instance for sessions '%s'", session.getId()), e);
        }
    }

    private JSL registerSessions(HttpSession session) throws JSL.FactoryException, JSL.ConnectException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JSLSettings_002.JCP_URL_APIS, urlAPIs);
        properties.put(JSLSettings_002.JCP_URL_AUTH, urlAuth);
        properties.put(JSLSettings_002.JCP_CLIENT_ID, clientId);
        properties.put(JSLSettings_002.JCP_CLIENT_SECRET, clientSecret);
        properties.put(JSLSettings_002.JCP_CLIENT_CALLBACK, clientCallback);
        properties.put(JSLSettings_002.JSLSRV_ID, srvId);
        properties.put(JSLSettings_002.JSLSRV_NAME, srvName);
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, locCommEnabled);
        //properties.put(JSLSettings_002.JSLCOMM_LOCAL_DISCOVERY, "DNSSD");
        JSL.Settings settings = FactoryJSL.loadSettings(properties, jslVersion);

        JSL jsl = FactoryJSL.createJSL(settings, jslVersion);
        jsl.connect();

        session.setAttribute("JSL-Instance", jsl);
        return jsl;
    }


    // Objects and components

    public List<JSLRemoteObject> listObjects(JSL jsl) {
        return jsl.getObjsMngr().getAllObjects();
    }

    public JSLRemoteObject getObj(JSL jsl, String objId) {
        JSLRemoteObject obj = jsl.getObjsMngr().getById(objId);
        if (obj == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Object with required id ('%s') not found.", objId));

        return obj;
    }

    public <T extends JSLComponent> T getComp(JSL jsl, String objId, String compPath, Class<T> compClass) {
        JSLRemoteObject obj = jsl.getObjsMngr().getById(objId);
        if (obj == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Object with required id ('%s') not found.", objId));

        JSLComponent comp = DefaultJSLComponentPath.searchComponent(obj.getStructure(), new DefaultJSLComponentPath(compPath));
        if (comp == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Component '%s' on object '%s' not found.", compPath, objId));

        if (!compClass.isInstance(comp))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Component '%s' on object '%s' is not of %s type ('%s').", compPath, objId, compClass.getSimpleName(), comp.getClass().getSimpleName()));

        return compClass.cast(comp);
    }

    public JSLRemoteObject findObj(JSL jsl, String objId) {
        try {
            return getObj(jsl, objId);

        } catch (Throwable ignore) {
            return null;
        }
    }

    public <T extends JSLComponent> T findComp(JSL jsl, String objId, String compPath, Class<T> compClass) {
        try {
            return getComp(jsl, objId, compPath, compClass);

        } catch (Throwable ignore) {
            return null;
        }
    }


    // User

    public JSLUserMngr getUserMngr(JSL jsl) {
        return jsl.getUserMngr();
    }

    public String getLoginUrl(JSL jsl) {
        return jsl.getJCPClient().getLoginUrl();
    }

    public boolean login(JSL jsl, String code) throws JCPClient2.ConnectionException, JCPClient2.JCPNotReachableException, JCPClient2.AuthenticationException {
        jsl.getJCPClient().setLoginCodeAndReconnect(code);
        return jsl.getJCPClient().isConnected();
    }

    public void logout(JSL jsl) {
        jsl.getJCPClient().userLogout();
    }


    // Service

    public JSL getJSL(JSL jsl) {
        return jsl;
    }


    // Permissions

    public static JOSPPerm.Type getObjPerm(JSLRemoteObject obj) {
        return obj.getServicePerm(obj.isCloudConnected() ? JOSPPerm.Connection.LocalAndCloud : JOSPPerm.Connection.OnlyLocal);
    }

    public boolean serviceCanPerm(JSLRemoteObject obj, JOSPPerm.Type type) {
        if (obj.getOwnerId().equals(JOSPPerm.WildCards.USR_ANONYMOUS_ID.toString()))
            return true;

        JOSPPerm.Type objPermType = getObjPerm(obj);
        switch (type) {
            case CoOwner:
                return objPermType == JOSPPerm.Type.CoOwner;
            case Actions:
                return objPermType == JOSPPerm.Type.CoOwner
                        || objPermType == JOSPPerm.Type.Actions;
            case Status:
                return objPermType == JOSPPerm.Type.CoOwner
                        || objPermType == JOSPPerm.Type.Actions
                        || objPermType == JOSPPerm.Type.Status;
            case None:
                return true;
        }
        return false;
    }

    public JOSPPerm getPerm(JSLRemoteObject obj, String permId) {
        JOSPPerm perm = null;
        for (JOSPPerm p : obj.getPerms())
            if (p.getId().equalsIgnoreCase(permId)) {
                perm = p;
                break;
            }
        if (perm == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Permission with required id ('%s') not found on object '%s'.", permId, obj.getId()));

        return perm;
    }


    public class JSLSpringException extends Throwable {
        public JSLSpringException(String msg, Throwable t) {
            super(msg, t);
        }
    }
}
