
// Components

function titleWelcomeStr() {
    return "Welcome to JOSP<br><span style='text-align:right;'>Eco-System</span>";
}

function dropDownUserMenu() {
    var html = "";

    html += "<div class='dropdown'>";
    html += "  <button class='dropbtn box_opts box_opts_active'><i class='fa fa-bars'></i></button>";
    html += "  <div class='dropdown-content'>";
    html += "    <a class='logout' href='" + backEndUrl + "/apis/user/1.0/login/?redirect_uri=' onclick=\"location.href=this.href+document.location.href;return false;\"><i class='fa fa-sign-in'></i>Login</a>";
    //html += "    <a class='logout' href='/apis/user/1.0/registration/'><i class='fa a-id-card-o'></i>Register</a>";
    html += "    <a class='login' href='javascript:showUserContent(true)'><i class='fa a-id-card-o'></i>Profile</a>";
    html += "    <a class='login' href='" + backEndUrl + "/apis/user/1.0/logout/?redirect_uri=" + frontEndUrl + "'><i class='fa fa-sign-out'></i>Logout</a>";
    html += "    <hr>";
    html += "    <a href='javascript:showJSLStatus(true)'><i class='fa fa-info'></i>JSL Status</a>";
    if (loggedUser_isAdmin)
        html += "    <a href='javascript:showAdminContent(true)'><i class='fa fa-info'></i>JCP FE Mngm</a>";
    html += "    <a href='javascript:showAbout(true)'><i class='fa fa-question-circle-o'></i>About</a>";
    html += "  </div>";
    html += "</div>";

    html += "<div class='box_opts'>";
    html += "<a href='javascript:showAccessControl(true)'><i class='fa fa-share-alt'></i></a>";

    return html;
}

function htmlFooterLeft() {
    var html = "<p><b>JOSP JCP FrontEnd</b> provided by <b><a href='www.johnosproject.com'>johnosproject.com</a></b></p>";
    html += "<p>Version: 2.0.0</p>";
    return html;
}

function htmlFooterCenter() {
    var html = "<p>Made with <3</p>";
    return html;
}

function htmlFooterRight() {
    var html = "<p><a href='https://bitbucket.org/johnosproject/com.robypomper.josp/src/master/README.md'>Documentation and repository</a></p>";
    return html;
}

function htmlBoxExpandable() {
    var html = "";
    html += "    <div style='float: right;' onClick='collapseBoxExpandable(this)'>";
    html += "        <i class='fa fa-angle-down down' aria-hidden='true'></i>";
    html += "        <i class='fa fa-angle-up up' aria-hidden='true'></i>";
    html += "    </div>";
    return html;
}

function collapseBoxExpandable(divExpColl) {
    var box = divExpColl.parentElement;
    box.classList.toggle("collapsed");
}

// Messages

function onErrorFetch(xhttpRequest) {
    alert("MESSAGE TO USER: Error can't fetch '" + xhttpRequest + "' resource");
}

function onWarningFetch(xhttpRequest) {
    alert("MESSAGE TO USER: Warning can't fetch '" + xhttpRequest + "' resource");
}


// Manage CSS classes       (check if can be simplfy with tag.classList.add('disabled');)

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


// Utils

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

function jsonToHTMLList(json) {
    var obj = typeof json === 'string' ? JSON.parse(json) : json;

    var html = "<ul>";
    for (const property in obj) {
        html += "<li style='display: flex;'>"
        if (typeof obj[property] === 'object') {
            html += property + ": ";
            html += jsonToHTML(obj[property]);

        } else {
            //html += property + ": " + obj[property];
            html += "<div>" + property + ":</div>";
            //html += "<div style='float:right;'>" + obj[property] + "</div>";
            //html += "<div style='float:right; width: 90%; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;'>" + obj[property] + "</div>";
            html += "<div style='flex-grow: 1; text-align: right; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;'>" + obj[property] + "</div>";
        }
        html += "</li>"
    }
    html += "</ul>";
    return html;
}

function jsonToHTMLTable(json) {
    var obj = typeof json === 'string' ? JSON.parse(json) : json;

    var html = "<table class='table_details'>";
    for (const property in obj) {
        html += "<tr>"
        html += "<td class='label'>" + property + "</td>";
        html += "<td class='value'>" + obj[property] + "</td>";
        html += "</tr>"
    }
    html += "</table>";
    return html;
}

function doubleTruncateDigit(double,display) {
    var strDouble = "" + double;
    return strDouble.substring(0,strDouble.indexOf('.')+display+1);
}

function stringToDate(str) {
    return new Date(Date.parse(str));
}

function dateToString(date) {
    return "" +
        ("0" + date.getUTCHours()).slice(-2) + ":" +
        ("0" + date.getUTCMinutes()).slice(-2) + ":" +
        ("0" + date.getUTCSeconds()).slice(-2) + " " +
        ("0" + date.getUTCDate()).slice(-2) + "/" +
        ("0" + (date.getUTCMonth()+1)).slice(-2) + "/" +
        date.getUTCFullYear();
}