在[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://blog.csdn.net/breaksoftware/article/details/138368523)中我们使用了protobuf-maven-plugin插件来给Proto文件生成Java语言版代码。本文我们将使用一种更简单的插件来完成这个功能。
本文实验的操作系统和代码库都和[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://blog.csdn.net/breaksoftware/article/details/138368523)一样。区别仅仅是pom.xml文件。
# protoc-jar-maven-plugin
本章我们使用的是protoc-jar-maven-plugin。它相较于protobuf-maven-plugin的优点是：自动识别操作系统，不用引入os-maven-plugin来新增对${os.detected.classifier}的识别。这样我们就可以用更简单的pom.xml来完成相同的功能。

```xml
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
                        <outputDirectory>src/main/java/protojava</outputDirectory>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
protocArtifact用于指定protoc生成的Java依赖的版本。
inputDirectories用于指定proto文件的位置。
outputDirectory用于指定产出的文件位置。
完整文件如下

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
                            <outputDirectory>src/main/java/protojava</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
```
# protobuf-maven-plugin
这个方案来源于[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://blog.csdn.net/breaksoftware/article/details/138368523)。贴在这儿供大家比较选择。
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
    </dependencies>

    <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.7.1</version>
            </extension>
        </extensions>
        <plugins>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.6.1</version>
                <extensions>true</extensions>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>test-compile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <additionalProtoPathElements>
                        <additionalProtoPathElement>${project.basedir}/src/main/proto</additionalProtoPathElement>
                    </additionalProtoPathElements>
                    <outputDirectory>${project.basedir}/src/main/java/protojava</outputDirectory>
                    <protocArtifact>com.google.protobuf:protoc:${protobuf-java.version}:exe:${os.detected.classifier}</protocArtifact>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```
# 测试代码
见[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://blog.csdn.net/breaksoftware/article/details/138368523)

# 参考资料

 - [https://os72.github.io/protoc-jar-maven-plugin/run-mojo.html#inputDirectories](https://os72.github.io/protoc-jar-maven-plugin/run-mojo.html#inputDirectories)
 - [https://os72.github.io/protoc-jar-maven-plugin/index.html](https://os72.github.io/protoc-jar-maven-plugin/index.html)
 - [https://mvnrepository.com/artifact/com.github.os72/protoc-jar-maven-plugin/3.11.4](https://mvnrepository.com/artifact/com.github.os72/protoc-jar-maven-plugin/3.11.4)
 - [https://github.com/os72/protoc-jar-maven-plugin](https://github.com/os72/protoc-jar-maven-plugin)
 - [https://medium.com/@lucian.ritan/setup-and-run-a-grpc-project-eda408c8cef0](https://medium.com/@lucian.ritan/setup-and-run-a-grpc-project-eda408c8cef0)
 - [https://github.com/grpc/grpc-java/blob/master/examples/pom.xml](https://github.com/grpc/grpc-java/blob/master/examples/pom.xml)
 - [https://stackoverflow.com/questions/35934276/using-grpc-in-maven](https://stackoverflow.com/questions/35934276/using-grpc-in-maven)
