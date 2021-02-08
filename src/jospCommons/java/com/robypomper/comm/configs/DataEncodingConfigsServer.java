package com.robypomper.comm.configs;

import com.robypomper.comm.server.Server;
import com.robypomper.comm.server.ServerClient;
import com.robypomper.comm.server.ServerClientsListener;

import java.nio.charset.Charset;
import java.util.Arrays;

public class DataEncodingConfigsServer implements DataEncodingConfigs {

    // Internal vars

    // server
    private final Server server;
    // data encoding configs
    private Charset charset;
    private byte[] delimiter = null;
    private String delimiterStr = null;


    // Constructors

    public DataEncodingConfigsServer(Server server) {
        this(server, CHARSET);
    }

    public DataEncodingConfigsServer(Server server, Charset charset) {
        this(server, charset, DELIMITER);
    }

    public DataEncodingConfigsServer(Server server, Charset charset, String delimiter) {
        this.server = server;
        server.addListener(serverClientsListener);

        setCharset(charset);
        setDelimiter(delimiter);
    }

    public DataEncodingConfigsServer(Server server, Charset charset, byte[] delimiter) {
        this.server = server;
        server.addListener(serverClientsListener);

        setCharset(charset);
        setDelimiter(delimiter);
    }

    private final ServerClientsListener serverClientsListener = new ServerClientsListener() {

        @Override
        public void onConnect(Server server, ServerClient client) {
            client.getDataEncodingConfigs().setCharset(charset);
            client.getDataEncodingConfigs().setDelimiter(delimiter);
            client.getDataEncodingConfigs().setDelimiter(delimiterStr);
        }

        @Override
        public void onDisconnect(Server server, ServerClient client) {
        }


        @Override
        public void onFail(Server server, ServerClient client, String failMsg, Throwable exception) {
        }

    };


    // Getter/setters

    public byte[] getDelimiter() {
        if (delimiter != null) return delimiter;
        return delimiterStr.getBytes(getCharset());
    }

    public String getDelimiterString() {
        if (delimiter != null) return new String(delimiter, getCharset());
        return delimiterStr;
    }

    public void setDelimiter(byte[] delimiter) {
        for (ServerClient client : server.getClients())
            if (Arrays.equals(client.getDataEncodingConfigs().getDelimiter(), this.delimiter))
                client.getDataEncodingConfigs().setDelimiter(delimiter);

        this.delimiter = delimiter;
    }

    public void setDelimiter(String delimiter) {
        for (ServerClient client : server.getClients())
            if (client.getDataEncodingConfigs().getDelimiterString().equals(this.delimiterStr))
                client.getDataEncodingConfigs().setDelimiter(delimiter);

        this.delimiterStr = delimiter;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        for (ServerClient client : server.getClients())
            if (client.getDataEncodingConfigs().getCharset() == this.charset)
                client.getDataEncodingConfigs().setCharset(charset);

        this.charset = charset;
    }

}
