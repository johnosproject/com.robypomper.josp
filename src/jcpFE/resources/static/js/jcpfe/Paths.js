
// Front End API's paths

var _pathFEVersion = "1.0"
var _pathFE_EP                          = "/apis/FE/EntryPoint/" + _pathFEVersion;
var pathFE_EP_Entrypoint                = _pathFE_EP + "/entrypoint";
var pathFE_EP_Session                   = _pathFE_EP + "/jslwbsession?session_id={session_id}";


// JSL Web Bridge API's paths

var _pathJSLWBVersion = "1.0"
var _pathJSLWB_Init                     = "/apis/JSL/Init/" + _pathJSLWBVersion;
var pathJSLWB_Init_Entrypoint           = _pathJSLWB_Init + "/sessionStatus";
var pathJSLWB_Init_Sse                  = _pathJSLWB_Init + "/sse";
var _pathJSLWB_Usr                      = "/apis/JSL/Usr/" + _pathJSLWBVersion;
var pathJSLWB_Usr_Login                 = _pathJSLWB_Usr + "/login/";                                               // "/apis/user/1.0/login/?redirect_uri=" +document.location.href;
var pathJSLWB_Usr_Login_Redirect        = _pathJSLWB_Usr + "/login/?redirect_uri={redirect_uri}";
var pathJSLWB_Usr_Registration_Redirect = _pathJSLWB_Usr + "/registration/?redirect_uri={redirect_uri}";            //"/apis/user/1.0/registration/?redirect_uri=" +document.location.href;
var pathJSLWB_Usr_Logout_Redirect       = _pathJSLWB_Usr + "/logout/?redirect_uri={redirect_uri}";                  //"/apis/user/1.0/logout/?redirect_uri=" +document.location.href;
var pathJSLWB_Usr                       = _pathJSLWB_Usr + "/";                                                     //"/apis/user/1.0/"
var _pathJSLWB_Srv                      = "/apis/JSL/Srv/" + _pathJSLWBVersion;
var pathJSLWB_Srv                       = _pathJSLWB_Srv + "/";                                                     // "/apis/service/1.0/"
var _pathJSLWB_Objs                     = "/apis/JSL/Objs/" + _pathJSLWBVersion;
var pathJSLWB_Objs                      = _pathJSLWB_Objs + "/";                                                    // "/apis/objsmngr/1.0/"
var pathJSLWB_ObjDetails                = _pathJSLWB_Objs + "/{obj_id}/";                                           // "/apis/objsmngr/1.0/" + objId + "/"
var pathJSLWB_ObjEvents                 = _pathJSLWB_Objs + "/{obj_id}/events/";                                    // "/apis/objsmngr/1.0/" + objId + "/events/"
var pathJSLWB_ObjName                   = _pathJSLWB_Objs + "/{obj_id}/name/";                                      // "/apis/objsmngr/1.0/" + detailObjId + "/name/"
var pathJSLWB_ObjOwner                  = _pathJSLWB_Objs + "/{obj_id}/owner/";                                     // "/apis/objsmngr/1.0/" + detailObjId + "/owner/"
var _pathJSLWB_Struct                   = "/apis/JSL/Structure/" + _pathJSLWBVersion;
var pathJSLWB_Struct                    = _pathJSLWB_Struct + "/{obj_id}/";                                         // "/apis/structure/1.0/" + objId + "/"
var pathJSLWB_StructComp                = _pathJSLWB_Struct + "/{obj_id}/{comp_path}/";                             // "/apis/structure/1.0/" + objId + "/" + compPath + "/"
var _pathJSLWB_Perms                    = "/apis/JSL/Permissions/" + _pathJSLWBVersion;
var pathJSLWB_Perms_Obj                 = _pathJSLWB_Perms + "/{obj_id}/";                                          // "/apis/permissions/1.0/" + objId + "/"
var pathJSLWB_Perms_Obj_Add             = _pathJSLWB_Perms + "/{obj_id}/add/";                                      //
var pathJSLWB_Perms_Obj_Upd             = _pathJSLWB_Perms + "/{obj_id}/upd/{perm_id}/";                            // "/apis/permissions/1.0/" + objId + "/upd/" + permId + "/"
var pathJSLWB_Perms_Obj_Dup             = _pathJSLWB_Perms + "/{obj_id}/dup/{perm_id}/";                            // "/apis/permissions/1.0/" + objId + "/dup/" + permId + "/"
var pathJSLWB_Perms_Obj_Del             = _pathJSLWB_Perms + "/{obj_id}/del/{perm_id}/";                            // "/apis/permissions/1.0/" + objId + "/del/" + permId + "/"
var _pathJSLWB_State                    = "/apis/JSL/State/" + _pathJSLWBVersion;
var pathJSLWB_State_Bool                = _pathJSLWB_State + "/bool/{obj_id}/{comp_path}/";                         //
var pathJSLWB_State_Rance               = _pathJSLWB_State + "/range/{obj_id}/{comp_path}/";                        //
var pathJSLWB_State_History             = _pathJSLWB_State + "/history/{obj_id}/{comp_path}/";                      // "/apis/state/1.0/history/" + objId + "/" + compPath + "/"
var _pathJSLWB_Admin                    = "/apis/JSL/Admin/" + _pathJSLWBVersion;
//var pathJSLWB_Admin_APIs_Instance       = _pathJSLWB_Admin + "/jcp/apis/status/instance";                           //
var pathJSLWB_Admin_APIs_Objs           = _pathJSLWB_Admin + "/jcp/apis/status/objs";                               // "/apis/JCP/2.0/jslwb/status/apis/status/objs"
var pathJSLWB_Admin_APIs_Srvs           = _pathJSLWB_Admin + "/jcp/apis/status/srvs";                               // "/apis/JCP/2.0/jslwb/status/apis/status/srvs"
var pathJSLWB_Admin_APIs_Usrs           = _pathJSLWB_Admin + "/jcp/apis/status/usrs";                               // "/apis/JCP/2.0/jslwb/status/apis/status/usrs"
//var pathJSLWB_Admin_GWs_Instance        = _pathJSLWB_Admin + "/jcp/gws/status/instance";                            //
var pathJSLWB_Admin_GWs_Servers         = _pathJSLWB_Admin + "/jcp/gws/status/servers";                             // "/apis/JCP/2.0/jslwb/status/apis/status/gws/status/clients"
//var pathJSLWB_Admin_JSLWB_Instance      = _pathJSLWB_Admin + "/jcp/jslwb/status/instance";                          //
//var pathJSLWB_Admin_FE_Instance         = _pathJSLWB_Admin + "/jcp/fe/status/instance";                             //
var pathJSLWB_Admin_BI                  = _pathJSLWB_Admin + "/jcp/{service}/status/instance";                      //
var pathJSLWB_Admin_Exec_Online         = _pathJSLWB_Admin + "/jcp/{service}/status/exec/online";                   //
var pathJSLWB_Admin_Exec_Cpu            = _pathJSLWB_Admin + "/jcp/{service}/status/exec/cpu";                      //
var pathJSLWB_Admin_Exec_Disks          = _pathJSLWB_Admin + "/jcp/{service}/status/exec/disks";                    //
var pathJSLWB_Admin_Exec_Java           = _pathJSLWB_Admin + "/jcp/{service}/status/exec/java";                     //
var pathJSLWB_Admin_Exec_Java_Ths       = _pathJSLWB_Admin + "/jcp/{service}/status/exec/java/threads";             //
var pathJSLWB_Admin_Exec_Memory         = _pathJSLWB_Admin + "/jcp/{service}/status/exec/memory";                   //
var pathJSLWB_Admin_Exec_Network        = _pathJSLWB_Admin + "/jcp/{service}/status/exec/network";                  //
var pathJSLWB_Admin_Exec_Network_Intfs  = _pathJSLWB_Admin + "/jcp/{service}/status/exec/network/intfs";            //
var pathJSLWB_Admin_Exec_Os             = _pathJSLWB_Admin + "/jcp/{service}/status/exec/os";                       //
var pathJSLWB_Admin_Exec_Process        = _pathJSLWB_Admin + "/jcp/{service}/status/exec/process";                  //
