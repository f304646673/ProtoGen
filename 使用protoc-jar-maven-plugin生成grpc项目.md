在[《使用protobuf-maven-plugin生成grpc项目》](https://blog.csdn.net/breaksoftware/article/details/138403103)中我们使用protobuf-maven-plugin完成了grpc代码的翻译。本文我们将只是替换pom.xml中的部分内容，使用protoc-jar-maven-plugin来完成相同的功能。总体来说protoc-jar-maven-plugin方案更加简便。
# 环境
见[《使用protobuf-maven-plugin生成grpc项目》](https://blog.csdn.net/breaksoftware/article/details/138403103)
# 准备工作
## 目录结构
见[《使用protobuf-maven-plugin生成grpc项目》](https://blog.csdn.net/breaksoftware/article/details/138403103)
# pom.xml
本次pom.xml的修改我们将基于[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法2》](https://blog.csdn.net/breaksoftware/article/details/138379125)。因为它是基于protoc-jar-maven-plugin翻译proto的message类型，而本文只需要新增对grpc的支持即可。
## 新增grpc依赖
这块的内容和[《使用protobuf-maven-plugin生成grpc项目》](https://blog.csdn.net/breaksoftware/article/details/138403103)中一致。
```xml
<properties>
    <io-grpc.version>1.63.0</io-grpc.version>
</properties>

<dependencies>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-netty-shaded</artifactId>
        <version>${io-grpc.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-protobuf</artifactId>
        <version>${io-grpc.version}</version>
    </dependency>
    <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-stub</artifactId>
        <version>${io-grpc.version}</version>
    </dependency>
    <dependency> <!-- necessary for Java 9+ -->
        <groupId>org.apache.tomcat</groupId>
        <artifactId>annotations-api</artifactId>
        <version>6.0.53</version>
        <scope>provided</scope>
    </dependency>

</dependencies>
```
## 分割message和service生成
之前我们使用下面代码生成message类型代码。

```xml
<configuration>
    <protoSourceRoot>${project.basedir}/src/main/proto</protoSourceRoot>
    <outputDirectory>${project.basedir}/src/main/java/protojava</outputDirectory>
    <protocArtifact>com.google.protobuf:protoc:${protobuf-java.version}:exe:${os.detected.classifier}</protocArtifact>
</configuration>
```
由于message和service类型生成指令不一样，于是我们要分开处理。这就要使用到outputTargets标签。而该标签和configuration/outputDirectory互斥，于是就要去掉configuration/outputDirectory，并在outputTargets/outputTarget下新增outputDirectory

```xml
<outputTargets>
    <outputTarget>
        <type>java</type>
        <outputDirectory>src/main/java/protojava</outputDirectory>
    </outputTarget>
    <outputTarget>
        <type>grpc-java</type>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:${io-grpc.version}</pluginArtifact>
        <outputDirectory>src/main/java/protojava</outputDirectory>
    </outputTarget>
</outputTargets>
```
上侧java部分用于生成proto中的message部分；grpc-java则用于生成proto中的service部分。
protoc的翻译操作也不用像使用protobuf-maven-plugin方案那样，要执行一次protobuf:compile后再执行一次protobuf:compile-custom
。而只需要执行一次protoc-jar:run。
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/4f8886cf61a8432cb00177973d40ab92.png)
## 完整文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>proto-message-gen</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <protobuf-java.version>4.26.1</protobuf-java.version>
        <io-grpc.version>1.63.0</io-grpc.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java</artifactId>
            <version>${protobuf-java.version}</version>
        </dependency>
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java-util</artifactId>
            <version>${protobuf-java.version}</version>
        </dependency>

        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-netty-shaded</artifactId>
            <version>${io-grpc.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-protobuf</artifactId>
            <version>${io-grpc.version}</version>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-stub</artifactId>
            <version>${io-grpc.version}</version>
        </dependency>
        <dependency> <!-- necessary for Java 9+ -->
            <groupId>org.apache.tomcat</groupId>
            <artifactId>annotations-api</artifactId>
            <version>6.0.53</version>
            <scope>provided</scope>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>com.github.os72</groupId>
                <artifactId>protoc-jar-maven-plugin</artifactId>
                <version>3.11.4</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>run</goal>
                        </goals>
                        <configuration>
                            <protocArtifact>com.google.protobuf:protoc:${protobuf-java.version}</protocArtifact>
                            <inputDirectories>
                                <include>src/main/proto</include>
                            </inputDirectories>
                            <outputTargets>
                                <outputTarget>
                                    <type>java</type>
                                    <outputDirectory>src/main/java/protojava</outputDirectory>
                                </outputTarget>
                                <outputTarget>
                                    <type>grpc-java</type>
                                    <pluginArtifact>io.grpc:protoc-gen-grpc-java:${io-grpc.version}</pluginArtifact>
                                    <outputDirectory>src/main/java/protojava</outputDirectory>
                                </outputTarget>
                            </outputTargets>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
```

# 测试代码
见[《使用protobuf-maven-plugin生成grpc项目》](https://blog.csdn.net/breaksoftware/article/details/138403103)

# 代码仓库
[https://github.com/f304646673/proto-gen.git](https://github.com/f304646673/proto-gen.git)

# 参考资料
- [https://os72.github.io/protoc-jar-maven-plugin/index.html](https://os72.github.io/protoc-jar-maven-plugin/index.html)
