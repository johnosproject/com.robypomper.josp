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
 * <p>
 * ToDo: docs JCPAPIsGroup structure
 */
public class JCPAPIsGroups {

    // Constants

    private static final String VER_TEST = "test";
    private static final String VER_VER = "ver";
    private static final String PATH_PREFIX = "apis";
    public static final String RES_PATH_DESC = "classpath:docs/api-%s-description.txt";


    // API's names

    public static final String API_EXMPL = "Examples";
    public static final String API_LOGIN = "Login";
    public static final String API_USRS = "User";
    public static final String API_OBJS = "Object";
    public static final String API_SRVS = "Service";
    public static final String API_PERM = "Permissions";
    public static final String API_CONF = "Configs";
    public static final String API_UPDS = "Updates";
    public static final String API_JGWS = "JOSPGWs";


    // API's Sub-Groups

    public static final String API_EXMPL_SG_METHODS_NAME = "Method examples";
    public static final APISubGroup API_EXMPL_SG_METHODS = new APISubGroup(API_EXMPL_SG_METHODS_NAME, "Simples methods to use as APIs samples.");
    public static final String API_EXMPL_SG_DB_NAME = "Database access examples";
    public static final APISubGroup API_EXMPL_SG_DB = new APISubGroup(API_EXMPL_SG_DB_NAME, "Simples methods that use the Username entity.");
    public static final String API_EXMPL_SG_AUTHENTICATION_NAME = "Authentication examples";
    public static final APISubGroup API_EXMPL_SG_AUTHENTICATION = new APISubGroup(API_EXMPL_SG_AUTHENTICATION_NAME, "Simples methods that check user Identity.");
    public static final String API_EXMPL_SG_AUTHORIZATION_NAME = "Authorization examples";
    public static final APISubGroup API_EXMPL_SG_AUTHORIZATION = new APISubGroup(API_EXMPL_SG_AUTHORIZATION_NAME, "Simples methods that check user Identity and Permissions.");

    public static final String API_LOGIN_SG_PLACEHOLDER_NAME = "Login placeholder";
    public static final APISubGroup API_LOGIN_SG_PLACEHOLDER = new APISubGroup(API_LOGIN_SG_PLACEHOLDER_NAME, "Placeholder sub-group");

    public static final String API_USRS_SG_BASE_NAME = "User's info";
    public static final APISubGroup API_USRS_SG_BASE = new APISubGroup(API_USRS_SG_BASE_NAME, "Get user info...");

    public static final String API_OBJS_SG_BASE_NAME = "Object's info";
    public static final APISubGroup API_OBJS_SG_BASE = new APISubGroup(API_OBJS_SG_BASE_NAME, "Register/get object info, generate object ids...");

    public static final String API_SRVS_SG_BASE_NAME = "Service's info'";
    public static final APISubGroup API_SRVS_SG_BASE = new APISubGroup(API_SRVS_SG_BASE_NAME, "Get service info...");

    public static final String API_PERM_SG_OBJ_NAME = "Object's permissions";
    public static final APISubGroup API_PERM_SG_OBJ = new APISubGroup(API_PERM_SG_OBJ_NAME, "Get/merge permissions, set owner...");

    public static final String API_CONF_SG_PLACEHOLDER_NAME = "Configs placeholder";
    public static final APISubGroup API_CONF_SG_PLACEHOLDER = new APISubGroup(API_CONF_SG_PLACEHOLDER_NAME, "Placeholder sub-group");

    public static final String API_UPDS_SG_PLACEHOLDER_NAME = "Updates placeholder";
    public static final APISubGroup API_UPDS_SG_PLACEHOLDER = new APISubGroup(API_UPDS_SG_PLACEHOLDER_NAME, "Placeholder sub-group");

    public static final String API_JGWS_SG_O2S_NAME = "JOSPGWs";
    public static final APISubGroup API_JGWS_SG_O2S = new APISubGroup(API_JGWS_SG_O2S_NAME, "Object2Service and Service2Object Gateway's APIs");

    public static final APISubGroup[] API_EXMPL_SUBGROUPS = {API_EXMPL_SG_METHODS, API_EXMPL_SG_DB, API_EXMPL_SG_AUTHENTICATION, API_EXMPL_SG_AUTHORIZATION};
    public static final APISubGroup[] API_LOGIN_SUBGROUPS = {API_LOGIN_SG_PLACEHOLDER};
    public static final APISubGroup[] API_USRS_SUBGROUPS = {API_USRS_SG_BASE};
    public static final APISubGroup[] API_OBJS_SUBGROUPS = {API_OBJS_SG_BASE};
    public static final APISubGroup[] API_SRVS_SUBGROUPS = {API_SRVS_SG_BASE};
    public static final APISubGroup[] API_PERM_SUBGROUPS = {API_PERM_SG_OBJ};
    public static final APISubGroup[] API_CONF_SUBGROUPS = {API_CONF_SG_PLACEHOLDER};
    public static final APISubGroup[] API_UPDS_SUBGROUPS = {API_UPDS_SG_PLACEHOLDER};
    public static final APISubGroup[] API_JGWS_SUBGROUPS = {API_JGWS_SG_O2S};


    // API's paths (only for @RequestMapping annotations) DEPRECATED

    public static final String PATH_EXMPL = "/apis/examples/" + VER_TEST;
    public static final String PATH_LOGIN = "/apis/login/" + VER_VER;
    //public static final String PATH_USRS = "/apis/user/" + VER_VER;
    //public static final String PATH_OBJS = "/apis/object/" + VER_VER;
    //public static final String PATH_SRVS = "/apis/service/" + VER_VER;
    //public static final String PATH_PERM = "/apis/permissions/" + VER_VER;
    public static final String PATH_CONF = "/apis/configs/" + VER_VER;
    public static final String PATH_UPDS = "/apis/updates/" + VER_VER;
    //public static final String PATH_JGWS = "/apis/jospgws/" + VER_VER;


    // Static declarations and initializations

    @Getter
    private static final Map<String, APIGroup> allGroups = new HashMap<>();

    static {
        allGroups.put(API_EXMPL, new APIGroup(API_EXMPL, VER_TEST, API_EXMPL_SUBGROUPS));
        allGroups.put(API_LOGIN, new APIGroup(API_LOGIN, VER_VER, API_LOGIN_SUBGROUPS));
        allGroups.put(API_USRS, new APIGroup(API_USRS, VER_VER, API_USRS_SUBGROUPS));
        allGroups.put(API_OBJS, new APIGroup(API_OBJS, VER_VER, API_OBJS_SUBGROUPS));
        allGroups.put(API_SRVS, new APIGroup(API_SRVS, VER_VER, API_SRVS_SUBGROUPS));
        allGroups.put(API_PERM, new APIGroup(API_PERM, VER_VER, API_PERM_SUBGROUPS));
        allGroups.put(API_CONF, new APIGroup(API_CONF, VER_VER, API_CONF_SUBGROUPS));
        allGroups.put(API_UPDS, new APIGroup(API_UPDS, VER_VER, API_UPDS_SUBGROUPS));
        allGroups.put(API_JGWS, new APIGroup(API_JGWS, VER_VER, API_JGWS_SUBGROUPS));
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
