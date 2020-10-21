
// Events vars

var isFirstFilled = false;
var isConnected = false;


// SSE Events

function emitOnConnected() {
    isConnected = true;
    if (!isFirstFilled) {
        isFirstFilled=true;

        fillPostUpdaterInitialization();

        fetchObjsListMenu();                    // Objs list (Nav Bar)
        fetchUsrMngm();                        // User (Opts links)
        fetchSrvMngm();                     // Service (Opts links)
    }

    var section = document.getElementsByTagName('section')[0];
    removeDisabledCssClass(section);
}

function emitOnDisconnected() {
    isConnected = false;
    var section = document.getElementsByTagName('section')[0];
    addDisabledCssClass(section);
}


// Object List events

function emitObjAdd(objId) {
    fetchObjsListMenu();

    if (currentPage==PAGE_ACCESS_CONTROL)
        showAccessControl(true);
}

function emitObjRem(objId) {
    fetchObjsListMenu();

    if (currentPage==PAGE_OBJ_DETAILS)
        if (objId==detailObjId)
            showHome(true);
    if (currentPage==PAGE_ACCESS_CONTROL)
        showAccessControl(true);
}


// Object events

function emitObjConnected(objId) {
    var objPermsTag = document.getElementById("div_" + objId + "_perms");
    if (objPermsTag!=null) removeDisabledCssClass(objPermsTag);

    if (currentPage==PAGE_OBJ_DETAILS)
        if (objId==detailObjId) {
            showObjectContent(objId,false);

            removeDisabledCssClassById("obj_" + objId + "_title");
            removeDisabledCssClassById("obj_" + objId + "_content");
        }
    if (currentPage==PAGE_ACCESS_CONTROL)
        removeDisabledCssClassById("obj_" + objId + "_perms");
}

function emitObjDisconnected(objId) {
    var objPermsTag = document.getElementById("div_" + objId + "_perms");
    if (objPermsTag!=null) addDisabledCssClass(objPermsTag);

    if (currentPage==PAGE_OBJ_DETAILS)
        if (objId==detailObjId) {
            showObjectContent(objId,false);

            addDisabledCssClassById("obj_" + objId + "_title");
            addDisabledCssClassById("obj_" + objId + "_content");
        }
    if (currentPage==PAGE_ACCESS_CONTROL)
        addDisabledCssClassById("obj_" + objId + "_perms");
}

function emitObjUpd(objId,what) {
    if (objId!=detailObjId)
        return;

    showObjectContent(objId,false);
    // can be optimized...
}

function emitStateUpd(objId,compPath) {
    if (currentPage==PAGE_OBJ_DETAILS)
        if (objId == detailObjId)
            fetchComponent(objId,compPath)
}
