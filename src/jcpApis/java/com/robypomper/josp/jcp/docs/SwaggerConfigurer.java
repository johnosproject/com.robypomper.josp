package com.robypomper.josp.jcp.docs;

import com.robypomper.josp.jcp.info.JCPAPIsGroups;
import com.robypomper.josp.jcp.info.JCPContacts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

import static com.robypomper.josp.jcp.info.JCPAPIsGroups.RES_PATH_DESC;


@Configuration
@EnableSwagger2
public class SwaggerConfigurer {

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
