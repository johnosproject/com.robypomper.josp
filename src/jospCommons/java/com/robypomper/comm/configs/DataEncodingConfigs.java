package com.robypomper.comm.configs;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface DataEncodingConfigs {

    // Class constants

    Charset CHARSET = StandardCharsets.UTF_8;
    String DELIMITER = "##";


    // Getter/setters

    byte[] getDelimiter();

    String getDelimiterString();

    void setDelimiter(byte[] delimiter);

    void setDelimiter(String delimiter);

    Charset getCharset();

    void setCharset(Charset charset);

}
