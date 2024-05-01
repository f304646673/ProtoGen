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
