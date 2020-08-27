/*********************+
    Updater
 **********************/

/**
 * This file provide the var and methods for the SSE updater connected to the JCP FE Backend.
 */

var updater = null;

function updateOnMessage(event) {
    console.log("SSE " + event.data);
    if (event.data.startsWith("Connected:")) {
        console.log(event.data);

    } else if (event.data.startsWith("csrf:")) {
        var token = event.data.substring("csrf:".length,event.data.indexOf(";"));
        var header = event.data.substring(event.data.lastIndexOf("header:")+"header:".length,event.data.length);
        setCsrf(token,header);

    } else if (event.data.startsWith("ObjAdd:")) {
        var objId = event.data.substring("ObjAdd:".length,event.data.indexOf(";"));
        emitObjAdd(objId);

    } else if (event.data.startsWith("ObjRem:")) {
        var objId = event.data.substring("ObjRem:".length,event.data.indexOf(";"));
        emitObjRem(objId);

    } else if (event.data.startsWith("ObjConnected:")) {
        var objId = event.data.substring("ObjConnected:".length);
        emitObjConnected(objId);

    } else if (event.data.startsWith("ObjDisconnected:")) {
        var objId = event.data.substring("ObjDisconnected:".length);
        emitObjDisconnected(objId);

    } else if (event.data.startsWith("ObjUpd:")) {
        var objId = event.data.substring("ObjUpd:".length,event.data.indexOf(";"));
        var what = event.data.substring(event.data.lastIndexOf("What:")+"What:".length,event.data.length);
        emitObjUpd(objId,what);

    } else if (event.data.startsWith("StateUpd:")) {
        var objId = event.data.substring("StateUpd:".length,event.data.indexOf(";"));
        var compPath = event.data.substring(event.data.lastIndexOf("Comp:")+"Comp:".length,event.data.length);
        emitStateUpd(objId,compPath);

    } else
        alert("Warning unknown message from SSE: " + event.data);
}

function updateOnOpen(event) {
    console.log("SSE Connected");
    emitOnConnected();
}

function updateOnError(event) {
    console.log("SSE Disconnected");
    emitOnDisconnected();
}
