const PAGE_OBJ_DETAILS = "objDetails";
const PAGE_OBJ_DETAILS_TITLE = "Object";
const PAGE_OBJ_INFO = "objInfo";
const PAGE_OBJ_INFO_TITLE = "Object > Info";
const PAGE_OBJ_PERMS = "objPerms";
const PAGE_OBJ_PERMS_TITLE = "Object > Access Control";

var detailObjId = null;
var defaultObjCompsCollapsed = true;


// showObjectContent()                          *1
// showObjectContentInfo()                      *2
// showObjectContentAccessControl()             *3
// -> htmlObjectContent()
// -> fetchObjectContent()
//   -> fillObjectContent()
//     -> htmlObjectContentTitle()
//     -> htmlObjectContentLinks()
// -> fetchObjectContentStruct() ...            *1
// -> htmlObjectContentInfo()                   *2
// -> fetchObjectContentInfo() ...              *2
// -> htmlObjectContentAccessControl()          *3
// -> fetchObjectContentAccessControl() ...     *3

function showObjectContent(objId,updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_OBJ_DETAILS, PAGE_OBJ_DETAILS_TITLE, "objId=" + objId);

    currentPage = PAGE_OBJ_DETAILS;
    var isSwitchObject = detailObjId != objId;
    detailObjId = objId;
    if (document.getElementById("div_obj_content") == null  || isSwitchObject) {
        setContent(htmlObjectContent());
        fetchObjectContent(objId);
    }
    setTitle(PAGE_OBJ_DETAILS_TITLE);
    if (document.getElementById("div_obj_content") != null)
        document.getElementById("div_obj_content").innerHTML = "";

    fetchObjectContentStruct(objId);
}

function showObjectContentInfo(objId,updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_OBJ_INFO, PAGE_OBJ_INFO_TITLE, "objId=" + objId);

    currentPage = PAGE_OBJ_DETAILS;
    var isSwitchObject = detailObjId != objId;
    detailObjId = objId;
    if (document.getElementById("div_obj_content") == null  || isSwitchObject) {
        setContent(htmlObjectContent());
        fetchObjectContent(objId);
    }
    if (document.getElementById("div_obj_content") != null)
        document.getElementById("div_obj_content").innerHTML = htmlObjectContentInfo(objId);

    fetchObjectContentInfo(objId);
}

function showObjectContentAccessControl(objId,updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_OBJ_PERMS, PAGE_OBJ_PERMS_TITLE, "objId=" + objId);

    currentPage = PAGE_OBJ_DETAILS;
    var isSwitchObject = detailObjId != objId;
    detailObjId = objId;
    if (document.getElementById("div_obj_content") == null  || isSwitchObject) {
        setContent(htmlObjectContent());
        fetchObjectContent(objId);
    }
    if (document.getElementById("div_obj_content") != null)
        document.getElementById("div_obj_content").innerHTML = htmlObjectContentAccessControl(objId);

    fetchObjectContentAccessControl(objId);
}

function htmlObjectContent() {
    setTitle(PAGE_OBJ_DETAILS_TITLE);

    var html = "";
    html += "    <div id='div_obj_title'>...</div>";
    html += "    <div id='div_obj_links'>...</div>";
    html += "    <hr>";
    html += "    <div id='div_obj_content'>...</div>";
    return html;
}

function fetchObjectContent(objId) {
    apiGET_retry("/apis/objsmngr/1.0/" + objId + "/",fillObjectContent,onErrorFetch);
}

function fillObjectContent(objJson) {
    obj = JSON.parse(objJson);

    if (document.getElementById("div_obj_title") != null)
        document.getElementById("div_obj_title").innerHTML = htmlObjectContentTitle(obj);
    if (document.getElementById("div_obj_links") != null)
        document.getElementById("div_obj_links").innerHTML = htmlObjectContentLinks(obj);

    if (!obj.isConnected) {
        addDisabledCssClassById("div_obj_title");
        addDisabledCssClassById("div_obj_content");
    }
}

function htmlObjectContentTitle(obj) {
    var titleId = "obj_" + obj.id + "_title";
    var html = "<h1 id='" + titleId + "'>";
    html += "    " + obj.name;
    html += "    <a href='javascript:void(0);' onclick='editObjectName_Title(this.parentElement,\"" + obj.name + "\");'>";
    html += "        <i class='fa fa-pencil' style='font-size: 0.8em;'></i>";
    html += "    </a>";
    html += "</h1>";
    return html;
}

function htmlObjectContentLinks(obj) {
    var html = "";
    html += "<div style='padding: 20px 0 20px 10px;'>";
    html += "    <p>Get more <a href='javascript:void(0);' onclick='showObjectContentInfo(&quot;" + obj.id + "&quot;,true)'>Obj Info</a>";
    html += "    <p>Setup the Obj's <a href='javascript:void(0);' onclick='showObjectContentAccessControl(&quot;" + obj.id + "&quot;,true)'>Access Control</a>";
    html += "</div>";
    return html;
}


// Object's Struct

// fetchObjectContentStruct()
// -> fillObjectContentStruct()
//   -> htmlObjectContentStruct()
//     -> htmlContainer()
//       -> htmlComponent()
//       -> htmlComponent()
//         -> htmlComponentBooleanState
//         -> htmlComponentRangeState
//         -> htmlComponentBooleanAction
//         -> htmlComponentRangeAction

function fetchObjectContentStruct(objId) {
    apiGET("/apis/structure/1.0/" + objId + "/",fillObjectContentStruct,onErrorFetch);
}

function fillObjectContentStruct(rootJson) {
    root = JSON.parse(rootJson);

    if (document.getElementById("div_obj_content") != null)  //div_struct
        document.getElementById("div_obj_content").innerHTML = htmlObjectContentStruct(root);
}

function htmlObjectContentStruct(root) {
    var titleId = "obj_" + obj.id + "_content";
    var html = "<div id='" + titleId + "'>";
    //html += "    <div class='box box_obj_details'>";
    //html += htmlBoxExpandable();
    //html += "        <h2>Model</h2>";
    //html += htmlObjectContentStruct_Model(root);
    //html += "    </div>";

    //html += "    <hr>";

    //html += "    <div class='box box_obj_struct'>";
    //html += htmlBoxExpandable();
    html += "        <h2>Structure</h2>";
    html += htmlContainer(root);
    //html += "    </div>";
    html += "</div>";
    return html;
}

function htmlObjectContentStruct_Model(root) {
    var html = "";
    html += "<h2>Model:</h2>";
    html += "    <b>Model:</b> " + root.model + "<br>";
    html += "    <b>Brand:</b> " + root.brand + "<br>";
    html += "    <b>Description:</b> " + root.description + "<br>";
    return html;
}

function htmlContainer(contJson) {
    var html = "<ul class='action_first'>";
    for (var i=0; i<contJson.subComps.length; i++) {
        html += "<li id='" + contJson.subComps[i].componentPath + "' class='box_obj_comp box_obj_comp_" + contJson.subComps[i].type + " " + (defaultObjCompsCollapsed ? "collapsed" : "") + "'>";
        html += htmlBoxExpandable();
        html += htmlComponent(contJson.subComps[i]);
        html += "</li>";
    }
    html += "</ul>";
    return html;
}

function htmlComponent(comp) {
    var html = "";
    html += "<p class='info'>";
    html += "    <spam style='color:gray;'>";
    html += comp.componentPath.substring(0,comp.componentPath.length-comp.name.length);
    html += "    </spam>";
    html += "    <b>";
    html += comp.name;
    html += "    </b>";
    if (comp.type != "Container")
        html += "<br><spam style='color:gray; font-size: 0.8em;'>(" + comp.type + ")</span>";
    html += "</p>";

    if (comp.type == "BooleanState") html += htmlComponentBooleanState(comp);
    else if (comp.type == "BooleanAction") html += htmlComponentBooleanAction(comp);
    else if (comp.type == "RangeState") html += htmlComponentRangeState(comp);
    else if (comp.type == "RangeAction") html += htmlComponentRangeAction(comp);
    else if (comp.type == "Container") html += htmlContainer(comp);

    return html;
}

function htmlComponentBooleanState(comp) {
    var html = "";
    html += "<p id='" + comp.componentPath + "_state' class='state action_first'>" + comp.state + "</p>";
    return html;
}

function htmlComponentRangeState(comp) {
    var html = "";
    html += "<p id='" + comp.componentPath + "_state' class='state action_first'>" + comp.state + "</p>";
    return html;
}

function htmlComponentBooleanAction(comp) {
    var html = "";
    html += "<p id='" + comp.componentPath + "_state' class='state action_first'>" + comp.state + "</p>";
    html += "<div id='" + comp.componentPath + "_action' class='action action_single action_first' onClick='executeAction(this,\"" + comp.pathSwitch + "\");'>Switch</div>";
    html += "<div id='" + comp.componentPath + "_action_switch' class='action action_main action_first' onClick='executeAction(this,\"" + comp.pathSwitch + "\");'>Switch</div>";
    html += "<div id='" + comp.componentPath + "_action_true' class='action action_first' onClick='executeAction(this,\"" + comp.pathTrue + "\");'>Set TRUE</div>";
    html += "<div id='" + comp.componentPath + "_action_false' class='action' onClick='executeAction(this,\"" + comp.pathFalse + "\");'>Set FALSE</div>";
    return html;
}

function htmlComponentRangeAction(comp) {
    var html = "";
    html += "<p id='" + comp.componentPath + "_state' class='state action_first'>" + comp.state + "</p>";
    html += "<div id='" + comp.componentPath + "_action' class='action action_single action_first'>";
    var value_slider_id = comp.componentPath + "_action_slider";
    var value_slider_path = comp.pathSetValue + "\" + document.getElementById(\"" + value_slider_id + "\").value";
    html += "    <p style='width: auto;padding: 5px;'>" + comp.min + "</p>";
    html += "    <input id='" + value_slider_id + "' type='range' min='" + comp.min + "' max='" + comp.max + "' step='" + comp.step + "' value='" + comp.state + "' onChange='executeAction(this.parentElement,\"" + value_slider_path + ");'>";
    html += "    <p style='width: auto;padding: 5px;'>" + comp.max + "</p>";
    html += "</div>";

    html += "<div id='" + comp.componentPath + "_action_dec' class='action action_first' onClick='executeAction(this,\"" + comp.pathDec + "\");'>Decrease</div>";
    html += "<div id='" + comp.componentPath + "_action_inc' class='action' onClick='executeAction(this,\"" + comp.pathInc + "\");'>Increase</div>";
    html += "<div id='" + comp.componentPath + "_action_min' class='action action_main action_first' onClick='executeAction(this,\"" + comp.pathMin + "\");'>Set MIN</div>";
    html += "<div id='" + comp.componentPath + "_action_1_2' class='action' onClick='executeAction(this,\"" + comp.pathSet1_2 + "\");'>Set 1/2</div>";
    html += "<div id='" + comp.componentPath + "_action_1_3' class='action' onClick='executeAction(this,\"" + comp.pathSet1_3 + "\");'>Set 1/3</div>";
    html += "<div id='" + comp.componentPath + "_action_2_3' class='action' onClick='executeAction(this,\"" + comp.pathSet2_3 + "\");'>Set 2/3</div>";
    html += "<div id='" + comp.componentPath + "_action_max' class='action action_main' onClick='executeAction(this,\"" + comp.pathMax + "\");'>Set MAX</div>";
    var value_input_id = "aaa1234";
    var value_path = comp.pathSetValue + "\" + document.getElementById(\"" + value_input_id + "\").value";
    html += "<div id='" + comp.componentPath + "_action_value' class='action action_main action_first' onClick='executeAction(this,\"" + value_path + ");'>Set Value</div>";
    html += "<input id='" + value_input_id + "' class='action action_main' type='text' style='margin: 10px; width: 100px;'>";
    return html;
}


// Object's Info

// htmlObjectContentInfo()
// fetchObjectContentInfo()
// -> fillObjectContentInfo_Obj()
// -> fillObjectContentInfo_Struct()

function htmlObjectContentInfo(objId) {
    setTitle("<p><a href='javascript:void(0);' onclick='showObjectContent(&quot;" + obj.id + "&quot;,true)'>Object</a> > Info</p>");

    var tableStyle = "width: 70%; margin: auto;"
    var html = "";

    var contentId = "obj_" + objId + "_content";
    html += "<div id='" + contentId + "'>";
    html += "    <h2>Info</h2>";
    html += "    <div style='" + tableStyle + "'>";
    html += "        <table class='table_details'>";
    html += "            <tr><td class='label'>ID</td><td class='value'><span id='val_obj_id'>...</span></td></tr>";
    html += "            <tr><td class='label'>Name</td><td class='value'><span id='val_obj_name'>...</span></td></tr>";
    html += "            <tr><td class='label'>Current Access Level</td><td class='value'><span id='val_obj_permission'>...</span></td></tr>";
    html += "            <tr><td class='label'>Owner</td><td class='value'><span id='val_obj_owner'>...</span></td></tr>";
    html += "            <tr><td class='label'>Brand</td><td class='value'><span id='val_obj_brand'>...</span></td></tr>";
    html += "            <tr><td class='label'>Model</td><td class='value'><span id='val_obj_model'>...</span></td></tr>";
    html += "            <tr><td class='label'>Description</td><td class='value'><span id='val_obj_description'>...</span></td></tr>";
    html += "            <tr><td class='label'></td><td class='value'><span id='val_obj_descrLong'>...</span></td></tr>";
    html += "        </table>";
    html += "    </div>";

    html += "<hr>";

    html += "    <h2>Communication</h2>";
    html += "    <div style='" + tableStyle + "'>";
    html += "        <table class='table_details'>";
    html += "            <tr><td class='label'>Is Connected</td><td class='value'><span id='val_obj_isConnected'>...</span></td></tr>";
    html += "            <tr><td class='label'>Is Cloud Connected</td><td class='value'><span id='val_obj_isCloudConnected'>...</span></td></tr>";
    html += "            <tr><td class='label'>Is Local Connected</td><td class='value'><span id='val_obj_isLocalConnected'>...</span></td></tr>";
    html += "        </table>";
    html += "    </div>";

    html += "<hr>";

    html += "    <h2>Versions</h2>";
    html += "    <div style='" + tableStyle + "'>";
    html += "        <table class='table_details'>";
    html += "            <tr><td class='label'>JOD Version</td><td class='value'><span id='val_obj_jodVersion'>...</span></td></tr>";
    html += "        </table>";
    html += "    </div>";

    html += "</div>";

    return html;
}

function fetchObjectContentInfo(objId) {
    apiGET("/apis/objsmngr/1.0/" + objId + "/",fillObjectContentInfo_Obj,onErrorFetch);
    apiGET("/apis/structure/1.0/" + objId + "/",fillObjectContentInfo_Struct,onErrorFetch);
}

function fillObjectContentInfo_Obj(objJson) {
    obj = JSON.parse(objJson);

    if (document.getElementById("val_obj_id") != null)
        document.getElementById("val_obj_id").innerHTML = obj.id;
    if (document.getElementById("val_obj_name") != null)
        document.getElementById("val_obj_name").innerHTML = obj.name;
    if (document.getElementById("val_obj_permission") != null)
        document.getElementById("val_obj_permission").innerHTML = obj.permission;
    if (document.getElementById("val_obj_owner") != null)
        document.getElementById("val_obj_owner").innerHTML = obj.owner;
    if (document.getElementById("val_obj_owner_editable") != null) {
        var html = "";
        html = "<span>";
        html += "    " + obj.owner;
        html += "    <a href='javascript:void(0);' onclick='editObjectOwner_Field(this.parentElement,\"" + obj.owner + "\");'>";
        html += "        <i class='fa fa-pencil' style='font-size: 0.8em;'></i>";
        html += "    </a>";
        html += "</span>";
        document.getElementById("val_obj_owner_editable").innerHTML = html;
    }
    if (document.getElementById("val_obj_isConnected") != null)
        document.getElementById("val_obj_isConnected").innerHTML = obj.isConnected;
    if (document.getElementById("val_obj_isCloudConnected") != null)
        document.getElementById("val_obj_isCloudConnected").innerHTML = obj.isCloudConnected;
    if (document.getElementById("val_obj_isLocalConnected") != null)
        document.getElementById("val_obj_isLocalConnected").innerHTML = obj.isLocalConnected;
    if (document.getElementById("val_obj_jodVersion") != null)
        document.getElementById("val_obj_jodVersion").innerHTML = obj.jodVersion;
}

function fillObjectContentInfo_Struct(objStructJson) {
    objStruct = JSON.parse(objStructJson);
    if (document.getElementById("val_obj_brand") != null)
        document.getElementById("val_obj_brand").innerHTML = objStruct.brand;
    if (document.getElementById("val_obj_description") != null)
        document.getElementById("val_obj_description").innerHTML = objStruct.description;
    if (document.getElementById("val_obj_descrLong") != null)
        document.getElementById("val_obj_descrLong").innerHTML = objStruct.descrLong;
    if (document.getElementById("val_obj_model") != null)
        document.getElementById("val_obj_model").innerHTML = objStruct.model;
}


// Object's Access Control

// htmlObjectContentAccessControl()
// fetchObjectContentAccessControl()
// -> fillObjectContentAccessControl_Perms()

function htmlObjectContentAccessControl(objId) {
    setTitle("<p><a href='javascript:void(0);' onclick='showObjectContent(&quot;" + objId + "&quot;,true)'>Object</a> > Access Control</p>");

    var tableStyle = "width: 70%; margin: auto;"
    var html = "";
    var tableId = "table_" + objId + "_perms";
    var linksId = "links_" + objId + "_perms";

    var contentId = "obj_" + objId + "_content";
    html += "<div id='" + contentId + "'>";

    html += "    <h2>Access Control</h2>";

    html += "    <p>Object's owner '<b id='val_obj_owner_editable'>...</b>'</p>";
    html += "    <p>Current service/user has '<b id='val_obj_permission'>...</b>' permission on object</p>";

    html += "    <div class='obj_perms'>";
    html += "       <table id='" + tableId + "'></table>";
    html += "    </div>";

    html += "    <ul id='" + linksId + "'>";
    html += "        <li>Add ";
    html += "            <a href='javascript:void(0);' onclick='addPermission_Row(\"" + tableId + "\",\"" + objId + "\",\"#All\",\"#All\",\"Actions\",\"OnlyLocal\",true)'>Public for Local</a>";
    html += "            permission";
    html += "        </li>";
    html += "        <li>Add ";
    html += "            <a href='javascript:void(0);' onclick='addPermission_Row(\"" + tableId + "\",\"" + objId + "\",\"#All\",\"#All\",\"Actions\",\"LocalAndCloud\",true)'>Public for All</a>";
    html += "            permission";
    html += "        </li>";
    html += "        <li>Add custom permission ";
    html += "            <a href='javascript:void(0);' onclick='addCustomPermission_Row(\"" + tableId + "\",\"" + objId + "\")'><i class='fa fa-plus-square-o'></i></a>";
    html += "        </li>";
    html += "    </ul>";

    html += "</div>";

    return html;
}

function fetchObjectContentAccessControl(objId) {
    apiGET("/apis/objsmngr/1.0/" + objId + "/",fillObjectContentInfo_Obj,onErrorFetch);
    apiGET("/apis/permissions/1.0/" + objId + "/",fillObjectContentAccessControl_Perms,onErrorFetch);
}

function fillObjectContentAccessControl_Perms(objPermsJson) {
    objPerms = JSON.parse(objPermsJson);

    var html = "        <tr>";
    html += "        <th>Service's ID</th>";
    html += "        <th>User's ID</th>";
    html += "        <th>Permission</th>";
    html += "        <th>Connection Type</th>";
    html += "        <th>Actions</th>";
    html += "        </tr>";
    for (var i = 0; i < objPerms.length; i++) {
        html += editablePermission_Row(objPerms[i].id,objPerms[i].objId,objPerms[i].srvId,objPerms[i].usrId,objPerms[i].type,objPerms[i].connection);
    }

    var tableId = "table_" + objPerms[0].objId + "_perms";
    if (document.getElementById(tableId) != null)
        document.getElementById(tableId).innerHTML = html;
}


// Editables

// Editable Name (ob)
var _originalName = null;

function editableObjectName_Title(objName) {
    var html = "<h1>" + objName + "<a href='javascript:void(0);' onclick='editObjectName_Title(this.parentElement,\"" + objName + "\");'><i class='fa fa-pencil' style='font-size: 0.8em;'></i></a></h1>";
    return html;
}

function editObjectName_Title(headerTag,defaultValue) {
    _originalName = defaultValue;
    var html = "<input id='obj_title_" + detailObjId + "' type='text' value='" + defaultValue + "''>";
    html += "<a href='javascript:void(0);' onclick='saveObjectName_Title(this.parentElement);'><i class='fa fa-check-circle-o' style='font-size: 0.8em;'></i></a>";
    html += "<a href='javascript:void(0);' onclick='replace(this.parentElement,editableObjectName_Title(_originalName));'><i class='fa fa-times' style='font-size: 0.8em;'></i></a>";

    headerTag.innerHTML = html;
}

function saveObjectName_Title(containerTag) {
    var input = containerTag.getElementsByTagName('input')[0];
    if (input==null) {
        alert("Errore");
        return;
    }

    var actionUrl = "/apis/objsmngr/1.0/" + detailObjId + "/name/";
    apiPOST(actionUrl,
        async function(responseText) {
            hideWaitingFeedback(containerTag.id);
            if (responseText == "true") {
                replace(containerTag,editableObjectName_Title(input.value));
                _originalName = null;
                await new Promise(r => setTimeout(r, 500));
                fetchObjsListMenu();
                return;
            }

            alert("Error on executing: " + actionUrl + "<br>" + responseText);
            replace(containerTag,editableObjectName_Title(_originalName));
            _originalName = null;
            showFailFeedback(containerTag.id);
        },
        function(xhttpRequest) {
            hideWaitingFeedback(containerTag.id);
            alert("Error on executing: " + actionUrl + "<br>" + xhttpRequest.responseText);
            replace(containerTag,editableObjectName_Title(_originalName));
            _originalName = null;
            showFailFeedback(containerTag.id);
        },
        "new_name=" + input.value
    );
    showWaitingFeedback(input.id);
}

// Editable Owner (ob)
var _originalOwner = null;

function editableObjectOwner_Field(objOwner) {
    var html = "<td class='value'>" + objOwner;
    html += "           <a href='javascript:void(0);' onclick='editObjectOwner_Field(this.parentElement,\"" + objOwner + "\");'><i class='fa fa-pencil' style='font-size: 0.8em;'></i></a>";
    html += "        </td>";
    return html;
}

function editObjectOwner_Field(headerTag,defaultValue) {
    _originalOwner = defaultValue;

    var html = "        <td id='obj_owner_" + detailObjId + "' class='value'>";
    //html += "           <input type='text' value='" + obj['owner'] + "'>"
    html += "<input list='usrId' value='" + obj['owner'] + "'>";
    html += "<datalist id='usrId'>";
    html += "  <option value='00000-00000-00000'>No Owner - Any user</option>";
    if (loggedUserId!=null)
        html += "  <option value='" + loggedUserId + "'>Current User</option>";
    if (obj['owner']!="00000-00000-00000" && (loggedUserId!=null || obj['owner']!=loggedUserId))
        html += "  <option value='" + obj['owner'] + "'>Latest</option>";
    html += "</datalist>";
    html += "           <a href='javascript:void(0);' onclick='saveObjectOwner_Field(this.parentElement,\"" + obj['owner'] + "\");'><i class='fa fa-check-circle-o'></i></a>";
    html += "           <a href='javascript:void(0);' onclick='replace(this.parentElement,editableObjectOwner_Field(_originalOwner));'><i class='fa fa-times'></i></a>";
    html += "        </td>";

    headerTag.innerHTML = html;
}

function saveObjectOwner_Field(containerTag) {
    var input = containerTag.getElementsByTagName('input')[0];
    if (input==null) {
        alert("Errore");
        return;
    }

    var actionUrl = "/apis/objsmngr/1.0/" + detailObjId + "/owner/";
    apiPOST(actionUrl,
    function(responseText) {
        hideWaitingFeedback(containerTag.id);
        if (responseText == "true") {
            replace(containerTag,editableObjectOwner_Field(input.value));
            _originalOwner = null;
            return;
        }

        alert("Error on executing: " + actionUrl + "<br>" + responseText);
        replace(containerTag,editableObjectOwner_Field(_originalOwner));
        _originalOwner = null;
        showFailFeedback(containerTag.id);
    },
    function(xhttpRequest) {
        hideWaitingFeedback(containerTag.id);
        alert("Error on executing: " + actionUrl + "<br>" + xhttpRequest.responseText);
        replace(containerTag,editableObjectOwner_Field(_originalOwner));
        _originalOwner = null;
        showFailFeedback(containerTag.id);
    },
    "new_owner=" + input.value);
    showWaitingFeedback(containerTag.id);
}


// Utils

function executeAction(element,actionUrl) {
            showWaitingFeedback(element.id);

            apiGET(actionUrl,
            function(responseText) {
                hideWaitingFeedback(element.id);
                if (responseText == "true")
                    return;

                alert("Error on executing: " + actionUrl + "<br>" + responseText);
                showFailFeedback(element.id);
            },
            function(xhttpRequest) {
                hideWaitingFeedback(element.id);
                alert("Error on executing: " + actionUrl + "<br>" + xhttpRequest.responseText);
                showFailFeedback(element.id);
            });
        }



// ???Update

//emitStateUpd () ->
function fetchComponent(objId,compPath) {
    apiGET("/apis/structure/1.0/" + objId + "/" + compPath + "/",fillComponent,onErrorFetch);
}

function fillComponent(compJson) {
    comp = JSON.parse(compJson);
    document.getElementById(comp.componentPath).innerHTML = htmlComponent(comp);
    // current method is called only from "SSE -> emitStateUpd(eventText) -> fetchComponent(objId,compPath)" stack
    showUpdateFeedback(comp.componentPath + "_state");
}