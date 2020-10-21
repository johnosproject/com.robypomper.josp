const PAGE_USER_PROFILE = "usrInfo";
const PAGE_USER_PROFILE_TITLE = "User Info";

// showUserContent()
// -> htmlUserContent()
// -> fetchUserContent()
//   -> fillUserContent()

function showUserContent(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_USER_PROFILE, PAGE_USER_PROFILE_TITLE);

    currentPage = PAGE_USER_PROFILE;
    setContent(htmlUserContent());
    fetchUserContent();
}

function htmlUserContent() {
    setTitle(PAGE_USER_PROFILE_TITLE);

    var html = "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<table class='table_details'>";

    html += "<tr><td class='label'>ID</td><td class='value'><span id='val_user_id'>...</span></td></tr>";
    html += "<tr><td class='label'>Username</td><td class='value'><span id='val_user_name'>...</span></td></tr>";

    html += "</table>";
    html += "</div>";
    return html;
}

function fetchUserContent() {
    var path = "/apis/user/1.0/";
    apiGET(path,fillUserContent,onErrorFetch);
}

function fillUserContent(userJson) {
    var user = JSON.parse(userJson);
    if (document.getElementById("val_user_id") != null)
        document.getElementById("val_user_id").innerHTML = user.id;
    if (document.getElementById("val_user_name") != null)
        document.getElementById("val_user_name").innerHTML = user.name;
}