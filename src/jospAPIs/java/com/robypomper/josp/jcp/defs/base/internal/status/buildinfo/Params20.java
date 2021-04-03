package com.robypomper.josp.jcp.defs.base.internal.status.buildinfo;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JCP All - Status / Build Info 2.0
 */
public class Params20 {

    // Build Info methods

    public static class BuildInfo extends com.robypomper.BuildInfo {

        public static BuildInfo clone(com.robypomper.BuildInfo other) {
            BuildInfo bi = new BuildInfo();
            bi.project = other.project;
            bi.sourceSet = other.sourceSet;
            bi.version = other.version;
            bi.versionBuild = other.versionBuild;
            bi.buildTime = other.buildTime;
            bi.javaVersion = other.javaVersion;
            bi.javaHome = other.javaHome;
            bi.gradleVersion = other.gradleVersion;
            bi.gitCommit = other.gitCommit;
            bi.gitCommitShort = other.gitCommitShort;
            bi.gitBranch = other.gitBranch;
            bi.user = other.user;
            bi.osName = other.osName;
            bi.osVersion = other.osVersion;
            bi.osArch = other.osArch;
            if (other.extra!=null)
                bi.extra = new HashMap<>(other.extra);
            return bi;
        }
    }

}
