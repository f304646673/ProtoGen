该项目包含2个项目、4个工程。
两个项目是：
1. 在Java开发环境下翻译proto中message类型。目录在/message下。
2. 在Java开发环境下翻译proto中service类型,并生成grpc的service代码。目录在/grpc下。
针对上述2个项目，分别使用protobuf-maven-plugin和protoc-jar-maven-plugin定制两种方案。个人认为protoc-jar-maven-plugin方案更简单。
上述工程对应的文档是：
- mesaage+protobuf-maven-plugin:[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法》](https://github.com/f304646673/proto-gen/blob/master/%E5%9C%A8%E4%B8%8D%E5%90%8C%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F%E4%B8%8A%E8%87%AA%E5%8A%A8%E7%94%9F%E6%88%90Protocol%20Buffers%E7%9A%84Java%E8%AF%AD%E8%A8%80%E5%8C%85%E7%9A%84%E6%96%B9%E6%B3%95.md)
- message+protoc-jar-maven-plugin:[《在不同操作系统上自动生成Protocol Buffers的Java语言包的方法2》](https://github.com/f304646673/proto-gen/blob/master/%E5%9C%A8%E4%B8%8D%E5%90%8C%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F%E4%B8%8A%E8%87%AA%E5%8A%A8%E7%94%9F%E6%88%90Protocol%20Buffers%E7%9A%84Java%E8%AF%AD%E8%A8%80%E5%8C%85%E7%9A%84%E6%96%B9%E6%B3%952.md)
- grpc+protobuf-maven-plugin:[《使用protobuf-maven-plugin生成grpc项目》](https://github.com/f304646673/proto-gen/blob/master/%E4%BD%BF%E7%94%A8protobuf-maven-plugin%E7%94%9F%E6%88%90grpc%E9%A1%B9%E7%9B%AE.md)
- grpc+protoc-jar-maven-plugin:[《使用protoc-jar-maven-plugin生成grpc项目》](https://github.com/f304646673/proto-gen/blob/master/%E4%BD%BF%E7%94%A8protoc-jar-maven-plugin%E7%94%9F%E6%88%90grpc%E9%A1%B9%E7%9B%AE.md)
