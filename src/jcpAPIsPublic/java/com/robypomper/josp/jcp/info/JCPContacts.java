package com.robypomper.josp.jcp.info;

import lombok.Data;
import lombok.Getter;

/**
 * Definitions class dedicated to project's contacts.
 */
public class JCPContacts {

    // Constants

    public static final String WWW_RP = "robypomper.com";
    public static final String MAIL_AT_RP = "%s@" + WWW_RP;
    public static final String URL_RP = "https://www." + WWW_RP;

    public static final String WWW_JOHN = "johnosproject.com";
    public static final String MAIL_AT_JOHN = "%s@" + WWW_JOHN;
    public static final String URL_JOHN = "https://www." + WWW_JOHN;

    public static final String WWW_CODAX = "codax.it";
    public static final String MAIL_AT_CODAX = "%s@" + WWW_CODAX;
    public static final String URL_CODAX = "https://www." + WWW_CODAX;


    // Static declarations and initializations

    @Getter
    private static final Contact john;
    @Getter
    private static final Contact roby;

    static {
        john = new Contact("John O.S. Project","",String.format(MAIL_AT_JOHN,"info"),URL_JOHN);
        roby = new Contact("Roberto","Pompermaier",String.format(MAIL_AT_JOHN,"robypomper"),URL_JOHN);
    }


    // Data types

    @Data
    public static class Contact {

        private final String name;
        private final String surname;
        private final String email;
        private final String url;

        public String getFullName() {
            return String.format("%s %s", getName(), getSurname());
        }
    }

}
