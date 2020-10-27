# JOSP Pre-Requisite installation

TODO


## Git

**Ubuntu:**

$ sudo apt-get install git

**Mac:**

Download and install Git at https://git-scm.com/download/mac from HomeBrew

**Windows:**

Download and install Git for Windows at https://git-scm.com/download/win


## Java JDK

**Ubuntu Install:**

$ sudo apt install default-jre					# at release time link to openjdk-11-jre (Avoid Java 14 version)

**Mac Install:**

Download and install Java JDK at https://www.oracle.com/java/technologies/javase-downloads.html#javasejdk from Oracle Official
                                                     at https://github.com/AdoptOpenJDK/homebrew-openjdk from HomeBrew/AdoptOpenJDK
                                                     (Avoid Java 14 version)

**Windows Install:**

Download and install Java JDK at https://www.oracle.com/java/technologies/javase-downloads.html#javasejdk (Avoid Java 14 version)

**Ubuntu switch version:**

$ sudo apt-get install openjdk-8-jdk
$ sudo update-alternatives --config java
$ java -version

**Mac switch version:**

$ /usr/libexec/java_home -V
$ export JAVA_HOME=`/usr/libexec/java_home -v [{JDK/JDK_NAME}]`
$ java -version
    Example:
    export JAVA_HOME=`/usr/libexec/java_home -v 1.8.0_262`

## Docker

**Ubuntu:**

$ sudo apt install docker.io  					# version 19.03.8-0ubuntu1.20.04
$ sudo usermod -aG docker $USER			        # https://docs.docker.com/engine/install/linux-postinstall, reboot VM
$ sudo apt install docker-compose 	 		    # version 1.25.0-1

**Mac:**

Download and install Docker Desktop at https://hub.docker.com/editions/community/docker-ce-desktop-mac

**Windows:**

Install WSL at https://docs.microsoft.com/en-us/windows/wsl/install-win10 and https://docs.microsoft.com/en-gb/windows/wsl/wsl2-kernel
Download and install Docker Desktop at https://www.docker.com/get-started
