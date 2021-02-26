
var objsListCache = null;
var objsListFilterOwner = true;
var objsListFilterShared = true;
var objsListFilterAnonymous = false;

// fetchObjsListMenu
// -> fillObjsListMenu
//   -> dropDownObjsListMenuFilter

function fetchObjsListMenu() {
    apiGET(backEndUrl,"/apis/objsmngr/1.0/",fillObjsListMenu,onErrorFetch);
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
    var objsListOrdered = new Map();
    for (i = 0; i < objsList.length; i++) {
        if (typeof objsListOrdered.get(objsList[i].model) == "undefined")
            objsListOrdered.set(objsList[i].model,[]);
        objsListOrdered.get(objsList[i].model).push(objsList[i]);
    }

    // Update menu
    var menuItems = [];
    menuItems.push(["<div class='title'><a href='javascript:showHome(true)'>Home</a></div>" + dropDownObjsListMenuFilter(),null]);
    for (var [model, objList] of objsListOrdered) {
        menuItems.push(["<div style='font-size: 0.8em; margin: 5px 10px'>" + model + " (" + objList.length + ")</div>",null]);

        for (k = 0; k < objList.length; k++) {
            var object = objList[k];
            var connectedColor = object.isConnected ? "lightgreen" : "mediumvioletred";
            var ownerIcon = "users";       // Shared       -   users
            if (object.owner == '00000-00000-00000')
                ownerIcon = "user-o";        // Own          -   user
            else if (typeof user !== 'undefined' && object.owner == user.id)
                ownerIcon = "user";      // Anonymous    -   user-o
            var objLabel = "<i class='fa fa-plug' aria-hidden='true' style='color: " + connectedColor + ";'></i>";
            objLabel += object.name;
            objLabel += "<i class='fa fa-" + ownerIcon + "' aria-hidden='true' style='color: white'></i>";

            if ((typeof user === "undefined" || !user.isAuthenticated)
             || (object.owner == user.id && objsListFilterOwner)
             || (object.owner != user.id && object.owner != '00000-00000-00000' && objsListFilterShared)
             || (object.owner == '00000-00000-00000' && objsListFilterAnonymous)
               )
                menuItems.push([objLabel,"showObjectContent(\"" + object.id + "\",true)"]);
        }
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
