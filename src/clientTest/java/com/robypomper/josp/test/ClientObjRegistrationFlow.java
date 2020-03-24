package com.robypomper.josp.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.robypomper.josp.jcp.apis.params.objs.GenerateObjId;
import com.robypomper.josp.jcp.apis.params.objs.RegisterObj;
import com.robypomper.josp.jcp.apis.paths.APIObjs;

import java.io.IOException;
import java.util.HashMap;
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
    }

}
