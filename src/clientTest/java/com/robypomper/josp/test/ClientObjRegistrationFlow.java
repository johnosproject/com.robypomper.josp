package com.robypomper.josp.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.josp.jcp.apis.params.objs.GenerateObjId;
import com.robypomper.josp.jcp.apis.params.objs.RegisterObj;
import com.robypomper.josp.jcp.apis.params.permissions.ObjPermission;
import com.robypomper.josp.jcp.apis.params.permissions.PermissionsTypes;
import com.robypomper.josp.jcp.apis.paths.APIObjs;
import com.robypomper.josp.jcp.apis.paths.APIPermissions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ClientObjRegistrationFlow implements ClientRequestFlow {

    @Override
    public void exec(OAuth20Service service, OAuth2AccessToken accessToken, String urlDomain) throws IOException, ExecutionException, InterruptedException {
        String objIdHw = "TEST1";
        String usrId = "testd9ed-8d63-43d6-a465-dbe9029e04c8";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


        System.out.println("Object registration:");

        // generate id (objIdHw, ownerId) -> return generated string "{ObjIdHw-XXXXX-XXXXX}" associated to objIdHw and ownerId
        System.out.println(APIObjs.URL_PATH_GENERATEID);
        GenerateObjId objIdParam = new GenerateObjId(objIdHw, usrId);
        String objId = RequestsMaker.execAndGetReq(service, accessToken, Verb.POST, APIObjs.URL_PATH_GENERATEID, mapper.writeValueAsString(objIdParam), headers, String.class);
        System.out.printf("ObjectId: %s%n", objId);

        // register obj (ObjInfo) -> register given ObjInfo to current obj and return resulting Object instance from JCP db
        headers.put(APIObjs.HEADER_OBJID, objId);
        System.out.println(APIObjs.URL_PATH_REGISTER);
        RegisterObj regObjParam = new RegisterObj("Test1_ObjName", "00000-00000-00000");
        regObjParam.setBrand("brand");
        regObjParam.setModel("model");
        regObjParam.setLongDescr("long descr");
        regObjParam.setStructure("struct");
        boolean registered = RequestsMaker.execAndGetReq(service, accessToken, Verb.POST, APIObjs.URL_PATH_REGISTER, mapper.writeValueAsString(regObjParam), headers, Boolean.class);
        System.out.printf("Registration success: %s%n", registered);

        // generate obj permissions (generatePermissionsStrategy) -> return a set of valid permission (don't store them on JCP db)
        System.out.println(APIPermissions.URL_PATH_OBJGENERATE + "/{strategy}");
        PermissionsTypes.GenerateStrategy strategy = PermissionsTypes.GenerateStrategy.STANDARD;
        ObjPermission[] objPerms = RequestsMaker.execAndGetReq(service, accessToken, Verb.GET, APIPermissions.URL_PATH_OBJGENERATE + "/" + strategy, headers, ObjPermission[].class);
        System.out.println("Generated obj perms:");
        System.out.println("UsrId      SrvId      Connec.    Type       UpdatedAt");
        for (ObjPermission p : objPerms)
            System.out.printf("%-10s %-10s %-10s %-10s %s%n",
                    p.usrId.substring(0, Math.min(p.usrId.length(), 10)),
                    p.srvId.substring(0, Math.min(p.srvId.length(), 10)),
                    p.connection.toString().substring(0, Math.min(p.connection.toString().length(), 10)),
                    p.type.toString(),
                    dateFormat.format(p.updatedAt));

        // merge obj permissions (permissions) -> merge and return given permission with those contained in the JCP db (it will update stored permission on JCP db and add new ones)
        System.out.println(APIPermissions.URL_PATH_OBJMERGE);

        Thread.sleep(1000);

        List<ObjPermission> objPermsParam = new LinkedList<>(Arrays.asList(objPerms));
        // Add LightMngm
        //objPermsParam.add(new ObjPermission(objId,Permissions.WildCards.USR_OWNER.toString(),"LightMngm",Permissions.Connection.OnlyLocal,Permissions.Type.Actions,new Date(1000)));
        // Del LightMngm
        objPermsParam.add(new ObjPermission(objId, PermissionsTypes.WildCards.USR_OWNER.toString(), "LightMngm", PermissionsTypes.Connection.OnlyLocal, PermissionsTypes.Type.Actions, new Date(0)));
        // Del LightMngm
        //objPermsParam.add(new ObjPermission(objId,Permissions.WildCards.USR_OWNER.toString(),"HomeMngm",Permissions.Connection.OnlyLocal,Permissions.Type.Actions,new Date(0)));
        ObjPermission[] objPermsStored = RequestsMaker.execAndGetReq(service, accessToken, Verb.POST, APIPermissions.URL_PATH_OBJMERGE, mapper.writeValueAsString(objPermsParam), headers, ObjPermission[].class);
        System.out.println("Stored obj perms:");
        System.out.println("UsrId      SrvId      Connec.    Type       UpdatedAt");
        for (ObjPermission p : objPermsStored)
            System.out.printf("%-10s %-10s %-10s %-10s %s%n",
                    p.usrId.substring(0, Math.min(p.usrId.length(), 10)),
                    p.srvId.substring(0, Math.min(p.srvId.length(), 10)),
                    p.connection.toString().substring(0, Math.min(p.connection.toString().length(), 10)),
                    p.type.toString(),
                    dateFormat.format(p.updatedAt));
    }

}
