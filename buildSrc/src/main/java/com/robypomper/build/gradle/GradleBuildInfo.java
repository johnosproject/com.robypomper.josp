package com.robypomper.build.gradle;

import com.robypomper.build.commons.Naming;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

public class GradleBuildInfo {

    private static final String GENERATED_SOURCE_PATH = "%s/build/generated/sources/buildInfo/java/%s/%s";
    private static final String GENERATED_RESOURCE_PATH = "%s/build/generated/resources/buildInfo/java/%s/%s";
    private static final String CLASS_BUILD_INFO = "package com.robypomper;\n" +
            "\n" +
            "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
            "\n" +
            "import java.io.File;\n" +
            "import java.io.IOException;\n" +
            "import java.net.URL;\n" +
            "import java.util.Map;\n" +
            "\n" +
            "public class BuildInfo {\n" +
            "\n" +
            "    public final static BuildInfo Current = load();\n" +
            "    \n" +
            "    public String Project;\n" +
            "    public String SourceSet;\n" +
            "    public String Version;\n" +
            "    public String VersionBuild;\n" +
            "    public String BuildTime;\n" +
            "    public String JavaVersion;\n" +
            "    public String JavaHome;\n" +
            "    public String GradleVersion;\n" +
            "    public String GitCommit;\n" +
            "    public String GitCommitShort;\n" +
            "    public String GitBranch;\n" +
            "    public String User;\n" +
            "    public String OSName;\n" +
            "    public String OSVersion;\n" +
            "    public Map<String,String> Extra;\n" +
            "\n" +
            "    private static BuildInfo load() {\n" +
            "        URL resource = BuildInfo.class.getClassLoader().getResource(\"buildInfo.json\");\n" +
            "        if (resource == null) {\n" +
            "            throw new IllegalArgumentException(\"File 'buildInfo.json' not found\");\n" +
            "        }\n" +
            "\n" +
            "        try {\n" +
            "            ObjectMapper mapper = new ObjectMapper();\n" +
            "            return mapper.readValue(new File(resource.getFile()), BuildInfo.class);\n" +
            "        } catch (IOException e) {\n" +
            "            throw new IllegalArgumentException(\"Error parsing file\", e);\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "}\n";
    private static final String CLASS_TMPL = "package com.robypomper.%s;\n" +
            "\n" +
            "import com.fasterxml.jackson.databind.ObjectMapper;\n" +
            "\n" +
            "import com.robypomper.BuildInfo;\n" +
            "import java.io.File;\n" +
            "import java.io.IOException;\n" +
            "import java.net.URL;\n" +
            "import java.util.Map;\n" +
            "\n" +
            "public class BuildInfo%s extends BuildInfo {\n" +
            "}\n";
    private static final String RESOURCE_PROPERTY_TMPL = "    \"%s\": \"%s\",\n";
    private static final String RESOURCE_EXTRA_TMPL = "        \"%s\": \"%s\",\n";
    private static final String RESOURCE_EXTRAS_TMPL = "    \"Extra\": {\n%s    }\n";
    private static final String RESOURCE_TMPL = "{\n%s}";


    public static void makeBuildInfoForSourceSet(Project project, SourceSet ss, String version) {
        makeBuildInfoForSourceSet(project, ss, version, null);
    }

    public static void makeBuildInfoForSourceSet(Project project, SourceSet ss, String version, Map<String, Object> extraInfo) {
        String genResourceCode = generateResource(project, ss, version, extraInfo);
        try {
            saveFile(new File(generatedResourcePath(project, ss, "buildInfo.json")), genResourceCode);
            ss.getResources().srcDir(generatedResourcePath(project, ss, ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String genSourceCode = generateSourceCode(ss);

        try {
            saveFile(new File(generatedSourcePath(project, ss, "BuildInfo.java")), CLASS_BUILD_INFO);
            saveFile(new File(generatedSourcePath(project, ss, String.format("BuildInfo%s.java", Naming.capitalize(ss.getName())))), genSourceCode);
            ss.getJava().srcDir(generatedSourcePath(project, ss, ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generatedSourcePath(Project project, SourceSet sourceSet, String fileName) {
        return String.format(GENERATED_SOURCE_PATH, project.getProjectDir().getPath(), sourceSet.getName(), fileName);
    }

    private static String generatedResourcePath(Project project, SourceSet sourceSet, String fileName) {
        return String.format(GENERATED_RESOURCE_PATH, project.getProjectDir().getPath(), sourceSet.getName(), fileName);
    }

    private static String generateResource(Project project, SourceSet ss, String version, Map<String, Object> extraInfo) {
        String properties = "";
        properties += String.format(RESOURCE_PROPERTY_TMPL, "Project", project.getName());
        properties += String.format(RESOURCE_PROPERTY_TMPL, "SourceSet", ss.getName());
        properties += String.format(RESOURCE_PROPERTY_TMPL, "Version", version);
        properties += String.format(RESOURCE_PROPERTY_TMPL, "VersionBuild", version + "-" + getBuildDate());
        properties += String.format(RESOURCE_PROPERTY_TMPL, "BuildTime", getCurrentDateTimeUTC());
        properties += String.format(RESOURCE_PROPERTY_TMPL, "JavaVersion", System.getProperty("java.specification.version"));
        properties += String.format(RESOURCE_PROPERTY_TMPL, "JavaHome", System.getProperty("java.home"));
        properties += String.format(RESOURCE_PROPERTY_TMPL, "GradleVersion", project.getGradle().getGradleVersion());
        properties += String.format(RESOURCE_PROPERTY_TMPL, "GitCommit", getGitCommit(project));
        properties += String.format(RESOURCE_PROPERTY_TMPL, "GitCommitShort", getGitCommitShort(project));
        properties += String.format(RESOURCE_PROPERTY_TMPL, "GitBranch", getGitBranch(project));
        properties += String.format(RESOURCE_PROPERTY_TMPL, "User", System.getProperty("user.name"));
        properties += String.format(RESOURCE_PROPERTY_TMPL, "OSName", System.getProperty("os.name"));
        properties += String.format(RESOURCE_PROPERTY_TMPL, "OSVersion", System.getProperty("os.version"));

        if (extraInfo != null) {
            StringBuilder extraProperties = new StringBuilder();
            for (Map.Entry<String, Object> extraInfoEntity : extraInfo.entrySet())
                extraProperties.append(String.format(RESOURCE_EXTRA_TMPL, extraInfoEntity.getKey(), extraInfoEntity.getValue().toString()));
            String extraPropertiesStr = extraProperties.toString();
            extraPropertiesStr = extraPropertiesStr.substring(0, extraPropertiesStr.length() - 2) + "\n";
            properties += String.format(RESOURCE_EXTRAS_TMPL, extraPropertiesStr);
        } else {
            properties = properties.substring(0, properties.length() - 2) + "\n";
        }

        return String.format(RESOURCE_TMPL, properties);
    }

    private static String getCurrentDateTimeUTC() {
        Date date = new Date();
        SimpleDateFormat sdf_utc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'zzz");
        sdf_utc.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf_utc.format(date);
    }

    private static String getBuildDate() {
        Date date = new Date();
        SimpleDateFormat sdf_utc = new SimpleDateFormat("yyyyMMddHHmm");
        sdf_utc.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf_utc.format(date);
    }

    private static String getGitCommit(Project project) {
        try {
            return execCmd("git rev-parse HEAD", project.getProjectDir());
        } catch (IOException e) {
            return "N/A";
        }
    }

    private static String getGitCommitShort(Project project) {
        try {
            return execCmd("git rev-parse --short HEAD", project.getProjectDir());
        } catch (IOException e) {
            return "N/A";
        }
    }

    private static String getGitBranch(Project project) {
        try {
            return execCmd("git rev-parse --abbrev-ref HEAD", project.getProjectDir());
        } catch (IOException e) {
            return "N/A";
        }
    }

    private static String generateSourceCode(SourceSet ss) {
        return String.format(CLASS_TMPL, ss.getName(), Naming.capitalize(ss.getName()));
    }

    private static void saveFile(File file, String genSourceCode) throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }

        FileWriter myWriter = new FileWriter(file);
        myWriter.write(genSourceCode);
        myWriter.close();
    }

    private static String execCmd(String cmd, File workingDir) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(workingDir);
        processBuilder.command("sh", "-c", cmd);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder cmdOutput = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            cmdOutput.append(line);
        }
        return cmdOutput.toString();
    }

}
