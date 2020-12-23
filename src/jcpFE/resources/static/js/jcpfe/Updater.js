
var updater = null;


// Updated event listeners

function updateOnMessage(event) {
    var data = event.data.replaceAll('"','');
    logSSE("Received '" + data + "'");
    if (data.startsWith("HB")) {
        // N/A

    } else if (data.startsWith("Connected:")) {
        // N/A

    } else if (data.startsWith("csrf:")) {
        var token = data.substring("csrf:".length,data.indexOf(";"));
        var header = data.substring(data.lastIndexOf("header:")+"header:".length,data.length);
        setCsrf(token,header);

    } else if (data.startsWith("cookie:")) {
        var value = data.substring("cookie:".length,data.indexOf(";"));
        setCookie(value);

    } else if (data.startsWith("ObjAdd:")) {
        var objId = data.substring("ObjAdd:".length);
        emitObjAdd(objId);

    } else if (data.startsWith("ObjRem:")) {
        var objId = data.substring("ObjRem:".length);
        emitObjRem(objId);

    } else if (data.startsWith("ObjConnected:")) {
        var objId = data.substring("ObjConnected:".length);
        emitObjConnected(objId);

    } else if (data.startsWith("ObjDisconnected:")) {
        var objId = data.substring("ObjDisconnected:".length);
        emitObjDisconnected(objId);

    } else if (data.startsWith("ObjUpd:")) {
        var objId = data.substring("ObjUpd:".length,data.indexOf(";"));
        var what = data.substring(data.lastIndexOf("What:")+"What:".length,data.length);
        emitObjUpd(objId,what);

    } else if (data.startsWith("StateUpd:")) {
        var objId = data.substring("StateUpd:".length,data.indexOf(";"));
        var compPath = data.substring(data.lastIndexOf("Comp:")+"Comp:".length,data.length);
        emitStateUpd(objId,compPath);

    } else {
        logSSE("UNKNOW DATA (" + data + ")");
        //alert("Warning unknown message from SSE: " + data);
    }
}

function updateOnOpen(event) {
    logSSE("Connected");
    emitOnConnected();
}

function updateOnError(event) {
    logSSE("Disconnected");
    emitOnDisconnected();
}

function logSSE(msg) {
    console.log("[" + dateToString(new Date()) + " @ SSE] " + msg);
}