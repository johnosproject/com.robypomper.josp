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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JSLSpringService {

    // Internal vars

    private final JSL jsl;


    // Constructor

    public JSLSpringService(@Value("${jsl.version:2.0.0}") String jslVersion,
                            @Value("${" + JSLSettings_002.JCP_URL_APIS + ":" + JSLSettings_002.JCP_URL_DEF_APIS + "}") String urlAPIs,
                            @Value("${" + JSLSettings_002.JCP_URL_AUTH + ":" + JSLSettings_002.JCP_URL_AUTH + "}") String urlAuth,
                            @Value("${" + JSLSettings_002.JCP_CLIENT_ID + ":}") String clientId,
                            @Value("${" + JSLSettings_002.JCP_CLIENT_SECRET + ":}") String clientSecret,
                            @Value("${" + JSLSettings_002.JCP_CLIENT_CALLBACK + ":}") String clientCallback,
                            @Value("${" + JSLSettings_002.JSLSRV_ID + ":}") String srvId) throws JSL.FactoryException, JSL.ConnectException {
        if (clientId.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP FrontEnd", JSLSettings_002.JCP_CLIENT_ID));
        if (clientSecret.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP FrontEnd", JSLSettings_002.JCP_CLIENT_SECRET));
        if (clientCallback.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP FrontEnd", JSLSettings_002.JCP_CLIENT_CALLBACK));
        if (srvId.isEmpty())
            throw new IllegalArgumentException(String.format("Properties '%s' must be set before run the JCP FrontEnd", JSLSettings_002.JSLSRV_ID));

        Map<String, Object> properties = new HashMap<>();
        properties.put(JSLSettings_002.JCP_URL_APIS, urlAPIs);
        properties.put(JSLSettings_002.JCP_URL_AUTH, urlAuth);
        properties.put(JSLSettings_002.JCP_CLIENT_ID, clientId);
        properties.put(JSLSettings_002.JCP_CLIENT_SECRET, clientSecret);
        properties.put(JSLSettings_002.JCP_CLIENT_CALLBACK, clientCallback);
        properties.put(JSLSettings_002.JSLSRV_ID, srvId);
        properties.put(JSLSettings_002.JSLSRV_NAME, "jcpFrontEnd");
        properties.put(JSLSettings_002.JSLCOMM_LOCAL_ENABLED, "false");
        //properties.put(JSLSettings_002.JSLCOMM_LOCAL_DISCOVERY, "DNSSD");
        JSL.Settings settings = FactoryJSL.loadSettings(properties, jslVersion);

        jsl = FactoryJSL.createJSL(settings, jslVersion);
        jsl.connect();
    }


    // Objects and components

    public List<JSLRemoteObject> listObjects() {
        return jsl.getObjsMngr().getAllObjects();
    }

    public JSLRemoteObject getObj(String objId) {
        JSLRemoteObject obj = jsl.getObjsMngr().getById(objId);
        if (obj == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Object with required id ('%s') not found.", objId));

        return obj;
    }

    public <T extends JSLComponent> T getComp(String objId, String compPath, Class<T> compClass) {
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

    public JSLRemoteObject findObj(String objId) {
        try {
            return getObj(objId);

        } catch (Throwable ignore) {
            return null;
        }
    }

    public <T extends JSLComponent> T findComp(String objId, String compPath, Class<T> compClass) {
        try {
            return getComp(objId, compPath, compClass);

        } catch (Throwable ignore) {
            return null;
        }
    }


    // User

    @Deprecated
    public JSLUser getJSLUser() {
        return new JSLUser(jsl.getUserMngr());
    }

    public JSLUserMngr getUserMngr() {
        return jsl.getUserMngr();
    }

    public String getLoginUrl() {
        return jsl.getJCPClient().getLoginUrl();
    }

    public boolean login(String code) throws JCPClient2.ConnectionException, JCPClient2.JCPNotReachableException, JCPClient2.AuthenticationException {
        jsl.getJCPClient().setLoginCodeAndReconnect(code);
        return jsl.getJCPClient().isConnected();
    }

    public void logout() {
        jsl.getJCPClient().userLogout();
    }


    // Service

    @Deprecated
    public JSLService getJSLService() {
        return new JSLService(jsl);
    }

    public JSL getJSL() {
        return jsl;
    }


    // Permissions

    public JOSPPerm.Type getObjPerm(JSLRemoteObject obj) {
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


    // Structures

    @Deprecated
    public static class JSLUser {

        public final String id;
        public final String name;
        public final boolean isAuthenticated;

        public JSLUser(JSLUserMngr usrMngr) {
            this(usrMngr.getUserId(), usrMngr.getUsername(), usrMngr.isUserAuthenticated());
        }

        public JSLUser(String userId, String name, boolean isAuthenticated) {
            this.id = userId;
            this.name = name;
            this.isAuthenticated = isAuthenticated;
        }
    }

    @Deprecated
    public static class JSLService {

        public final JSL jsl;

        public JSLService(JSL jsl) {
            this.jsl = jsl;

            jsl.version();
            jsl.versionsJOSPProtocol();
            jsl.versionsJCPAPIs();
            jsl.versionsJOSPObject();

        }

        // IDs

        public String srvName() {
            return jsl.getServiceInfo().getSrvName();
        }

        public String srvId() {
            return jsl.getServiceInfo().getSrvId();
        }

        public String usrId() {
            return jsl.getServiceInfo().getUserId();
        }

        public String instId() {
            return jsl.getServiceInfo().getInstanceId();
        }


        // Connect/disconnect

        public JSL.Status status() {
            return jsl.status();
        }

        public void connect() throws JSL.ConnectException {
            jsl.connect();
        }

        public void disconnect() throws JSL.ConnectException {
            jsl.disconnect();
        }


        // Versions

        public String srvVersion() {
            return jsl.version();
        }

        public List<String> srvVersionsJOSPProtocol() {
            return Arrays.asList(jsl.versionsJOSPProtocol());
        }

        public List<String> srvVersionsJCPAPIs() {
            return Arrays.asList(jsl.versionsJCPAPIs());
        }

        public List<String> srvVersionsJOSPObject() {
            return Arrays.asList(jsl.versionsJOSPObject());
        }

    }

}
