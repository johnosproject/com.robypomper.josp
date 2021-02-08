package com.robypomper.comm.peer;

public class PeerInfoRemoteDefault extends PeerInfoDefault implements PeerInfoRemote {

    // Constructors

    public PeerInfoRemoteDefault(String remoteId, String protoName) {
        super(remoteId, protoName, false);
    }

}
