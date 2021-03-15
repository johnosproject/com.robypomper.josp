
var currentPage = false;
var backEndUrl = "N/A";
var frontEndUrl = "N/A";
var poolRetryTime = 1000;
var sessionIntervalTime = 5 * 60 * 1000;


// WebApp start

function startWebApp(entrypointUrl) {
    backEndUrl = entrypointUrl;
    frontEndUrl = document.location.origin;
    fillPreUpdaterInitialization();

    initJSLInstanceAndSSE();
}

function initJSLInstanceAndSSE() {
    // Query JSL Instance status to WB
    // If JSL Instance is NOT Init
    //   Init JSL Instance via FE (with WB's sessionId)
    // Start updater

    var jslInstStatusResponse = apiGETSync(backEndUrl,pathJSLWB_Init_Entrypoint);
    if (jslInstStatusResponse.status != 200)
        return false;

    var jslInstStatus = JSON.parse(jslInstStatusResponse.responseText);
    if (!jslInstStatus.isJSLInit)
        if (apiGETSync("",pathFE_EP_Session.replace("{session_id}",jslInstStatus.sessionId)).status != 200)
            return false;

    updater = startUpdater(backEndUrl + pathJSLWB_Init_Sse,updateOnMessage,updateOnOpen,updateOnError);
    return true;
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

function pollReady () {
    if (!isConnected) {
        setTimeout(pollReady, poolRetryTime);
        return;
    }

    if (isAuthenticated==null
     || serviceId==null) {
        setTimeout(pollReady, poolRetryTime);
        return;
    }


    if (!fillRequiredContent(document.location))        // fill required content
        showHome(false);                                // or default (showHome)
    startSessionHeartBeat();
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

    } else if (page == PAGE_OBJ_EVENTS) {
        var objId = findGetParameter(documentUrl,'objId');
        if (objId==null)
            return false;
        showObjectContentEvents(objId,false);

    } else if (page == PAGE_OBJ_STATUS_HISTORY) {
        var objId = findGetParameter(documentUrl,'objId');
        var compPath = findGetParameter(documentUrl,'compPath');
        if (objId==null || compPath==null)
            return false;
        showObjectContentStatusHistory(objId,compPath,false);

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

    } else if (page == PAGE_ADMIN_HOME) {
        showAdminContent(false);
    } else if (page == PAGE_ADMIN_APIS) {
        showAdminContentAPIs(false);
    } else if (page == PAGE_ADMIN_GWS) {
        showAdminContentGWs(false);
    } else if (page == PAGE_ADMIN_JSLWB) {
        showAdminContentJSLWB(false);
    } else if (page == PAGE_ADMIN_FE) {
        showAdminContentFE(false);
    } else if (page == PAGE_ADMIN_OBJECTS) {
        showAdminContentObjects(false);
    } else if (page == PAGE_ADMIN_SERVICES) {
        showAdminContentServices(false);
    } else if (page == PAGE_ADMIN_USER) {
        showAdminContentUsers(false);

    } else if (page == PAGE_ADMIN_BUILD_INFO + "APIs") {
        showJCPServiceBuildInfo(false,"APIs");
    } else if (page == PAGE_ADMIN_BUILD_INFO + "GWs") {
        showJCPServiceBuildInfo(false,"GWs");
    } else if (page == PAGE_ADMIN_BUILD_INFO + "JSLWB") {
        showJCPServiceBuildInfo(false,"JSLWB");
    } else if (page == PAGE_ADMIN_BUILD_INFO + "FE") {
        showJCPServiceBuildInfo(false,"FE");
    } else if (page == PAGE_ADMIN_EXEC + "APIs") {
        showJCPServiceExecutable(false,"APIs");
    } else if (page == PAGE_ADMIN_EXEC + "GWs") {
        showJCPServiceExecutable(false,"GWs");
    } else if (page == PAGE_ADMIN_EXEC + "JSLWB") {
        showJCPServiceExecutable(false,"JSLWB");
    } else if (page == PAGE_ADMIN_EXEC + "FE") {
        showJCPServiceExecutable(false,"FE");
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


// Session heartbeat

function startSessionHeartBeat() {
    window.setTimeout(function() {
        //apiGET(backEndUrl,"/apis/JCP/2.0/status",function(responseText) {
        apiGET(backEndUrl,pathJSLWB_Init_Entrypoint,function(responseText) {
            startSessionHeartBeat();
        },onErrorFetch);
    },sessionIntervalTime);
}