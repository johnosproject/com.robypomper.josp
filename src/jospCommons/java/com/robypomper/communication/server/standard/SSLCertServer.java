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

package com.robypomper.communication.server.standard;

import com.robypomper.communication.UtilsJKS;
import com.robypomper.communication.client.standard.SSLCertSharingClient;
import com.robypomper.communication.server.ClientInfo;
import com.robypomper.communication.server.DefaultServer;
import com.robypomper.communication.server.events.DefaultServerEvent;
import com.robypomper.communication.server.events.ServerClientEvents;
import com.robypomper.communication.server.events.ServerMessagingEvents;
import com.robypomper.communication.trustmanagers.AbsCustomTrustManager;
import com.robypomper.log.Mrk_Commons;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * SSL sharing Certificate server.
 * <p>
 * This is a {@link DefaultServer} implementation that allow server to share
 * SSL certificates with the client. For each new client this server send his
 * public certificate, and, if the client need authentication, then it can send
 * his certificate and this server store it to associated TrustManager.
 * <p>
 * The TrustManager managed by this class can be used to initialize a
 * {@link javax.net.ssl.SSLContext} so a client can register him self to the
 * server without previously key sharing.
 */
public class SSLCertServer extends DefaultServer {

    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    private final File certPubFile;
    private final AbsCustomTrustManager certTrustManager;
    private final Map<String, byte[]> clientCertBuffer = new HashMap<>();
    private final SSLCertServerListener listener;


    // Constructor

    /**
     * Default constructor.
     *
     * @param serverId         the server id.
     * @param port             the server's port.
     * @param certPubPath      server's public certificate file's path.
     * @param certTrustManager instance of TrustManager where client's keys will
     *                         be stored.
     */
    public SSLCertServer(String serverId, int port, String certPubPath, AbsCustomTrustManager certTrustManager) throws SSLCertServerException {
        this(serverId, port, certPubPath, certTrustManager, null);
    }

    /**
     * Default constructor.
     *
     * @param serverId         the server id.
     * @param port             the server's port.
     * @param certPubPath      server's public certificate file's path.
     * @param certTrustManager instance of TrustManager where client's keys will
     *                         be stored.
     * @param listener         listener for certificate events (send and stored).
     */
    public SSLCertServer(String serverId, int port, String certPubPath, AbsCustomTrustManager certTrustManager,
                         SSLCertServerListener listener) throws SSLCertServerException {
        super(serverId, port,
                null,
                new SSLCertServerClientEventsListener(),
                new SSLCertServerMessagingEventsListener());

        this.certPubFile = new File(certPubPath);
        if (!certPubFile.exists())
            throw new SSLCertServerException(String.format("Public cert file '%s' not found.", certPubPath));
        this.certTrustManager = certTrustManager;
        this.listener = listener;
    }


    // Server's events override

    /**
     * This method is executed on event
     * {@link SSLCertServerClientEventsListener#onClientConnection(ClientInfo)}
     * <p>
     * Send server's public certificate to each connected the client.
     * <p>
     * Read certificate file and tx to the client.
     *
     * @param client the ClientInfo corresponding to connected client.
     */
    private void sendServerCertificate(ClientInfo client) {
        try {
            byte[] dataRead = SSLCertSharingClient.readFile(certPubFile);

            sendData(client, dataRead);
            log.debug(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Server send local certificate to client '%s'", client.getClientId()));

            if (listener != null) listener.onCertificateSend(client);
            Thread.sleep(100);

        } catch (ServerStoppedException e) {
            log.warn(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Server disconnected, can't transmit local certificate to client '%s'", client.getClientId()));

        } catch (ClientNotConnectedException e) {
            log.warn(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Can't transmit local certificate to client '%s' because not connected", client.getClientId()));

        } catch (IOException e) {
            log.warn(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Can't read public certificate '%s' because %s", certPubFile, e.getMessage()));

        } catch (InterruptedException ignore) {
        }
    }

    /**
     * Data received from clients are related to client's certificate.
     * <p>
     * Because it's possible the client send his certificate in multiple chucks,
     * this function use a buffer for each client where it store all sequential
     * chucks received until the connection with the client is closed.
     *
     * @param client   the ClientInfo corresponding to connected client.
     * @param readData the data tx from the client containing the client's
     *                 certificate.
     * @return always <code>true</code> as the data were processed.
     */
    private boolean bufferClientCertificate(ClientInfo client, byte[] readData) {
        if (!clientCertBuffer.containsKey(client.getClientId()))
            clientCertBuffer.put(client.getClientId(), readData);
        else
            clientCertBuffer.put(client.getClientId(), concatenate(clientCertBuffer.get(client.getClientId()), readData));

        storeClientCertificate(client);
        return true;
    }

    /**
     * On client disconnection, this method get all data received from the client
     * and use them to load client's certificate to the internal TrustManager.
     *
     * @param client the ClientInfo corresponding to connected client.
     */
    private void storeClientCertificate(ClientInfo client) {
        if (!clientCertBuffer.containsKey(client.getClientId())) {
            log.warn(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Server '%s' don't received certificate from client '%s'", getServerId(), client.getClientId()));
            return;
        }

        byte[] clientCertBytes = clientCertBuffer.get(client.getClientId());
        log.debug(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Server '%s' add certificate from client '%s' to trust store", getServerId(), client.getClientId()));

        try {
            certTrustManager.addCertificateByte(client.getClientId(), clientCertBytes);
            if (listener != null) listener.onCertificateStored(certTrustManager, client);

        } catch (AbsCustomTrustManager.UpdateException | UtilsJKS.LoadingException e) {
            log.warn(Mrk_Commons.COMM_SSL_CERTSRV, String.format("Server '%s' can't add certificate from client '%s' because %s", getServerId(), client.getClientId(), e.getMessage()));
        }
    }

    /**
     * Utils method to concatenate two byte arrays.
     *
     * @param firstArray  the first part of returned array.
     * @param secondArray the second part of returned array.
     * @return the byte array composed by firstArray and secondArray arrays.
     */
    private byte[] concatenate(byte[] firstArray, byte[] secondArray) {
        byte[] concatenated = new byte[firstArray.length + secondArray.length];
        System.arraycopy(firstArray, 0, concatenated, 0, firstArray.length);
        System.arraycopy(secondArray, 0, concatenated, firstArray.length, secondArray.length);
        return concatenated;
    }


    // Server event listeners

    /**
     * Link the {@link #onClientConnection(ClientInfo)} event to
     * {@link SSLCertServer#sendServerCertificate(ClientInfo)} method.
     */
    private static class SSLCertServerClientEventsListener extends DefaultServerEvent implements ServerClientEvents {

        // Client connection events

        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link SSLCertServer#sendServerCertificate(ClientInfo)}
         * method.
         */
        @Override
        public void onClientConnection(ClientInfo client) {
            ((SSLCertServer) getServer()).sendServerCertificate(client);
        }


        // Client disconnection events

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClientDisconnection(ClientInfo client) {
        }


        /**
         * {@inheritDoc}
         * <p>
         * This implementation do nothing.
         */
        @Override
        public void onClientServerDisconnected(ClientInfo client) {}

        /**
         * {@inheritDoc}
         * <p>
         * This implementation do nothing.
         */
        @Override
        public void onClientGoodbye(ClientInfo client) {}

        /**
         * {@inheritDoc}
         * <p>
         * This implementation do nothing.
         */
        @Override
        public void onClientTerminated(ClientInfo client) {}


        // Client errors events

        @Override
        public void onClientError(ClientInfo client, Throwable e) {}

    }

    /**
     * Link the {@link #onDataReceived(ClientInfo, byte[])} events to the
     * {@link SSLCertServer#bufferClientCertificate(ClientInfo, byte[])} method.
     */
    private static class SSLCertServerMessagingEventsListener extends DefaultServerEvent implements ServerMessagingEvents {

        // Data send events

        /**
         * {@inheritDoc}
         * <p>
         * This implementation do nothing.
         */
        @Override
        public void onDataSend(ClientInfo client, byte[] writtenData) {

        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation do nothing.
         */
        @Override
        public void onDataSend(ClientInfo client, String writtenData) {

        }


        // Data received events


        /**
         * {@inheritDoc}
         * <p>
         * Link to the {@link SSLCertServer#bufferClientCertificate(ClientInfo, byte[])}
         * method.
         */
        @Override
        public boolean onDataReceived(ClientInfo client, byte[] readData) {
            return ((SSLCertServer) getServer()).bufferClientCertificate(client, readData);
        }

        /**
         * {@inheritDoc}
         * <p>
         * This implementation do nothing.
         */
        @Override
        public boolean onDataReceived(ClientInfo client, String readData) {
            return false;
        }

    }


    // SSLCertServer event listeners

    /**
     * Interface for SSLCertServer listeners.
     */
    public interface SSLCertServerListener {

        /**
         * Event triggered when the server send his certificate to the client.
         *
         * @param client the client that received the server's certificate.
         */
        void onCertificateSend(ClientInfo client);

        /**
         * Event triggered when the server received and stored the client's
         * certificate.
         *
         * @param certTrustManager the {@link javax.net.ssl.TrustManager} where
         *                         received certificate was stored.
         * @param client           the client that own stored certificate.
         */
        void onCertificateStored(AbsCustomTrustManager certTrustManager, ClientInfo client);

    }


    // Exception

    /**
     * Exception thrown on SSLCertServer errors.
     */
    public static class SSLCertServerException extends Throwable {
        public SSLCertServerException(String msg) {
            super(msg);
        }
    }

}
