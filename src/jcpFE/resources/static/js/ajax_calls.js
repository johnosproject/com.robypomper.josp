/*********************+
    Ajax Calls
 **********************/

/*
This file provide all functions required for Ajax Calls (Get and POST).
*/

var csrfToken = null;
var csrfHeader = null;

// Get calls

function apiGET(path,processResponse,processError) {
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

    xhttp.open("GET", path, true);
    //if (csrfToken!=null)
    //    xhttp.setRequestHeader(csrfHeader, csrfToken)

    try {
        xhttp.send();
    } catch {}
}


// Post calls

function apiPOST(path,processResponse,processError,params) {
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

    xhttp.open("POST", path, true);

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