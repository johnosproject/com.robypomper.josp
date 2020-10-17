
var feedTimeMs = 1000;
var feedUpdColor = "#2c64ba";
var feedWaitColor = "#ffff0020";
var feedSuccessColor = "#00800020";
var feedErrorColor = "#ff000020";
var feedOldStyles = {};


// Feedback methods

function showUpdateFeedback(tagId) {
    var element = document.getElementById(tagId);

    var oldStyle = element.style.textShadow;
    element.style.textShadow='0 0 15px ' + feedUpdColor;

    window.setTimeout(function() {
        var element = document.getElementById(tagId);
        element.style.textShadow = oldStyle;
    },feedTimeMs);
}

async function showWaitingFeedback(tagId) {
    var element = document.getElementById(tagId);

    feedOldStyles[tagId] = element.style.backgroundColor;
    element.style.backgroundColor=feedWaitColor;
}

function hideWaitingFeedback(tagId) {
    var element = document.getElementById(tagId);
    element.style.backgroundColor = feedOldStyles[tagId];

    showSuccessFeedback(tagId);
}

function showSuccessFeedback(tagId) {
    var element = document.getElementById(tagId);

    feedOldStyles[tagId] = element.style.backgroundColor;
    element.style.backgroundColor=feedSuccessColor;

    window.setTimeout(function() {
        var element = document.getElementById(tagId);
        element.style.backgroundColor = feedOldStyles[tagId];
    },feedTimeMs);
}

function showFailFeedback(tagId) {
    var element = document.getElementById(tagId);

    feedOldStyles[tagId] = element.style.backgroundColor;
    element.style.backgroundColor=feedErrorColor;

    window.setTimeout(function() {
        var element = document.getElementById(tagId);
        element.style.backgroundColor = feedOldStyles[tagId];
    },feedTimeMs);
}
