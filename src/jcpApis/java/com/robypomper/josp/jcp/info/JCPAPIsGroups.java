package com.robypomper.josp.jcp.info;

import lombok.Data;
import lombok.Getter;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Definitions class dedicated to JCP APIs Groups.
 *
 * ToDo: docs JCPAPIsGroup structure
 */
public class JCPAPIsGroups {

    // Constants

    private static final String VER_TEST = "test";
    private static final String PATH_PREFIX = "apis";
    public static final String RES_PATH_DESC = "classpath:docs/api-%s-description.txt";


    // API's names

    public static final String API_EXMPL = "Examples";
    public static final String API_LOGIN = "Login";
    public static final String API_OBJS = "Objs";

    // API's Sub-Groups

    public static final String API_EXMPL_SG_METHODS_NAME = "Method examples";
    public static final APISubGroup API_EXMPL_SG_METHODS = new APISubGroup(API_EXMPL_SG_METHODS_NAME,"Simples methods to use as APIs samples.");
    public static final String API_EXMPL_SG_DB_NAME = "Database access examples";
    public static final APISubGroup API_EXMPL_SG_DB = new APISubGroup(API_EXMPL_SG_DB_NAME,"Simples methods that use the Username entity.");
    public static final String API_EXMPL_SG_AUTHENTICATION_NAME = "Authentication examples";
    public static final APISubGroup API_EXMPL_SG_AUTHENTICATION = new APISubGroup(API_EXMPL_SG_AUTHENTICATION_NAME,"Simples methods that check user Identity.");
    public static final String API_EXMPL_SG_AUTHORIZATION_NAME = "Authorization examples";
    public static final APISubGroup API_EXMPL_SG_AUTHORIZATION = new APISubGroup(API_EXMPL_SG_AUTHORIZATION_NAME,"Simples methods that check user Identity and Permissions.");
    public static final APISubGroup[] API_EXMPL_SUBGROUPS = {API_EXMPL_SG_METHODS, API_EXMPL_SG_DB, API_EXMPL_SG_AUTHENTICATION, API_EXMPL_SG_AUTHORIZATION};
    public static final APISubGroup[] API_LOGIN_SUBGROUPS = {};
    public static final APISubGroup[] API_OBJS_SUBGROUPS = {};


    // API's paths (only for @RequestMapping annotations)

    public static final String PATH_EXMPL = "/apis/examples/test/";
    public static final String PATH_LOGIN = "/apis/login/";
    public static final String PATH_OBJS = "/apis/objs/";


    // Static declarations and initializations

    @Getter
    private static Map<String,APIGroup> allGroups = new HashMap<>();

    static {
        allGroups.put(API_EXMPL,new APIGroup(API_EXMPL,VER_TEST,API_EXMPL_SUBGROUPS));
        allGroups.put(API_LOGIN,new APIGroup(API_LOGIN,VER_TEST,API_LOGIN_SUBGROUPS));
        allGroups.put(API_OBJS,new APIGroup(API_OBJS,VER_TEST,API_OBJS_SUBGROUPS));
    }

    public static APIGroup getAPIGroupByName(String apiName) {
        return allGroups.get(apiName);
    }


    // Data types

    @Data
    public static class APIGroup {

        private final String name;

        private final String version;

        private final APISubGroup[] subGroups;


        public String getPath() {
            return String.format("/%s/%s/%s/**", PATH_PREFIX, getName(), getVersion()).toLowerCase();
        }

        public String getTitle() {
            return String.format("JCP APIs - %s", getName());
        }

        public String getDescriptionPath() {
            return String.format(RES_PATH_DESC, name).toLowerCase();
        }

        public String getDescription() {
            return getLoadDescription(getDescriptionPath());
        }

        public static String getLoadDescription(String resPath) {
            try {
                ResourceLoader loader = new DefaultResourceLoader();
                Resource res = loader.getResource(resPath);
                Reader reader = new InputStreamReader(res.getInputStream());
                return FileCopyUtils.copyToString(reader);

            } catch (IOException err) {
                return String.format("Description not found, check '%s' file.", resPath);
            }
        }
    }

    @Data
    public static class APISubGroup {

        private final String name;

        private final String description;
    }

}
