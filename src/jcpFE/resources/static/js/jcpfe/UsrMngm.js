
var isAuthenticated = null;
var loggedUserId = "00000-00000-00000";
var loggedUserName = "Anonymous";
var onUserLoggedListeners = [];
var onUserLogoutListeners = [];


// Fetch&Fill methods

function fetchUsrMngm() {
    var path = "/apis/user/1.0/";
    apiGET(path,fillUsrMngm,onErrorFetch);
}

function fillUsrMngm(userJson) {
    user = JSON.parse(userJson);
    if (user.isAuthenticated && (isAuthenticated==null || !isAuthenticated)) {
        isAuthenticated = true;
        for (var i=0; i<onUserLoggedListeners.length; i++)
            onUserLoggedListeners[i](user);
    }
    if (!user.isAuthenticated && (isAuthenticated==null || isAuthenticated)) {
        isAuthenticated = false;
        for (var i=0; i<onUserLoggedListeners.length; i++)
            onUserLogoutListeners[i](user);
    }
}


// Loggin/Logout listeners

onUserLoggedListeners.push(function(user) {
    loggedUserId = user.id;
    loggedUserName = user.name;
    showLoginCssClass();
    hideLogoutCssClass();
});

onUserLogoutListeners.push(function(user) {
    loggedUserId = "00000-00000-00000";
    loggedUserName = "Anonymous";
    showLogoutCssClass();
    hideLoginCssClass();
});
