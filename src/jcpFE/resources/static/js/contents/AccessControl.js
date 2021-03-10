const PAGE_ACCESS_CONTROL = "share";
const PAGE_ACCESS_CONTROL_TITLE = "Access Control Center";

// showAccessControl()
// -> htmlAccessControlContent()
// -> fetchAccessControlContent()
//   -> fetchAccessControlContentObject()
//     -> fillAccessControlContentObject_Add()
//       -> objectPermissionsToHtmlBox()
//       -> htmlAccessControlContentObject()

function showAccessControl(updateHistory) {
    if (updateHistory) setNewPageHistory(PAGE_ACCESS_CONTROL, PAGE_ACCESS_CONTROL_TITLE);

    currentPage = PAGE_ACCESS_CONTROL;
    setContent(htmlAccessControlContent());
    fetchAccessControlContent();
}

function htmlAccessControlContent() {
    setTitle(PAGE_ACCESS_CONTROL_TITLE);

    var html = "<div id='div_permissions'>";
    html += styleObjectContentAccessControl();
    html += "</div>";
    return html;
}

function fetchAccessControlContent(objId) {
    // Fetch the object's list and for each object fetch his permission with fetchAccessControlContentObject() method
    apiGET(backEndUrl,pathJSLWB_Objs,function(objsListJson) {
        var objsList = JSON.parse(objsListJson);

        for (i = 0; i < objsList.length; i++)
            fetchAccessControlContentObject(objsList[i].id,objsList[i].name,objsList[i].owner,objsList[i].isCloudConnected,objsList[i].permission);
    },onErrorFetch);
}

function fetchAccessControlContentObject(objId,objName,objOwner,objConnected,currentPermissions) {
    apiGET(backEndUrl,pathJSLWB_Perms_Obj.replace("{obj_id}",objId),function(permsListJson) {
        fillAccessControlContentObject_Add(permsListJson,objName,objOwner,objConnected,currentPermissions);
    },onWarningFetch);
}

function fillAccessControlContentObject_Add(permsListJson,objName,objOwner,isConnected,currentPermissions) {
    permsList = JSON.parse(permsListJson);

    // Update div_struct
    appendInnerHTMLById("div_permissions",htmlAccessControlContentObject(permsList,objName,objOwner,isConnected,currentPermissions));
}

function htmlAccessControlContentObject(permsList,objName,objOwner,isConnected,currentPermissions) {
    var html = "";
//[ {
//"id" : "VNIULWJRCXOIWCXRVFFZ",
//"objId" : "DYDFL-DNKVK-OHACN",
//"srvId" : "#All",
//"usrId" : "#Owner",
//"type" : "CoOwner",
//"connection" : "LocalAndCloud",
//"lastUpdate" : 1598361570253,
//"pathUpd" : "/apis/permissions/1.0/DYDFL-DNKVK-OHACN/upd/VNIULWJRCXOIWCXRVFFZ/",
//"pathDel" : "/apis/permissions/1.0/DYDFL-DNKVK-OHACN/del/VNIULWJRCXOIWCXRVFFZ/",
//"pathDup" : "/apis/permissions/1.0/DYDFL-DNKVK-OHACN/dup/VNIULWJRCXOIWCXRVFFZ/"
//} ]

    if (permsList.length==0)
        return "No permissions fetched.";

    var divId = "div_" + permsList[0].objId + "_perms";
    var titleId = "title_" + permsList[0].objId + "_perms";
    var tableId = "table_" + permsList[0].objId + "_perms";
    var linksId = "links_" + permsList[0].objId + "_perms";

    html += "<div id='" + divId + "' class='box obj_perms" + (!isConnected ? " disabled" : "") + "'>";
    html += htmlBoxExpandable();

    html += "    <h2 id='" + titleId + "'>";
    html += "       " + objName;
    html += "       <span style='color:gray;font-size:0.7em;'>(" + permsList[0].objId + ")</span>";
    html += "    </h2>";

    html += "    <p>Object's owner <b>'" + objOwner + "'</b></p>";
    html += "    <p>Current service/user has <b>'" + currentPermissions + "'</b> permission on object</p>";

    html += "    <table id='" + tableId + "'>";
    html += "        <tr>";
    html += "        <th>Service's ID</th>";
    html += "        <th>User's ID</th>";
    html += "        <th>Permission</th>";
    html += "        <th>Connection Type</th>";
    html += "        <th>Actions</th>";
    html += "        </tr>";
    for (var i = 0; i < permsList.length; i++) {
        html += editablePermission_Row(permsList[i].id,permsList[i].objId,permsList[i].srvId,permsList[i].usrId,permsList[i].type,permsList[i].connection);
    }
    html += "    </table>";

    html += "    <ul id='" + linksId + "'>";
    html += "        <li>Add ";
    html += "            <a href='javascript:void(0);' onclick='addPermission_Row(\"" + tableId + "\",\"" + permsList[0].objId + "\",\"#All\",\"#All\",\"Actions\",\"OnlyLocal\",true)'>Public for Local</a>";
    html += "            permission";
    html += "        </li>";
    html += "        <li>Add ";
    html += "            <a href='javascript:void(0);' onclick='addPermission_Row(\"" + tableId + "\",\"" + permsList[0].objId + "\",\"#All\",\"#All\",\"Actions\",\"LocalAndCloud\",true)'>Public for All</a>";
    html += "            permission";
    html += "        </li>";
    html += "        <li>Add custom permission ";
    html += "            <a href='javascript:void(0);' onclick='addCustomPermission_Row(\"" + tableId + "\",\"" + permsList[0].objId + "\")'><i class='fa fa-plus-square-o'></i></a>";
    html += "        </li>";
    html += "    </ul>";

    html += "</div>";

    return html;
}


// Editables

// Editable Permission Row
var defaultPermissionUpdate = 5000;
var _originalPermId = [];
var _originalObjId = [];
var _originalSrvId = [];
var _originalUsrId = [];
var _originalPermType = [];
var _originalConnType = [];

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

    var pathUpd = pathJSLWB_Perms_Obj_Upd.replace("{obj_id}",objId).replace("{perm_id}",permId);
    var pathDel = pathJSLWB_Perms_Obj_Del.replace("{obj_id}",objId).replace("{perm_id}",permId);
    var pathDup = pathJSLWB_Perms_Obj_Dup.replace("{obj_id}",objId).replace("{perm_id}",permId);
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
    replaceInnerHTML(cols[0],srvInput);

    var usrInput = "<input list='usrId' value='" + usrId + "'>";
    usrInput += "<datalist id='usrId'>";
    usrInput += "  <option value='#All'>Any user</option>";
    usrInput += "  <option value='#Owner'>Only object's Owner</option>";
    if (loggedUserId!=null)
        usrInput += "  <option value='" + loggedUserId + "'>Current User</option>";
    if (usrId!="#All" && usrId!="#Owner" && (loggedUserId!=null || usrId!=loggedUserId))
        usrInput += "  <option value='" + usrId + "'>Latest</option>";
    usrInput += "</datalist>";
    replaceInnerHTML(cols[1],usrInput);

    var permInput = "<input list='permType' value='" + permType + "'>";
    permInput += "<datalist id='permType'>";
    permInput += "  <option value='None'>Deny any object's access</option>";
    permInput += "  <option value='Status'>Can read object's info and states</option>";
    permInput += "  <option value='Actions'>Can execute object's actions</option>";
    permInput += "  <option value='CoOwner'>Can set object's perms and owners</option>";
    permInput += "</datalist>";
    replaceInnerHTML(cols[2],permInput);

    var connInput = "<input list='connType' value='" + connType + "'>";
    connInput += "<datalist id='connType'>";
    connInput += "  <option value='OnlyLocal'>Access granted Only on Local connections</option>";
    connInput += "  <option value='LocalAndCloud'>Access granted for Cloud & Local connections</option>";
    connInput += "</datalist>";
    replaceInnerHTML(cols[3],connInput);
    replaceInnerHTML(cols[4],"<a href='javascript:void(0);' onclick='savePermission_Row(this.parentElement.parentElement,\"" + updUrl + "\",\"" + objId + "\"," + (permId!=null ? "\"" + permId + "\"" : "null") + ")'><i class='fa fa-check-circle-o'></i></a>");
    if (permId==null)
        appendInnerHTML(cols[4],"<a href='javascript:void(0);' onclick='this.parentElement.parentElement.parentElement.removeChild(this.parentElement.parentElement);'><i class='fa fa-times'></i></a>");
    else {
        appendInnerHTML(cols[4],"<a href='javascript:void(0);' onclick='replace(this.parentElement.parentElement,editablePermission_Row(_originalPermId[\"" + permId_ + "\"],_originalObjId[\"" + permId_ + "\"],_originalSrvId[\"" + permId_ + "\"],_originalUsrId[\"" + permId_ + "\"],_originalPermType[\"" + permId_ + "\"],_originalConnType[\"" + permId_ + "\"]));'><i class='fa fa-times'></i></a>");
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

    apiPOST(backEndUrl,updUrl,
        async function(responseText) {
            hideWaitingFeedbackByIdTag(rowTag.id);
            if (responseText == "true") {
                if (permId==null) {
                    replace(rowTag,editablePermission_Row(permId,objId,srvIdInput.value,usrIdInput.value,permTypeInput.value,connTypeInput.value));
                    await new Promise(r => setTimeout(r, defaultPermissionUpdate));
                    //showAccessControl();
                    fillRequiredContent(document.location);
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
            showFailFeedbackByIdTag(containerTag.id);
        },
        function(xhttpRequest) {
            hideWaitingFeedbackByIdTag(rowTag.id);
            alert("Error on executing: " + updUrl + "<br>" + xhttpRequest.responseText);
            replace(rowTag,editablePermission_Row(_originalPermId[permId_],_originalObjId[permId_],_originalSrvId[permId_],_originalUsrId[permId_],_originalPermType[permId_],_originalConnType[permId_]));
            _originalPermId[permId_] = null;
            _originalObjId[permId_] = null;
            _originalSrvId[permId_] = null;
            _originalUsrId[permId_] = null;
            _originalPermType[permId_] = null
            _originalConnType[permId_] = null;
            showFailFeedbackByIdTag(containerTag.id);
        },
        "srv_id=" + srvIdInput.value + "&usr_id=" + usrIdInput.value + "&type=" + permTypeInput.value + "&conn=" + connTypeInput.value
    );
    showWaitingFeedbackByIdTag(rowTag.id);
}

function clonePermission_Row(rowTag,dupUrl,objId,permId) {
    if (checkPermission()) return;

    apiGET(backEndUrl,dupUrl,
        async function(responseText) {
            hideWaitingFeedbackByIdTag(rowTag.id);
            if (responseText == "true") {
                await new Promise(r => setTimeout(r, defaultPermissionUpdate));
                //showAccessControl();
                fillRequiredContent(document.location);
                return;
            }

            alert("Error on executing: " + dupUrl + "<br>" + responseText);
            showFailFeedbackByIdTag(containerTag.id);
        },
        function(xhttpRequest) {
            hideWaitingFeedbackByIdTag(rowTag.id);
            alert("Error on executing: " + dupUrl + "<br>" + xhttpRequest.responseText);
            showFailFeedbackByIdTag(containerTag.id);
        }
    );
    showWaitingFeedbackByIdTag(rowTag.id);
}

function deletePermission_Row(rowTag,delUrl,objId,permId) {
    if (checkPermission()) return;

    apiGET(backEndUrl,delUrl,
        async function(responseText) {
            hideWaitingFeedbackByIdTag(rowTag.id);
            if (responseText == "true") {
                await new Promise(r => setTimeout(r, defaultPermissionUpdate));
                //showAccessControl();
                fillRequiredContent(document.location);
                return;
            }

            alert("Error on executing: " + delUrl + "<br>" + responseText);
            showFailFeedbackByIdTag(containerTag.id);
        },
        function(xhttpRequest) {
            hideWaitingFeedbackByIdTag(rowTag.id);
            alert("Error on executing: " + delUrl + "<br>" + xhttpRequest.responseText);
            showFailFeedbackByIdTag(containerTag.id);
        }
    );
    showWaitingFeedbackByIdTag(rowTag.id);
}

function addCustomPermission_Row(tableTagId,objId) {
    return addPermission_Row(tableTagId,objId,"#All","#All","None","OnlyLocal",false);
}

function addPermission_Row(tableTagId,objId,srvId,usrId,permType,connType,save) {
    if (checkPermission()) return;

    var tableTag = document.getElementById(tableTagId);
    var tBodyTag = tableTag.getElementsByTagName("tbody")[0];

    appendInnerHTML(tBodyTag,editablePermission_Row(null,objId,srvId,usrId,permType,connType));
    var rowTag = tBodyTag.lastElementChild;

    editPermission_Row(rowTag,pathJSLWB_Perms_Obj_Add.replace("{obj_id}",objId),objId,null);
    if (save)
        savePermission_Row(rowTag,pathAdd,objId,null);
}

function checkPermission() {
    if (obj.permission!="CoOwner") {
        alert("Can't add new permission, because 'CoOwner' permission required.");
        return false;
    }
}

// Utils

function replace(tag,html) {
    var parent = tag.parentElement;
    parent.removeChild(tag);
    appendInnerHTML(parent,html);
}

