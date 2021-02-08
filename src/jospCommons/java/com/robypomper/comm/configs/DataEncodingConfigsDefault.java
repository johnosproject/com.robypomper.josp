package com.robypomper.comm.configs;

import java.nio.charset.Charset;

public class DataEncodingConfigsDefault implements DataEncodingConfigs {


    // Internal vars

    // data encoding configs
    private Charset charset;
    private byte[] delimiter = null;
    private String delimiterStr = null;


    // Constructors

    public DataEncodingConfigsDefault() {
        this(CHARSET);
    }

    public DataEncodingConfigsDefault(Charset charset) {
        this(charset, DELIMITER);
    }

    public DataEncodingConfigsDefault(Charset charset, String delimiter) {
        setCharset(charset);
        setDelimiter(delimiter);
    }

    public DataEncodingConfigsDefault(Charset charset, byte[] delimiter) {
        setCharset(charset);
        setDelimiter(delimiter);
    }


    // Getter/setters

    @Override
    public byte[] getDelimiter() {
        if (delimiter != null) return delimiter;
        return delimiterStr.getBytes(getCharset());
    }

    @Override
    public String getDelimiterString() {
        if (delimiter != null) return new String(delimiter, getCharset());
        return delimiterStr;
    }

    @Override
    public void setDelimiter(byte[] delimiter) {
        this.delimiter = delimiter;
        if (delimiter != null)
            this.delimiterStr = null;
    }

    @Override
    public void setDelimiter(String delimiter) {
        if (delimiterStr != null)
            this.delimiter = null;
        this.delimiterStr = delimiter;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}
