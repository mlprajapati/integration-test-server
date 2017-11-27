## How to Automate Integration Tests for Web Services

In this article, I will focus mainly on how to evaluate the effectiveness of the test cases, which can be measured by the amount of code executed by the test case. I am talking about test or code coverage.

## Background

Like executing test cases for integration tests, test coverage also takes a slightly different approach. Rather, I would say, a break down approach; this means we cannot simply launch a runner and start capturing a report on the fly. It needs to be broken down into following sub-processes:

```
instrument > execute > report
```

An on-the-fly approach should work, but in most cases, a web service deployment environment adds some extra behaviors at the class loading level, so it might conflict with your approach.

## Solution

There are lots of commercial and OSS tools available for this, providing different kinds of reporting/visualization support. But, at the bare minimum, we need to attach an agent to the Server process that captures and logs some kind of metadata, and both compile and run time, to generate the report.

The simple yet powerful tool for this, in my opinion, is emma. Emma provides both on-the-fly and offline approaches to generate the desired report. It captures coverage at four levels:

•   Class

•   Method

•   Line

•   Block


You can use emma as an external tool and as well as an automated task. Currently it supports automation via ANT task but it can easily be plugged with maven using maven-antrun-plugin.

## Pre-Requisites

To start, you would need to download emma-<version>.jar and emma_ant<version>.jar.
  
•   Create a build.xml in your web service project and link an emma task to your ant build.

```
<path id="emma.lib">
    <pathelement location="${emma.dir}/lib/emma-2.0.5312.jar" />
    <pathelement location="${emma.dir}/lib/emma_ant-2.0.5312.jar" />
</path>

```

•   Create a folder for emma and put the build.xml and above jar in a lib folder under it.

Your structure should look like below:

/ path / to / your / project folder

|____ main/java/src
|____ pom.xml
|____ emma            
      |____ build.xml            
      |____ lib                        
            |____ emma-<version>.jar                        
            |____ emma_ant<version>.jar


Now you are ready for your main solution.

•   Add maven-antrun-plugin to your Maven build in pom.xml.

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-antrun-plugin</artifactId>
    <version>1.7</version>
</plugin>

```

•   Add instrument and report executions to your antrun goal.

```

<executions>
    <execution>
        <id>emma-instr</id>
        <phase>compile</phase>
        <goals><goal>run</goal></goals>
        <configuration>
            <target>
                <property name="basedir" value="." />
                <property name="emma.dir" value="${basedir}\emma" />
                <property name="build.classpath" value="/path/to/your/build/directory" />
                <ant antfile="${emma.dir}\build.xml">
                  <target name="instr"/>
                </ant>
            </target>
        </configuration>
    </execution>
    <execution>
        <id>emma-report</id>
        <phase>report</phase>
        <goals><goal>run</goal></goals>
        <configuration>
            <target>
                <property name="basedir" value="." />
                <property name="emma.dir" value="${basedir}\emma" />
                <ant antfile="${emma.dir}\build.xml">
                    <target name="report"/>
                </ant>
            </target>
        </configuration>
    </execution>
</executions>

```

•   Add instrument and report tasks to your ant build in build.xml.

```

<path id="run.classpath">
    <pathelement location="${build.classpath}" />
</path>

<target name="instr">
    <emma enabled="true">
        <instr instrpathref="run.classpath" metadatafile="${emma.dir}/work/coverage.em" mode="overwrite" />
    </emma>
</target>

<target name="report">
    <emma enabled="true">
        <report>
            <fileset dir="${emma.dir}">
                <include name="work/coverage.em" />
            </fileset>
            <html outfile="${emma.dir}/report/coverage.html" />
        </report>
    </emma>
</target>

```

•   Voila! That's it!



Now you are ready to generate the automated coverage of your integration test for your web service.

You can compile/deploy your application and run the test against it. Make sure compilation include following goal/execution

```
mvn clean compile antrun:run@emma-instr
```

The output metadata file would be available under work folder in emma directory. You need to run following goal/execution to
generate your report.

```
mvn antrun:run@emma-report
```
