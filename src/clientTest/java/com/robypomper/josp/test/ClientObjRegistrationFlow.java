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
        System.out.println(String.format("ObjectId: %s", objId));

        // is registered () -> true if current obj is already registered on JCP db
        System.out.println(APIObjs.URL_PATH_REGISTER_IS);
        headers.put(APIObjs.HEADER_OBJID, objId);
        boolean isReg = RequestsMaker.execAndGetReq(service, accessToken, Verb.GET, APIObjs.URL_PATH_REGISTER_IS, headers, Boolean.class);
        System.out.println(String.format("isRegistered: %s", isReg));

        // register obj (ObjInfo) -> register given ObjInfo to current obj and return resulting Object instance from JCP db
        System.out.println(APIObjs.URL_PATH_REGISTER);
        if (isReg) {
            System.out.println("Object already registered, registration skipped.");
        } else {
            RegisterObj regObjParam = new RegisterObj("Test1_ObjName", "root {...}");
            regObjParam.setModel("Test1_ObjModel");
            boolean registered = RequestsMaker.execAndGetReq(service, accessToken, Verb.POST, APIObjs.URL_PATH_REGISTER, mapper.writeValueAsString(regObjParam), headers, Boolean.class);
            System.out.println(String.format("Registration success: %s", registered));
        }


        // is owned () -> true if current obj is already associated an owner
        System.out.println(APIPermissions.URL_PATH_OBJOWNER_IS);
        boolean isOwnerReg = RequestsMaker.execAndGetReq(service, accessToken, Verb.GET, APIPermissions.URL_PATH_OBJOWNER_IS, headers, Boolean.class);
        System.out.println(String.format("isOwnerRegistered: \n%s", isOwnerReg));

        // register obj owner (ownerId) -> associate given owner to current obj
        System.out.println(APIPermissions.URL_PATH_OBJOWNER);
        if (isOwnerReg) {
            System.out.println("Object owner already set, owner set skipped.");
        } else {
            String usrIdParam = mapper.writeValueAsString(usrId);
            boolean isOwnerSet = RequestsMaker.execAndGetReq(service, accessToken, Verb.POST, APIPermissions.URL_PATH_OBJOWNER, usrIdParam, headers, Boolean.class);
            System.out.println(String.format("isOwnerSet: %s", isOwnerSet));
        }

        // update obj owner (ownerId) -> update associated owner to current obj with given owner
        //objInfo.setModel("Test1_ObjModel_Updated");
        //objParam.setName("Test1_ObjName_Updated");
        //obj = RequestsMaker.execAndGetReq(service, accessToken, Verb.POST, urlObjReg, mapper.writeValueAsString(objParam), headers, Object.class);


        // generate obj permissions (generatePermissionsStrategy) -> return a set of valid permission (don't store them on JCP db)
        System.out.println(APIPermissions.URL_PATH_OBJGENERATE + "/{strategy}");
        PermissionsTypes.GenerateStrategy strategy = PermissionsTypes.GenerateStrategy.STANDARD;
        ObjPermission[] objPerms = RequestsMaker.execAndGetReq(service, accessToken, Verb.GET, APIPermissions.URL_PATH_OBJGENERATE + "/" + strategy, headers, ObjPermission[].class);
        System.out.println("Generated obj perms:");
        System.out.println("UsrId      SrvId      Connec.    Type       UpdatedAt");
        for (ObjPermission p : objPerms)
            System.out.println(String.format("%-10s %-10s %-10s %-10s %s",
                    p.usrId.substring(0, Math.min(p.usrId.length(), 10)),
                    p.srvId.substring(0, Math.min(p.srvId.length(), 10)),
                    p.connection.toString().substring(0, Math.min(p.connection.toString().length(), 10)),
                    p.type.toString(),
                    dateFormat.format(p.updatedAt)));

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
            System.out.println(String.format("%-10s %-10s %-10s %-10s %s",
                    p.usrId.substring(0, Math.min(p.usrId.length(), 10)),
                    p.srvId.substring(0, Math.min(p.srvId.length(), 10)),
                    p.connection.toString().substring(0, Math.min(p.connection.toString().length(), 10)),
                    p.type.toString(),
                    dateFormat.format(p.updatedAt)));
    }

}
