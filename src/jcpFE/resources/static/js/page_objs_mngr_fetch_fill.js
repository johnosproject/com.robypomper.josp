/*********************+
    Fetch & Fill
 **********************/

/*
 * This file provide functions to fetch data from JCP FE Backend and fill that data into HTML.
 * Moreover this file provide methods to generate html components, styles and manage css classes.
 *
 * fetch{XY}()              methods to fetch data and elaborate with correspondent fill{XY}() method
 * fill{XY}()               methods to fill received data in to html structure
 * get{XY}ToHtml{Z}()       methods to create static html structure
 * {XY}ToHtml{Z}()          methods to create html structure filled with data from method's params
 * {XY}Style()              methods to create html '<style>' tag with html structure's style
 * show/hide{XY}()          methods to show/hide css classes
 * add/remove{XY}()         methods to add/remove css classes to html tags
 */

// Fill vars

var detailObjId = null;
var loggedUserId = null;
var loggedUserName = null;
var serviceId = null;
var serviceVersion = null;
var objsListCache = null;
var objsListFilterOwner = true;
var objsListFilterShared = true;
var objsListFilterAnonymous = false;
var defaultObjCompsCollapsed = true;



// Fetch methods

function fetchObjsList() {
    apiGET("/apis/objsmngr/1.0/",fillObjsList,onErrorFetch);
}

function fetchObjDetails(objId) {
    // This method try two times before call onErrorFetch() method
    var url = "/apis/objsmngr/1.0/" + objId + "/";
    apiGET(url,fillObjDetails,
        async function onError_RetryFetchObjDetails() {
            await new Promise(r => setTimeout(r, 1000));
            apiGET(url,fillObjDetails,onErrorFetch);
        }
    );
}

function fetchObjInfo(objId) {
    //apiGET("/apis/objsmngr/1.0/" + objId + "/",fillObj_Header,onErrorFetch);
    apiGET("/apis/objsmngr/1.0/" + objId + "/",fillObjInfo_Obj,onErrorFetch);
    apiGET("/apis/structure/1.0/" + objId + "/",fillObjInfo_Struct,onErrorFetch);
}

function fetchObjPerms(objId) {
    apiGET("/apis/objsmngr/1.0/" + objId + "/",fillObjInfo_Obj,onErrorFetch);
    apiGET("/apis/permissions/1.0/" + objId + "/",fillObjPerms,onErrorFetch);
}

function fetchObjStruct(objId) {
    apiGET("/apis/structure/1.0/" + obj.id + "/",fillObjStructure,onErrorFetch);
}

function fetchComponent(objId,compPath) {
    apiGET("/apis/structure/1.0/" + objId + "/" + compPath + "/",fillComponent,onErrorFetch);
}

function fetchUser() {
    apiGET("/apis/user/1.0/",fillUser,onErrorFetch);
}

function fetchService() {
    apiGET("/apis/service/1.0/",fillService,onErrorFetch);
}

function fetchAllPermissions(objId) {
    // Fetch the object's list and for each object fetch his permission with fetchSinglePermissions() method
    apiGET("/apis/objsmngr/1.0/",function(objsListJson) {
        setTitle_Str("Objects Permissions:");
        var objsList = JSON.parse(objsListJson);

        for (i = 0; i < objsList.length; i++)
            fetchSinglePermissions(objsList[i].id,objsList[i].name,objsList[i].owner,objsList[i].isCloudConnected,objsList[i].permission);
    },onErrorFetch);
}

function fetchSinglePermissions(objId,objName,objOwner,objConnected,currentPermissions) {
    apiGET("/apis/permissions/1.0/" + objId + "/",function(permsListJson) {
        fillPermission_Add(permsListJson,objName,objOwner,objConnected,currentPermissions);
    },onWarningFetch);
}

function onErrorFetch(xhttpRequest) {
    alert("Error can't fetch '" + xhttpRequest + "' resource");
}

function onWarningFetch(xhttpRequest) {
    alert("Warning can't fetch '" + xhttpRequest + "' resource");
}


// Fill methods

function fillPreUpdaterInitialization() {
    // Nav bar
    setLogo_Img("img/logo2.png");
    setMenu_List([]);

    // Main
    setTitle_Str(getTitle_Welcome_Str());
    setFooter_Cols(getFooter_Col1_HTML(),getFooter_Col2_HTML(),getFooter_Col3_HTML());
}

function fillPostUpdaterInitialization() {
    setOpts(getOpts_ObjsMngr());

    if (!fillRequiredContent(document.location))        // fill required content
        showObjects(false);                             // or default (showObjects)
}

function fillRequiredContent(documentUrl) {
    var page = findGetParameter(documentUrl,'page');
    if (page == PAGE_OBJ_DETAILS) {
        var objId = findGetParameter(documentUrl,'objId');
        if (objId==null)
            return false;
        showObjectDetails(objId,false);

    } else if (page == PAGE_OBJ_INFO) {
        var objId = findGetParameter(documentUrl,'objId');
        if (objId==null)
            return false;
        showObjectInfo(objId,false);

    } else if (page == PAGE_OBJ_PERMS) {
        var objId = findGetParameter(documentUrl,'objId');
        if (objId==null)
            return false;
        showObjectPermissions(objId,false);

    } else if (page == PAGE_OBJS_LIST) {
        showObjects(false);

    } else if (page == PAGE_USR_INFO) {
        showUser(false);

    } else if (page == PAGE_SRV_INFO) {
        showService(false);

    } else if (page == PAGE_ABOUT) {
        showAbout(false);

    } else if (page == PAGE_SHARE) {
        showShare(false);

    } else
        return false;

    return true;
}

function fillObjsList(objsListJson) {
    var objsList;

    if (objsListJson != null)
        objsList = JSON.parse(objsListJson);
    else if (objsListCache != null)
        objsList = objsListCache;

    if (objsListJson == null && objsListCache == null) {
        fetchObjsList();
        return;
    }
    objsListCache = objsList;

    // Update menu
    var menuItems = [];
    menuItems.push(["<div class='title'>OBJECTS</div>" + getMenu_ObjsSelectorDropDown_HTML(),null]);
    for (i = 0; i < objsList.length; i++) {
        var connI = objsList[i].isConnected ? "C" : "D";
        if (typeof user === "undefined" || !user.isAuthenticated)
            if (objsList[i].owner == '00000-00000-00000')
                menuItems.push([objsList[i].name + " [" + connI + "A]","showObjectDetails(\"" + objsList[i].id + "\",true)"]);
            else
                menuItems.push([objsList[i].name,"showObjectDetails(\"" + objsList[i].id + "\",true)"]);
        else
            if (objsList[i].owner == user.id && objsListFilterOwner)
                menuItems.push([objsList[i].name + " [" + connI + "]","showObjectDetails(\"" + objsList[i].id + "\",true)"]);
            else if (objsList[i].owner != user.id && objsList[i].owner != '00000-00000-00000' && objsListFilterShared)
                menuItems.push([objsList[i].name + " [" + connI + "S]","showObjectDetails(\"" + objsList[i].id + "\",true)"]);
            else if (objsList[i].owner == '00000-00000-00000' && objsListFilterAnonymous)
                menuItems.push([objsList[i].name + " [" + connI + "A]","showObjectDetails(\"" + objsList[i].id + "\",true)"]);
    }

    if (menuItems.length==1) {
        menuItems.push(["",null]);
        menuItems.push(["<div style='font-size: 0.6em;width: 100%;'>No objects found!<br><br>" +
                         "Check objects filters<br>" +
                         "or register new objects.</div>",null]);
        menuItems.push(["",null]);
    }

    setMenu_List_OnClick(menuItems);
}

function fillObjDetails(objJson) {
    obj = JSON.parse(objJson);
    detailObjId = obj.id;

    if (currentPage == PAGE_OBJ_DETAILS)
        setTitle("<p>Object</p>");

    var html = "";
    html += "    <div id='div_obj_title'>" + objectTitleToHtml(obj) + "</div>";
    html += "    <div id='div_obj_links'>" + objectLinksToHtml(obj) + "</div>";

    html += "    <hr>";

    var contentId = "obj_" + obj.id + "_content";
    html += "<div id='" + contentId + "'>";
    //html += "    <div id='div_struct' class='div_obj_row' style='width: fit-content; margin: 10px auto;' />"     // for fetchObjStruct() -> fillObjStructure()        // CONTENT CENTRED
    html += "    <div id='div_struct' class='div_obj_row' style='margin: 10px auto;' />"     // for fetchObjStruct() -> fillObjStructure()
    html += "</div>";

    setContent(html);
    fetchObjStruct(obj.id);

    if (!obj.isCloudConnected) {
        addDisabledCssClassById("obj_" + obj.id + "_title");
        addDisabledCssClassById("obj_" + obj.id + "_content");
    }
}

function fillObjStructure(rootJson) {
    root = JSON.parse(rootJson);

    // Update div_struct
    document.getElementById("div_struct").innerHTML = objectStructureToHtmlBox(root);
}

function fillComponent(compJson) {
    comp = JSON.parse(compJson);
    document.getElementById(comp.componentPath).innerHTML = componentToHtml(comp);
    // current method is called only from "SSE -> emitStateUpd(eventText) -> fetchComponent(objId,compPath)" stack
    showUpdateFeedback(comp.componentPath + "_state");
}

function fillObj_Header(objJson) {
    obj = JSON.parse(objJson);


    //more object info: Id, brand, model
    //<span style='color:gray;font-size:0.7em;'>(" + permsList[0].objId + ")</span>

    if (document.getElementById("div_obj_title") != null)
        document.getElementById("div_obj_title").innerHTML = objectTitleToHtml(obj);
    if (document.getElementById("div_obj_links") != null)
        document.getElementById("div_obj_links").innerHTML = objectLinksToHtml(obj);


    if (!obj.isCloudConnected) {
        addDisabledCssClassById("obj_" + obj.id + "_title");
        addDisabledCssClassById("obj_" + obj.id + "_content");
    }
}

function fillObjInfo_Obj(objJson) {
    fillObj_Header(objJson, "Info");

    obj = JSON.parse(objJson);

    if (currentPage == PAGE_OBJ_INFO)
        setTitle("<p><a href='javascript:void(0);' onclick='showObjectDetails(&quot;" + obj.id + "&quot;,true)'>Object</a> > Info</p>");

    if (document.getElementById("val_obj_id") != null)
        document.getElementById("val_obj_id").innerHTML = obj.id;
    if (document.getElementById("val_obj_name") != null)
        document.getElementById("val_obj_name").innerHTML = obj.name;
    if (document.getElementById("val_obj_permission") != null)
        document.getElementById("val_obj_permission").innerHTML = obj.permission;
    if (document.getElementById("val_obj_owner") != null)
        document.getElementById("val_obj_owner").innerHTML = obj.owner;
    if (document.getElementById("val_obj_isConnected") != null)
        document.getElementById("val_obj_isConnected").innerHTML = obj.isConnected;
    if (document.getElementById("val_obj_isCloudConnected") != null)
        document.getElementById("val_obj_isCloudConnected").innerHTML = obj.isCloudConnected;
    if (document.getElementById("val_obj_isLocalConnected") != null)
        document.getElementById("val_obj_isLocalConnected").innerHTML = obj.isLocalConnected;
    if (document.getElementById("val_obj_jodVersion") != null)
        document.getElementById("val_obj_jodVersion").innerHTML = obj.jodVersion;
}

function fillObjInfo_Struct(objStructJson) {
    objStruct = JSON.parse(objStructJson);
    if (document.getElementById("val_obj_brand") != null)
        document.getElementById("val_obj_brand").innerHTML = objStruct.brand;
    if (document.getElementById("val_obj_description") != null)
        document.getElementById("val_obj_description").innerHTML = objStruct.description;
    if (document.getElementById("val_obj_descrLong") != null)
        document.getElementById("val_obj_descrLong").innerHTML = objStruct.descrLong;
    if (document.getElementById("val_obj_model") != null)
        document.getElementById("val_obj_model").innerHTML = objStruct.model;
}

function fillObjPerms(objPermsJson) {
    objPerms = JSON.parse(objPermsJson);

    if (currentPage == PAGE_OBJ_PERMS)
        setTitle("<p><a href='javascript:void(0);' onclick='showObjectDetails(&quot;" + objPerms[0].objId + "&quot;,true)'>Object</a> > Access Control</p>");

    var html = "        <tr>";
    html += "        <th>Service's ID</th>";
    html += "        <th>User's ID</th>";
    html += "        <th>Permission</th>";
    html += "        <th>Connection Type</th>";
    html += "        <th>Actions</th>";
    html += "        </tr>";
    for (var i = 0; i < objPerms.length; i++) {
        html += editablePermission_Row(objPerms[i].id,objPerms[i].objId,objPerms[i].srvId,objPerms[i].usrId,objPerms[i].type,objPerms[i].connection);
    }

    var tableId = "table_" + objPerms[0].objId + "_perms";
    if (document.getElementById(tableId) != null)
        document.getElementById(tableId).innerHTML = html;
}

function fillUser(userJson) {
    if (currentPage == PAGE_USR_INFO)
        setTitle_Str("User Profile:");

    user = JSON.parse(userJson);

    if (document.getElementById("val_user_id") != null)
        document.getElementById("val_user_id").innerHTML = user.id;
    if (document.getElementById("val_user_name") != null)
        document.getElementById("val_user_name").innerHTML = user.name;

    if (user.isAuthenticated) {
        showLoginCssClass();
        hideLogoutCssClass();
        loggedUserId = user.id;
        loggedUserName = user.name;
    } else {
        showLogoutCssClass();
        hideLoginCssClass();
        loggedUserId = null;
        loggedUserName = null;
   }
}

function fillService(serviceJson) {
    if (currentPage == PAGE_SRV_INFO)
        setTitle_Str("Service Details:");

    service = JSON.parse(serviceJson);

    serviceId = service.srvId;
    serviceVersion = service.jslVersion;

    if (document.getElementById("val_service_name") != null)
        document.getElementById("val_service_name").innerHTML = service.name;
    if (document.getElementById("val_service_status") != null)
        document.getElementById("val_service_status").innerHTML = service.status;
    if (document.getElementById("val_service_isJCPConnected") != null)
        document.getElementById("val_service_isJCPConnected").innerHTML = service.isJCPConnected;
    if (document.getElementById("val_service_isCloudConnected") != null)
        document.getElementById("val_service_isCloudConnected").innerHTML = service.isCloudConnected;
    if (document.getElementById("val_service_isLocalRunning") != null)
        document.getElementById("val_service_isLocalRunning").innerHTML = service.isLocalRunning;
    if (document.getElementById("val_service_srvId") != null)
        document.getElementById("val_service_srvId").innerHTML = service.srvId;
    if (document.getElementById("val_service_usrId") != null)
        document.getElementById("val_service_usrId").innerHTML = service.usrId;
    if (document.getElementById("val_service_instId") != null)
        document.getElementById("val_service_instId").innerHTML = service.instId;
    if (document.getElementById("val_service_jslVersion") != null)
        document.getElementById("val_service_jslVersion").innerHTML = service.jslVersion;
    if (document.getElementById("val_service_supportedJCPAPIsVersions") != null)
        document.getElementById("val_service_supportedJCPAPIsVersions").innerHTML = service.supportedJCPAPIsVersions;
    if (document.getElementById("val_service_supportedJOSPProtocolVersions") != null)
        document.getElementById("val_service_supportedJOSPProtocolVersions").innerHTML = service.supportedJOSPProtocolVersions;
    if (document.getElementById("val_service_supportedJODVersions") != null)
        document.getElementById("val_service_supportedJODVersions").innerHTML = service.supportedJODVersions;
    if (document.getElementById("val_service_sessionId") != null)
        document.getElementById("val_service_sessionId").innerHTML = service.sessionId;
}

function fillPermission_Add(permsListJson,objName,objOwner,isConnected,currentPermissions) {
    permsList = JSON.parse(permsListJson);

    // Update div_struct
    document.getElementById("div_permissions").innerHTML += objectPermissionsToHtmlBox(permsList,objName,objOwner,isConnected,currentPermissions);
}

function fillPermission(permsListJson,objName,objOwner,isConnected,currentPermissions) {
    permsList = JSON.parse(permsListJson);

    // Update div_struct
    document.getElementById("div_permissions").innerHTML = objectPermissionsToHtmlBox(permsList,objName,objOwner,isConnected,currentPermissions);
}

    function findGetParameter(documentUrl,parameterName) {
        var result = null;
        var tmp = [];

        documentUrl.search
            .substr(1)
            .split("&")
            .forEach(function (item) {
                tmp = item.split("=");
                if (tmp[0] === parameterName)
                    result = decodeURIComponent(tmp[1]);
            });

        return result;
    }



// HTML components (Static)

function getTitle_Welcome_Str() {
    return "Welcome to JOSP<br><span style='text-align:right;'>Eco-System</span>";
}

function getMenu_ObjsSelectorDropDown_HTML() {
    var html = "";

    if (typeof user !== "undefined" && user.isAuthenticated) {
        html += "<div class='dropdown'>";
        html += "  <button class='dropbtn box_opts box_opts_active'><i class='fa fa-sliders'></i></button>";
        html += "  <div class='dropdown-content'>";
        html += "    <a>";
        html += "      <input type='checkbox' onclick='updMenuObjsListFilter()' id='objs_list_filter_owner'" + (objsListFilterOwner ? " checked='true'" : "") + "' />";
        html += "      <label for='objs_list_filter_owner'>My objects</label>";
        html += "    </a>";
        html += "    <a>";
        html += "      <input type='checkbox' onclick='updMenuObjsListFilter()' id='objs_list_filter_shared'" + (objsListFilterShared ? " checked='true'" : "") + "' />";
        html += "      <label for='objs_list_filter_shared'>Shared with me</label>";
        html += "    </a>";
        html += "    <a>";
        html += "      <input type='checkbox' onclick='updMenuObjsListFilter()' id='objs_list_filter_anonymous'" + (objsListFilterAnonymous ? " checked='true'" : "") + "' />";
        html += "      <label for='objs_list_filter_anonymous'>Anonymous objects</label>";
        html += "    </a>";
        html += "  </div>";
        html += "</div>";
    }
    return html;
}

function getOpts_ObjsMngr() {
    var html = "";

    html += "<div class='dropdown'>";
    html += "  <button class='dropbtn box_opts box_opts_active'><i class='fa fa-bars'></i></button>";
    html += "  <div class='dropdown-content'>";
    html += "    <a class='logout' href='/apis/user/1.0/login/'><i class='fa fa-sign-in'></i>Login</a>";
    //html += "    <a class='logout' href='/apis/user/1.0/registration/'><i class='fa a-id-card-o'></i>Register</a>";
    html += "    <a class='login' href='javascript:showUser(true)'><i class='fa a-id-card-o'></i>Profile</a>";
    html += "    <a class='login' href='/apis/user/1.0/logout/'><i class='fa fa-sign-out'></i>Logout</a>";
    html += "    <hr>";
    html += "    <a href='javascript:showService(true)'><i class='fa fa-info'></i>JCP FE Info</a>";
    html += "    <a href='javascript:showAbout(true)'><i class='fa fa-question-circle-o'></i>About</a>";
    html += "  </div>";
    html += "</div>";

    html += "<div class='box_opts'>";
    html += "<a href='javascript:showShare(true)'><i class='fa fa-share-alt'></i></a>";

    return html;
}

function getContent_Objects_Html() {
    var html = "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<p>Ready to enjoy the limitless happiness of the connected world?<br></p>";
    html += "<p>Let's start selecting an object from the side menu,<br>";
    html += "if there's not objects listed then ";
    html += "<span class='logout'>";
    html += "<a href='/apis/user/1.0/login/'>login</a>";
    html += " (or <a href='/apis/user/1.0/registration/'>register</a>)</br>";
    html += " or try to ";
    html += "</span>";
    html += "<a href='#'>add new one</a>.</p>";
    html += "</div>";
    return html;
}

function getContent_ObjectInfo_Html() {
    var tableStyle = "width: 70%; margin: auto;"
    var html = "";

    var html = "";
    html += "    <div id='div_obj_title'></div>";
    html += "    <div id='div_obj_links'></div>";

    html += "    <hr>";

    var contentId = "obj_" + obj.id + "_content";
    html += "<div id='" + contentId + "'>";
    html += "    <h2>Info</h2>";
    html += "    <div style='" + tableStyle + "'>";
    html += "        <table class='table_details'>";
    html += "            <tr><td class='label'>ID</td><td class='value'><span id='val_obj_id'>...</span></td></tr>";
    html += "            <tr><td class='label'>Name</td><td class='value'><span id='val_obj_name'>...</span></td></tr>";
    html += "            <tr><td class='label'>Current Access Level</td><td class='value'><span id='val_obj_permission'>...</span></td></tr>";
    html += "            <tr><td class='label'>Owner</td><td class='value'><span id='val_obj_owner'>...</span></td></tr>";
    html += "            <tr><td class='label'>Brand</td><td class='value'><span id='val_obj_brand'>...</span></td></tr>";
    html += "            <tr><td class='label'>Model</td><td class='value'><span id='val_obj_model'>...</span></td></tr>";
    html += "            <tr><td class='label'>Description</td><td class='value'><span id='val_obj_description'>...</span></td></tr>";
    html += "            <tr><td class='label'></td><td class='value'><span id='val_obj_descrLong'>...</span></td></tr>";
    html += "        </table>";
    html += "    </div>";

    html += "<hr>";

    html += "    <h2>Communication</h2>";
    html += "    <div style='" + tableStyle + "'>";
    html += "        <table class='table_details'>";
    html += "            <tr><td class='label'>Is Connected</td><td class='value'><span id='val_obj_isConnected'>...</span></td></tr>";
    html += "            <tr><td class='label'>Is Cloud Connected</td><td class='value'><span id='val_obj_isCloudConnected'>...</span></td></tr>";
    html += "            <tr><td class='label'>Is Local Connected</td><td class='value'><span id='val_obj_isLocalConnected'>...</span></td></tr>";
    html += "        </table>";
    html += "    </div>";

    html += "<hr>";

    html += "    <h2>Versions</h2>";
    html += "    <div style='" + tableStyle + "'>";
    html += "        <table class='table_details'>";
    html += "            <tr><td class='label'>JOD Version</td><td class='value'><span id='val_obj_jodVersion'>...</span></td></tr>";
    html += "        </table>";
    html += "    </div>";

    html += "</div>";

    return html;
}

function getContent_ObjectPerms_Html(objId) {
    var tableStyle = "width: 70%; margin: auto;"
    var html = "";
    var tableId = "table_" + objId + "_perms";
    var linksId = "links_" + objId + "_perms";

    var html = "";
    html += "    <div id='div_obj_title'></div>";
    html += "    <div id='div_obj_links'></div>";

    html += "    <hr>";

    var contentId = "obj_" + obj.id + "_content";
    html += "<div id='" + contentId + "'>";

    html += "    <h2>Access Control</h2>";

    html += objectPermissionsStyles();
    html += "    <p>Object's owner '<b id='val_obj_owner'>...</b>'</p>";
    html += "    <p>Current service/user has '<b id='val_obj_permission'>...</b>' permission on object</p>";

    html += "    <div class='obj_perms'>";
    html += "       <table id='" + tableId + "'></table>";
    html += "    </div>";

    html += "    <ul id='" + linksId + "'>";
    html += "        <li>Add ";
    html += "            <a href='javascript:void(0);' onclick='addPermission_Row(\"" + tableId + "\",\"" + objId + "\",\"#All\",\"#All\",\"Actions\",\"OnlyLocal\",true)'>Public for Local</a>";
    html += "            permission";
    html += "        </li>";
    html += "        <li>Add ";
    html += "            <a href='javascript:void(0);' onclick='addPermission_Row(\"" + tableId + "\",\"" + objId + "\",\"#All\",\"#All\",\"Actions\",\"LocalAndCloud\",true)'>Public for All</a>";
    html += "            permission";
    html += "        </li>";
    html += "        <li>Add custom permission ";
    html += "            <a href='javascript:void(0);' onclick='addCustomPermission_Row(\"" + tableId + "\",\"" + objId + "\")'><i class='fa fa-plus-square-o'></i></a>";
    html += "        </li>";
    html += "    </ul>";

    html += "</div>";

    return html;
}

function getContent_User_Html() {
    var html = "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>ID</td><td class='value'><span id='val_user_id'>...</span></td></tr>";
    html += "<tr><td class='label'>Username</td><td class='value'><span id='val_user_name'>...</span></td></tr>";

    html += "</table>";
    html += "</div>";
    return html;
}

function getContent_Service_Html() {
    var html = "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Name</td><td id='val_service_name' class='value'>...</td></tr>";
    html += "<tr><td class='label'>ID</td><td class='value'><span id='val_service_srvId'>...</span>/<span id='val_service_usrId'>...</span>/<span id='val_service_instId'>...</span></td></tr>";
    html += "<tr><td class='label'>Status</td><td class='value'><span id='val_service_status'>...</span></td></tr>";
    html += "<tr><td class='label'>JCP Connected</td><td class='value'><span id='val_service_isJCPConnected'>...</span></td></tr>";
    html += "<tr><td class='label'>JOSP GW Connected</td><td class='value'><span id='val_service_isCloudConnected'>...</span></td></tr>";
    html += "<tr><td class='label'>local_running</td><td class='value'><span id='val_service_isLocalRunning'>...</span></td></tr>";
    html += "<tr><td class='label'>JSL Version</td><td class='value'><span id='val_service_jslVersion'>...</span></td></tr>";
    html += "<tr><td class='label'>Supported JCP APIs</td><td class='value'><span id='val_service_supportedJCPAPIsVersions'>...</span></td></tr>";
    html += "<tr><td class='label'>Supported JOSP Protocol</td><td class='value'><span id='val_service_supportedJOSPProtocolVersions'>...</span></td></tr>";
    html += "<tr><td class='label'>Supported JOD Agents</td><td class='value'><span id='val_service_supportedJODVersions'>...</span></td></tr>";
    html += "<tr><td class='label'>Session ID:</td><td class='value'><span id='val_service_sessionId'>...</span></td></tr>";

    html += "</table>";
    html += "</div>";
    return html;
}

function getContent_Share_Html() {
    var html = "<div id='div_permissions' />";
    return html;
}

function getFooter_Col1_HTML() {
    var html = "<p><b>JOSP JCP FrontEnd</b> provided by <b><a href='www.johnosproject.com'>johnosproject.com</a></b></p>";
    html += "<p>Version: 2.0.0</p>";
    return html;
}

function getFooter_Col2_HTML() {
    var html = "<p>Made with <3</p>";
    return html;
}

function getFooter_Col3_HTML() {
    var html = "<p><a href='https://bitbucket.org/johnosproject/com.robypomper.josp/src/master/README.md'>Documentation and repository</a></p>";
    return html;
}


// HTML component (Dynamic)

function objectDetailsToHtml(obj) {
    var html = "";
    html += objectStyles();

    html += "<div class='div_obj_row div_obj_row_first'>";
    html += objectDetailsMainToHtmlBox(obj);
    html += objectDetailsSecurityToHtmlBox(obj);
    html += "</div>";

    return html;
}

function objectTitleToHtml(obj) {
    var titleId = "obj_" + obj.id + "_title";
    var html = "<h1 id='" + titleId + "'>";
    html += "    " + obj.name;
    html += "    <a href='javascript:void(0);' onclick='editObjectName_Title(this.parentElement,\"" + obj.name + "\");'>";
    html += "        <i class='fa fa-pencil' style='font-size: 0.8em;'></i>";
    html += "    </a>";
    html += "</h1>";
    return html;
}

function objectLinksToHtml(obj) {
    var html = "";
    html += "<div style='padding: 20px 0 20px 10px;'>";
    html += "    <p>Get more <a href='javascript:void(0);' onclick='showObjectInfo(&quot;" + obj['id'] + "&quot;,true)'>Obj Info</a>";
    html += "    <p>Setup the Obj's <a href='javascript:void(0);' onclick='showObjectPermissions(&quot;" + obj['id'] + "&quot;,true)'>Access Control</a>";
    html += "</div>";
    return html;
}

function objectDetailsMainToHtmlBox(obj) {
    var html = "";
    html += "<div class='box'>";
    html += boxExpandCollapseToHTML();
    html += "    <h2><a href='javascript:void(0);' onclick='showObjectInfo(&quot;" + obj['id'] + "&quot;,true)'>Details</a></h2>";
    html += "    <table class='table_details'>";
    html += "    <tr><td class='label'>" + 'id:'            + "</td><td class='value'>" + obj['id']                 + "</td></tr>";
    html += "    <tr><td class='label'>" + 'is Connected:'  + "</td><td class='value'>" + obj['isCloudConnected']   + "</td></tr>";
    html += "    <tr><td class='label'>" + 'jod version:'   + "</td><td class='value'>" + obj['jodVersion']         + "</td></tr>";
    html += "    </table>"
    html += "</div>";
    return html;
}

function objectDetailsSecurityToHtmlBox(obj) {
    var html = "";
    html += "<div class='box'>";
    html += boxExpandCollapseToHTML();
    html += "    <h2><a href='javascript:void(0);' onclick='showObjectInfo(&quot;" + obj['id'] + "&quot;,true)'>Security</a></h2>";
    html += "    <table class='table_details'>";
    html += "    <tr><td class='label'>" + 'owner:'      + "</td>";
    html += "        " + editableObjectOwner_Field(obj['owner']);
    html += "        </tr>";
    html += "    <tr><td class='label'>" + 'permission:' + "</td><td class='value'>" + obj['permission'] + "</td></tr>";
    html += "    </table>"
    html += "</div>";
    return html;
}

function objectStructureToHtmlBox(root) {
    var html = "";
    html += objectStructureStyles();
    //html += objectRootDetailsToHtmlBox(root);

    //html += "<div class='box box_obj_struct'>";
    //html += boxExpandCollapseToHTML();
    html += "    <h2>Structure</h2>";
    html += containerToHtmlList(root);
    //html += "</div>";
    return html;
}

function objectRootDetailsToHtmlBox(root) {
    var html = "";
    html += "<div class='box box_obj_details'>";
    html += boxExpandCollapseToHTML();
    html += "<h2>Model:</h2>";
    html += "    <b>Model:</b> " + root.model + "<br>";
    html += "    <b>Brand:</b> " + root.brand + "<br>";
    html += "    <b>Description:</b> " + root.description + "<br>";
    html += "</div>";
    return html;
}

function containerToHtmlList(contJson) {
    var html = "";
    html += "<ul class='action_first'>";
    for (var i=0; i<contJson.subComps.length; i++) {
        html += "<li id='" + contJson.subComps[i].componentPath + "' class='box_obj_comp box_obj_comp_" + contJson.subComps[i].type + " " + (defaultObjCompsCollapsed ? "collapsed" : "") + "'>";
        html += boxExpandCollapseToHTML();
        html += componentToHtml(contJson.subComps[i]);
        html += "</li>";
    }
    html += "</ul>";
    return html;
}

function componentToHtml(comp) {
    var html = "";

    html += "<p class='info'><spam style='color:gray;'>";
    html += comp.componentPath.substring(0,comp.componentPath.length-comp.name.length);
    html += "</spam><b>";
    html += comp.name;
    html += "</b>";
    if (comp.type != "Container")
        html += "<br><spam style='color:gray; font-size: 0.8em;'>(" + comp.type + ")";
    html += "</span></p>";

    if (comp.type == "BooleanState") {
        html += "<p id='" + comp.componentPath + "_state' class='state action_first'>" + comp.state + "</p>";

    } else if (comp.type == "BooleanAction") {
        html += "<p id='" + comp.componentPath + "_state' class='state action_first'>" + comp.state + "</p>";
        html += "<div id='" + comp.componentPath + "_action' class='action action_single action_first' onClick='execute(this,\"" + comp.pathSwitch + "\");'>Switch</div>";
        html += "<div id='" + comp.componentPath + "_action_switch' class='action action_main action_first' onClick='execute(this,\"" + comp.pathSwitch + "\");'>Switch</div>";
        html += "<div id='" + comp.componentPath + "_action_true' class='action action_first' onClick='execute(this,\"" + comp.pathTrue + "\");'>Set TRUE</div>";
        html += "<div id='" + comp.componentPath + "_action_false' class='action' onClick='execute(this,\"" + comp.pathFalse + "\");'>Set FALSE</div>";

    } else if (comp.type == "RangeState") {
        html += "<p id='" + comp.componentPath + "_state' class='state action_first'>" + comp.state + "</p>";

    } else if (comp.type == "RangeAction") {
        html += "<p id='" + comp.componentPath + "_state' class='state action_first'>" + comp.state + "</p>";
        html += "<div id='" + comp.componentPath + "_action' class='action action_single action_first'>";
        var value_slider_id = comp.componentPath + "_action_slider";
        var value_slider_path = comp.pathSetValue + "\" + document.getElementById(\"" + value_slider_id + "\").value";
        html += "    <p style='width: auto;padding: 5px;'>" + comp.min + "</p>";
        html += "    <input id='" + value_slider_id + "' type='range' min='" + comp.min + "' max='" + comp.max + "' step='" + comp.step + "' value='" + comp.state + "' onChange='execute(this.parentElement,\"" + value_slider_path + ");'>";
        html += "    <p style='width: auto;padding: 5px;'>" + comp.max + "</p>";
        html += "</div>";

        html += "<div id='" + comp.componentPath + "_action_dec' class='action action_first' onClick='execute(this,\"" + comp.pathDec + "\");'>Decrease</div>";
        html += "<div id='" + comp.componentPath + "_action_inc' class='action' onClick='execute(this,\"" + comp.pathInc + "\");'>Increase</div>";
        html += "<div id='" + comp.componentPath + "_action_min' class='action action_main action_first' onClick='execute(this,\"" + comp.pathMin + "\");'>Set MIN</div>";
        html += "<div id='" + comp.componentPath + "_action_1_2' class='action' onClick='execute(this,\"" + comp.pathSet1_2 + "\");'>Set 1/2</div>";
        html += "<div id='" + comp.componentPath + "_action_1_3' class='action' onClick='execute(this,\"" + comp.pathSet1_3 + "\");'>Set 1/3</div>";
        html += "<div id='" + comp.componentPath + "_action_2_3' class='action' onClick='execute(this,\"" + comp.pathSet2_3 + "\");'>Set 2/3</div>";
        html += "<div id='" + comp.componentPath + "_action_max' class='action action_main' onClick='execute(this,\"" + comp.pathMax + "\");'>Set MAX</div>";
        var value_input_id = "aaa1234";
        var value_path = comp.pathSetValue + "\" + document.getElementById(\"" + value_input_id + "\").value";
        html += "<div id='" + comp.componentPath + "_action_value' class='action action_main action_first' onClick='execute(this,\"" + value_path + ");'>Set Value</div>";
        html += "<input id='" + value_input_id + "' class='action action_main' type='text' style='margin: 10px; width: 100px;'>";

    } else if (comp.type == "Container")
        html += containerToHtmlList(comp);

    return html;
}

function objectPermissionsToHtmlBox(permsList,objName,objOwner,isConnected,currentPermissions) {
    var html = "";
//[ {
//"id" : "VNIULWJRCXOIWCXRVFFZ",
//"objId" : "DYDFL-DNKVK-OHACN",
//"srvId" : "#All",
//"usrId" : "#Owner",
//"type" : "CoOwner",
//"connection" : "LocalAndCloud",
//"lastUpdate" : 1598361570253,
//"pathUpd" : "/apis/permissions/1.0/DYDFL-DNKVK-OHACN/upd/VNIULWJRCXOIWCXRVFFZ/",
//"pathDel" : "/apis/permissions/1.0/DYDFL-DNKVK-OHACN/del/VNIULWJRCXOIWCXRVFFZ/",
//"pathDup" : "/apis/permissions/1.0/DYDFL-DNKVK-OHACN/dup/VNIULWJRCXOIWCXRVFFZ/"
//} ]

    if (permsList.length==0)
        return "No permissions fetched.";

    var divId = "div_" + permsList[0].objId + "_perms";
    var titleId = "title_" + permsList[0].objId + "_perms";
    var tableId = "table_" + permsList[0].objId + "_perms";
    var linksId = "links_" + permsList[0].objId + "_perms";

    html += objectPermissionsStyles();

    html += "<div id='" + divId + "' class='box obj_perms" + (!isConnected ? " disabled" : "") + "'>";
    html += boxExpandCollapseToHTML();

    html += "    <h2 id='" + titleId + "'>";
    html += "       " + objName;
    html += "       <span style='color:gray;font-size:0.7em;'>(" + permsList[0].objId + ")</span>";
    html += "    </h2>";

    html += "    <p>Object's owner <b>'" + objOwner + "'</b></p>";
    html += "    <p>Current service/user has <b>'" + currentPermissions + "'</b> permission on object</p>";

    html += "    <table id='" + tableId + "'>";
    html += "        <tr>";
    html += "        <th>Service's ID</th>";
    html += "        <th>User's ID</th>";
    html += "        <th>Permission</th>";
    html += "        <th>Connection Type</th>";
    html += "        <th>Actions</th>";
    html += "        </tr>";
    for (var i = 0; i < permsList.length; i++) {
        html += editablePermission_Row(permsList[i].id,permsList[i].objId,permsList[i].srvId,permsList[i].usrId,permsList[i].type,permsList[i].connection);
    }
    html += "    </table>";

    html += "    <ul id='" + linksId + "'>";
    html += "        <li>Add ";
    html += "            <a href='javascript:void(0);' onclick='addPermission_Row(\"" + tableId + "\",\"" + permsList[0].objId + "\",\"#All\",\"#All\",\"Actions\",\"OnlyLocal\",true)'>Public for Local</a>";
    html += "            permission";
    html += "        </li>";
    html += "        <li>Add ";
    html += "            <a href='javascript:void(0);' onclick='addPermission_Row(\"" + tableId + "\",\"" + permsList[0].objId + "\",\"#All\",\"#All\",\"Actions\",\"LocalAndCloud\",true)'>Public for All</a>";
    html += "            permission";
    html += "        </li>";
    html += "        <li>Add custom permission ";
    html += "            <a href='javascript:void(0);' onclick='addCustomPermission_Row(\"" + tableId + "\",\"" + permsList[0].objId + "\")'><i class='fa fa-plus-square-o'></i></a>";
    html += "        </li>";
    html += "    </ul>";

    html += "</div>";

    return html;
}


// Object's component (HTML) styles

function objectStyles() {
    var html = "";
    html += "<style>";
    html += "    .div_obj_row table {";
    html += "        min-width: 310px;";
    html += "    }";
//    html += "    .tb_obj_details {";
//    html += "        width: 50%;";
//    html += "        float: left;";
//    html += "        min-width: 310px;";
//    html += "    }";
//    html += "    .tb_obj_details tr td {";
//    html += "        padding: 1px 10px;";
//    html += "    }";
//    html += "    .tb_obj_details tr .label {";
//    html += "        text-align: right;";
//    html += "    }";
//    html += "    .tb_obj_details tr .value {";
//    html += "        font-weight: 400;";
//    html += "    }";
    html += "    .div_obj_row {";
    html += "        margin: 10px 0;";
    html += "        display: flex;";
    html += "        flex-direction: row;";
    html += "        justify-content: center;";
    html += "        flex-wrap: wrap;";
    html += "    }";
    html += "    .div_obj_row div {";
    html += "        margin: 0 10px;";
    html += "    }";

    html += "    @media only screen and (max-width: 700px) {";
    html += "        .div_obj_row div  {";
    //html += "            margin: auto;";
    html += "            float: none;";
    //html += "            width: 80%;";
    html += "        }";
    html += "       .div_obj_row_first div {";
    html += "            flex-grow: 1;";
    html += "       }";
    html += "    }";
    html += "";
    html += "</style>";
    return html;
}

function objectStructureStyles() {
    var html = "";
    html += "<style>";
    html += "    .box_obj_details {";
    html += "        width: 200px;";
    html += "        flex-grow: 1;";
    html += "    }";
    html += "    #div_struct {";
    html += "        flex-direction: row-reverse;";
    html += "        align-items: baseline;";
    html += "    }";
    html += "    .box_obj_struct {";
    html += "        flex-grow: 1;";
    html += "    }";
    html += "    .box_obj_struct ul {";
    html += "        padding: 0px;";
    html += "        width: 100%;";
    html += "    }";
    html += "    .box_obj_comp {";
    html += "        border: 1px solid #ccc!important;";
    html += "        border-radius: 16px;";
    html += "        padding: 10px;";
    html += "        overflow: auto;";
    html += "        box-shadow: 0 3px 6px 0 rgba(0, 0, 0, 0.4), 0 6px 15px 0 rgba(0, 0, 0, 0);";
    html += "        background-color: #00000005;";
    html += "        margin: 5px;";
    html += "    }";
    html += "    .box_obj_comp > * {";
    html += "        float: left;";
    html += "        padding: 5px;";
    html += "    }";
    html += "    .box_obj_comp.collapsed > * {";
    html += "        padding: 5px;";
    html += "        display: none";
    html += "    }";
    html += "    .box_obj_comp.collapsed > div:first-child {";
    html += "        display: block";
    html += "    }";
    html += "    .box_obj_comp.collapsed .info {";
    html += "        display: block";
    html += "    }";
    html += "    .box_obj_comp.collapsed .info > spam {";
    html += "        display: none";
    html += "    }";
    html += "    .box_obj_comp.collapsed .state {";
    html += "        display: block";
    html += "    }";
    html += "    .box_obj_comp ul {";
    html += "       width: 100%;";
    html += "       padding-left: 0px;";
    html += "    }";
    html += "    .state {";
    html += "        clear: none !important;";
    html += "        float: right;";
    html += "        width: 100px;";
    html += "        color: #404040;";
    html += "        font-size: 1.3em;";
    html += "        text-align: center;";
    html += "        text-transform: uppercase;";
    html += "        text-decoration-line: underline;";
    html += "    }";
    html += "    .action {";
    html += "        margin: 10px !important;";
    html += "        width: 100px;";
    html += "        text-align: center;";
    html += "        transition: box-shadow .3s;";
    html += "        border: 1px solid #ccc!important;";
    html += "        border-radius: 16px;";
    html += "        padding: 10px;";
    html += "        margin: 10px;";
    html += "        box-shadow: 0 3px 6px 0 rgba(0, 0, 0, 0.4), 0 6px 15px 0 rgba(0, 0, 0, 0);";
    html += "        display: flex;";
    html += "        justify-content: center;";
    html += "    }";
    html += "    .action, .action * {";
    html += "        display: none;";
    html += "    }";
    html += "    .action_main, .action_main * {";
    html += "        display: none;";
    html += "    }";
    html += "    .action_single, .action_single * {";
    html += "        display: flex;";
    html += "        width: 90%;";
    html += "        margin: auto !important;";
    html += "        float: none;";
    html += "        max-width: 300px;";
    html += "    }";
    html += "    .action:hover {";
    html += "        box-shadow: 0 3px 6px 0 rgba(0, 0, 0, 0.8), 0 6px 15px 0 rgba(0, 0, 0, 0);";
    html += "    }";
    html += "    .action_first {";
    html += "        clear: both;";
    html += "    }";
    html += "";
    html += "</style>";
    html += "";
    return html;
}

function objectPermissionsStyles() {
    var html = "";
    html += "<style>";
    html += ".obj_perms table {";
    html += "    margin: 20px auto;";
    html += "}";
    html += ".obj_perms td, .obj_perms th {";
    html += "    border: 1px solid #ccc;";
    html += "    padding: 5px;";
    html += "}";
    html += ".obj_perms th {";
    html += "    font-size: 1.1em;";
    html += "    padding: 10px;";
    html += "    background-color: #5b9dc326;";
    html += "}";
    html += ".obj_perms td p {";
    html += "    max-width: 150px;";
    html += "    white-space: nowrap;";
    html += "    overflow: hidden;";
    html += "    text-overflow: ellipsis;";
    html += "}";
    html += ".obj_perms .btn_actions {";
    html += "    text-align: center;";
    html += "}";
    html += "</style>";
    return html;
}


// Manage CSS classes

function showLoginCssClass() {
    var para = document.createElement("style");
    para.id = "style_login"
    var node = document.createTextNode(".login { display: block !important; } .logout { display: none !important; }");
    para.appendChild(node);

    var element = document.getElementsByTagName('head')[0].appendChild(para);
}

function hideLoginCssClass() {
    var elem = document.getElementById("style_login");
    if (elem==null)
        return;

    elem.parentNode.removeChild(elem);
}

function showLogoutCssClass() {
    var para = document.createElement("style");
    para.id = "style_logout"
    var node = document.createTextNode(".login { display: none !important; } .logout { display: block !important; }");
    para.appendChild(node);

    var element = document.getElementsByTagName('head')[0].appendChild(para);
}

function hideLogoutCssClass() {
    var elem = document.getElementById("style_logout");
    if (elem==null)
        return;

    elem.parentNode.removeChild(elem);
}

function addDisabledCssClassById(tagId) {
    addDisabledCssClass(document.getElementById(tagId));
}

function addDisabledCssClass(tag) {
    tag.classList.add('disabled');
}

function removeDisabledCssClassById(tagId) {
    removeDisabledCssClass(document.getElementById(tagId));
}

function removeDisabledCssClass(tag) {
    tag.classList.remove('disabled');
}