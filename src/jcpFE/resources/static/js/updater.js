/*********************+
    Updater
 **********************/

/*
This file provide the start function for the Updater mechanism that use a SSE connection.
*/

// Updater's methods

function startUpdater(path,updateOnMessage,updateOnOpen,updateOnError) {
    const evtSource = new EventSource(path);
    evtSource.onmessage = updateOnMessage;
    evtSource.onopen = updateOnOpen;
    evtSource.onerror = updateOnError;
    return evtSource;
}


// Example methods

function DEFAULT_updateOnMessage(event) {
    // switch update type
    //   dispatch update notification ->
    //   -> request updated info
    //   -> display updated info

    //if (event.data.startsWith("ABC:"))
    //    console.log(event.data);
    //else if (event.data.startsWith("XYZ:"))
    //    emitObjAdd();
    //else if ...

    //else
    //    console.log("Unknown message from SSE: " + event.data);
}

function DEFAULT_updateOnOpen(event) {
    //console.log("SSE Connected");
}

function DEFAULT_updateOnError(event) {
    //console.log("SSE Disconnected");
}