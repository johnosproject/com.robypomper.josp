const PAGE_ADMIN_HOME = "Admin";
const PAGE_ADMIN_HOME_TITLE = "Admin";

const PAGE_ADMIN_BUILD_INFO = "admBuild";
const PAGE_ADMIN_BUILD_INFO_TITLE = "Admin > JCP {service} > Build Info";
const PAGE_ADMIN_EXEC = "admExec";
const PAGE_ADMIN_EXEC_TITLE = "Admin > JCP {service} > Executable";

const PAGE_ADMIN_APIS = "admAPIs";
const PAGE_ADMIN_APIS_TITLE = "Admin > JCP APIs";
const PAGE_ADMIN_GWS = "admGWs";
const PAGE_ADMIN_GWS_TITLE = "Admin > JCP GWs";
const PAGE_ADMIN_JSLWB = "admJSLWB";
const PAGE_ADMIN_JSLWB_TITLE = "Admin > JCP JSL Web Bridge";
const PAGE_ADMIN_FE = "admFE";
const PAGE_ADMIN_FE_TITLE = "Admin > JCP Front End";
const PAGE_ADMIN_OBJECTS = "admObjs";
const PAGE_ADMIN_OBJECTS_TITLE = "Admin > Objects";
const PAGE_ADMIN_SERVICES = "admSrvs";
const PAGE_ADMIN_SERVICES_TITLE = "Admin > Services";
const PAGE_ADMIN_USER = "admUsrs";
const PAGE_ADMIN_USER_TITLE = "Admin > Users";


// showAdminContent()                           *1
// showAdminContentAPIs()                       *2
// showAdminContentGWs()                        *3
// showAdminContentJSLWB()                      *4
// showAdminContentFE()                         *5
// showAdminContentObjects()                    *6
// showAdminContentServices()                   *7
// showAdminContentUsers()                      *8
// -> htmlAdminContentUnauthorized()
// -> htmlAdminContent()
// -> fetchAdminContentHeader()
//   -> fillAdminContentHeader()
// -> fetchAdminContent() ...                   *1
// -> htmlAdminContentAPIs()                    *2
// -> fetchAdminContentAPIs() ...               *2
// -> htmlAdminContentGWs()                     *3
// -> fetchAdminContentGWs() ...                *4
// -> htmlAdminContentJSLWB()                   *5
// -> fetchAdminContentJSLWB() ...              *5
// -> htmlAdminContentFE()                      *5
// -> fetchAdminContentFE() ...                 *5
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
    replaceInnerHTMLById("div_admin_content",htmlAdminContentAPIs());

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
    replaceInnerHTMLById("div_admin_content",htmlAdminContentGWs());

    fetchAdminContentGWs();
}

function showAdminContentJSLWB(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_JSLWB, PAGE_ADMIN_JSLWB_TITLE);

    currentPage = PAGE_ADMIN_JSLWB;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    replaceInnerHTMLById("div_admin_content",htmlAdminContentJSLWB());

    fetchAdminContentJSLWB();
}

function showAdminContentFE(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_FE, PAGE_ADMIN_FE_TITLE);

    currentPage = PAGE_ADMIN_FE;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    replaceInnerHTMLById("div_admin_content",htmlAdminContentFE());

    fetchAdminContentFE();
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
    html += "            <li><a href='javascript:showAdminContentObjects(true)'>Objects</a></li>";
    html += "            <li><a href='javascript:showAdminContentServices(true)'>Services</a></li>";
    html += "            <li><a href='javascript:showAdminContentUsers(true)'>User</a></li>";
    html += "        </ul>";
    html += "        <ul style='list-style-type: none; margin: 20px; padding: 0; display: flex; justify-content: space-around;'>";
    html += "            <li><a href='javascript:showAdminContentAPIs(true)'>JCP APIs</a></li>";
    html += "            <li><a href='javascript:showAdminContentGWs(true)'>JCP GWs</a></li>";
    html += "            <li><a href='javascript:showAdminContentJSLWB(true)'>JCP JSL Web bridge</a></li>";
    html += "            <li><a href='javascript:showAdminContentFE(true)'>JCP Front End</a></li>";
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
    apiGET(backEndUrl,pathJSLWB_Admin_BI.replace("{service}", "apis"),fillAdminContentHeaderAPIs,onErrorFetch);
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


// Admin's Build Info

// showJCPServiceBuildInfo()
// htmlJCPServiceBuildInfo()
// fetchJCPServiceBuildInfo()
// -> fillJCPServiceBuildInfo()

function showJCPServiceBuildInfo(updateHistory,service) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_BUILD_INFO + service, PAGE_ADMIN_BUILD_INFO_TITLE.replace("{service}", service));

    currentPage = PAGE_ADMIN_BUILD_INFO + service;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    replaceInnerHTMLById("div_admin_content",htmlJCPServiceBuildInfo(service));

    fetchJCPServiceBuildInfo(service);
}

function htmlJCPServiceBuildInfo(service) {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > <a href='javascript:void(0);' onclick='showAdminContent" + service + "(true)'>JOSP " + service + "</a> > Build Info</p>");

    var html = "";
    html += "<div style='display: flex; margin: 20px; justify-content: space-around;'>";
    html += "<a href='javascript:showAdminContent" + service + "(true)'>Up</a>";
    html += "<a href='javascript:showJCPServiceBuildInfo(true,\"" + service + "\")'>Refresh</a>";
    html += "<a href='javascript:showJCPServiceExecutable(true,\"" + service + "\")'>Executable</a>";
    html += "</div>";

    html += "<h2>JCP " + service + "'s Build Info</h2>";
    html += "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Project</td><td id='val_adm_bi_project' class='value'>...</td></tr>";
    html += "<tr><td class='label'>SourceSet</td><td id='val_adm_bi_sourceset' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Version</td><td id='val_adm_bi_version' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Version Build</td><td id='val_adm_bi_version_build' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Build Time</td><td id='val_adm_bi_build_time' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Java Version</td><td id='val_adm_bi_java_version' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Java Home</td><td id='val_adm_bi_java_home' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Gradle Version</td><td id='val_adm_bi_gradle_version' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Git Commit</td><td id='val_adm_bi_git_commit' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Git Commit Short</td><td id='val_adm_bi_git_commit_short' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Git Branch</td><td id='val_adm_bi_git_branch' class='value'>...</td></tr>";
    html += "<tr><td class='label'>User</td><td id='val_adm_bi_user' class='value'>...</td></tr>";
    html += "<tr><td class='label'>OS Name</td><td id='val_adm_bi_os_name' class='value'>...</td></tr>";
    html += "<tr><td class='label'>OS Version</td><td id='val_adm_bi_os_version' class='value'>...</td></tr>";

    html += "</table>";


    html += "<h2>JCP " + service + "'s Extra</h2>";
    html += "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<div id='val_adm_bi_extra' style='width: 70%; margin: auto;' />";
    html += "</div>";

    html += "</div>";
    return html;
}

function fetchJCPServiceBuildInfo(service) {
    apiGET(backEndUrl,pathJSLWB_Admin_BI.replace("{service}", service.toLowerCase()),fillJCPServiceBuildInfo,onErrorFetch);
}

function fillJCPServiceBuildInfo(jcpBuildInfoJson) {
    var jcpBuildInfoAll = JSON.parse(jcpBuildInfoJson);
    if (jcpBuildInfoAll!=null && jcpBuildInfoAll instanceof Array)
        jcpBuildInfoAll = jcpBuildInfoAll[0];
    var jcpBuildInfo = jcpBuildInfoAll.buildInfo;
    replaceInnerHTMLById("val_adm_bi_project",jcpBuildInfo.Project);
    replaceInnerHTMLById("val_adm_bi_sourceset",jcpBuildInfo.SourceSet);
    replaceInnerHTMLById("val_adm_bi_version",jcpBuildInfo.Version);
    replaceInnerHTMLById("val_adm_bi_version_build",jcpBuildInfo.VersionBuild);
    replaceInnerHTMLById("val_adm_bi_build_time",jcpBuildInfo.BuildTime);
    replaceInnerHTMLById("val_adm_bi_java_version",jcpBuildInfo.JavaVersion);
    replaceInnerHTMLById("val_adm_bi_java_home",stringTruncateAndCopy(jcpBuildInfo.JavaHome));
    replaceInnerHTMLById("val_adm_bi_gradle_version",jcpBuildInfo.GradleVersion);
    replaceInnerHTMLById("val_adm_bi_git_commit",stringTruncateAndCopy(jcpBuildInfo.GitCommit));
    replaceInnerHTMLById("val_adm_bi_git_commit_short",jcpBuildInfo.GitCommitShort);
    replaceInnerHTMLById("val_adm_bi_git_branch",jcpBuildInfo.GitBranch);
    replaceInnerHTMLById("val_adm_bi_user",jcpBuildInfo.User);
    replaceInnerHTMLById("val_adm_bi_os_name",jcpBuildInfo.OSName);
    replaceInnerHTMLById("val_adm_bi_os_version",jcpBuildInfo.OSVersion);

    var biExtraTable = "";
    if (typeof jcpBuildInfo.Extra != "undefined") {
        for (var i=0; i<jcpBuildInfo.Extra.length; i++)
            biExtraTable += "<tr><td class='label'>Extra: " + jcpBuildInfo.Extra[i].key + "</td><td class='value'>" + jcpBuildInfo.Extra[i].value + "</td></tr>";
    } else
        biExtraTable += "<tr><td class='label'>Extra: -</td><td class='value'>-</td></tr>";
    replaceInnerHTMLById("val_adm_bi_extra",biExtraTable);
}


// Admin's Executable

// showJCPServiceExecutable()
// htmlJCPServiceExecutable()
// fetchJCPServiceExecutable()
// -> fillJCPServiceExecutableOnline()
// -> fillJCPServiceExecutableCpu()
// -> fillJCPServiceExecutableDisks()
// -> fillJCPServiceExecutableJava()
// -> fillJCPServiceExecutableJavaThreads()
// -> fillJCPServiceExecutableMemory()
// -> fillJCPServiceExecutableNetwork()
// -> fillJCPServiceExecutableNetworkIntfs()
// -> fillJCPServiceExecutableOs()
// -> fillJCPServiceExecutableProcess()

function showJCPServiceExecutable(updateHistory,service) {
    if (updateHistory) setNewPageHistory(PAGE_ADMIN_EXEC + service, PAGE_ADMIN_EXEC_TITLE.replace("{service}", service));

    currentPage = PAGE_ADMIN_EXEC + service;
    if (!loggedUser_isAdmin) {
        setContent(htmlAdminContentUnauthorized());
        return;
    }
    if (document.getElementById("div_admin_content") == null) {
        setContent(htmlAdminContent());
        fetchAdminContentHeader();
    }
    replaceInnerHTMLById("div_admin_content",htmlJCPServiceExecutable(service));

    fetchJCPServiceExecutable(service);
}

function htmlJCPServiceExecutable(service) {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > <a href='javascript:void(0);' onclick='showAdminContent" + service + "(true)'>JOSP " + service + "</a> > Executable</p>");

    var html = "";
    html += "<div style='display: flex; margin: 20px; justify-content: space-around;'>";
    html += "<a href='javascript:showAdminContent" + service + "(true)'>Up</a>";
    html += "<a href='javascript:showJCPServiceBuildInfo(true,\"" + service + "\")'>Build Info</a>";
    html += "<a href='javascript:showJCPServiceExecutable(true,\"" + service + "\")'>Refresh</a>";
    html += "</div>";

    html += "<h2>JCP " + service + "'s Executable</h2>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Online</td><td id='val_adm_exec_online' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";

    html += "<h2>JCP " + service + "'s CPU</h2>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Count (CPU)</td><td id='val_adm_exec_cpu_count' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Load Avg (%)</td><td id='val_adm_exec_cpu_load_avg' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";

    html += "<h2>JCP " + service + "'s Disks</h2>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<div id='val_adm_exec_disks' style='width: 70%; margin: auto;' />";
    html += "</div>";

    html += "</table>";
    html += "</div>";

    html += "<h2>JCP " + service + "'s Java</h2>";
    html += "<h3>Java VirtualMachine</h3>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>VM Name</td><td id='val_adm_exec_java_vm_vm_name' class='value'>...</td></tr>";
    html += "<tr><td class='label'>VM Version</td><td id='val_adm_exec_java_vm_vm_version' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Spec Name</td><td id='val_adm_exec_java_vm_spec_name' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Spec Vendor</td><td id='val_adm_exec_java_vm_spec_vendor' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Spec Version</td><td id='val_adm_exec_java_vm_spec_version' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Spec Mngm Version</td><td id='val_adm_exec_java_vm_spec_mngm_version' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";

    html += "<h3>Java Runtime</h3>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>System props</td><td id='val_adm_exec_java_runtime_runtime_system_props' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Input args</td><td id='val_adm_exec_java_runtime_runtime_input_args' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Path Class</td><td id='val_adm_exec_java_runtime_runtime_path_class' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Path Boot Class</td><td id='val_adm_exec_java_runtime_runtime_path_boot_class' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Path Library</td><td id='val_adm_exec_java_runtime_runtime_path_library' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";

    html += "<h3>Java Timing</h3>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Time Start</td><td id='val_adm_exec_java_timing_time_start' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Time running (seconds)</td><td id='val_adm_exec_java_timing_time_running' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";

    html += "<h3>Java Classes</h3>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Classes loaded</td><td id='val_adm_exec_java_classes_classes_loaded' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Classes loaded total</td><td id='val_adm_exec_java_classes_classes_loaded_total' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Classes unloaded</td><td id='val_adm_exec_java_classes_classes_unloaded' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";

    html += "<h3>Java Memory</h3>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Memory Init (MB)</td><td id='val_adm_exec_java_memory_memory_init' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Memory Used (MB)</td><td id='val_adm_exec_java_memory_memory_used' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Memory Committed (MB)</td><td id='val_adm_exec_java_memory_memory_committed' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Memory Max (MB)</td><td id='val_adm_exec_java_memory_memory_max' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Heap Init (MB)</td><td id='val_adm_exec_java_memory_memory_heap_init' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Heap Used (MB)</td><td id='val_adm_exec_java_memory_memory_heap_used' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Heap Free (MB)</td><td id='val_adm_exec_java_memory_memory_heap_free' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Heap Committed (MB)</td><td id='val_adm_exec_java_memory_memory_heap_committed' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Heap Max (MB)</td><td id='val_adm_exec_java_memory_memory_heap_max' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";

    html += "<h3>Java Threads</h3>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    //html += "<tr><td class='label'></td><td id='val_adm_exec_java_threads_threads_ids' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Threads Count</td><td id='val_adm_exec_java_threads_threads_count' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Threads Count Daemon</td><td id='val_adm_exec_java_threads_threads_count_daemon' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Threads Count Peak</td><td id='val_adm_exec_java_threads_threads_count_peak' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Threads Started</td><td id='val_adm_exec_java_threads_threads_count_started' class='value'>...</td></tr>";

    html += "</table>";

    html += "</div>";

    html += "<div id='val_adm_exec_java_ths_table' style='width: 70%; margin: auto;'>";
    html += "</div>";

//    html += "<h2>JCP " + service + "'s Memory</h2>";
//    html += "<div style='width: 70%; margin: auto;'>";
//    html += "<table class='table_details'>";
//
//    //html += "<tr><td class='label'></td><td id='val_adm_exec_' class='value'>...</td></tr>";
//
//    html += "</table>";
//    html += "</div>";

    html += "<h2>JCP " + service + "'s Network</h2>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Loopback</td><td id='val_adm_exec_network_addr_loopback' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Localhost</td><td id='val_adm_exec_network_addr_localhost' class='value'>...</td></tr>";

    html += "</table>";

    html += "</div>";

    html += "<div id='val_adm_exec_network_intfs_table' style='width: 70%; margin: auto;' />";
    html += "</div>";

    html += "<h2>JCP " + service + "'s OS</h2>";
    html += "<div style='width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>Name</td><td id='val_adm_exec_os_name' class='value'>...</td></tr>";
    html += "<tr><td class='label'>Arch</td><td id='val_adm_exec_os_arch' class='value'>...</td></tr>";

    html += "</table>";
    html += "</div>";

//    html += "<h2>JCP " + service + "'s Process</h2>";
//    html += "<div style='width: 70%; margin: auto;'>";
//    html += "<table class='table_details'>";
//
//    //html += "<tr><td class='label'></td><td id='val_adm_exec_' class='value'>...</td></tr>";
//
//    html += "</table>";
//    html += "</div>";

    return html;
}

function fetchJCPServiceExecutable(service) {
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Online.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableOnline,onErrorFetch);
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Cpu.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableCpu,onErrorFetch);
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Disks.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableDisks,onErrorFetch);
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Java.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableJava,onErrorFetch);
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Java_Ths.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableJavaThreads,onErrorFetch);
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Memory.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableMemory,onErrorFetch);
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Network.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableNetwork,onErrorFetch);
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Network_Intfs.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableNetworkIntfs,onErrorFetch);
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Os.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableOs,onErrorFetch);
    apiGET(backEndUrl,pathJSLWB_Admin_Exec_Process.replace("{service}", service.toLowerCase()),fillJCPServiceExecutableProcess,onErrorFetch);
}

function fillJCPServiceExecutableOnline(jcpExecOnlineJson) {
    var jcpExecOnline = JSON.parse(jcpExecOnlineJson);
    if (jcpExecOnline!=null && jcpExecOnline instanceof Array)
        jcpExecOnline = jcpExecOnline[0];
    replaceInnerHTMLById("val_adm_exec_online",jcpExecOnline);
}

function fillJCPServiceExecutableCpu(jcpExecCpuJson) {
    var jcpExecCpu = JSON.parse(jcpExecCpuJson);
    if (jcpExecCpu!=null && jcpExecCpu instanceof Array)
        jcpExecCpu = jcpExecCpu[0];
    replaceInnerHTMLById("val_adm_exec_cpu_count",jcpExecCpu.count);
    replaceInnerHTMLById("val_adm_exec_cpu_load_avg",doubleTruncateDigit(jcpExecCpu.loadAvg,2));
}

function fillJCPServiceExecutableDisks(jcpExecDisksJson) {
    var jcpExecDisks = JSON.parse(jcpExecDisksJson);
    if (jcpExecDisks!=null && jcpExecDisks instanceof Array)
        jcpExecDisks = jcpExecDisks[0];

    var disksTable = "";
    disksTable += "<table>";

    //disksTable += "<tr><td class='label'>Loopback</td><td id='val_adm_exec_network_addr_loopback' class='value'>...</td></tr>";

    disksTable += "</table>";


    replaceInnerHTMLById("val_adm_exec_disks",disksTable);
}

function fillJCPServiceExecutableJava(jcpExecJavaJson) {
    var jcpExecJava = JSON.parse(jcpExecJavaJson);
    if (jcpExecJava!=null && jcpExecJava instanceof Array)
        jcpExecJava = jcpExecJava[0];
    replaceInnerHTMLById("val_adm_exec_java_vm_vm_name",jcpExecJava.vmName);
    replaceInnerHTMLById("val_adm_exec_java_vm_vm_version",jcpExecJava.vmVersion);
    replaceInnerHTMLById("val_adm_exec_java_vm_spec_name",jcpExecJava.specName);
    replaceInnerHTMLById("val_adm_exec_java_vm_spec_vendor",jcpExecJava.specVendor);
    replaceInnerHTMLById("val_adm_exec_java_vm_spec_version",jcpExecJava.specVersion);
    replaceInnerHTMLById("val_adm_exec_java_vm_spec_mngm_version",jcpExecJava.specMngmVersion);

    var runtimeSystemProps = JSON.stringify(jcpExecJava.runtimeSystemProps).replaceAll(',',',\n').replace('{','').replace('}','');
    replaceInnerHTMLById("val_adm_exec_java_runtime_runtime_system_props",stringTruncateAndCopy(runtimeSystemProps));
    var runtimeInputArgs = jcpExecJava.runtimeInputArgs.toString().replaceAll(',',',\n');
    replaceInnerHTMLById("val_adm_exec_java_runtime_runtime_input_args",stringTruncateAndCopy(runtimeInputArgs));
    replaceInnerHTMLById("val_adm_exec_java_runtime_runtime_path_class",stringTruncateAndCopy(jcpExecJava.runtimePathClass));
    replaceInnerHTMLById("val_adm_exec_java_runtime_runtime_path_boot_class",stringTruncateAndCopy(jcpExecJava.runtimePathBootClass));
    replaceInnerHTMLById("val_adm_exec_java_runtime_runtime_path_library",stringTruncateAndCopy(jcpExecJava.runtimePathLibrary));

    replaceInnerHTMLById("val_adm_exec_java_timing_time_start",dateToString(new Date(jcpExecJava.timeStart)));
    replaceInnerHTMLById("val_adm_exec_java_timing_time_running",jcpExecJava.timeRunning/1000);

    replaceInnerHTMLById("val_adm_exec_java_classes_classes_loaded",jcpExecJava.classesLoaded);
    replaceInnerHTMLById("val_adm_exec_java_classes_classes_loaded_total",jcpExecJava.classesLoadedTotal);
    replaceInnerHTMLById("val_adm_exec_java_classes_classes_unloaded",jcpExecJava.classesUnloaded);

    replaceInnerHTMLById("val_adm_exec_java_memory_memory_init",jcpExecJava.memoryInit);
    replaceInnerHTMLById("val_adm_exec_java_memory_memory_used",jcpExecJava.memoryUsed);
    replaceInnerHTMLById("val_adm_exec_java_memory_memory_committed",jcpExecJava.memoryCommitted);
    replaceInnerHTMLById("val_adm_exec_java_memory_memory_max",jcpExecJava.memoryMax);
    replaceInnerHTMLById("val_adm_exec_java_memory_memory_heap_init",jcpExecJava.memoryHeapInit);
    replaceInnerHTMLById("val_adm_exec_java_memory_memory_heap_used",jcpExecJava.memoryHeapUsed);
    replaceInnerHTMLById("val_adm_exec_java_memory_memory_heap_free",jcpExecJava.memoryHeapFree);
    replaceInnerHTMLById("val_adm_exec_java_memory_memory_heap_committed",jcpExecJava.memoryHeapCommitted);
    replaceInnerHTMLById("val_adm_exec_java_memory_memory_heap_max",jcpExecJava.memoryHeapMax);

    replaceInnerHTMLById("val_adm_exec_java_threads_threads_count",jcpExecJava.threadsCount);
    replaceInnerHTMLById("val_adm_exec_java_threads_threads_count_daemon",jcpExecJava.threadsCountDaemon);
    replaceInnerHTMLById("val_adm_exec_java_threads_threads_count_peak",jcpExecJava.threadsCountPeak);
    replaceInnerHTMLById("val_adm_exec_java_threads_threads_count_started",jcpExecJava.threadsCountStarted);
}

function fillJCPServiceExecutableJavaThreads(jcpExecJavaThreadsJson) {
    var jcpExecJavaThreads = JSON.parse(jcpExecJavaThreadsJson);
    if (jcpExecJavaThreads!=null && jcpExecJavaThreads instanceof Array)
        jcpExecJavaThreads = jcpExecJavaThreads[0];

    var rowStyleTable  = "style='border: 1px solid black;margin: 20px 0;'";
    var rowStyleHeader = "style='border: 1px solid black;font-weight: bold;'";
    var rowStyleText   = "style='border: 1px solid black;white-space: nowrap;padding: 3px;'";
    var rowStyleNum    = "style='border: 1px solid black;white-space: nowrap;padding: 3px;text-align: right;'";

    var threadsTable = "";
    threadsTable += "<table " + rowStyleTable + ">";
    threadsTable += "<tr>";
    threadsTable += "<th " + rowStyleHeader + ">ID</th>";
    threadsTable += "<th " + rowStyleHeader + ">Name</th>";
    threadsTable += "<th " + rowStyleHeader + ">State</th>";
    threadsTable += "<th " + rowStyleHeader + ">Time CPU (s)</th>";
    threadsTable += "<th " + rowStyleHeader + ">Time User (s)</th>";
    threadsTable += "<th " + rowStyleHeader + ">Time Waiting (s)</th>";
    threadsTable += "</tr>";

    for (var i=0; i<jcpExecJavaThreads.length; i++) {
        threadsTable += "<tr>";
        threadsTable += "<td " + rowStyleText + ">" + jcpExecJavaThreads[i].id + "</td>";
        threadsTable += "<td " + rowStyleText + ">" + jcpExecJavaThreads[i].name + "</td>";
        threadsTable += "<td " + rowStyleText + ">" + jcpExecJavaThreads[i].state + "</td>";
        threadsTable += "<td " + rowStyleNum + ">" + jcpExecJavaThreads[i].timeCpu / 1000 + "</td>";
        threadsTable += "<td " + rowStyleNum + ">" + jcpExecJavaThreads[i].timeUser / 1000 + "</td>";
        threadsTable += "<td " + rowStyleNum + ">" + jcpExecJavaThreads[i].timeWaited / 1000 + "</td>";
        threadsTable += "</tr>";
    }

    threadsTable += "</table>";

    replaceInnerHTMLById("val_adm_exec_java_ths_table",threadsTable);
}

function fillJCPServiceExecutableMemory(jcpExecMemoryJson) {
    //var jcpExecMemory = JSON.parse(jcpExecMemoryJson);
    //if (jcpExecMemory!=null && jcpExecMemory instanceof Array)
    //    jcpExecMemory = jcpExecMemory[0];
    //replaceInnerHTMLById("val_adm_exec_memory_",jcpExecMemory.addrLoopback);
}

function fillJCPServiceExecutableNetwork(jcpExecNetworkJson) {
    var jcpExecNetwork = JSON.parse(jcpExecNetworkJson);
    if (jcpExecNetwork!=null && jcpExecNetwork instanceof Array)
        jcpExecNetwork = jcpExecNetwork[0];
    replaceInnerHTMLById("val_adm_exec_network_addr_loopback",stringTruncateAndCopy(jcpExecNetwork.addrLoopback));
    replaceInnerHTMLById("val_adm_exec_network_addr_localhost",stringTruncateAndCopy(jcpExecNetwork.addrLocalhost));
}

function fillJCPServiceExecutableNetworkIntfs(jcpExecNetworkIntfsJson) {
    var jcpExecNetworkIntfs = JSON.parse(jcpExecNetworkIntfsJson);
    if (jcpExecNetworkIntfs!=null && jcpExecNetworkIntfs instanceof Array)
        jcpExecNetworkIntfs = jcpExecNetworkIntfs[0];

    var rowStyleTable  = "style='border: 1px solid black;'margin: 20px 0;";
    var rowStyleHeader = "style='border: 1px solid black;font-weight: bold;'";
    var rowStyleText   = "style='border: 1px solid black;white-space: nowrap;padding: 3px;'";
    var rowStyleNum    = "style='border: 1px solid black;white-space: nowrap;padding: 3px;text-align: right;'";

    var intfsTable = "";
    intfsTable += "<table " + rowStyleTable + ">";
    intfsTable += "<tr>";
    intfsTable += "<th " + rowStyleHeader + ">IDx</th>";
    intfsTable += "<th " + rowStyleHeader + ">Name</th>";
    intfsTable += "<th " + rowStyleHeader + ">isUp</th>";
    intfsTable += "<th " + rowStyleHeader + ">isLoopback</th>";
    intfsTable += "<th " + rowStyleHeader + ">Address</th>";
    intfsTable += "</tr>";

    for (var i=0; i<jcpExecNetworkIntfs.length; i++) {
        intfsTable += "<tr>";
        intfsTable += "<td " + rowStyleText + ">" + jcpExecNetworkIntfs[i].index + "</td>";
        intfsTable += "<td " + rowStyleText + ">" + jcpExecNetworkIntfs[i].nameDisplay + "</td>";
        intfsTable += "<td " + rowStyleText + ">" + jcpExecNetworkIntfs[i].isUp + "</td>";
        intfsTable += "<td " + rowStyleText + ">" + jcpExecNetworkIntfs[i].isLoopback + "</td>";
        var address = "";
        for (var k=0; k<jcpExecNetworkIntfs[i].address.length; k++)
            address += jcpExecNetworkIntfs[i].address[k] + "<br>";
        intfsTable += "<td " + rowStyleText + ">" + address + "</td>";
        intfsTable += "</tr>";
    }
    intfsTable += "</table>";

    replaceInnerHTMLById("val_adm_exec_network_intfs_table",intfsTable);
}

function fillJCPServiceExecutableOs(jcpExecOsJson) {
   var jcpExecOs = JSON.parse(jcpExecOsJson);
    if (jcpExecOs!=null && jcpExecOs instanceof Array)
        jcpExecOs = jcpExecOs[0];
   replaceInnerHTMLById("val_adm_exec_os_name",jcpExecOs.name);
   replaceInnerHTMLById("val_adm_exec_os_arch",jcpExecOs.arch);
}

function fillJCPServiceExecutableProcess(jcpExecProcessJson) {
    //var jcpExecProcess = JSON.parse(jcpExecProcessJson);
    //if (jcpExecProcess!=null && jcpExecProcess instanceof Array)
    //    jcpExecProcess = jcpExecProcess[0];
    //replaceInnerHTMLById("val_adm_exec_process_",jcpExecProcess.);
}

// Admin's APIs

// htmlAdminContentAPIs()
// fetchAdminContentAPIs()
// -> fillAdminContentAPIs()

function htmlAdminContentAPIs() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > JOSP GWs</p>");

    var html = "";
    html += "<div style='display: flex; margin: 20px; justify-content: space-around;'>";
    html += "<a href='javascript:showAdminContentAPIs(true)'>Refresh</a>";
    html += "<a href='javascript:showJCPServiceBuildInfo(true,\"APIs\")'>Build Info</a>";
    html += "<a href='javascript:showJCPServiceExecutable(true,\"APIs\")'>Executable</a>";
    html += "</div>";

    return html;
}

function fetchAdminContentAPIs(objId) {
    fetchAdminContentGWs()
}

function fillAdminContentAPIs(jcpGWsJson) {
    fillAdminContentGWs(jcpGWsJson);
}

// Admin's Gateways

// htmlAdminContentGWs()
// fetchAdminContentGWs()
// -> fillAdminContentGWs()

function htmlAdminContentGWs() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > JOSP GWs</p>");

    var html = "";
    html += "<div style='display: flex; margin: 20px; justify-content: space-around;'>";
    html += "<a href='javascript:showAdminContentGWs(true)'>Refresh</a>";
    html += "<a href='javascript:showJCPServiceBuildInfo(true,\"GWs\")'>Build Info</a>";
    html += "<a href='javascript:showJCPServiceExecutable(true,\"GWs\")'>Executable</a>";
    html += "</div>";

    html += "<h2>JCP APIs's Gateways</h2>";
    html += "<div id='val_adm_gws' style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "</div>";
    return html;

    return html;
}

function fetchAdminContentGWs() {
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


// Admin's JSLWB

// htmlAdminContentJSLWB()
// fetchAdminContentJSLWB()
// -> fillAdminContentJSLWB()

function htmlAdminContentJSLWB() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > JOSP JSL Web Bridge</p>");

    var html = "";
    html += "<div style='display: flex; margin: 20px; justify-content: space-around;'>";
    html += "<a href='javascript:showAdminContentJSLWB(true)'>Refresh</a>";
    html += "<a href='javascript:showJCPServiceBuildInfo(true,\"JSLWB\")'>Build Info</a>";
    html += "<a href='javascript:showJCPServiceExecutable(true,\"JSLWB\")'>Executable</a>";
    html += "</div>";

    return html;
}

function fetchAdminContentJSLWB(objId) {
    fetchAdminContentGWs()
}

function fillAdminContentJSLWB(jcpGWsJson) {
    fillAdminContentGWs(jcpGWsJson);
}


// Admin's FE

// htmlAdminContentFE()
// fetchAdminContentFE()
// -> fillAdminContentFE()

function htmlAdminContentFE() {
    setTitle("<p><a href='javascript:void(0);' onclick='showAdmin(true)'>Admin</a> > JOSP Front End</p>");

    var html = "";
    html += "<div style='display: flex; margin: 20px; justify-content: space-around;'>";
    html += "<a href='javascript:showAdminContentFE(true)'>Refresh</a>";
    html += "<a href='javascript:showJCPServiceBuildInfo(true,\"FE\")'>Build Info</a>";
    html += "<a href='javascript:showJCPServiceExecutable(true,\"FE\")'>Executable</a>";
    html += "</div>";

    return html;
}

function fetchAdminContentFE(objId) {
    fetchAdminContentGWs()
}

function fillAdminContentFE(jcpGWsJson) {
    fillAdminContentGWs(jcpGWsJson);
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
    var jcpObjs = JSON.parse(jcpObjsJson);
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
