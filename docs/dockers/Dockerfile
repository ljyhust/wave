# CentOS with JDK 8
# Author   dockerzlnewbie

# build a new image with basic  centos
FROM centos:6.8
# who is the author
MAINTAINER lijinyang

# make a new directory to store the jdk files
RUN mkdir /usr/local/java

# copy the jdk  archive to the image,and it will automaticlly unzip the tar file
ADD OpenJDK8U-jdk_x64_linux_hotspot_8u292b10.tar.gz /usr/local/java/

# make a symbol link 
RUN ln -s /usr/local/java/jdk8u292-b10 /usr/local/java/jdk

# set environment variables
ENV JAVA_HOME /usr/local/java/jdk
ENV JRE_HOME ${JAVA_HOME}/jre
ENV CLASSPATH .:${JAVA_HOME}/lib:${JRE_HOME}/lib
ENV PATH ${JAVA_HOME}/bin:$PATH

WORKDIR /maven
# maven
ADD apache-maven-3.5.4-bin.tar.gz /usr/local/

COPY settings.xml /usr/local/apache-maven-3.5.4/conf
RUN ln -s /usr/local/apache-maven-3.5.4 /usr/local/maven

# set env
ENV MAVEN_HOME /usr/local/maven
ENV PATH ${MAVEN_HOME}/bin:$PATH

WORKDIR /app

CMD ["/bin/bash"]