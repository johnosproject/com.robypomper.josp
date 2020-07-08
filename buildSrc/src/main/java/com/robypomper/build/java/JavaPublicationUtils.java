package com.robypomper.build.java;

import groovy.util.Node;
import groovy.xml.QName;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.publish.maven.MavenPom;


/**
 * Add support pure java applications.
 * <p>
 * This class register 2 tasks: jar and run tasks.
 * <p>
 * First one, the jar, task assemble in a single jar all source set java sources.
 * Second one, the run task, allow to execute the main class specified as a java
 * application.
 */
public class JavaPublicationUtils {

    static public void initPom(org.gradle.api.publish.maven.MavenPom pom, String name, String description,
                               String url, String urlGit, String urlGitRepo) {

        pom.getName().set(name);
        pom.getDescription().set(description);
        pom.getUrl().set(url);

        pom.licenses(mavenPomLicenseSpec -> {
            mavenPomLicenseSpec.license(mavenPomLicense -> {
                mavenPomLicense.getName().set("???");
                mavenPomLicense.getUrl().set("???");
            });
        });

        pom.developers(mavenPomDeveloperSpec -> {
            mavenPomDeveloperSpec.developer(mavenPomDeveloper -> {
                mavenPomDeveloper.getId().set("robypomper");
                mavenPomDeveloper.getName().set("Roberto Pompermaier");
                mavenPomDeveloper.getEmail().set("robypomper@johnosproject.com");
            });
        });

        pom.scm(mavenPomScm -> {
            mavenPomScm.getConnection().set(String.format("scm:git:https://%s", urlGit));
            mavenPomScm.getDeveloperConnection().set(String.format("scm:git:ssh://%s", urlGit));
            mavenPomScm.getUrl().set(urlGitRepo);
        });

    }

    static public void injectDependenciesToPom(MavenPom pom, Configuration fromConfig, boolean preClean) {
        pom.withXml(xmlProvider -> {
            Node dependenciesMngrNode = (Node) xmlProvider.asNode().getAt(new QName("dependencyManagement")).get(0);
            if (dependenciesMngrNode == null)
                dependenciesMngrNode = xmlProvider.asNode().appendNode(new QName("dependencyManagement"));

            Node dependenciesNode = (Node) dependenciesMngrNode.getAt(new QName("dependencies")).get(0);
            if (dependenciesNode == null) dependenciesNode = dependenciesMngrNode.appendNode(new QName("dependencies"));
            final Node dependenciesNodeFinal = dependenciesNode;

            if (preClean)
                dependenciesNodeFinal.setValue("");

            fromConfig.getAllDependencies().forEach(it -> {
                if (it instanceof ExternalModuleDependency) {
                    Node dependencyNode = dependenciesNodeFinal.appendNode("dependency");
                    dependencyNode.appendNode("groupId", it.getGroup());
                    dependencyNode.appendNode("artifactId", it.getName());
                    dependencyNode.appendNode("version", it.getVersion());
                }
            });
        });
    }

    static public void clearAllDependenciesToPom(MavenPom pom) {
        pom.withXml(xmlProvider -> {
            Node dependenciesMngrNode = (Node) xmlProvider.asNode().getAt(new QName("dependencyManagement")).get(0);
            if (dependenciesMngrNode == null)
                dependenciesMngrNode = xmlProvider.asNode().appendNode(new QName("dependencyManagement"));

            Node dependenciesNode = (Node) dependenciesMngrNode.getAt(new QName("dependencies")).get(0);
            if (dependenciesNode == null) dependenciesNode = dependenciesMngrNode.appendNode(new QName("dependencies"));
            final Node dependenciesNodeFinal = dependenciesNode;

            dependenciesNodeFinal.setValue("");
        });
    }

}
