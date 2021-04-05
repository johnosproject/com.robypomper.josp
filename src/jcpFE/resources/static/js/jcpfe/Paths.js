
// Front End API's paths

var _pathFEVersion = "2.0"
var _pathFE_EP                          = "apis/pub/frontend/entrypoint/" + _pathFEVersion;
var pathFE_EP_Entrypoint                = _pathFE_EP + "/entrypoint";
var pathFE_EP_Session                   = _pathFE_EP + "/jslwbsession?session_id={session_id}";


// JSL Web Bridge Core API's paths

var _pathJSLWBCoreVersion = "2.0"
var _pathJSLWB_Init                     = "apis/pub/jslwebbridge/core/init/" + _pathJSLWBCoreVersion;
var pathJSLWB_Init_Entrypoint           = _pathJSLWB_Init + "/status";
var pathJSLWB_Init_Sse                  = _pathJSLWB_Init + "/sse";
var _pathJSLWB_Srv                      = "apis/pub/jslwebbridge/core/service/" + _pathJSLWBCoreVersion;
var pathJSLWB_Srv                       = _pathJSLWB_Srv + "/";
var _pathJSLWB_Usr                      = "apis/pub/jslwebbridge/core/user/" + _pathJSLWBCoreVersion;
var pathJSLWB_Usr_Login                 = _pathJSLWB_Usr + "/login/";
var pathJSLWB_Usr_Login_Redirect        = _pathJSLWB_Usr + "/login/?redirect_uri={redirect_uri}";
var pathJSLWB_Usr_Registration_Redirect = _pathJSLWB_Usr + "/registration/?redirect_uri={redirect_uri}";
var pathJSLWB_Usr_Logout_Redirect       = _pathJSLWB_Usr + "/logout/?redirect_uri={redirect_uri}";
var pathJSLWB_Usr                       = _pathJSLWB_Usr + "/";
var _pathJSLWB_Objs                     = "apis/pub/jslwebbridge/core/objects/" + _pathJSLWBCoreVersion;
var pathJSLWB_Objs                      = _pathJSLWB_Objs + "/";
var pathJSLWB_ObjDetails                = _pathJSLWB_Objs + "/{obj_id}/";
var pathJSLWB_ObjEvents                 = _pathJSLWB_Objs + "/{obj_id}/events/";
var pathJSLWB_ObjName                   = _pathJSLWB_Objs + "/{obj_id}/name/";
var pathJSLWB_ObjOwner                  = _pathJSLWB_Objs + "/{obj_id}/owner/";
var _pathJSLWB_Struct                   = "apis/pub/jslwebbridge/core/objects/structure/" + _pathJSLWBCoreVersion;
var pathJSLWB_Struct                    = _pathJSLWB_Struct + "/{obj_id}/";
var pathJSLWB_StructComp                = _pathJSLWB_Struct + "/{obj_id}/{comp_path}/";
var _pathJSLWB_State                    = "apis/pub/jslwebbridge/core/objects/states/" + _pathJSLWBCoreVersion;
var pathJSLWB_State_Bool                = _pathJSLWB_State + "/bool/{obj_id}/{comp_path}/";
var pathJSLWB_State_Rance               = _pathJSLWB_State + "/range/{obj_id}/{comp_path}/";
var pathJSLWB_State_History             = _pathJSLWB_State + "/history/{obj_id}/{comp_path}/";
var _pathJSLWB_Perms                    = "apis/pub/jslwebbridge/core/objects/permissions/" + _pathJSLWBCoreVersion;
var pathJSLWB_Perms_Obj                 = _pathJSLWB_Perms + "/{obj_id}/";
var pathJSLWB_Perms_Obj_Add             = _pathJSLWB_Perms + "/{obj_id}/add/";
var pathJSLWB_Perms_Obj_Upd             = _pathJSLWB_Perms + "/{obj_id}/upd/{perm_id}/";
var pathJSLWB_Perms_Obj_Dup             = _pathJSLWB_Perms + "/{obj_id}/dup/{perm_id}/";
var pathJSLWB_Perms_Obj_Del             = _pathJSLWB_Perms + "/{obj_id}/del/{perm_id}/";


// JSL Web Bridge Admin API's paths

var _pathJSLWBAdminVersion = "2.0";
var pathJSLWB_Admin_CONST_APIs = "apis";
var pathJSLWB_Admin_CONST_GWs = "gateways";
var pathJSLWB_Admin_CONST_JSLWB = "jslwebbridge";
var pathJSLWB_Admin_CONST_FE = "frontend";


var _pathJSLWB_Admin                    = "apis/pub/jslwebbridge/admin/{service}";
var pathJSLWB_Admin_APIs_StatusIdx      = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_APIs) + "/status/" + _pathJSLWBAdminVersion + "/";
var pathJSLWB_Admin_APIs_Objs           = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_APIs) + "/status/" + _pathJSLWBAdminVersion + "/objects";
var pathJSLWB_Admin_APIs_Obj            = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_APIs) + "/status/" + _pathJSLWBAdminVersion + "/objects/{obj_id}";
var pathJSLWB_Admin_APIs_Srvs           = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_APIs) + "/status/" + _pathJSLWBAdminVersion + "/services";
var pathJSLWB_Admin_APIs_Srv            = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_APIs) + "/status/" + _pathJSLWBAdminVersion + "/services/{srv_id}";
var pathJSLWB_Admin_APIs_Usrs           = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_APIs) + "/status/" + _pathJSLWBAdminVersion + "/users";
var pathJSLWB_Admin_APIs_Usr            = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_APIs) + "/status/" + _pathJSLWBAdminVersion + "/users/{usr_id}";
var pathJSLWB_Admin_APIs_Gateways       = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_APIs) + "/status/" + _pathJSLWBAdminVersion + "/gateways";
var pathJSLWB_Admin_APIs_Gateway        = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_APIs) + "/status/" + _pathJSLWBAdminVersion + "/gateways/{gw_id}";
var pathJSLWB_Admin_GWs_StatusAll       = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_GWs) + "/status/" + _pathJSLWBAdminVersion + "/all";
var pathJSLWB_Admin_GWs_StatusIdx       = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_GWs) + "/status/" + _pathJSLWBAdminVersion + "/{gw_server_id}";
var pathJSLWB_Admin_GWs_Gateways        = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_GWs) + "/status/" + _pathJSLWBAdminVersion + "/{gw_server_id}/gws";
var pathJSLWB_Admin_GWs_Gateway         = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_GWs) + "/status/" + _pathJSLWBAdminVersion + "/{gw_server_id}/gws/{gw_id}";
var pathJSLWB_Admin_GWs_GatewayClient   = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_GWs) + "/status/" + _pathJSLWBAdminVersion + "/{gw_server_id}/gws/{gw_id}/{gw_client_id}";
var pathJSLWB_Admin_GWs_Broker          = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_GWs) + "/status/" + _pathJSLWBAdminVersion + "/{gw_server_id}/broker";
var pathJSLWB_Admin_GWs_BrokerObj       = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_GWs) + "/status/" + _pathJSLWBAdminVersion + "/{gw_server_id}/broker/obj/{obj_id}";
var pathJSLWB_Admin_GWs_BrokerSrv       = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_GWs) + "/status/" + _pathJSLWBAdminVersion + "/{gw_server_id}/broker/srv/{srv_id}";
var pathJSLWB_Admin_GWs_BrokerObjDB     = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_GWs) + "/status/" + _pathJSLWBAdminVersion + "/{gw_server_id}/broker/objdb/{obj_id}";
var pathJSLWB_Admin_JSLWB_StatusIdx     = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_JSLWB) + "/status/" + _pathJSLWBAdminVersion + "/";
var pathJSLWB_Admin_JSLWB_Sessions      = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_JSLWB) + "/status/" + _pathJSLWBAdminVersion + "/sessions";
var pathJSLWB_Admin_JSLWB_Session       = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_JSLWB) + "/status/" + _pathJSLWBAdminVersion + "/sessions/{session_id}";
var pathJSLWB_Admin_FE_StatusIdx        = _pathJSLWB_Admin.replace("{service}",pathJSLWB_Admin_CONST_FE) + "/status/" + _pathJSLWBAdminVersion + "/";

var pathJSLWB_Admin_BI                  = _pathJSLWB_Admin + "/buildinfo/" + _pathJSLWBAdminVersion + "/";
var pathJSLWB_Admin_Exec_Online         = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/online";
var pathJSLWB_Admin_Exec_Process        = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/process";
var pathJSLWB_Admin_Exec_JavaIdx        = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/java";
var pathJSLWB_Admin_Exec_JavaVM         = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/java/vm";
var pathJSLWB_Admin_Exec_JavaRuntime    = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/java/runtime";
var pathJSLWB_Admin_Exec_JavaTimes      = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/java/times";
var pathJSLWB_Admin_Exec_JavaClasses    = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/java/classes";
var pathJSLWB_Admin_Exec_JavaMemory     = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/java/memory";
var pathJSLWB_Admin_Exec_JavaThreads    = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/java/threads";
var pathJSLWB_Admin_Exec_JavaThread     = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/java/threads/{thread_id}";
var pathJSLWB_Admin_Exec_Os             = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/os";
var pathJSLWB_Admin_Exec_Cpu            = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/cpu";
var pathJSLWB_Admin_Exec_Memory         = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/memory";
var pathJSLWB_Admin_Exec_Disks          = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/disks";
var pathJSLWB_Admin_Exec_Disk           = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/disks/{disk_id}";
var pathJSLWB_Admin_Exec_Networks       = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/networks";
var pathJSLWB_Admin_Exec_Network        = _pathJSLWB_Admin + "/exec/" + _pathJSLWBAdminVersion + "/networks/{ntwk_id}}";

// usage apiGET(backEndUrl,pathJSLWB_Admin_BI.replace("{service}", "apis"),fillAdminContentHeaderAPIs,onErrorFetch);