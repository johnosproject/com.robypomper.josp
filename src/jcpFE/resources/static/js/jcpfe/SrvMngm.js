
var serviceId = null;
var serviceVersion = "0.0.0";


// Fetch&Fill methods

function fetchSrvMngm() {
    //var path = "/apis/service/1.0/";
    apiGET(backEndUrl,pathJSLWB_Srv,fillSrvMngm,onErrorFetch);
}

function fillSrvMngm(serviceJson) {
    service = JSON.parse(serviceJson);

    serviceId = service.srvId;
    serviceVersion = service.jslVersion;
}
