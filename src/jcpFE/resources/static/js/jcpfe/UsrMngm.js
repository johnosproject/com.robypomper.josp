
var isAuthenticated = null;
var loggedUserId = "00000-00000-00000";
var loggedUserName = "Anonymous";
var loggedUser_isAuthenticated = false;
var loggedUser_isAdmin = false;
var loggedUser_isMaker = false;
var loggedUser_isDeveloper = false;
var onUserLoggedListeners = [];
var onUserLogoutListeners = [];


// Login methods

function login() {
    var loginUrlPath = "/apis/user/1.0/login/?redirect_uri=" +document.location.href;
    apiGET(backEndUrl,loginUrlPath,function(responseText) {
        responseText = responseText.replaceAll('"','');
        window.location.href = responseText;
    },onErrorFetch);
}

function registration() {
    var loginUrlPath = "/apis/user/1.0/registration/?redirect_uri=" +document.location.href;
    apiGET(backEndUrl,loginUrlPath,function(responseText) {
        responseText = responseText.replaceAll('"','');
        window.location.href = responseText;
    },onErrorFetch);
}

function logout() {
    var loginUrlPath = "/apis/user/1.0/logout/?redirect_uri=" +document.location.href;
    apiGET(backEndUrl,loginUrlPath,function(responseText) {
        responseText = responseText.replaceAll('"','');
        window.location.href = responseText;
    },onErrorFetch);
}

// Fetch&Fill methods

function fetchUsrMngm() {
    apiGET(backEndUrl,"/apis/user/1.0/",fillUsrMngm,onErrorFetch);
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
    loggedUser_isAuthenticated = user.isAuthenticated;
    loggedUser_isAdmin = user.isAdmin;
    loggedUser_isMaker = user.isMaker;
    loggedUser_isDeveloper = user.isDeveloper;
    showLoginCssClass();
    hideLogoutCssClass();

    setOpts(dropDownUserMenu());
});

onUserLogoutListeners.push(function(user) {
    loggedUserId = "00000-00000-00000";
    loggedUserName = "Anonymous";
    showLogoutCssClass();
    hideLoginCssClass();
});
