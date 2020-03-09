package com.robypomper.josp.jcp.docs;

import com.google.common.collect.Lists;
import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import com.robypomper.josp.jcp.info.JCPContacts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationCodeGrant;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ClientCredentialsGrant;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.LoginEndpoint;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.Tag;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.robypomper.josp.jcp.info.JCPAPIsGroups.RES_PATH_DESC;

/**
 * Class to configure Swagger docs features on JCP APIs services.
 *
 * This class get APIs group and sub-groups info from {@link JCPAPIsGroups} class
 * and transform them in Swagger Dockets and Swagger tags.
 *
 * This class, also define, for Swagger environment:
 * AuthFlows: AuthCode, ClientCred, Implicit*
 * Scopes: obj, srv, mng
 */
@Configuration
@EnableSwagger2
public class SwaggerConfigurer {

    private static final String DEFAULT_INCLUDE_PATTERN_NONE = "/none";

    public static final String ROLE_OBJ = "obj";
    public static final String ROLE_OBJ_SWAGGER = "role_obj";
    public static final String ROLE_OBJ_DESC = "Objects scope";
    public static final String ROLE_SRV = "srv";
    public static final String ROLE_SRV_SWAGGER = "role_srv";
    public static final String ROLE_SRV_DESC = "Services scope";
    public static final String ROLE_MNG = "mng";
    public static final String ROLE_MNG_SWAGGER = "role_mng";
    public static final String ROLE_MNG_DESC = "JCP Manager scope";

    public static final String OAUTH_IMPL = "ImplicitCodeFlow";
    public static final String OAUTH_PASS = "AuthCodeFlow";
    public static final String OAUTH_CRED = "ClientCredentialsFlow";
    private static final String OAUTH_URL_AUTH = "https://localhost:8998/auth/realms/jcp/protocol/openid-connect/auth";
    private static final String OAUTH_URL_TOKEN = "https://localhost:8998/auth/realms/jcp/protocol/openid-connect/token";
    private static final String OAUTH_TOKEN_NAME = "token";
    private static final String OAUTH_CLIENT_ID = "";
    private static final String OAUTH_CLIENT_SECRET = "";

    public static final String OAUTH_FLOW_DEF_SRV = OAUTH_IMPL;
    public static final String OAUTH_FLOW_DEF_OBJ = OAUTH_IMPL;
    public static final String OAUTH_FLOW_DEF_MNG = OAUTH_IMPL;
    public static final String OAUTH_FLOW_DEF_TEST = OAUTH_IMPL;

    private static final Contact ContactJohn = new Contact(JCPContacts.getJohn().getFullName(),JCPContacts.getJohn().getUrl(),JCPContacts.getJohn().getEmail());


    // API's groups

    @Bean
    public Docket home() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(metadata(null))
                .select()
                .apis(RequestHandlerSelectors.none())
                .paths(PathSelectors.none())
                .build();
    }

    @Bean
    public Docket exampleApis() {
        return createAPIsGroup(JCPAPIsGroups.getAPIGroupByName(JCPAPIsGroups.API_EXMPL));
    }

    @Bean
    public Docket loginApis() {
        return createAPIsGroup(JCPAPIsGroups.getAPIGroupByName(JCPAPIsGroups.API_LOGIN));
    }

    @Bean
    public Docket objsApis() {
        return createAPIsGroup(JCPAPIsGroups.getAPIGroupByName(JCPAPIsGroups.API_OBJS));
    }


    // Private configurers

    private Docket createAPIsGroup(JCPAPIsGroups.APIGroup api) {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName(api.getName())
                .apiInfo(metadata(api))

                .forCodeGeneration(true)
                .useDefaultResponseMessages(false)
                .genericModelSubstitutes(ResponseEntity.class)

                .securitySchemes(Lists.newArrayList(
                        //oauthAuthCodeFlow()
                        //,oauthClientCredentialFlow()
                        oauthImplicitFlow()
                ))

                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant(api.getPath()))
                .build()
                .useDefaultResponseMessages(false)
                ;

        Tag[] tags = subGroupsToTags(api.getSubGroups());
        if (tags.length>0) {
            Tag tag = tags[0];
            tags = Arrays.copyOfRange(tags, 1, tags.length);
            docket.tags(tag, tags);
        }

        return docket;
    }

    private OAuth oauthAuthCodeFlow() {
        List<GrantType> grants = new ArrayList<>();

        grants.add(new AuthorizationCodeGrant(
                new TokenRequestEndpoint(OAUTH_URL_AUTH,
                        OAUTH_CLIENT_ID,
                        OAUTH_CLIENT_SECRET),
                new TokenEndpoint(OAUTH_URL_TOKEN,
                        OAUTH_TOKEN_NAME)
        ));

        return new OAuth(OAUTH_PASS,getScopesList(),grants);
    }

    private OAuth oauthClientCredentialFlow() {
        List<GrantType> grants = new ArrayList<>();

        grants.add(new ClientCredentialsGrant(
                OAUTH_URL_TOKEN
        ));

        return new OAuth(OAUTH_CRED,getScopesList(),grants);
    }

    private OAuth oauthImplicitFlow() {
        List<GrantType> grants = new ArrayList<>();

        grants.add(new ImplicitGrant(
                new LoginEndpoint(OAUTH_URL_AUTH),
                OAUTH_TOKEN_NAME
        ));

        return new OAuth(OAUTH_IMPL,getScopesList(),grants);
    }

    private List<AuthorizationScope> getScopesList() {
        return Arrays.asList(getScopes());
    }

    private AuthorizationScope[] getScopes() {
        AuthorizationScope[] scopes = new AuthorizationScope[3];
        scopes[0] = new AuthorizationScope(ROLE_OBJ_SWAGGER, ROLE_OBJ_DESC);
        scopes[1] = new AuthorizationScope(ROLE_SRV_SWAGGER, ROLE_SRV_DESC);
        scopes[2] = new AuthorizationScope(ROLE_MNG_SWAGGER, ROLE_MNG_DESC);
        return scopes;
    }

//    private SecurityContext securityContext() {
//        List<SecurityReference> auths = Lists.newArrayList(new SecurityReference("JWT", getScopes()));
//
//        return SecurityContext.builder()
//                .securityReferences(auths)
//                .forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN_NONE))
//                .build();
//    }

    private ApiInfo metadata(JCPAPIsGroups.APIGroup api) {
        String title = api!=null ? api.getTitle() : "JCP APIs";
        String description = api!=null ? api.getDescription() : JCPAPIsGroups.APIGroup.getLoadDescription(String.format(RES_PATH_DESC, "home"));
        String version = api!=null ? api.getVersion() : "";

        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(ContactJohn)
                .build();
    }

    private Tag[] subGroupsToTags(JCPAPIsGroups.APISubGroup[] subGroups) {
        Tag[] tags = new Tag[subGroups.length];
        for (int i=0; i< subGroups.length; i++)
            tags[i] = new Tag(subGroups[i].getName(),subGroups[i].getDescription());
        return tags;
    }
}
