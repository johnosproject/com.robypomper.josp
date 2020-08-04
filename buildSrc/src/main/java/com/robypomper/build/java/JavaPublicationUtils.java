/* *****************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2020 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **************************************************************************** */

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
        initPom(pom, name, description, url, urlGit, urlGitRepo, null, null);
    }

    static public void initPom(org.gradle.api.publish.maven.MavenPom pom, String name, String description,
                               String url, String urlGit, String urlGitRepo,
                               String licence, String urlLicence) {

        pom.getName().set(name);
        pom.getDescription().set(description);
        pom.getUrl().set(url);

        pom.licenses(mavenPomLicenseSpec -> {
            mavenPomLicenseSpec.license(mavenPomLicense -> {
                if (licence != null) mavenPomLicense.getName().set(licence);
                if (urlLicence != null) mavenPomLicense.getUrl().set(urlLicence);
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
            Node dependenciesNode = null;
            try {
                dependenciesNode = (Node) xmlProvider.asNode().getAt(new QName("dependencies")).get(0);
            } catch (IndexOutOfBoundsException ignore) {
            }
            if (dependenciesNode == null) dependenciesNode = xmlProvider.asNode().appendNode(new QName("dependencies"));
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
