const PAGE_ADMIN_HOME = "Admin";
const PAGE_ADMIN_HOME_TITLE = "Admin";

const PAGE_ADMIN_SYSTEM = "admSystem";
const PAGE_ADMIN_SYSTEM_TITLE = "Admin > System";
const PAGE_ADMIN_SYSTEM_JSL = "admSystemJSL";
const PAGE_ADMIN_SYSTEM_JSL_TITLE = "Admin > System > JSL Instances";

const PAGE_ADMIN_APIS = "admAPIs";
const PAGE_ADMIN_APIS_TITLE = "Admin > JCP APIs";
const PAGE_ADMIN_GWS = "admGWs";
const PAGE_ADMIN_GWS_TITLE = "Admin > JOSP GWs";
const PAGE_ADMIN_OBJECTS = "admObjs";
const PAGE_ADMIN_OBJECTS_TITLE = "Admin > Objects";
const PAGE_ADMIN_SERVICES = "admSrvs";
const PAGE_ADMIN_SERVICES_TITLE = "Admin > Services";
const PAGE_ADMIN_USER = "admUsrs";
const PAGE_ADMIN_USER_TITLE = "Admin > Users";


// showAdminContent()                           *1
// showAdminContentGWs()                        *5
// showAdminContentObjects()                    *6
// showAdminContentServices()                   *7
// showAdminContentUsers()                      *8
// -> htmlAdminContentUnauthorized()
// -> htmlAdminContent()
// -> fetchAdminContentHeader()
//   -> fillAdminContentHeader()
// -> fetchAdminContent() ...                   *1
// -> htmlAdminContentGWs()                     *5
// -> fetchAdminContentGWs() ...                *5
// -> htmlAdminContentObjects()                 *6
// -> fetchAdminContentObjects() ...            *6
// -> htmlAdminContentServices()                *7
// -> fetchAdminContentServices() ...           *7
// -> htmlAdminContentUser()                    *8
// -> fetchAdminContentUsers() ...              *8

function showAdminContent(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_HOME, PAGE_ADMIN_HOME_TITLE);

    currentPage = PAGE_ADMIN_HOME;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    fetchAdminContent();
}

function showAdminContentGWs(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_GWS, PAGE_ADMIN_GWS_TITLE);

    currentPage = PAGE_ADMIN_GWS;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    replaceInnerHTMLById("div_admin_content",htmlAdminContentGWs());

    fetchAdminContentGWs();
}

function showAdminContentObjects(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_OBJECTS, PAGE_ADMIN_OBJECTS_TITLE);

    currentPage = PAGE_ADMIN_OBJECTS;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    replaceInnerHTMLById("div_admin_content",htmlAdminContentObjects());

    fetchAdminContentObjects();
}

function showAdminContentServices(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_SERVICES, PAGE_ADMIN_SERVICES_TITLE);

    currentPage = PAGE_ADMIN_SERVICES;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    replaceInnerHTMLById("div_admin_content",htmlAdminContentServices());

    fetchAdminContentServices();
}

function showAdminContentUsers(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_USER, PAGE_ADMIN_USER_TITLE);

    currentPage = PAGE_ADMIN_USER;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    replaceInnerHTMLById("div_admin_content",htmlAdminContentUsers());

    fetchAdminContentUsers();
}


function htmlAdminContentUnauthorized() {
    setTitle(PAGE_ADMIN_HOME_TITLE);

    var html = "";
    html += "    <h2 class='error'>You have not required authorization to visit this page</h2>";
    html += "    <p>Please, <a href='javascript:history.back()'>go back</a>.</p>";
    return html;
}

function htmlAdminContent() {
    setTitle(PAGE_ADMIN_HOME_TITLE);
    var html = "";
    html += "    <div id='div_admin_title'>";
    html += "        ";
    html += "    </div>";

    html += "    <div style='display: flex; justify-content: space-around; margin: 10px;'>";
    html += "    <div id='div_admin_stats_jcpapis' style='margin-bottom: 5px;'>";
    html += "        <p><b>JCP APIs Server</b><br>Started at <b id='val_jcpapis_timeStart'>...</b></p>";
    html += "        <p>Using <b id='val_jcpapis_cpuUsed'>...</b>% CPU and memory <b id='val_jcpapis_memoryUsed'>...</b>MB</p>";
    html += "    </div>";
    html += "    </div>";

    html += "    <hr style='margin-bottom: 20px;'>";

    html += "    <div id='div_admin_menu'>";
    html += "        <ul style='list-style-type: none; margin: 20px; padding: 0; display: flex; justify-content: space-around;'>";
    html += "            <li><a href='javascript:showAdminContent(true)'>Home</a></li>";
    html += "            <li><a href='javascript:showAdminContentGWs(true)'>JOSP GWs</a></li>";
    html += "            <li><a href='javascript:showAdminContentObjects(true)'>Objects</a></li>";
    html += "            <li><a href='javascript:showAdminContentServices(true)'>Services</a></li>";
    html += "            <li><a href='javascript:showAdminContentUsers(true)'>User</a></li>";
    html += "        </ul>";
    html += "    </div>";

    html += "    <hr>";

    html += "    <div id='div_admin_content'>...</div>";

    return html;
}

function fetchAdminContent() {
    //apiGET(backEndUrl,"/apis/CloudStatus/1.0/",fillAdminContent,onErrorFetch);
//    apiGET(backEndUrl,"/apis/CloudStatus/1.0/state/process",function(o){},onErrorFetch);
//    apiGET(backEndUrl,"/apis/CloudStatus/1.0/state/java",function(o){},onErrorFetch);
//    apiGET(backEndUrl,"/apis/CloudStatus/1.0/state/os",function(o){},onErrorFetch);
//    apiGET(backEndUrl,"/apis/CloudStatus/1.0/state/cpu",function(o){},onErrorFetch);
//    apiGET(backEndUrl,"/apis/CloudStatus/1.0/state/memory",function(o){},onErrorFetch);
//    apiGET(backEndUrl,"/apis/CloudStatus/1.0/state/disks",function(o){},onErrorFetch);
//    apiGET(backEndUrl,"/apis/CloudStatus/1.0/state/network",function(o){},onErrorFetch);
//    apiGET(backEndUrl,"/apis/CloudStatus/1.0/state/jcpfe",function(o){},onErrorFetch);
//    apiGET(backEndUrl,"/apis/CloudStatus/1.0/state/jcpfe/instances",function(o){},onErrorFetch);
}

function fetchAdminContentHeader() {
    //apiGET(backEndUrl,"/apis/JCP/2.0/jslwb/status",alert("GWs Status OK"),alert("GWs Status ERR"));    // OK
    apiGET(backEndUrl,pathJSLWB_Admin_APIs_Instance,fillAdminContentHeaderAPIs,onErrorFetch);    // OK
    apiGET(backEndUrl,pathJSLWB_Admin_GWs_Instance,function() {alert("GWs Status OK")},function(){alert("GWs Status ERR")});    // OK
    apiGET(backEndUrl,pathJSLWB_Admin_JSLWB_Instance,function(){alert("JSL WB Status OK")},function(){alert("JSL WB Status ERR")});    // OK
    apiGET(backEndUrl,pathJSLWB_Admin_FE_Instance,function(){alert("FE Status OK")},function(){alert("FE Status ERR")});    // OK
}

function fillAdminContent(adminJson) {
    admin = JSON.parse(adminJson);

    replaceInnerHTMLById("div_admin_title",htmlObjectContentTitle(obj));
}

function fillAdminContentHeaderAPIs(stateJCPAPIsJson) {
    stateJCPAPIs = JSON.parse(stateJCPAPIsJson);
    replaceInnerHTMLById("val_jcpapis_timeStart",dateToString(new Date(stateJCPAPIs.timeStart)));
    //replaceInnerHTMLById("val_jcpapis_timeRunning",stateJCPAPIs.timeRunning);     // Not displayed
    replaceInnerHTMLById("val_jcpapis_cpuUsed",doubleTruncateDigit(stateJCPAPIs.cpuUsed,1));
    replaceInnerHTMLById("val_jcpapis_memoryUsed",doubleTruncateDigit(stateJCPAPIs.memoryUsed,2));
}


// Admin's Gateways

// htmlAdminContentGWs()
// fetchAdminContentGWs()
// -> fillAdminContentGWs()

function htmlAdminContentGWs() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > JOSP GWs</p>");

    var html = "";
    html += "<div>";
    html += "<a href='javascript:showAdminContentGWs(true)'>Refresh</a>";
    html += "</div>";

    html += "<h2>JCP APIs's Gateways</h2>";
    html += "<div id='val_adm_gws' style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "</div>";
    return html;

    return html;
}

function fetchAdminContentGWs(objId) {
    apiGET(backEndUrl,pathJSLWB_Admin_GWs_Servers,fillAdminContentGWs,onErrorFetch);
}

function fillAdminContentGWs(jcpGWsJson) {
    var jcpGWs = JSON.parse(jcpGWsJson);
    var html = "";
    for (var i=0; i<jcpGWs.length; i++)
        html += gwToHTML(jcpGWs[i]);

    replaceInnerHTMLById("val_adm_gws",html);
}

function gwToHTML(gw) {
    var grayStyle = "color: gray;";
    var cellStyle = "border: 1px solid black; padding: 5px;";

    var html = "";
    html += "<h3>" + gw.id + " <span style='" + grayStyle + "'>(" + gw.type + ")</h3>";
    html += "<p>Server is " + gw.status + " with " + gw.clientsCount + "/" + gw.maxClientsCount + " clients connected</p>";
    html += "<p style='" + grayStyle + "'>Address: (internal: " + gw.internalAddress + "; public: " + gw.publicAddress + ")</p>";

    if (gw.clientsCount==0) {
        html += "<p>No clients connected</p>";
    } else {
        html += "<table style='border: 1px solid black; margin: 10px;'>";
        html += "<tr>";
        html += "    <th style='border: 1px solid black;'>Client ID</th>";
        html += "    <th style='border: 1px solid black;'>Status</th>";
        html += "    <th style='border: 1px solid black;'>Remote</th>";
        html += "</tr>";
        for (var i=0; i<gw.clientsList.length; i++) {
            html += "<tr>";
            html += "    <td style=' " + cellStyle + "'>" + gw.clientsList[i].id + "</td>";
            html += "    <td style=' " + cellStyle + "'>" + (gw.clientsList[i].isConnected ? "CONNECTED" : "NOT CONN.") + "</td>";
            var remote = gw.clientsList[i].remote;
            remote = remote.substring(remote.indexOf("(")+1,remote.indexOf(")"));
            html += "    <td style=' " + cellStyle + "'>" + remote + "</td>";
            html += "<tr>";
        }
        html += "</table>";
    }

    return html;
}


// Admin's Objects

// htmlAdminContentObjects()
// fetchAdminContentObjects()
// -> fillAdminContentObjects()

function htmlAdminContentObjects() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > Objects</p>");

    var html = "";
    html += "<div>";
    html += "<a href='javascript:showAdminContentObjects(true)'>Refresh</a>";
    html += "</div>";

    html += "<h2>JCP APIs's Objects</h2>";
    html += "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Objects count</td><td id='val_adm_objs_count' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Objects Online/Offline count</td><td><span id='val_adm_objs_online_count' class='value'>...</span> / <span id='val_adm_objs_offline_count' class='value'>...</span></td></tr>";
    html += "<tr><td class='label'>Objects Active/Inactive count</td><td><span id='val_adm_objs_active_count' class='value'>...</span> / <span id='val_adm_objs_inactive_count' class='value'>...</span></td></tr>";
    html += "<tr><td class='label'>Objects Owners count</td><td id='val_adm_objs_owners_count' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";
    return html;

    return html;
}

function fetchAdminContentObjects(objId) {
    apiGET(backEndUrl,pathJSLWB_Admin_APIs_Objs,fillAdminContentObjects,onErrorFetch);
}

function fillAdminContentObjects(jcpObjsJson) {
    jcpObjs = JSON.parse(jcpObjsJson);
    replaceInnerHTMLById("val_adm_objs_count",jcpObjs.Count);
    replaceInnerHTMLById("val_adm_objs_online_count",jcpObjs.onlineCount);
    replaceInnerHTMLById("val_adm_objs_offline_count",jcpObjs.offlineCount);
    replaceInnerHTMLById("val_adm_objs_active_count",jcpObjs.activeCount);
    replaceInnerHTMLById("val_adm_objs_inactive_count",jcpObjs.inactiveCount);
    replaceInnerHTMLById("val_adm_objs_owners_count",jcpObjs.ownersCount);
}


// Admin's Services

// htmlAdminContentServices()
// fetchAdminContentServices()
// -> fillAdminContentServices()

function htmlAdminContentServices() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > Services</p>");

    var html = "";
    html += "<div>";
    html += "<a href='javascript:showAdminContentService(true)'>Refresh</a>";
    html += "</div>";

    html += "<h2>JCP APIs's Servics</h2>";
    html += "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Service count</td><td id='val_adm_srvs_count' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Service Online/Offline count</td><td><span id='val_adm_srvs_online_count' class='value'>...</span> / <span id='val_adm_srvs_offline_count' class='value'>...</span></td></tr>";
    html += "<tr><td class='label'>Instances count</td><td id='val_adm_insts_count' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Instances Online/Offline count</td><td><span id='val_adm_insts_online_count' class='value'>...</span> / <span id='val_adm_insts_offline_count' class='value'>...</span></td></tr>";

    html += "</table>";
    html += "</div>";
    return html;

    return html;
}

function fetchAdminContentServices(objId) {
    apiGET(backEndUrl,pathJSLWB_Admin_APIs_Srvs,fillAdminContentServices,onErrorFetch);
}

function fillAdminContentServices(jcpObjsJson) {
    jcpObjs = JSON.parse(jcpObjsJson);
    replaceInnerHTMLById("val_adm_srvs_count",jcpObjs.count);
    replaceInnerHTMLById("val_adm_srvs_online_count",jcpObjs.onlineCount);
    replaceInnerHTMLById("val_adm_srvs_offline_count",jcpObjs.offlineCount);
    replaceInnerHTMLById("val_adm_insts_count",jcpObjs.instancesCount);
    replaceInnerHTMLById("val_adm_insts_online_count",jcpObjs.instancesOnlineCount);
    replaceInnerHTMLById("val_adm_insts_offline_count",jcpObjs.instancesOfflineCount);
}


// Admin's Users

// htmlAdminContentUsers()
// fetchAdminContentUsers()
// -> fillAdminContentUsers()

function htmlAdminContentUsers() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > Users</p>");

    var html = "";
    html += "<div>";
    html += "<a href='javascript:showAdminContentUsers(true)'>Refresh</a>";
    html += "</div>";

    html += "<h2>JCP APIs's Users</h2>";
    html += "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Users count</td><td id='val_adm_usrs_count' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";
    return html;

    return html;
}

function fetchAdminContentUsers(objId) {
    apiGET(backEndUrl,pathJSLWB_Admin_APIs_Usrs,fillAdminContentUsers,onErrorFetch);
}

function fillAdminContentUsers(jcpObjsJson) {
    jcpObjs = JSON.parse(jcpObjsJson);
    replaceInnerHTMLById("val_adm_usrs_count",jcpObjs.count);
}



// Utils

function reduce(info,prop) {
    var tmp = info[prop];
    //var tmpHTML = "<span style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 1px;'>";
    var tmpHTML = "";//"<span style='width: 100%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; line-height: 1em;'>";
    tmpHTML += JSON.stringify(tmp);
    //tmpHTML += "</span>";
    info[prop] = tmpHTML;
}
