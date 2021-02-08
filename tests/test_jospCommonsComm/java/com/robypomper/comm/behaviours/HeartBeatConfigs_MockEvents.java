package com.robypomper.comm.behaviours;

import com.robypomper.comm.peer.Peer;
import com.robypomper.java.JavaListeners;

import java.util.ArrayList;
import java.util.List;

public class HeartBeatConfigs_MockEvents extends HeartBeatConfigs_Mock {

    private final List<HeartBeatListener> listeners = new ArrayList<>();

    @Override
    public void addListener(HeartBeatListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HeartBeatListener listener) {
        listeners.remove(listener);
    }

    public void emitOnSuccess(Peer peer, HeartBeatConfigs hb) {
        JavaListeners.emitter(this, listeners, "onSuccess", new JavaListeners.ListenerMapper<HeartBeatListener>() {
            @Override
            public void map(HeartBeatListener l) {
                l.onSuccess(peer, hb);
            }
        });
    }

    public void emitOnFail(Peer peer, HeartBeatConfigs hb) {
        JavaListeners.emitter(this, listeners, "onFail", new JavaListeners.ListenerMapper<HeartBeatListener>() {
            @Override
            public void map(HeartBeatListener l) {
                l.onFail(peer, hb);
            }
        });
    }

}
