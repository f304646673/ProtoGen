各语言的Protocol Buffers文件都需要通过protoc来生成，这个动作往往需要手动输入命令完成。本文介绍的方法，将借助Maven来实现自动化生成工作。这样开发者只要专注于proto的定义，且不用将生成的文件上传到代码仓库，从而降低开发的复杂度。
# Protocol Buffers介绍
Protocol Buffers是一个强大的数据序列化工具，它提供了一种高效、便捷、可读性强且安全性高的方式来处理结构化数据。它能够将复杂的数据结构转换为紧凑的二进制格式，从而方便地进行网络传输或硬盘存储。接收方可以使用相同的数据结构定义来解析这些二进制数据，从而还原成原始的数据。
ProtoBuf的用途广泛，特别适用于需要频繁处理数据的场景，如网络通信和数据存储。在网络通信中，ProtoBuf可以帮助开发者在不同系统和平台之间实现高效、可靠的数据交换和通信。而在数据存储方面，ProtoBuf则提供了一种紧凑、可移植的数据表示方式，使得数据的读写和存储变得更加高效和便捷。
特别是在多语言开发环境下，不同语言可以通过Protocol Buffers描述文件生成各自语言的代码，从而实现：一套定义，多语言便捷使用的目的。
本文我们将介绍如果使用Maven自动生成Java语言包。
# 环境
首先介绍下测试环境
## Windows 10
### Java
```bash
java.exe -version
```

> openjdk version "21.0.2" 2024-01-16
OpenJDK Runtime Environment (build 21.0.2+13-58)
OpenJDK 64-Bit Server VM (build 21.0.2+13-58, mixed mode, sharing)

### Maven
IntelliJ捆绑的Maven3.9.5
![在这里插入图片描述](https://img-blog.csdnimg.cn/direct/26a8a7244e0944edbe94a3e88875015b.png)

## Ubuntu TLS 22
### Java
```bash
java -version
```

> openjdk version "21.0.2" 2024-01-16
OpenJDK Runtime Environment (build 21.0.2+13-Ubuntu-122.04.1)
OpenJDK 64-Bit Server VM (build 21.0.2+13-Ubuntu-122.04.1, mixed mode, sharing)

### Maven

```bash
mvn -version
```

> Apache Maven 3.6.3
Maven home: /usr/share/maven
Java version: 21.0.2, vendor: Private Build, runtime: /usr/lib/jvm/java-21-openjdk-amd64
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.15.0-105-generic", arch: "amd64", family: "unix"

# 准备工作
## 目录结构

```bash
├── pom.xml
└── src
    └── main
        ├── java
        │   ├── org
        │   │   └── example
        │   │       ├── AddressBuilder.java
        │   │       ├── Main.java
        │   │       ├── PersonBuilder.java
        │   │       ├── RequestBuilder.java
        │   │       └── ResponseBuilder.java
        └── proto
            ├── person.proto
            ├── request.proto
            └── response.proto
```
proto目录放置的是我们需要被编码为Java语言的Protocol Buffers文件。
# pom.xml的配置
## protoc
protoc是将proto文件转译成各种编程语言对应的源码的工具，所以这个工具一定是要使用的。只是我们不希望开发人员自己关注该工具的维护，而是统一在pom.xml中自动维护。protobuf-maven-plugin这个插件就提供了这个功能。
### 维护protoc的插件
```xml
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
</plugin>
```
### 指定读取的proto文件位置
additionalProtoPathElements可以指定一系列proto文件路径的位置。本例中我们的proto都在一个文件夹下，所以只用设定一个additionalProtoPathElement就行。借助这个属性，我们可以在复杂的项目中，管理多个proto文件路径。
```xml
<configuration>
    <additionalProtoPathElements>
        <additionalProtoPathElement>${project.basedir}/src/main/proto</additionalProtoPathElement>
    </additionalProtoPathElements>
</configuration>
```
### 指定生成路径
假如我们希望生成的文件不在target目录下，则可以考虑该指定protoc的产出路径。

```xml
 <configuration>
         <outputDirectory>${project.basedir}/src/main/java/protojava</outputDirectory>
</configuration>
```
这样最终我们的目录会变成如下结构

```bash
├── pom.xml
└── src
    └── main
        ├── java
        │   ├── org
        │   │   └── example
        │   │       ├── AddressBuilder.java
        │   │       ├── Main.java
        │   │       ├── PersonBuilder.java
        │   │       ├── RequestBuilder.java
        │   │       └── ResponseBuilder.java
        │   └── protojava
        │       └── com
        │           └── proto
        │               └── message
        │                   └── gen
        │                       └── proto
        │                           ├── AcademicDiplomas.java
        │                           ├── Address.java
        │                           ├── AddressOrBuilder.java
        │                           ├── Person.java
        │                           ├── PersonOrBuilder.java
        │                           ├── PersonOuterClass.java
        │                           ├── RequestOuterClass.java
        │                           └── ResponseOuterClass.java
        └── proto
            ├── person.proto
            ├── request.proto
            └── response.proto
```
### 多操作系统支持
现实中，一个项目的开发人员可能因为不同的习惯而需要在不同操作系统上进行开发，比如Windows、Mac或Linux。这些操作系统的可执行程序的文件格式不一样，这样就需要不同protoc来支持。为了完成这个功能，我们需要引入${os.detected.classifier}来识别操作系统。

> [INFO] os.detected.classifier: windows-x86_64
> [INFO] os.detected.classifier: linux-x86_64

具体的配置如下：

```bash
<configuration>
     <protocArtifact>com.google.protobuf:protoc:3.7.6:exe:${os.detected.classifier}</protocArtifact>
</configuration>
```
要使用${os.detected.classifier}还需要os-maven-plugin

```xml
<extensions>
    <extension>
        <groupId>kr.motd.maven</groupId>
        <artifactId>os-maven-plugin</artifactId>
        <version>1.7.1</version>
    </extension>
</extensions>
```
### 指定protobuf-java的版本
protoc生成文件只是proto文件的解释，而不会包含更底层的Protocol Buffers代码。
比如本例中

```bash
enum AcademicDiplomas {
    POSTGRADUATE = 0;
    BACHELOR = 1;
    JUNIOR_COLLEGE = 2;
    VOCATIONAL = 3;
    SENIOR = 4;
    JUNIOR = 5; 
}
```

生成的代码如下
```java
package com.proto.message.gen.proto;

/**
 * Protobuf enum {@code AcademicDiplomas}
 */
public enum AcademicDiplomas
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>POSTGRADUATE = 0;</code>
   */
  POSTGRADUATE(0),
  /**
   * <code>BACHELOR = 1;</code>
   */
  BACHELOR(1),
  /**
   * <code>JUNIOR_COLLEGE = 2;</code>
   */
  JUNIOR_COLLEGE(2),
  /**
   * <code>VOCATIONAL = 3;</code>
   */
  VOCATIONAL(3),
  /**
   * <code>SENIOR = 4;</code>
   */
  SENIOR(4),
  /**
   * <code>JUNIOR = 5;</code>
   */
  JUNIOR(5),
  UNRECOGNIZED(-1),
  ;

  static {
    com.google.protobuf.RuntimeVersion.validateProtobufGencodeVersion(
      com.google.protobuf.RuntimeVersion.RuntimeDomain.PUBLIC,
      /* major= */ 4,
      /* minor= */ 26,
      /* patch= */ 1,
      /* suffix= */ "",
      AcademicDiplomas.class.getName());
  }
  /**
   * <code>POSTGRADUATE = 0;</code>
   */
  public static final int POSTGRADUATE_VALUE = 0;
  /**
   * <code>BACHELOR = 1;</code>
   */
  public static final int BACHELOR_VALUE = 1;
  /**
   * <code>JUNIOR_COLLEGE = 2;</code>
   */
  public static final int JUNIOR_COLLEGE_VALUE = 2;
  /**
   * <code>VOCATIONAL = 3;</code>
   */
  public static final int VOCATIONAL_VALUE = 3;
  /**
   * <code>SENIOR = 4;</code>
   */
  public static final int SENIOR_VALUE = 4;
  /**
   * <code>JUNIOR = 5;</code>
   */
  public static final int JUNIOR_VALUE = 5;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static AcademicDiplomas valueOf(int value) {
    return forNumber(value);
  }

  /**
   * @param value The numeric wire value of the corresponding enum entry.
   * @return The enum associated with the given numeric wire value.
   */
  public static AcademicDiplomas forNumber(int value) {
    switch (value) {
      case 0: return POSTGRADUATE;
      case 1: return BACHELOR;
      case 2: return JUNIOR_COLLEGE;
      case 3: return VOCATIONAL;
      case 4: return SENIOR;
      case 5: return JUNIOR;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<AcademicDiplomas>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      AcademicDiplomas> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<AcademicDiplomas>() {
          public AcademicDiplomas findValueByNumber(int number) {
            return AcademicDiplomas.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalStateException(
          "Can't get the descriptor of an unrecognized enum value.");
    }
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return com.proto.message.gen.proto.PersonOuterClass.getDescriptor().getEnumTypes().get(0);
  }

  private static final AcademicDiplomas[] VALUES = values();

  public static AcademicDiplomas valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private AcademicDiplomas(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:AcademicDiplomas)
}
```
生成的AcademicDiplomas要继承于com.google.protobuf.ProtocolMessageEnum，而后者不存在于当前代码中。这就需要引入包含这些底层代码的其他依赖，比如protobuf-java。这些依赖有版本号，也就意味着protoc也要与之适配。这样我们就可以将版本号提出来，作为属性供后面各个依赖以及protoc来使用。

```xml
<properties>
     <protobuf-java.version>4.26.1</protobuf-java.version>
</properties>
```

```xml
<configuration>
    <protocArtifact>com.google.protobuf:protoc:${protobuf-java.version}:exe:${os.detected.classifier}</protocArtifact>
</configuration>
```
## 引入依赖
这些依赖就是提供Protocol Buffers Java的底层代码，比如com.google.protobuf.ProtocolMessageEnum、com.google.protobuf.GeneratedMessage和com.google.protobuf.MessageOrBuilder之类。

```xml
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
```
## 整个文件
整体来说，dependencies部分提供底层代码依赖；build部分用于自动生成proto各个操作系统上的Java文件包。
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
# 测试
## person.proto
### proto

```bash
syntax = "proto3";

option java_multiple_files = true;
option java_package = "com.proto.message.gen.proto";

message Address {
    string city = 1;
    string street = 2;
    string door = 3;
}

message Person {
    string name = 1;
    int32 age = 2;
    float weight = 3;
    AcademicDiplomas academicdiplomas = 4;
    repeated Address address = 5;
}

enum AcademicDiplomas {
    POSTGRADUATE = 0;
    BACHELOR = 1;
    JUNIOR_COLLEGE = 2;
    VOCATIONAL = 3;
    SENIOR = 4;
    JUNIOR = 5; 
}
```
### java

```java
// AddressBuilder.java
package org.example;
import com.proto.message.gen.proto.Address;

public class AddressBuilder {
    public Address buildAddress(String city, String street, String door) {
        Address.Builder addressBuilder = Address.newBuilder();
        addressBuilder.setCity(city);
        addressBuilder.setStreet(street);
        addressBuilder.setDoor(door);
        return addressBuilder.build();
    }
}
```

```java
// PersonBuilder.java
package org.example;
import com.proto.message.gen.proto.Person;
import com.proto.message.gen.proto.Address;

public class PersonBuilder {
    public Person buildPerson(String name, int age) {
        AddressBuilder addressBuilder = new AddressBuilder();
        Person.Builder builder = Person.newBuilder();
        builder.setName(name);
        builder.setAge(age);
        builder.setWeight(70.5F);

        for (int i = 1; i <= 5; i++) {
            Address address = addressBuilder.buildAddress("city" + i, "street" + i, "door" + i);
            builder.addAddress(address);
        }
        return builder.build();
    }
}

```

## request.proto
### proto

```bash
syntax = "proto3";

option java_package = "com.proto.message.gen.proto";

message Request {
    string name = 1;
    int32 age = 2;
}
```
### java

```java
// RequestBuilder.java
package org.example;
import com.proto.message.gen.proto.RequestOuterClass;

public class RequestBuilder {
    public RequestOuterClass.Request buildRequest() {
        RequestOuterClass.Request.Builder builder = RequestOuterClass.Request.newBuilder();
        builder.setName("Bob");
        builder.setAge(24);
        return builder.build();
    }
}

```

## reponse.proto
### proto

```bash
syntax = "proto3";

option java_package = "com.proto.message.gen.proto";

import "person.proto";

message Response {
    repeated Person peoples = 1;
}
```

### java

```java
// ResponseBuilder.java
package org.example;

import com.proto.message.gen.proto.ResponseOuterClass;
import com.proto.message.gen.proto.Person;
public class ResponseBuilder {
    public ResponseOuterClass.Response buildResponse() {
        ResponseOuterClass.Response.Builder builder = ResponseOuterClass.Response.newBuilder();
        for (int i = 1; i <= 5; i++) {
            Person person = new PersonBuilder().buildPerson("name" + i, i);
            builder.addPeoples(person);
        }
        return builder.build();
    }
}

```

# 参考资料
- [https://www.xolstice.org/protobuf-maven-plugin/](https://www.xolstice.org/protobuf-maven-plugin/)
- [https://www.xolstice.org/protobuf-maven-plugin/compile-mojo.html](https://www.xolstice.org/protobuf-maven-plugin/compile-mojo.html)
- [https://stackoverflow.com/questions/67577800/how-to-use-google-protobuf-compiler-with-maven-compiler-plugin](https://stackoverflow.com/questions/67577800/how-to-use-google-protobuf-compiler-with-maven-compiler-plugin)
