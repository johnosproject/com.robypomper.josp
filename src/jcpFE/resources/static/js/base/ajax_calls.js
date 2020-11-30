/*********************+
    Ajax Calls
 **********************/

/*
This file provide all functions required for Ajax Calls (Get and POST).
*/

var csrfToken = null;
var csrfHeader = null;

// Get calls

function apiGET(baseUrl,path,processResponse,processError) {
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

    xhttp.open("GET", baseUrl + path, true);
    var cookies="";
    xhttp.withCredentials = true;
    //if (csrfToken!=null)
    //    xhttp.setRequestHeader(csrfHeader, csrfToken)

    try {
        xhttp.send();
    } catch {}
}

function apiGET_retry(baseUrl,path,processResponse,processError,retryTime) {
    if (retryTime==null) retryTime=1000;

    apiGET(baseUrl,path,processResponse,
        async function onError_RetryFetchObjDetails() {
            await new Promise(r => setTimeout(r, retryTime));
            apiGET(baseUrl,path,processResponse,processError);
        }
    );}


// Post calls

function apiPOST(baseUrl,path,processResponse,processError,params) {
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
    xhttp.withCredentials = true;

    if (params!=null) {
        xhttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        try {
            if (csrfToken!=null)
                xhttp.send(params + "&_csrf=" + csrfToken);
        } catch {}
    }
}


function setCsrf(token,header) {
    csrfToken = token;
    csrfHeader = header;
}
