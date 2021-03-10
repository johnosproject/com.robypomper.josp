
// Events vars

var isFirstFilled = false;
var isConnected = false;


// SSE Events

function emitOnConnected() {
    isConnected = true;
    if (!isFirstFilled) {
        isFirstFilled=true;

        fillPostUpdaterInitialization();

        fetchUsrMngm();                         // User (Opts links)
        fetchObjsListMenu();                    // Objs list (Nav Bar)
        fetchSrvMngm();                         // Service (Opts links)
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
    fetchObjsListMenu();

    if (objId==detailObjId) {
        removeDisabledCssClassById("obj_" + objId + "_title");

        if (currentPage==PAGE_OBJ_DETAILS) {
            showObjectContent(objId,false);
            removeDisabledCssClassById("obj_" + objId + "_content");
        }

        if (currentPage==PAGE_ACCESS_CONTROL)
            removeDisabledCssClassById("obj_" + objId + "_perms");
    }
}

function emitObjDisconnected(objId) {
    fetchObjsListMenu();

    if (objId==detailObjId) {
        addDisabledCssClassById("obj_" + objId + "_title");

        if (currentPage==PAGE_OBJ_DETAILS) {
            showObjectContent(objId,false);
            addDisabledCssClassById("obj_" + objId + "_content");
        }

        if (currentPage==PAGE_ACCESS_CONTROL)
            addDisabledCssClassById("obj_" + objId + "_perms");
    }
}

function emitObjUpd(objId,what) {
    if (objId!=detailObjId)
        return;

    showObjectContent(objId,false);
    // can be optimized...
}

function emitStateUpd(objId,compPath) {
    if (objId != detailObjId)
        return;

    if (currentPage==PAGE_OBJ_DETAILS)
        fetchComponent(objId,compPath)
}
