package com.robypomper.comm.configs;

import com.robypomper.comm.client.ClientWrapper;

import java.nio.charset.Charset;

public class DataEncodingConfigsWrapper implements DataEncodingConfigs {

    // Internal vars

    // wrapper
    private final ClientWrapper clientWrapper;
    // data encoding configs
    private Charset charset;
    private byte[] delimiter = null;
    private String delimiterStr = null;


    // Constructors

    public DataEncodingConfigsWrapper(ClientWrapper clientWrapper) {
        this(clientWrapper, CHARSET);
    }

    public DataEncodingConfigsWrapper(ClientWrapper clientWrapper, Charset charset) {
        this(clientWrapper, charset, DELIMITER);
    }

    public DataEncodingConfigsWrapper(ClientWrapper clientWrapper, Charset charset, String delimiter) {
        this.clientWrapper = clientWrapper;

        setCharset(charset);
        setDelimiter(delimiter);
    }

    public DataEncodingConfigsWrapper(ClientWrapper clientWrapper, Charset charset, byte[] delimiter) {
        this.clientWrapper = clientWrapper;

        setCharset(charset);
        setDelimiter(delimiter);
    }


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
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getDataEncodingConfigs().setDelimiter(delimiter);

        this.delimiter = delimiter;
    }

    public void setDelimiter(String delimiter) {
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getDataEncodingConfigs().setDelimiter(delimiter);

        this.delimiterStr = delimiter;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        if (clientWrapper.getWrapper() != null)
            clientWrapper.getWrapper().getDataEncodingConfigs().setCharset(charset);

        this.charset = charset;
    }

}
