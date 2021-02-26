
var feedTimeMs = 1000;
var feedUpdColor = "#2c64ba";
var feedWaitColor = "#ffff0020";
var feedSuccessColor = "#00800020";
var feedErrorColor = "#ff000020";
var feedOldStyles = {};


// Feedback methods

function showUpdateFeedbackByIdTag(tagId) {
    var element = document.getElementById(tagId);
    if (element==null) {
        log.error("Can't showUpdateFeedbackByIdTag because element " + tagId + " not found.");
        return;
    }
    showUpdateFeedback(element);
}

function showUpdateFeedback(element) {
    var oldStyle = element.style.textShadow;
    element.style.textShadow='0 0 15px ' + feedUpdColor;

    window.setTimeout(function() {
        element.style.textShadow = oldStyle;
    },feedTimeMs);
}

async function showWaitingFeedbackByIdTag(tagId) {
    var element = document.getElementById(tagId);
    if (element==null) {
        log.error("Can't showWaitingFeedbackByIdTag because element " + tagId + " not found.");
        return;
    }
    showWaitingFeedback(element);
}

async function showWaitingFeedback(element) {
    feedOldStyles[element] = element.style.backgroundColor;
    element.style.backgroundColor=feedWaitColor;
}

function hideWaitingFeedbackByIdTag(tagId) {
    var element = document.getElementById(tagId);
    if (element==null) {
        log.error("Can't hideWaitingFeedbackByIdTag because element " + tagId + " not found.");
        return;
    }
    hideWaitingFeedback(element);
}

function hideWaitingFeedback(element) {
    element.style.backgroundColor = feedOldStyles[element];

    showSuccessFeedback(element);
}

function showSuccessFeedbackByIdTag(tagId) {
    var element = document.getElementById(tagId);
    if (element==null) {
          log.error("Can't showSuccessFeedbackByIdTag because element " + tagId + " not found.");
          return;
    }
    showSuccessFeedback(element);
}

function showSuccessFeedback(element) {
    feedOldStyles[element] = element.style.backgroundColor;
    element.style.backgroundColor=feedSuccessColor;

    window.setTimeout(function() {
        element.style.backgroundColor = feedOldStyles[element];
    },feedTimeMs);
}

function showFailFeedbackByIdTag(tagId) {
    var element = document.getElementById(tagId);
    if (element==null) {
        log.error("Can't showFailFeedbackByIdTag because element " + tagId + " not found.");
        return;
    }
    showFailFeedback(element);
}

function showFailFeedback(element) {
    feedOldStyles[element] = element.style.backgroundColor;
    element.style.backgroundColor=feedErrorColor;

    window.setTimeout(function() {
        element.style.backgroundColor = feedOldStyles[element];
    },feedTimeMs);
}
