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
    html += "<tr><td class='label'>Status</td><td class='value'><span id='val_service_state'>...</span></td></tr>";
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
    apiGET(backEndUrl,pathJSLWB_Srv,fillJSLStatusContent,onErrorFetch);
}

function fillJSLStatusContent(serviceJson) {
    service = JSON.parse(serviceJson);
    replaceInnerHTMLById("val_service_name",service.name);
    replaceInnerHTMLById("val_service_state",service.state);
    replaceInnerHTMLById("val_service_isJCPConnected",service.isJCPConnected);
    replaceInnerHTMLById("val_service_isCloudConnected",service.isCloudConnected);
    replaceInnerHTMLById("val_service_isLocalRunning",service.isLocalRunning);
    replaceInnerHTMLById("val_service_srvId",service.srvId);
    replaceInnerHTMLById("val_service_usrId",service.usrId);
    replaceInnerHTMLById("val_service_instId",service.instId);
    replaceInnerHTMLById("val_service_jslVersion",service.jslVersion);
    replaceInnerHTMLById("val_service_supportedJCPAPIsVersions",service.supportedJCPAPIsVersions);
    replaceInnerHTMLById("val_service_supportedJOSPProtocolVersions",service.supportedJOSPProtocolVersions);
    replaceInnerHTMLById("val_service_supportedJODVersions",service.supportedJODVersions);
    replaceInnerHTMLById("val_service_sessionId",service.sessionId);
}
