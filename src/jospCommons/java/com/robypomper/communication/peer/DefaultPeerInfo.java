package com.robypomper.communication.peer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Default implementation for PeerInfo interface.
 */
public class DefaultPeerInfo implements PeerInfo {

    // Class constants

    private static final String FULL_ADDRESS_FORMATTER = "%s:%d";

    // Internal vars

    private final Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;


    // Constructor

    /**
     * Default constructor that initialize the PeerInfo with given socket.
     *
     * @param socket the communication channel socket.
     */
    public DefaultPeerInfo(Socket socket) {
        this.socket = socket;
    }


    // PeerInfo comparison

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof PeerInfo))
            return false;

        PeerInfo otherPeer = (PeerInfo) other;
        return (
                otherPeer.getPeerAddress().equals(getPeerAddress())
                        && otherPeer.getPeerPort() == getPeerPort()
                        && otherPeer.getLocalAddress().equals(getLocalAddress())
                        && otherPeer.getLocalPort() == getLocalPort()
        );
    }


    // Peer getters

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getPeerAddress() {
        return socket.getInetAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPeerPort() {
        return socket.getPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPeerFullAddress() {
        return String.format(FULL_ADDRESS_FORMATTER, getPeerAddress(), getPeerPort());
    }


    // Local getters

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocalFullAddress() {
        return String.format(FULL_ADDRESS_FORMATTER, getLocalAddress(), getLocalPort());
    }


    // Connection getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }

    protected Socket getSocket() {
        return socket;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataInputStream getInStream() throws PeerNotConnectedException, PeerStreamsException {
        if (!isConnected())
            throw new PeerNotConnectedException(this);

        if (inputStream == null)
            try {
                inputStream = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                throw new PeerStreamsException(this, e);
            }

        return inputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataOutputStream getOutStream() throws PeerNotConnectedException, PeerStreamsException {
        if (!isConnected())
            throw new PeerNotConnectedException(this);

        if (outputStream == null)
            try {
                outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                throw new PeerStreamsException(this, e);
            }

        return outputStream;
    }

}
