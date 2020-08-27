/*********************+
    Events
 **********************/

/*
 * This file provide functions to control the JCP FE UI: show main content, display action's feedback, made editable
 * UI fields and manage the browser history.
 *
 * sse events
 * objects list events
 * single object events
 */

// Events vars

var isFirstFilled = false;


// SSE Events

function emitOnConnected() {
    if (!isFirstFilled) {
        isFirstFilled=true;

        fillPostUpdaterInitialization();

        fetchObjsList();                    // Objs list (Nav Bar)
        fetchUser();                        // User (Opts links)
        fetchService();                     // Service (Opts links)
    }

    var section = document.getElementsByTagName('section')[0];
    removeDisabledCssClass(section);
}

function emitOnDisconnected() {
    var section = document.getElementsByTagName('section')[0];
    addDisabledCssClass(section);
}


// Object List events

function emitObjAdd(objId) {
    fetchObjsList();

    if (currentPage==PAGE_SHARE)
        showShare(true);
}

function emitObjRem(objId) {
    fetchObjsList();

    if (currentPage==PAGE_OBJ_DETAILS)
        if (objId==detailObjId)
            showObjects(true);
    if (currentPage==PAGE_SHARE)
        showShare(true);
}


// Object events

function emitObjConnected(objId) {
    var objPermsTag = document.getElementById("div_" + objId + "_perms");
    if (objPermsTag!=null) removeDisabledCssClass(objPermsTag);

    if (currentPage==PAGE_OBJ_DETAILS)
        if (objId==detailObjId) {
            showObjectDetails(objId,false);

            removeDisabledCssClassById("obj_" + objId + "_title");
            removeDisabledCssClassById("obj_" + objId + "_content");
        }
    if (currentPage==PAGE_SHARE)
        removeDisabledCssClassById("obj_" + objId + "_perms");
}

function emitObjDisconnected(objId) {
    var objPermsTag = document.getElementById("div_" + objId + "_perms");
    if (objPermsTag!=null) addDisabledCssClass(objPermsTag);

    if (currentPage==PAGE_OBJ_DETAILS)
        if (objId==detailObjId) {
            showObjectDetails(objId,false);

            addDisabledCssClassById("obj_" + objId + "_title");
            addDisabledCssClassById("obj_" + objId + "_content");
        }
    if (currentPage==PAGE_SHARE)
        addDisabledCssClassById("obj_" + objId + "_perms");
}

function emitObjUpd(objId,what) {
    if (objId!=detailObjId)
        return;

    showObjectDetails(objId,false);
    // can be optimized...
}

function emitStateUpd(objId,compPath) {
    if (currentPage==PAGE_OBJ_DETAILS)
        if (objId == detailObjId)
            fetchComponent(objId,compPath)
}
