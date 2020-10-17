const PAGE_ABOUT = "abount";
const PAGE_ABOUT_TITLE = "JCP FrontEnd About";

// showAbout()
// -> htmlAbout()

function showAbout(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ABOUT, PAGE_ABOUT_TITLE);

    currentPage = PAGE_ABOUT;
    setContent(htmlAbout());
}

function htmlAbout() {
    setTitle(PAGE_ABOUT_TITLE);

    var html = "<p>The most awesome JCP FE version</p>";
    return html;
}