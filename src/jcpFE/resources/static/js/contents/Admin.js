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
// showAdminContentSystem()                     *2
// showAdminContentSystemJSL()                  *3
// showAdminContentAPIs()                       *4
// showAdminContentGWs()                        *5
// showAdminContentObjects()                    *6
// showAdminContentServices()                   *7
// showAdminContentUsers()                      *8
// -> htmlAdminContentUnauthorized()
// -> htmlAdminContent()
// -> fetchAdminContentHeader()
//   -> fillAdminContentHeader()
// -> fetchAdminContent() ...                   *1
// -> htmlAdminContentSystem()                  *2
// -> fetchAdminContentSystem() ...             *2
// -> htmlAdminContentSystemJSL()               *3
// -> fetchAdminContentSystemJSL() ...          *3
// -> htmlAdminContentAPIs()                    *4
// -> fetchAdminContentAPIs() ...               *4
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

function showAdminContentSystem(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_SYSTEM, PAGE_ADMIN_SYSTEM_TITLE);

    currentPage = PAGE_ADMIN_SYSTEM;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    if (document.getElementById("div_admin_content") != null)
        document.getElementById("div_admin_content").innerHTML = htmlAdminContentSystem();

    fetchAdminContentSystem();
}

function showAdminContentSystemJSL(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_SYSTEM_JSL, PAGE_ADMIN_SYSTEM_JSL_TITLE);

    currentPage = PAGE_ADMIN_SYSTEM_JSL;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    if (document.getElementById("div_admin_content") != null)
        document.getElementById("div_admin_content").innerHTML = htmlAdminContentSystemJSL();

    fetchAdminContentSystemJSL();
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
    if (document.getElementById("div_admin_content") != null)
        document.getElementById("div_admin_content").innerHTML = htmlAdminContentObjects();

    fetchAdminContentObjects();
}

function showAdminContentAPIs(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_APIS, PAGE_ADMIN_APIS_TITLE);

    currentPage = PAGE_ADMIN_APIS;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    if (document.getElementById("div_admin_content") != null)
        document.getElementById("div_admin_content").innerHTML = htmlAdminContentAPIs();

    fetchAdminContentAPIs();
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
    if (document.getElementById("div_admin_content") != null)
        document.getElementById("div_admin_content").innerHTML = htmlAdminContentGWs();

    fetchAdminContentGWs();
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
    if (document.getElementById("div_admin_content") != null)
        document.getElementById("div_admin_content").innerHTML = htmlAdminContentServices();

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
    if (document.getElementById("div_admin_content") != null)
        document.getElementById("div_admin_content").innerHTML = htmlAdminContentUsers();

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
    html += "    <div id='div_admin_stats_jcpfe' style='margin-bottom: 5px;'>";
    html += "        <p><b>JCP FE Server</b><br>Started at <b id='val_jcpfe_timeStart'>...</b></p>";
    html += "        <p>Using <b id='val_jcpfe_cpuUsed'>...</b>% CPU and memory <b id='val_jcpfe_memoryUsed'>...</b>MB</p>";
    html += "        <p>Working on <b id='val_jcpfe_sessionsJslCount'>...</b> / <b id='val_jcpfe_sessionsCount'>...</b> (jsl/http) sessions</p>";
    html += "    </div>";
    html += "    <div id='div_admin_stats_jcpapis' style='margin-bottom: 5px;'>";
    html += "        <p><b>JCP APIs Server</b><br>Started at <b id='val_jcpapis_timeStart'>...</b></p>";
    html += "        <p>Using <b id='val_jcpapis_cpuUsed'>...</b>% CPU and memory <b id='val_jcpapis_memoryUsed'>...</b>MB</p>";
    html += "    </div>";
    html += "    </div>";

    html += "    <hr style='margin-bottom: 20px;'>";

    html += "    <div id='div_admin_menu'>";
    html += "        <ul style='list-style-type: none; margin: 20px; padding: 0; display: flex; justify-content: space-around;'>";
    html += "            <li><a href='javascript:showAdminContent(true)'>Home</a></li>";
    html += "            <li><a href='javascript:showAdminContentSystem(true)'>System</a></li>";
    html += "            <li><a href='javascript:showAdminContentAPIs(true)'>JCP APIs</a></li>";
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
    //apiGET("/apis/CloudStatus/1.0/",fillAdminContent,onErrorFetch);
//    apiGET("/apis/CloudStatus/1.0/state/process",function(o){},onErrorFetch);
//    apiGET("/apis/CloudStatus/1.0/state/java",function(o){},onErrorFetch);
//    apiGET("/apis/CloudStatus/1.0/state/os",function(o){},onErrorFetch);
//    apiGET("/apis/CloudStatus/1.0/state/cpu",function(o){},onErrorFetch);
//    apiGET("/apis/CloudStatus/1.0/state/memory",function(o){},onErrorFetch);
//    apiGET("/apis/CloudStatus/1.0/state/disks",function(o){},onErrorFetch);
//    apiGET("/apis/CloudStatus/1.0/state/network",function(o){},onErrorFetch);
//    apiGET("/apis/CloudStatus/1.0/state/jcpfe",function(o){},onErrorFetch);
//    apiGET("/apis/CloudStatus/1.0/state/jcpfe/instances",function(o){},onErrorFetch);
}

function fetchAdminContentHeader() {
    apiGET("/apis/CloudStatus/1.0/state/jcpfe",fillAdminContentHeaderFE,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/jcpapis",fillAdminContentHeaderAPIs,onErrorFetch);
}

function fillAdminContent(adminJson) {
    admin = JSON.parse(adminJson);

    if (document.getElementById("div_admin_title") != null)
        document.getElementById("div_admin_title").innerHTML = htmlObjectContentTitle(obj);
}

function fillAdminContentHeaderFE(stateJCPFEJson) {
    stateJCPFE = JSON.parse(stateJCPFEJson);
    if (document.getElementById("val_jcpfe_timeStart") != null)
        document.getElementById("val_jcpfe_timeStart").innerHTML = dateToString(stringToDate(stateJCPFE.timeStart));
    if (document.getElementById("val_jcpfe_timeRunning") != null)
        document.getElementById("val_jcpfe_timeRunning").innerHTML = stateJCPFE.timeRunning;
    if (document.getElementById("val_jcpfe_cpuUsed") != null)
        document.getElementById("val_jcpfe_cpuUsed").innerHTML = doubleTruncateDigit(stateJCPFE.cpuUsed,2);
    if (document.getElementById("val_jcpfe_memoryUsed") != null)
        document.getElementById("val_jcpfe_memoryUsed").innerHTML = doubleTruncateDigit(stateJCPFE.memoryUsed,2);
    if (document.getElementById("val_jcpfe_sessionsCount") != null)
        document.getElementById("val_jcpfe_sessionsCount").innerHTML = stateJCPFE.sessionsCount;
    if (document.getElementById("val_jcpfe_sessionsJslCount") != null)
        document.getElementById("val_jcpfe_sessionsJslCount").innerHTML = stateJCPFE.sessionsJslCount;
}

function fillAdminContentHeaderAPIs(stateJCPAPIsJson) {
    stateJCPAPIs = JSON.parse(stateJCPAPIsJson);
    if (document.getElementById("val_jcpapis_timeStart") != null)
        document.getElementById("val_jcpapis_timeStart").innerHTML = dateToString(stringToDate(stateJCPAPIs.timeStart));
    if (document.getElementById("val_jcpapis_timeRunning") != null)
        document.getElementById("val_jcpapis_timeRunning").innerHTML = stateJCPAPIs.timeRunning;
    if (document.getElementById("val_jcpapis_cpuUsed") != null)
        document.getElementById("val_jcpapis_cpuUsed").innerHTML = doubleTruncateDigit(stateJCPAPIs.cpuUsed,2);
    if (document.getElementById("val_jcpapis_memoryUsed") != null)
        document.getElementById("val_jcpapis_memoryUsed").innerHTML = doubleTruncateDigit(stateJCPAPIs.memoryUsed,2);
//    if (document.getElementById("val_jcpfe_sessionsCount") != null)
//        document.getElementById("val_jcpfe_sessionsCount").innerHTML = stateJCPFE.sessionsCount;
//    if (document.getElementById("val_jcpfe_sessionsJslCount") != null)
//        document.getElementById("val_jcpfe_sessionsJslCount").innerHTML = stateJCPFE.sessionsJslCount;
}


// Admin's System

// htmlAdminContentSystem()
// fetchAdminContentSystem()
// -> fillAdminContentSystem_JCPFE()
// -> fillAdminContentSystem_Java()
// -> fillAdminContentSystem_Process()
// -> fillAdminContentSystem_Os()
// -> fillAdminContentSystem_CPU()
// -> fillAdminContentSystem_Memory()
// -> fillAdminContentSystem_Disks()
// -> fillAdminContentSystem_Network()

function htmlAdminContentSystem() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > System</p>");

    var html = "";
    html += "<div>";
    html += "<a href='javascript:showAdminContentSystem(true)'>Refresh</a> or ";
    html += "<a href='javascript:showAdminContentSystemJSL(true)'>List all JSL instances</a>";
    html += "</div>";

    html += "<h2>JCP FrontEnd</h2>";
    html += "<div id='div_admin_jcpfe' style='width: 70%; margin: auto;'></div>";

    html += "<h2>Java</h2>";
    html += "<div id='div_admin_java' style='width: 70%; margin: auto;'></div>";

    html += "<h2>Process</h2>";
    html += "<div id='div_admin_process' style='width: 70%; margin: auto;'></div>";

    html += "<h2>OS</h2>";
    html += "<div id='div_admin_os' style='width: 70%; margin: auto;'></div>";

    html += "<hr>";

    html += "<h2>CPU</h2>";
    html += "<div id='div_admin_cpu' style='width: 70%; margin: auto;'></div>";

    html += "<h2>Memory</h2>";
    html += "<div id='div_admin_memory' style='width: 70%; margin: auto;'></div>";

    html += "<h2>Disks</h2>";
    html += "<div id='div_admin_disks' style='width: 70%; margin: auto;'></div>";

    html += "<hr>";

    html += "<h2>Network</h2>";
    html += "<div id='div_admin_network' style='width: 70%; margin: auto;'></div>";


    return html;
}

function fetchAdminContentSystem(objId) {
    apiGET("/apis/CloudStatus/1.0/state/jcpfe",fillAdminContentSystem_JCPFE,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/java",fillAdminContentSystem_Java,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/process",fillAdminContentSystem_Process,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/os",fillAdminContentSystem_Os,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/cpu",fillAdminContentSystem_CPU,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/memory",fillAdminContentSystem_Memory,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/disks",fillAdminContentSystem_Disks,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/network",fillAdminContentSystem_Network,onErrorFetch);
}

function fillAdminContentSystem_JCPFE(infoJson) {
    if (document.getElementById("div_admin_jcpfe") != null)
        document.getElementById("div_admin_jcpfe").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentSystem_Java(infoJson) {
    info = JSON.parse(infoJson);
    reduce(info,"runtimeSystemProps");
    reduce(info,"runtimeInputArgs");
    reduce(info,"runtimePathClass");
    reduce(info,"runtimePathBootClass");
    reduce(info,"runtimePathLibrary");
    reduce(info,"threadsIds");
    reduce(info,"threads");

    if (document.getElementById("div_admin_java") != null)
        document.getElementById("div_admin_java").innerHTML = jsonToHTMLTable(JSON.stringify(info));
}

function fillAdminContentSystem_Process(infoJson) {
    if (document.getElementById("div_admin_process") != null)
        document.getElementById("div_admin_process").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentSystem_Os(infoJson) {
    if (document.getElementById("div_admin_os") != null)
        document.getElementById("div_admin_os").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentSystem_CPU(infoJson) {
    if (document.getElementById("div_admin_cpu") != null)
        document.getElementById("div_admin_cpu").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentSystem_Memory(infoJson) {
    if (document.getElementById("div_admin_memory") != null)
        document.getElementById("div_admin_memory").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentSystem_Disks(infoJson) {
    if (document.getElementById("div_admin_disks") != null)
        document.getElementById("div_admin_disks").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentSystem_Network(infoJson) {
    info = JSON.parse(infoJson);
    reduce(info,"listInterfaces");

    if (document.getElementById("div_admin_network") != null)
        document.getElementById("div_admin_network").innerHTML = jsonToHTMLTable(JSON.stringify(info));
}


// Admin's System JSL

// htmlAdminContentSystemJSL()
// fetchAdminContentSystemJSL()
// -> fillAdminContentSystemJSL()

function htmlAdminContentSystemJSL() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > <a href='javascript:void(0);' onclick='showAdminContentSystem(true)'>System</a> > JSL Instances</p>");

    var html = "";
    html += "<div>";
    html += "<a href='javascript:showAdminContentSystem(true)'>Go back</a> or ";
    html += "<a href='javascript:showAdminContentSystemJSL(true)'>Refresh</a>";
    html += "</div>";

    html += "<div id='div_admin_jsl_instances' style='width: 70%; margin: auto;'></div>";

    return html;
}

function fetchAdminContentSystemJSL(objId) {
    apiGET("/apis/CloudStatus/1.0/state/jcpfe/instances",fillAdminContentSystemJSL,onErrorFetch);
}

function fillAdminContentSystemJSL(jslInstancesJson) {
    jslInstances = JSON.parse(jslInstancesJson);

    var users = [];
    var usersCount = 0;
    users['00000-00000-00000'] = { id: "00000-00000-00000", name: "Anonymous", sessions: 0};
    for (var i=0; i<jslInstances.length; i++) {
        if (users[jslInstances[i].usrId]==null) {
            users[jslInstances[i].usrId] = { id: jslInstances[i].usrId, name: jslInstances[i].usrName, sessions: 1};
            usersCount++;
        } else
            users[jslInstances[i].usrId].sessions++;
    }

    var html = "";

    html += "<p>Currently online authenticated users " + usersCount + "<br>";
    html += "and anonymous sessions " + users['00000-00000-00000'].sessions;

    html += "<table class='table_details' style='margin-top: 5px; margin-bottom: 5px; width: 60%; min-width: 300px;'>";
    for (const property in users) {
        html += "<tr>"
        html += "<td class='label' style='border: 1px solid black;'>" + users[property].name + "</td>";
        html += "<td class='value' style='border: 1px solid black;'>" + users[property].id + "</td>";
        html += "<td class='value' style='border: 1px solid black;'>" + users[property].sessions + "</td>";
        html += "</tr>"
    }
    html += "</table>";

    for (var i=0; i<jslInstances.length; i++) {
        html += "<h2>" + jslInstances[i].usrName + " / " + jslInstances[i].instId + "</h2>";
        html += jsonToHTMLTable(jslInstances[i]);
    }

    if (document.getElementById("div_admin_jsl_instances") != null)
        document.getElementById("div_admin_jsl_instances").innerHTML = html;
}


// Admin's JCP APIs

// htmlAdminContentAPIs()
// fetchAdminContentAPIs()
// -> fillAdminContentAPIs_JCPAPIs()
// -> fillAdminContentAPIs_Java()
// -> fillAdminContentAPIs_Process()
// -> fillAdminContentAPIs_Os()
// -> fillAdminContentAPIs_CPU()
// -> fillAdminContentAPIs_Memory()
// -> fillAdminContentAPIs_Disks()
// -> fillAdminContentAPIs_Network()

function htmlAdminContentAPIs() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > JCP APIs</p>");

    var html = "";
    html += "<div>";
    html += "<a href='javascript:showAdminContentAPIs(true)'>Refresh</a>";
    html += "</div>";

    html += "<h2>JCP APIs</h2>";
    html += "<div id='div_admin_jcpapis' style='width: 70%; margin: auto;'></div>";

    html += "<h2>Java</h2>";
    html += "<div id='div_admin_java' style='width: 70%; margin: auto;'></div>";

    html += "<h2>Process</h2>";
    html += "<div id='div_admin_process' style='width: 70%; margin: auto;'></div>";

    html += "<h2>OS</h2>";
    html += "<div id='div_admin_os' style='width: 70%; margin: auto;'></div>";

    html += "<hr>";

    html += "<h2>CPU</h2>";
    html += "<div id='div_admin_cpu' style='width: 70%; margin: auto;'></div>";

    html += "<h2>Memory</h2>";
    html += "<div id='div_admin_memory' style='width: 70%; margin: auto;'></div>";

    html += "<h2>Disks</h2>";
    html += "<div id='div_admin_disks' style='width: 70%; margin: auto;'></div>";

    html += "<hr>";

    html += "<h2>Network</h2>";
    html += "<div id='div_admin_network' style='width: 70%; margin: auto;'></div>";

    return html;
}

function fetchAdminContentAPIs(objId) {
    apiGET("/apis/CloudStatus/1.0/state/jcpapis/",fillAdminContentAPIs_JCPAPIs,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/jcpapis/java",fillAdminContentAPIs_Java,onErrorFetch);
    //apiGET("/apis/CloudStatus/1.0/state/jcpapis/java/threads",fillAdminContentAPIs_Java,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/jcpapis/process",fillAdminContentAPIs_Process,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/jcpapis/os",fillAdminContentAPIs_Os,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/jcpapis/cpu",fillAdminContentAPIs_CPU,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/jcpapis/memory",fillAdminContentAPIs_Memory,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/jcpapis/disks",fillAdminContentAPIs_Disks,onErrorFetch);
    apiGET("/apis/CloudStatus/1.0/state/jcpapis/network",fillAdminContentSystem_Network,onErrorFetch);
    //apiGET("/apis/CloudStatus/1.0/state/jcpapis/network/intfs",fillAdminContentSystem_Network,onErrorFetch);
}

function fillAdminContentAPIs_JCPAPIs(infoJson) {
    if (document.getElementById("div_admin_jcpapis") != null)
        document.getElementById("div_admin_jcpapis").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentAPIs_Java(infoJson) {
    info = JSON.parse(infoJson);
    reduce(info,"runtimeSystemProps");
    reduce(info,"runtimeInputArgs");
    reduce(info,"runtimePathClass");
    reduce(info,"runtimePathBootClass");
    reduce(info,"runtimePathLibrary");
    reduce(info,"threadsIds");
    reduce(info,"threads");

    if (document.getElementById("div_admin_java") != null)
        document.getElementById("div_admin_java").innerHTML = jsonToHTMLTable(JSON.stringify(info));
}

function fillAdminContentAPIs_Process(infoJson) {
    if (document.getElementById("div_admin_process") != null)
        document.getElementById("div_admin_process").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentAPIs_Os(infoJson) {
    if (document.getElementById("div_admin_os") != null)
        document.getElementById("div_admin_os").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentAPIs_CPU(infoJson) {
    if (document.getElementById("div_admin_cpu") != null)
        document.getElementById("div_admin_cpu").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentAPIs_Memory(infoJson) {
    if (document.getElementById("div_admin_memory") != null)
        document.getElementById("div_admin_memory").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentAPIs_Disks(infoJson) {
    if (document.getElementById("div_admin_disks") != null)
        document.getElementById("div_admin_disks").innerHTML = jsonToHTMLTable(infoJson);
}

function fillAdminContentAPIs_Network(infoJson) {
    info = JSON.parse(infoJson);
    reduce(info,"listInterfaces");

    if (document.getElementById("div_admin_network") != null)
        document.getElementById("div_admin_network").innerHTML = jsonToHTMLTable(JSON.stringify(info));
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
    apiGET("/apis/CloudStatus/1.0/mngm/gws",fillAdminContentGWs,onErrorFetch);
}

function fillAdminContentGWs(jcpGWsJson) {
    jcpGWs = JSON.parse(jcpGWsJson);
    var html = "";
    for (var i=0; i<jcpGWs.length; i++)
        html += gwToHTML(jcpGWs[i]);

    if (document.getElementById("val_adm_gws") != null)
        document.getElementById("val_adm_gws").innerHTML = html;
}

function gwToHTML(gw) {
    var grayStyle = "color: gray;";

    var html = "";
    html += "<h3>" + gw.id + "<span style='" + grayStyle + "'>(" + gw.type + ")</h3>";
    html += "<p>Server is " + (gw.isRunning ? "running" : "NOT running") + " with " + gw.clientsCount + " clients connected</p>";
    html += "<p><span style='" + grayStyle + "'>(Addr: " + gw.address + "; HostName: " + gw.hostName + "; CanonicalHostName: " + gw.hostNameCanonical + ")</span></p>";

    if (gw.clientsCount==0) {
        html += "<p>No clients connected</p>";
    } else {
        html += "<table style='border: 1px solid black; margin: 10px;'>";
        html += "<tr><th style='border: 1px solid black;'>Client ID</th><th style='border: 1px solid black;'>Status</th><tr>";
        for (var i=0; i<gw.clientsList.length; i++) {
            html += "<tr><td style='border: 1px solid black;'>" + gw.clientsList[i].id + "</td><td style='border: 1px solid black;'>" + (gw.clientsList[i].isConnected ? "CONNECTED" : "NOT CONN.") + "</td><tr>";
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
    apiGET("/apis/CloudStatus/1.0/mngm/objects",fillAdminContentObjects,onErrorFetch);
}

function fillAdminContentObjects(jcpObjsJson) {
    jcpObjs = JSON.parse(jcpObjsJson);
    if (document.getElementById("val_adm_objs_count") != null)
        document.getElementById("val_adm_objs_count").innerHTML = jcpObjs.objectsCount;
    if (document.getElementById("val_adm_objs_online_count") != null)
        document.getElementById("val_adm_objs_online_count").innerHTML = jcpObjs.objectsOnlineCount;
    if (document.getElementById("val_adm_objs_offline_count") != null)
        document.getElementById("val_adm_objs_offline_count").innerHTML = jcpObjs.objectsOfflineCount;
    if (document.getElementById("val_adm_objs_active_count") != null)
        document.getElementById("val_adm_objs_active_count").innerHTML = jcpObjs.objectsActiveCount;
    if (document.getElementById("val_adm_objs_inactive_count") != null)
        document.getElementById("val_adm_objs_inactive_count").innerHTML = jcpObjs.objectsInactiveCount;
    if (document.getElementById("val_adm_objs_owners_count") != null)
        document.getElementById("val_adm_objs_owners_count").innerHTML = jcpObjs.objectsOwnersCount;
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
    apiGET("/apis/CloudStatus/1.0/mngm/services",fillAdminContentServices,onErrorFetch);
}

function fillAdminContentServices(jcpObjsJson) {
    jcpObjs = JSON.parse(jcpObjsJson);
    if (document.getElementById("val_adm_srvs_count") != null)
        document.getElementById("val_adm_srvs_count").innerHTML = jcpObjs.servicesCount;
    if (document.getElementById("val_adm_srvs_online_count") != null)
        document.getElementById("val_adm_srvs_online_count").innerHTML = jcpObjs.servicesOnlineCount;
    if (document.getElementById("val_adm_srvs_offline_count") != null)
        document.getElementById("val_adm_srvs_offline_count").innerHTML = jcpObjs.servicesOfflineCount;
    if (document.getElementById("val_adm_insts_count") != null)
        document.getElementById("val_adm_insts_count").innerHTML = jcpObjs.servicesInstancesCount;
    if (document.getElementById("val_adm_insts_online_count") != null)
        document.getElementById("val_adm_insts_online_count").innerHTML = jcpObjs.servicesInstancesOnlineCount;
    if (document.getElementById("val_adm_insts_offline_count") != null)
        document.getElementById("val_adm_insts_offline_count").innerHTML = jcpObjs.servicesInstancesOfflineCount;
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
    apiGET("/apis/CloudStatus/1.0/mngm/users",fillAdminContentUsers,onErrorFetch);
}

function fillAdminContentUsers(jcpObjsJson) {
    jcpObjs = JSON.parse(jcpObjsJson);
    if (document.getElementById("val_adm_usrs_count") != null)
        document.getElementById("val_adm_usrs_count").innerHTML = jcpObjs.usersCount;
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
