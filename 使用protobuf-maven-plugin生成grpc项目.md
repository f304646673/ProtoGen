在[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://blog.csdn.net/breaksoftware/article/details/138368523)一文中，我们使用了protobuf-maven-plugin来生成proto中的message类型结构体。本文我们将使用该插件，完成grpc依赖的生成。
# 环境
参见[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://blog.csdn.net/breaksoftware/article/details/138368523)
# 准备工作
## 目录结构
主要结构参见[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://blog.csdn.net/breaksoftware/article/details/138368523)。但是本次我们在src/main/proto下放置了router.proto文件，它定义了服务接口。
```bash
syntax = "proto3";
import "request.proto";
import "response.proto";

option java_package = "com.proto.message.gen.proto";

service Router {
  rpc GetPeoples (Request) returns (Response) {}
}
```
Request和Response来源于外部proto文件，所以我们通过import引入。
# pom.xml的配置
## 依赖
和[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://blog.csdn.net/breaksoftware/article/details/138368523)中介绍的message类型一样，protoc只是辅助生成proto文件对应的代码，而不会生成底层代码。所以我们需要引入底层代码依赖

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
## 插件
我们需要针对protoc生成service类型新增一个插件

```xml
<pluginId>grpc-java</pluginId>
<pluginArtifact>io.grpc:protoc-gen-grpc-java:${io-grpc.version}:exe:${os.detected.classifier}</pluginArtifact>         
```
## 关闭自动清理
clearOutputDirectory的默认值是true。它意思protoc翻译proto时，会将输出目录清空。
```xml
<clearOutputDirectory>false</clearOutputDirectory>
```
我们关闭这个选项的原因是，message和service类型需要在maven中执行两次生成操作。如果开启这个选项，第二次生成会将第一次生成的结果清空，结果导致文件缺失。
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/a3e04a6baafc40afaf78c784157536de.png)
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
                    <clearOutputDirectory>false</clearOutputDirectory>
                    <protoSourceRoot>${project.basedir}/src/main/proto</protoSourceRoot>
                    <outputDirectory>${project.basedir}/src/main/java/protojava</outputDirectory>
                    <protocArtifact>com.google.protobuf:protoc:${protobuf-java.version}:exe:${os.detected.classifier}</protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>io.grpc:protoc-gen-grpc-java:${io-grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```
# 测试
protobuf:compile会将message类型proto翻译到导出目录；protobuf:compile-custom则会将service类型proto翻译到导出目录。相较于[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://blog.csdn.net/breaksoftware/article/details/138368523)的翻译结果，第二步骤会多产出RouterGrpc文件，其中会包含我们后续会使用的RouterImplBase类。
## 编写服务端Service

```java
package org.example;
import com.proto.message.gen.proto.RequestOuterClass;
import com.proto.message.gen.proto.ResponseOuterClass;
import com.proto.message.gen.proto.RouterGrpc;

import io.grpc.stub.StreamObserver;

public final class RouterService extends RouterGrpc.RouterImplBase {
    @Override
    public void getPeoples(RequestOuterClass.Request request, StreamObserver<ResponseOuterClass.Response> responseObserver) {
        System.out.printf("Request received: %s\n", request.toString());
        ResponseBuilder responseBuilder = new ResponseBuilder();
        ResponseOuterClass.Response response = responseBuilder.buildResponse();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```
这是一种结构性写法，即：

 - 继承ImplBase类。
 - 重载proto中定义的方法。
 - 使用StreamObserver的onNext返回值。
 - 使用StreamObserver的onCompleted结束本次请求。

## 编写服务端启动代码
这也是结构性的写法：
- 使用Grpc.newServerBuilderForPort等方法构建一个ServerBuilder。
- 通过ServerBuilder的addService给服务新增Service。这意味着可以多次调用addService方法来给服务增多多个Service。
- 使用ServerBuilder的build方法创建一个Server。
- 调用Server的start启动服务。
```java
final int port = 50051;
final Server server;
try {
    RouterService service = new RouterService();
    server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create()).addService(service).build().start();
} catch (IOException e) {
    throw new RuntimeException(e);
}

try {
    server.awaitTermination();
} catch (InterruptedException e) {
    throw new RuntimeException(e);
}
```
## 编写客户端代码
这也是结构性的写法：
- 使用ManagedChannelBuilder构建一个channel。
- 使用RouterGrpc.newBlockingStub等方法，以及上一步创建的channel，构建一个stub。
- 调用stub的、与服务端对应的方法，发送请求并接收返回结果。
```java
try {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", port)
            .usePlaintext()
            .build();

    RouterGrpc.RouterBlockingStub blockingStub = RouterGrpc.newBlockingStub(channel);
    RequestBuilder requestBuilder = new RequestBuilder();
    RequestOuterClass.Request request = requestBuilder.buildRequest();
    ResponseOuterClass.Response response = blockingStub.getPeoples(request);
    System.out.printf("Response received: %s\n", response.toString());
} catch (Exception e) {
    throw new RuntimeException(e);
}
```
## 完整代码

```java
// RouterService.java
package org.example;
import com.proto.message.gen.proto.RequestOuterClass;
import com.proto.message.gen.proto.ResponseOuterClass;
import com.proto.message.gen.proto.RouterGrpc;

import io.grpc.stub.StreamObserver;

public final class RouterService extends RouterGrpc.RouterImplBase {
    @Override
    public void getPeoples(RequestOuterClass.Request request, StreamObserver<ResponseOuterClass.Response> responseObserver) {
        System.out.printf("Request received: %s\n", request.toString());
        ResponseBuilder responseBuilder = new ResponseBuilder();
        ResponseOuterClass.Response response = responseBuilder.buildResponse();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

```

```java
// Main.java
package org.example;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.InvalidProtocolBufferException;
import com.proto.message.gen.proto.RequestOuterClass;
import com.proto.message.gen.proto.ResponseOuterClass;
import com.proto.message.gen.proto.RouterGrpc;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        final int port = 50051;
        final Server server;
        try {
            RouterService service = new RouterService();
            server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create()).addService(service).build().start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Add a shutdown hook to clean up resources when the JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down gRPC server since JVM is shutting down");
                server.shutdown();
                try {
                    // Wait for RPCs to complete processing
                    if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
                        // That was plenty of time. Let's cancel the remaining RPCs
                        server.shutdownNow();
                        // shutdownNow isn't instantaneous, so give a bit of time to clean resources up
                        // gracefully. Normally this will be well under a second.
                        server.awaitTermination(5, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException ex) {
                    server.shutdownNow();
                }
                System.out.println("Server shut down");
            }
        });

        // start a thead to call service
        start_client(port);

        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void start_client(int port) {
        new Thread(() -> {
            try {
                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", port)
                        .usePlaintext()
                        .build();

                RouterGrpc.RouterBlockingStub blockingStub = RouterGrpc.newBlockingStub(channel);
                RequestBuilder requestBuilder = new RequestBuilder();
                RequestOuterClass.Request request = requestBuilder.buildRequest();
                ResponseOuterClass.Response response = blockingStub.getPeoples(request);
                System.out.printf("Response received: %s\n", response.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
```
# 代码仓库
[https://github.com/f304646673/proto-gen.git](https://github.com/f304646673/proto-gen.git)
# 参考资料
- [https://github.com/grpc/grpc-java/tree/master/examples/example-hostname/src/main/java/io/grpc/examples/hostname](https://github.com/grpc/grpc-java/tree/master/examples/example-hostname/src/main/java/io/grpc/examples/hostname)
- [https://www.baeldung.com/grpc-introduction](https://www.baeldung.com/grpc-introduction)
- [https://www.xolstice.org/protobuf-maven-plugin/compile-mojo.html](https://www.xolstice.org/protobuf-maven-plugin/compile-mojo.html)
- [https://grpc.io/docs/languages/java/basics/](https://grpc.io/docs/languages/java/basics/)
