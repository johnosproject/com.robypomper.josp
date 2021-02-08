/* *****************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

package com.robypomper.communication_deprecated.peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * The PeerInfo class provide information to the peer at the other side of the
 * communication channel.
 */
public interface PeerInfo {

    // Class constants

    Charset CHARSET = StandardCharsets.UTF_8;


    // PeerInfo comparison

    /**
     * {@inheritDoc}
     */
    @Override
    boolean equals(Object other);


    // Peer getters

    /**
     * @return peer side connection's address.
     */
    InetAddress getPeerAddress();

    /**
     * @return peer side connection's port.
     */
    int getPeerPort();

    /**
     * @return peer side connection's address and port.
     */
    String getPeerFullAddress();


    // Local getters

    /**
     * @return local side connection's address.
     */
    InetAddress getLocalAddress();

    /**
     * @return local side connection's port.
     */
    int getLocalPort();

    /**
     * @return local side connection's address and port.
     */
    String getLocalFullAddress();


    // Connection getters

    /**
     * @return <code>true</code> if current client still connected.
     */
    boolean isConnected();

    /**
     * Initialize internal input stream and return it.
     *
     * @return the connection's input stream.
     */
    DataInputStream getInStream() throws PeerNotConnectedException, PeerStreamsException;

    /**
     * Initialize internal output stream and return it.
     *
     * @return the connection's output stream.
     */
    DataOutputStream getOutStream() throws PeerNotConnectedException, PeerStreamsException;


    // Socket config

    /**
     * Set internal socket setSoTimeout().
     */
    void setTCPTimeout(int timeout);


    // Exceptions

    /**
     * Base exception for PeerInfo exceptions.
     */
    class AbsPeerException extends Throwable {
        private final PeerInfo peer;

        public AbsPeerException(String msg, Throwable t, PeerInfo peer) {
            super(msg, t);
            this.peer = peer;
        }

        public PeerInfo getPeer() {
            return peer;
        }
    }

    /**
     * Exceptions thrown when the peer is not connected.
     */
    class PeerNotConnectedException extends AbsPeerException {
        public PeerNotConnectedException(PeerInfo peer) {
            super(String.format("Peer host '%s' not connected", peer.getPeerFullAddress()), null, peer);
        }
    }

    /**
     * Exceptions thrown when errors occurs on peer streams initialization or
     * usage.
     */
    class PeerStreamsException extends AbsPeerException {
        public PeerStreamsException(PeerInfo peer, IOException e) {
            super(String.format("Can't get peer host '%s' streams because %s", peer.getPeerFullAddress(), e.getMessage()), e, peer);
        }
    }

}
