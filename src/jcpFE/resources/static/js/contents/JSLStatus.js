const PAGE_JSL_STATUS = "jslInfo";
const PAGE_JSL_STATUS_TITLE = "JSL Info";

// showJSLStatus()
// -> htmlJSLStatusContent()
// -> fetchJSLStatusContent()
//   -> fillJSLStatusContent()

function showJSLStatus(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_JSL_STATUS, PAGE_JSL_STATUS_TITLE);

    currentPage = PAGE_JSL_STATUS;
    setContent(htmlJSLStatusContent());
    fetchJSLStatusContent();
}

function htmlJSLStatusContent() {
    setTitle(PAGE_JSL_STATUS_TITLE);

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

function fetchJSLStatusContent() {
    var path = "/apis/service/1.0/";
    apiGET(path,fillJSLStatusContent,onErrorFetch);
}

function fillJSLStatusContent(serviceJson) {
    service = JSON.parse(serviceJson);
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
