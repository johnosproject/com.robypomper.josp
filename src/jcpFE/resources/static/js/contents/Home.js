const PAGE_HOME = "home";
const PAGE_HOME_TITLE = "My JOSP Eco System";

// showHome()
// -> htmlHome()

function showHome(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_HOME, PAGE_HOME_TITLE);

    currentPage = PAGE_HOME;
    setContent(htmlHome());
}

function htmlHome() {
    setTitle(PAGE_HOME_TITLE);

    var html = "<div style='min-height: 300px; width: 70%; margin: auto;'>";
    html += "<p>Ready to enjoy the limitless happiness of the connected world?<br></p>";
    html += "<p>Let's start selecting an object from the side menu,<br>";
    html += "if there's not objects listed then ";
    html += "<span class='logout'>";
    html += "<a href='" + backEndUrl + "/apis/user/1.0/login/?redirect_uri=" + frontEndUrl + "'>login</a>";
    html += " (or <a href='/apis/user/1.0/registration/'>register</a>)</br>";
    html += " or try to ";
    html += "</span>";
    html += "<a href='#'>add new one</a>.</p>";
    html += "</div>";
    return html;
}