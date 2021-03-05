
var updater = null;


// Updated event listeners

function updateOnMessage(event) {
    var data = event.data;
    if (data.startsWith("\"")) data = data.substring(1)
    if (data.endsWith("\"")) data = data.substring(0,data.length-1);
    data = data.replaceAll("\\","");

    if (data.startsWith("HB")) {
        debugSSE("Received 'HB'");
        // N/A
        return;

    }

    var event = JSON.parse(data);
    var processed = false;
    if (event.what.startsWith("JCP_APIS")) {
        processed = processJCPAPIs(event);
    } else if (event.what.startsWith("JCP_GWS")) {
        processed = processJCPGWs(event);
    } else if (event.what.startsWith("OBJ_UPD")) {
        processed = processObjUpdate(event);
    } else if (event.what.startsWith("OBJ")) {
        processed = processObj(event);
    }

    //} else if (data.startsWith("csrf:")) {
    //    var token = data.substring("csrf:".length,data.indexOf(";"));
    //    var header = data.substring(data.lastIndexOf("header:")+"header:".length,data.length);
    //    setCsrf(token,header);
    //
    //} else if (data.startsWith("cookie:")) {
    //    var value = data.substring("cookie:".length,data.indexOf(";"));
    //    setCookie(value);

    if (!processed)
        warnSSE("UNKNOW DATA (" + data + ")");
}

function updateOnOpen(event) {
    logSSE("Connected");
    emitOnConnected();
}

function updateOnError(event) {
    logSSE("Disconnected");
    updater.close()
    tryReConnectUpdater();
    emitOnDisconnected();
}


// Message processing methods

function processJCPAPIs(event) {
    if (event.what == "JCP_APIS_CONN") {
        // event.url
        // N/A
        logSSE("Received 'JCP_APIS_CONN' on " + event.url);
        return true;

    } else if (event.what == "JCP_APIS_DISCONN") {
        // event.url
        // N/A
        logSSE("Received 'JCP_APIS_DISCONN' on " + event.url);
        return true;

    } else if (event.what == "JCP_APIS_FAIL_GEN") {
        // event.url
        // event.error
        // N/A
        warnSSE("Received 'JCP_APIS_FAIL_GEN' on " + event.url + "(" + event.error + ")");
        return true;

    } else if (event.what == "JCP_APIS_FAIL_AUTH") {
        // event.url
        // event.error
        // N/A
        warnSSE("Received 'JCP_APIS_FAIL_AUTH' on " + event.url);
        return true;

    } else if (event.what == "JCP_APIS_LOGIN") {
        // event.url
        // event.userid
        // event.username
        // N/A
        logSSE("Received 'JCP_APIS_LOGIN' for user " + event.username);
        return true;

    } else if (event.what == "JCP_APIS_LOGOUT") {
        // event.url
        // N/A
        logSSE("Received 'JCP_APIS_LOGOUT'");
        return true;

    }

    return false;
}

function processJCPGWs(event) {
    if (event.what == "JCP_GWS_CONNECTING") {
        // event.proto
        // event.host
        // event.port
        // N/A
        debugSSE("Received 'JCP_GWS_CONNECTING' on " + event.proto + "://" + event.host + ":" + event.port);
        return true;

    } else if (event.what == "JCP_GWS_WAITING") {
        // event.proto
        // event.host
        // event.port
        // N/A
        logSSE("Received 'JCP_GWS_WAITING' on " + event.proto + "://" + event.host + ":" + event.port);
        return true;

    } else if (event.what == "JCP_GWS_CONNECTED") {
        // event.proto
        // event.host
        // event.port
        // N/A
        logSSE("Received 'JCP_GWS_CONNECTED' on " + event.proto + "://" + event.host + ":" + event.port);
        return true;

    } else if (event.what == "JCP_GWS_DISCONNECTING") {
        // event.proto
        // event.host
        // event.port
        // N/A
        debugSSE("Received 'JCP_GWS_DISCONNECTING' on " + event.proto + "://" + event.host + ":" + event.port);
        return true;

    } else if (event.what == "JCP_GWS_DISCONNECTED") {
        // event.proto
        // event.host
        // event.port
        // N/A
        logSSE("Received 'JCP_GWS_DISCONNECTED' on " + event.proto + "://" + event.host + ":" + event.port);
        return true;

    } else if (event.what == "JCP_GWS_FAIL") {
        // event.proto
        // event.host
        // event.port
        // event.error
        // N/A
        warnSSE("Received 'JCP_GWS_FAIL' on " + event.proto + "://" + event.host + ":" + event.port + " [" + event.error + "]");
        return true;
    }

    return false;
}

function processObj(event) {
    if (event.what == "OBJ_ADD") {
        // event.objId
        emitObjAdd(event.objId);
        logSSE("Received 'OBJ_ADD' on " + event.objId);
        return true;

    } else if (event.what == "OBJ_REM") {
        // event.objId
        emitObjRem(event.objId);
        logSSE("Received 'OBJ_REM' on " + event.objId);
        return true;

    } else if (event.what == "OBJ_CONN") {
        // event.objId
        emitObjConnected(event.objId);
        logSSE("Received 'OBJ_CONN' on " + event.objId);
        return true;

    } else if (event.what == "OBJ_DISCONN") {
        // event.objId
        emitObjDisconnected(event.objId);
        logSSE("Received 'OBJ_DISCONN' on " + event.objId);
        return true;
    }

    return false;
}

function processObjUpdate(event) {
    if (event.what == "OBJ_UPD_STRUCT") {
        // event.objId
        // N/A
        logSSE("Received 'OBJ_UPD_STRUCT' on " + event.objId);
        return true;

    } else if (event.what == "OBJ_UPD_PERMS") {
        // event.objId
        // N/A
        logSSE("Received 'OBJ_UPD_PERMS' on " + event.objId);
        return true;

    } else if (event.what == "OBJ_UPD_PERM_SRV") {
        // event.objId
        // event.new
        // event.old
        // N/A
        logSSE("Received 'OBJ_UPD_PERM_SRV' on " + event.objId + " value = " + event.new);
        return true;

    } else if (event.what == "OBJ_UPD_COMP") {
        // event.objId
        // event.compPath
        // event.new
        // event.old
        emitStateUpd(event.objId,event.compPath);
        debugSSE("Received 'OBJ_UPD_COMP' on " + event.objId + " for " + event.compPath + " value = " + event.new);
        return true;

    } else if (event.what == "OBJ_UPD_INFO_NAME") {
        // event.objId
        // event.new
        // event.old
        emitObjUpd(event.objId,"Name");
        debugSSE("Received 'OBJ_UPD_INFO_NAME' on " + event.objId + " value = " + event.new);
        return true;

    } else if (event.what == "OBJ_UPD_INFO_OWNER") {
        // event.objId
        // event.new
        // event.old
        emitObjUpd(event.objId,"Owner");
        debugSSE("Received 'OBJ_UPD_INFO_OWNER' on " + event.objId + " value = " + event.new);
        return true;

    } else if (event.what == "OBJ_UPD_INFO_JOD_VERSION") {
        // event.objId
        // event.new
        // event.old
        emitObjUpd(event.objId,"JODVersion");
        debugSSE("Received 'OBJ_UPD_INFO_JOD_VERSION' on " + event.objId + " value = " + event.new);
        return true;

    } else if (event.what == "OBJ_UPD_INFO_MODEL") {
        // event.objId
        // event.new
        // event.old
        emitObjUpd(event.objId,"Model");
        debugSSE("Received 'OBJ_UPD_INFO_MODEL' on " + event.objId + " value = " + event.new);
        return true;

    } else if (event.what == "OBJ_UPD_INFO_BRAND") {
        // event.objId
        // event.new
        // event.old
        emitObjUpd(event.objId,"Brand");
        debugSSE("Received 'OBJ_UPD_INFO_BRAND' on " + event.objId + " value = " + event.new);
        return true;

    } else if (event.what == "OBJ_UPD_INFO_LONG_DESCR") {
        // event.objId
        // event.new
        // event.old
        emitObjUpd(event.objId,"LongDescr");
        debugSSE("Received 'OBJ_UPD_INFO_LONG_DESCR' on " + event.objId + " value = " + event.new);
        return true;
    }

    return false;
}


// Error processing methods

function tryReConnectUpdater() {
    var timer = setInterval(function() {
        if (initJSLInstanceAndSSE())
            clearInterval(timer);
        else
            updater.close();
    }, 5000);
}


// Log methods

function debugSSE(msg) {
    console.debug("[" + dateToString(new Date()) + " @ SSE] " + msg);
}

function logSSE(msg) {
    console.log("[" + dateToString(new Date()) + " @ SSE] " + msg);
}

function warnSSE(msg) {
    console.warn("[" + dateToString(new Date()) + " @ SSE] " + msg);
}