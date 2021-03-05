/*********************+
    Ajax Calls
 **********************/

/*
This file provide all functions required for Ajax Calls (Get and POST).
*/

// Get calls

function apiGETSync(baseUrl,path) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", baseUrl + path, false);

    _apiGET(xhttp);
    return xhttp;
}

async function apiGETSyncRetry(baseUrl,path,retryTime) {
    if (retryTime==null) retryTime=1000;

    var xhttp = apiGETSync(baseUrl,path);
    if (xhttp.status != 200) {
        await sleep(retryTime);
        return apiGETSync(baseUrl,path);
    }
}

function apiGET(baseUrl,path,processResponse,processError) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (xhttp.readyState == 4)
            if (xhttp.status == 200)
                processResponse(xhttp.responseText);
            else
                processError(xhttp);
    };
    xhttp.onerror = function() {
        processError(xhttp);
    };
    xhttp.open("GET", baseUrl + path, true);

    _apiGET(xhttp);
}

function apiGET_retry(baseUrl,path,processResponse,processError,retryTime) {
    if (retryTime==null) retryTime=1000;

    apiGET(baseUrl,path,processResponse,
        async function apiGET_retryFail() {
            await sleep(retryTime);
            apiGET(baseUrl,path,processResponse,processError);
        }
    );
}

function _apiGET(xhttp) {
    xhttp.withCredentials = true;

    try {
        xhttp.send();
    } catch {}
}


// Post calls

function apiPOST(baseUrl,path,processResponse,processError,params) {

    apiOPTIONS(baseUrl,path,function() {

        var xhttp = new XMLHttpRequest();

        xhttp.onreadystatechange = function() {
            if (this.readyState == 4)
                if (this.status == 200)
                    processResponse(this.responseText);
                else
                    processError(this);

        };
        xhttp.onerror = function() {
            processError(this);
        };

        xhttp.open("POST", baseUrl + path, true);
        var cookies="";
        var body = "";
        xhttp.withCredentials = true;

        if (params!=null) {
            xhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            body += params;
        }

        if (csrfToken!=null)
            body += "&_csrf=" + csrfToken;

        try {
            xhttp.send(body);
        } catch {}

    }, processError);

}


// Other calls

function apiOPTIONS(baseUrl,path,processResponse,processError) {
    var xhttp = new XMLHttpRequest();

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4)
            if (this.status == 200)
                processResponse(this.responseText);
            else
                processError(this);

    };
    xhttp.onerror = function() {
        processError(this);
    };

    xhttp.open("OPTIONS", baseUrl + path, true);
    var cookies="";
    xhttp.withCredentials = true;

    try {
        xhttp.send();
    } catch {}
}


// Utils methods

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}
