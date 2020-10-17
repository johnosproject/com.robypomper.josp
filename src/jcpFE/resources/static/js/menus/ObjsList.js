
var objsListCache = null;
var objsListFilterOwner = true;
var objsListFilterShared = true;
var objsListFilterAnonymous = false;

// fetchObjsListMenu
// -> fillObjsListMenu
//   -> dropDownObjsListMenuFilter

function fetchObjsListMenu() {
    apiGET("/apis/objsmngr/1.0/",fillObjsListMenu,onErrorFetch);
}

function fillObjsListMenu(objsListJson) {
    var objsList;

    if (objsListJson != null)
        objsList = JSON.parse(objsListJson);
    else if (objsListCache != null)
        objsList = objsListCache;

    if (objsListJson == null && objsListCache == null) {
        fetchObjsListMenu();
        return;
    }
    objsListCache = objsList;

    // Update menu
    var menuItems = [];
    menuItems.push(["<div class='title'>OBJECTS</div>" + dropDownObjsListMenuFilter(),null]);
    for (i = 0; i < objsList.length; i++) {
        var connI = objsList[i].isConnected ? "C" : "D";
        if (typeof user === "undefined" || !user.isAuthenticated)
            if (objsList[i].owner == '00000-00000-00000')
                menuItems.push([objsList[i].name + " [" + connI + "A]","showObjectContent(\"" + objsList[i].id + "\",true)"]);
            else
                menuItems.push([objsList[i].name,"showObjectContent(\"" + objsList[i].id + "\",true)"]);
        else
            if (objsList[i].owner == user.id && objsListFilterOwner)
                menuItems.push([objsList[i].name + " [" + connI + "]","showObjectContent(\"" + objsList[i].id + "\",true)"]);
            else if (objsList[i].owner != user.id && objsList[i].owner != '00000-00000-00000' && objsListFilterShared)
                menuItems.push([objsList[i].name + " [" + connI + "S]","showObjectContent(\"" + objsList[i].id + "\",true)"]);
            else if (objsList[i].owner == '00000-00000-00000' && objsListFilterAnonymous)
                menuItems.push([objsList[i].name + " [" + connI + "A]","showObjectContent(\"" + objsList[i].id + "\",true)"]);
    }

    if (menuItems.length==1) {
        menuItems.push(["",null]);
        menuItems.push(["<div style='font-size: 0.6em;width: 100%;'>No objects found!<br><br>" +
                         "Check objects filters<br>" +
                         "or register new objects.</div>",null]);
        menuItems.push(["",null]);
    }

    setMenu_List_OnClick(menuItems);
}

function dropDownObjsListMenuFilter() {
    var html = "";

    if (typeof user !== "undefined" && user.isAuthenticated) {
        html += "<div class='dropdown'>";
        html += "  <button class='dropbtn box_opts box_opts_active'><i class='fa fa-sliders'></i></button>";
        html += "  <div class='dropdown-content'>";
        html += "    <a>";
        html += "      <input type='checkbox' onclick='updMenuObjsListFilter()' id='objs_list_filter_owner'" + (objsListFilterOwner ? " checked='true'" : "") + "' />";
        html += "      <label for='objs_list_filter_owner'>My objects</label>";
        html += "    </a>";
        html += "    <a>";
        html += "      <input type='checkbox' onclick='updMenuObjsListFilter()' id='objs_list_filter_shared'" + (objsListFilterShared ? " checked='true'" : "") + "' />";
        html += "      <label for='objs_list_filter_shared'>Shared with me</label>";
        html += "    </a>";
        html += "    <a>";
        html += "      <input type='checkbox' onclick='updMenuObjsListFilter()' id='objs_list_filter_anonymous'" + (objsListFilterAnonymous ? " checked='true'" : "") + "' />";
        html += "      <label for='objs_list_filter_anonymous'>Anonymous objects</label>";
        html += "    </a>";
        html += "  </div>";
        html += "</div>";
    }
    return html;
}


// Utils

function updMenuObjsListFilter() {
      var owner = document.getElementById('objs_list_filter_owner').checked
      var shared = document.getElementById('objs_list_filter_shared').checked;
      var anonymous = document.getElementById('objs_list_filter_anonymous').checked;
      updObjsListFilter(owner,shared,anonymous);
}

function updObjsListFilter(owner, shared, anonymous) {
    objsListFilterOwner = owner;
    objsListFilterShared = shared;
    objsListFilterAnonymous = anonymous;

    fillObjsListMenu();
}
