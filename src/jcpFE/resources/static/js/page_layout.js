/*********************+
    Page Layout
 **********************/

/*
This file provide all features required to fill the page layout's components:

    <section            >                   .
        <nav            id="div_nav">       .
            <div        id="div_logo">      html, img
            <div        id="div_menu">      html, itemList, itemListJS
        <div            id="div_main">
            <header     id="div_header">
                <div    id="div_title">     html, str
                <div    id="div_opts">      html
            <article    id="div_content">   html
            <footer     id="div_footer">    html
*/


// Element's ids

const divNav_id     = "div_nav";
const divLogo_id    = "div_logo";
const divMenu_id    = "div_menu";
const divMain_id    = "div_main";
const divHeader_id  = "div_header";
const divTitle_id   = "div_title";
const divOpts_id    = "div_opts";
const divContent_id = "div_content";
const divFooter_id  = "div_footer";


// Logo methods

function setLogo(logoHtml) {
    document.getElementById(divLogo_id).innerHTML = logoHtml;
}

function setLogo_Img(logo) {
    document.getElementById(divLogo_id).innerHTML = "<img src='" + logo + "'>";
}


// Menu methods

function setMenu(menuHtml) {
    document.getElementById(divMenu_id).innerHTML = menuHtml;
}

function setMenu_List(menuItems) {
    var html = "<ul>";
    for(var i = 0; i < menuItems.length; i++)
        if (menuItems[i][1] != null)
            html += "<li><a href='" + menuItems[i][1] + "'>" + menuItems[i][0] + "</a></li>";
        else
            html += "<li class='title'>" + menuItems[i][0] + "</li>";
    html += "</ul>";

    document.getElementById(divMenu_id).innerHTML = html;
}

function setMenu_List_OnClick(menuItems) {
    var html = "<ul>";
    for(var i = 0; i < menuItems.length; i++)
        if (menuItems[i][1] != null)
            html += "<li><a href='javascript:void(0);' onclick='" + menuItems[i][1] + "'>" + menuItems[i][0] + "</a></li>";
        else
            html += "<li class='title'>" + menuItems[i][0] + "</li>";
    html += "</ul>";

    document.getElementById(divMenu_id).innerHTML = html;
}


// Title methods

function setTitle(titleHtml) {
    document.getElementById(divTitle_id).innerHTML = titleHtml;
}

function setTitle_Str(title) {
    document.getElementById(divTitle_id).innerHTML = "<h1>" + title + "</h1>";
}


// Opts methods

function setOpts(optsHtml) {
    document.getElementById(divOpts_id).innerHTML = optsHtml;
}


// Content methods

function setContent(contentHtml) {
    document.getElementById(divContent_id).innerHTML = contentHtml;
}


// Footer methods

function setFooter(footerHtml) {
    document.getElementById(divFooter_id).innerHTML = footerHtml;
}

function setFooter_Cols(col1,col2) {
    var html = "<table style='width:100%'><tr>";
    html += "<td style='text-align: left;'>" + col1 + "</td>";
    html += "<td style='text-align: right;'>" + col2 + "</td>";
    html += "</tr></table>";
    document.getElementById(divFooter_id).innerHTML = html;
}

function setFooter_Cols(col1,col2,col3) {
    var html = "<table style='width:100%'><tr>";
    html += "<td style='width:33%; text-align: left;'>" + col1 + "</td>";
    html += "<td style='width:33%; text-align: center;'>" + col2 + "</td>";
    html += "<td style='width:33%; text-align: right;'>" + col3 + "</td>";
    html += "</tr></table>";
    document.getElementById(divFooter_id).innerHTML = html;
}