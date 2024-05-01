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
