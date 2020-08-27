/*********************+
    Functions
 **********************/

/*
 * This file provide functions to control the JCP FE UI: show main content, display action's feedback, made editable
 * UI fields and manage the browser history.
 *
 * show{PAGE}()         set page content
 * show/hide{XY}()      display actions feedback
 * editable{XY}()       display editable fields (method for edit and save)
 * history()            history management methods
 */

// Functions vars

// Store the page name of current displayed content.
var currentPage = false;

// Page's names
const PAGE_OBJ_DETAILS = "objDetails";
const PAGE_OBJS_LIST = "objsList";
const PAGE_USR_INFO = "usrInfo";
const PAGE_SRV_INFO = "srvInfo";
const PAGE_ABOUT = "about";
const PAGE_SHARE = "share";


// Show content methods

function showObjectDetails(objId,updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_OBJ_DETAILS, "Object Details","objId=" + objId);

    currentPage = PAGE_OBJ_DETAILS;
    fetchObjDetails(objId);
}

function showObjects(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_OBJS_LIST, "Objects List");

    currentPage = PAGE_OBJS_LIST;
    setContent(getContent_Objects_Html());
}

function showUser(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_USR_INFO, "User Info");

    currentPage = PAGE_USR_INFO;
    setContent(getContent_User_Html());
    fetchUser();
}

function showService(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_SRV_INFO,"JCP FE Info");

    currentPage = PAGE_SRV_INFO;
    setContent(getContent_Service_Html());
    fetchService();
}

function showAbout(updateHistory) {
    //if (updateHistory) setNewPageHistory(PAGE_ABOUT,"JCP FE About");

    //currentPage = PAGE_ABOUT;
    //setContent(getContent_About_Html());
}

function showShare(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_SHARE,"Object Sharing");

    currentPage = PAGE_SHARE;
    setContent(getContent_Share_Html());
    fetchAllPermissions();
}

        function execute(element,actionUrl) {
            showWaitingButton(element.id);

            apiGET(actionUrl,
            function(responseText) {
                hideWaitingButton(element.id);
                if (responseText == "true")
                    return;

                alert("Error on executing: " + actionUrl + "<br>" + responseText);
                showFailButton(element.id);
            },
            function(xhttpRequest) {
                hideWaitingButton(element.id);
                alert("Error on executing: " + actionUrl + "<br>" + xhttpRequest.responseText);
                showFailButton(element.id);
            });
        }


// Feedback methods

function showUpdateField(tagId) {
    var element = document.getElementById(tagId);

    var oldStyle = element.style.textShadow;
    element.style.textShadow='0 0 15px #2c64ba';

    window.setTimeout(function() {
        var element = document.getElementById(tagId);
        element.style.textShadow = oldStyle;
    },1000);
}

function showWaitingButton(tagId) {
    var elem = document.getElementById(tagId);

    var ico = document.createElement("i");
    ico.id = tagId + "_waitIco";
    ico.className = "fa fa-spinner fa-spin"

    elem.appendChild(ico);
}

function hideWaitingButton(tagId) {
    var elem = document.getElementById(tagId + "_waitIco");
    if (elem==null)
        return;

    var parent = elem.parentNode;
    parent.removeChild(elem);

    showSuccessButton(parent.id);
}

function showSuccessButton(tagId) {
    var elem = document.getElementById(tagId);

    var ico = document.createElement("i");
    ico.id = tagId + "_successIco";
    ico.className = "fa fa-check-circle-o"

    elem.appendChild(ico);

    setTimeout(function() {
        elem.removeChild(ico);
    }, 1000);
}

function showFailButton(tagId) {
    var elem = document.getElementById(tagId);

    var ico = document.createElement("i");
    ico.id = tagId + "_failIco";
    ico.className = "fa fa-exclamation-circle"

    elem.appendChild(ico);

    setTimeout(function() {
        elem.removeChild(ico);
    }, 1000);
}


// Editable fields

var _originalName = null;
var _originalOwner = null;
var _originalPermId = [];
var _originalObjId = [];
var _originalSrvId = [];
var _originalUsrId = [];
var _originalPermType = [];
var _originalConnType = [];

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
            hideWaitingButton(containerTag.id);
            if (responseText == "true") {
                replace(containerTag,editableObjectName_Title(input.value));
                _originalName = null;
                await new Promise(r => setTimeout(r, 500));
                fetchObjsList();
                return;
            }

            alert("Error on executing: " + actionUrl + "<br>" + responseText);
            replace(containerTag,editableObjectName_Title(_originalName));
            _originalName = null;
            showFailButton(containerTag.id);
        },
        function(xhttpRequest) {
            hideWaitingButton(containerTag.id);
            alert("Error on executing: " + actionUrl + "<br>" + xhttpRequest.responseText);
            replace(containerTag,editableObjectName_Title(_originalName));
            _originalName = null;
            showFailButton(containerTag.id);
        },
        "new_name=" + input.value
    );
    showWaitingButton(input.id);
}

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
        hideWaitingButton(containerTag.id);
        if (responseText == "true") {
            replace(containerTag,editableObjectOwner_Field(input.value));
            _originalOwner = null;
            return;
        }

        alert("Error on executing: " + actionUrl + "<br>" + responseText);
        replace(containerTag,editableObjectOwner_Field(_originalOwner));
        _originalOwner = null;
        showFailButton(containerTag.id);
    },
    function(xhttpRequest) {
        hideWaitingButton(containerTag.id);
        alert("Error on executing: " + actionUrl + "<br>" + xhttpRequest.responseText);
        replace(containerTag,editableObjectOwner_Field(_originalOwner));
        _originalOwner = null;
        showFailButton(containerTag.id);
    },
    "new_owner=" + input.value);
    showWaitingButton(containerTag.id);
}

function editablePermission_Row(permId,objId,srvId,usrId,permType,connType) {
    var style = "max-width: 150px;";
    style += "white-space: nowrap;";
    style += "overflow: hidden;";
    style += "text-overflow: ellipsis;";
    var elemId = "row_" + objId + "_" + permId;

    var html = "";
    html += "<tr id='" + elemId + "'>";
    html += "    <td><p>" + srvId + "</p></td>";
    html += "    <td><p>" + usrId + "</p></td>";
    html += "    <td><p>" + permType + "</p></td>";
    html += "    <td><p>" + connType + "</p></td>";
    html += "    <td class='btn_actions'>";

    var pathUpd = "/apis/permissions/1.0/" + objId + "/upd/" + permId + "/";
    var pathDel = "/apis/permissions/1.0/" + objId + "/del/" + permId + "/";
    var pathDup = "/apis/permissions/1.0/" + objId + "/dup/" + permId + "/";
    html += "        <a href='javascript:void(0);' onclick='editPermission_Row(this.parentElement.parentElement,\"" + pathUpd + "\",\"" + objId + "\",\"" + permId + "\")'><i class='fa fa-pencil'></i></a>";
    html += "        <a href='javascript:void(0);' onclick='clonePermission_Row(this.parentElement.parentElement,\"" + pathDup + "\",\"" + objId + "\",\"" + permId + "\")'><i class='fa fa-clone'></i></a>";
    html += "        <a href='javascript:void(0);' onclick='deletePermission_Row(this.parentElement.parentElement,\"" + pathDel + "\",\"" + objId + "\",\"" + permId + "\")'><i class='fa fa-trash'></i></a>";
    html += "    </td>";
    html += "</tr>";
    return html;
}

function editPermission_Row(rowTag,updUrl,objId,permId) {
    var cols = rowTag.getElementsByTagName("td");
    var srvId = cols[0].getElementsByTagName("p")[0].innerHTML;
    var usrId = cols[1].getElementsByTagName("p")[0].innerHTML;
    var permType = cols[2].getElementsByTagName("p")[0].innerHTML;
    var connType = cols[3].getElementsByTagName("p")[0].innerHTML;
    var actions = cols[4].innerHTML;

    var permId_ = permId!=null ? permId : "newPerm";
    _originalPermId[permId_] = permId;
    _originalObjId[permId_] = objId;
    _originalSrvId[permId_] = srvId;
    _originalUsrId[permId_] = usrId;
    _originalPermType[permId_] = permType
    _originalConnType[permId_] = connType;

    var srvInput = "<input list='srvId' value='" + srvId + "'>";
    srvInput += "<datalist id='srvId'>";
    srvInput += "  <option value='#All'>Any service</option>";
    if (serviceId!=null)
        srvInput += "  <option value='" + serviceId + "'>Current service</option>";
    if (srvId!="#All" && srvId!=serviceId)
        srvInput += "  <option value='" + srvId + "'>Latest</option>";
    srvInput += "</datalist>";
    cols[0].innerHTML = srvInput;

    var usrInput = "<input list='usrId' value='" + usrId + "'>";
    usrInput += "<datalist id='usrId'>";
    usrInput += "  <option value='#All'>Any user</option>";
    usrInput += "  <option value='#Owner'>Only object's Owner</option>";
    if (loggedUserId!=null)
        usrInput += "  <option value='" + loggedUserId + "'>Current User</option>";
    if (usrId!="#All" && usrId!="#Owner" && (loggedUserId!=null || usrId!=loggedUserId))
        usrInput += "  <option value='" + usrId + "'>Latest</option>";
    usrInput += "</datalist>";
    cols[1].innerHTML = usrInput;

    var permInput = "<input list='permType' value='" + permType + "'>";
    permInput += "<datalist id='permType'>";
    permInput += "  <option value='None'>Deny any object's access</option>";
    permInput += "  <option value='Status'>Can read object's info and states</option>";
    permInput += "  <option value='Actions'>Can execute object's actions</option>";
    permInput += "  <option value='CoOwner'>Can set object's perms and owners</option>";
    permInput += "</datalist>";
    cols[2].innerHTML = permInput;

    var connInput = "<input list='connType' value='" + connType + "'>";
    connInput += "<datalist id='connType'>";
    connInput += "  <option value='OnlyLocal'>Access granted Only on Local connections</option>";
    connInput += "  <option value='LocalAndCloud'>Access granted for Cloud & Local connections</option>";
    connInput += "</datalist>";
    cols[3].innerHTML = connInput;
    cols[4].innerHTML = "<a href='javascript:void(0);' onclick='savePermission_Row(this.parentElement.parentElement,\"" + updUrl + "\",\"" + objId + "\"," + (permId!=null ? "\"" + permId + "\"" : "null") + ")'><i class='fa fa-check-circle-o'></i></a>";
    if (permId==null)
        cols[4].innerHTML += "<a href='javascript:void(0);' onclick='this.parentElement.parentElement.parentElement.removeChild(this.parentElement.parentElement);'><i class='fa fa-times'></i></a>";
    else {
        cols[4].innerHTML += "<a href='javascript:void(0);' onclick='replace(this.parentElement.parentElement,editablePermission_Row(_originalPermId[\"" + permId_ + "\"],_originalObjId[\"" + permId_ + "\"],_originalSrvId[\"" + permId_ + "\"],_originalUsrId[\"" + permId_ + "\"],_originalPermType[\"" + permId_ + "\"],_originalConnType[\"" + permId_ + "\"]));'><i class='fa fa-times'></i></a>";
    }
}

function savePermission_Row(rowTag,updUrl,objId,permId) {
    var cols = rowTag.getElementsByTagName("td");
    var srvIdInput = cols[0].getElementsByTagName('input')[0];
    var usrIdInput = cols[1].getElementsByTagName('input')[0];
    var permTypeInput = cols[2].getElementsByTagName('input')[0];
    var connTypeInput = cols[3].getElementsByTagName('input')[0];

    if (srvIdInput==null && usrIdInput==null && permTypeInput==null && connTypeInput==null) {
        alert("Errore input");
        return;
    }

    var permId_ = permId!=null ? permId : "newPerm";

    apiPOST(updUrl,
        async function(responseText) {
            hideWaitingButton(rowTag.id);
            if (responseText == "true") {
                if (permId==null) {
                    replace(rowTag,editablePermission_Row(permId,objId,srvIdInput.value,usrIdInput.value,permTypeInput.value,connTypeInput.value));
                    await new Promise(r => setTimeout(r, 2000));
                    showShare();
                    return;
                }
                replace(rowTag,editablePermission_Row(permId,objId,srvIdInput.value,usrIdInput.value,permTypeInput.value,connTypeInput.value));

                _originalPermId[permId_] = null;
                _originalObjId[permId_] = null;
                _originalSrvId[permId_] = null;
                _originalUsrId[permId_] = null;
                _originalPermType[permId_] = null
                _originalConnType[permId_] = null;
                return;
            }

            alert("Error on executing: " + updUrl + "<br>" + responseText);
            replace(rowTag,editablePermission_Row(_originalPermId[permId_],_originalObjId[permId_],_originalSrvId[permId_],_originalUsrId[permId_],_originalPermType[permId_],_originalConnType[permId_]));
            _originalPermId[permId_] = null;
            _originalObjId[permId_] = null;
            _originalSrvId[permId_] = null;
            _originalUsrId[permId_] = null;
            _originalPermType[permId_] = null
            _originalConnType[permId_] = null;
            showFailButton(containerTag.id);
        },
        function(xhttpRequest) {
            hideWaitingButton(rowTag.id);
            alert("Error on executing: " + updUrl + "<br>" + xhttpRequest.responseText);
            replace(rowTag,editablePermission_Row(_originalPermId[permId_],_originalObjId[permId_],_originalSrvId[permId_],_originalUsrId[permId_],_originalPermType[permId_],_originalConnType[permId_]));
            _originalPermId[permId_] = null;
            _originalObjId[permId_] = null;
            _originalSrvId[permId_] = null;
            _originalUsrId[permId_] = null;
            _originalPermType[permId_] = null
            _originalConnType[permId_] = null;
            showFailButton(containerTag.id);
        },
        "srv_id=" + srvIdInput.value + "&usr_id=" + usrIdInput.value + "&type=" + permTypeInput.value + "&conn=" + connTypeInput.value
    );
    showWaitingButton(rowTag.id);
}

function clonePermission_Row(rowTag,dupUrl,objId,permId) {
    apiGET(dupUrl,
        async function(responseText) {
            hideWaitingButton(rowTag.id);
            if (responseText == "true") {
                await new Promise(r => setTimeout(r, 2000));
                showShare();
                return;
            }

            alert("Error on executing: " + dupUrl + "<br>" + responseText);
            showFailButton(containerTag.id);
        },
        function(xhttpRequest) {
            hideWaitingButton(rowTag.id);
            alert("Error on executing: " + dupUrl + "<br>" + xhttpRequest.responseText);
            showFailButton(containerTag.id);
        }
    );
    showWaitingButton(rowTag.id);
}

function deletePermission_Row(rowTag,delUrl,objId,permId) {
    apiGET(delUrl,
        async function(responseText) {
            hideWaitingButton(rowTag.id);
            if (responseText == "true") {
                await new Promise(r => setTimeout(r, 2000));
                showShare();
                return;
            }

            alert("Error on executing: " + delUrl + "<br>" + responseText);
            showFailButton(containerTag.id);
        },
        function(xhttpRequest) {
            hideWaitingButton(rowTag.id);
            alert("Error on executing: " + delUrl + "<br>" + xhttpRequest.responseText);
            showFailButton(containerTag.id);
        }
    );
    showWaitingButton(rowTag.id);
}

function addCustomPermission_Row(tableTagId,objId) {
    return addPermission_Row(tableTagId,objId,"#All","#All","None","OnlyLocal",false);
}

function addPermission_Row(tableTagId,objId,srvId,usrId,permType,connType,save) {
    var tableTag = document.getElementById(tableTagId);
    var tBodyTag = tableTag.getElementsByTagName("tbody")[0];

    tBodyTag.innerHTML += editablePermission_Row(null,objId,srvId,usrId,permType,connType);
    var rowTag = tBodyTag.lastElementChild;

    var pathAdd = "/apis/permissions/1.0/" + objId + "/add/";
    editPermission_Row(rowTag,pathAdd,objId,null);
    if (save)
        savePermission_Row(rowTag,pathAdd,objId,null);
}

    function replace(tag,html) {
        var parent = tag.parentElement;
        parent.removeChild(tag);
        parent.innerHTML += html;
    }


// History methods

function setNewPageHistory(page,title) {
    setNewPageHistory(page,title,null);
}

function setNewPageHistory(page,title,extra) {
    window.history.pushState("{page: '" + page + "'}", title, "?page=" + page + (extra!=null ? "&" + extra : ""));
}

window.onpopstate = function(event) {
    fillRequiredContent(document.location,false);
};