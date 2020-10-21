
var currentPage = false;


// WebApp start

function startWebApp() {
    fillPreUpdaterInitialization();
    updater = startUpdater("/apis/sse/1.0/init",updateOnMessage,updateOnOpen,updateOnError);
}


// Pre/Post/Required fill methods

function fillPreUpdaterInitialization() {
    // Nav bar
    setLogo_Img("img/logo2.png");
    setMenu_List([]);

    // Main
    setTitle_Str(titleWelcomeStr());
    setFooter_Cols(htmlFooterLeft(),htmlFooterCenter(),htmlFooterRight());
}

function fillPostUpdaterInitialization() {
    setOpts(dropDownUserMenu());

    pollReady();
}

var waitTime = 1000;

function pollReady () {
    if (!isConnected)
        setTimeout(pollReady, waitTime);

    else if (isAuthenticated==null
     || serviceId==null)
        setTimeout(pollReady, waitTime);

    else
        if (!fillRequiredContent(document.location))        // fill required content
            showHome(false);                                // or default (showHome)
}

function fillRequiredContent(documentUrl) {
    var page = findGetParameter(documentUrl,'page');
    if (page == PAGE_OBJ_DETAILS) {
        var objId = findGetParameter(documentUrl,'objId');
        if (objId==null)
            return false;
        showObjectContent(objId,false);

    } else if (page == PAGE_OBJ_INFO) {
        var objId = findGetParameter(documentUrl,'objId');
        if (objId==null)
            return false;
        showObjectContentInfo(objId,false);

    } else if (page == PAGE_OBJ_PERMS) {
        var objId = findGetParameter(documentUrl,'objId');
        if (objId==null)
            return false;
        showObjectContentAccessControl(objId,false);

    } else if (page == PAGE_HOME) {
        showHome(false);

    } else if (page == PAGE_USER_PROFILE) {
        showUserContent(false);

    } else if (page == PAGE_JSL_STATUS) {
        showJSLStatus(false);

    } else if (page == PAGE_ABOUT) {
        showAbout(false);

    } else if (page == PAGE_ACCESS_CONTROL) {
        showAccessControl(false);

    } else
        return false;

    return true;
}


// History methods

function setNewPageHistory(page,title) {
    setNewPageHistory(page,title,null);
}

function setNewPageHistory(page,title,extra) {
    window.history.pushState("{page: '" + page + "'}", title, "?page=" + page + (extra!=null ? "&" + extra : ""));
}

window.onpopstate = function(event) {
    fillRequiredContent(document.location,false);
};