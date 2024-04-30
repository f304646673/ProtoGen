package org.example;
import com.google.protobuf.InvalidProtocolBufferException;
import com.proto.message.gen.proto.RequestOuterClass;
import com.proto.message.gen.proto.ResponseOuterClass;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InvalidProtocolBufferException {
        test_request();
        test_response();
    }

    private static void test_request() throws InvalidProtocolBufferException {
        RequestBuilder requestBuilder = new RequestBuilder();
        RequestOuterClass.Request requestFrom = requestBuilder.buildRequest();
        byte[] bytes = requestFrom.toByteArray();

        RequestOuterClass.Request requestTo =  RequestOuterClass.Request.parseFrom(bytes);
        if (requestFrom.equals(requestTo)) {
            System.out.println("The objects are equal");
        } else {
            System.out.println("The objects are not equal");
        }
    }

    private static void test_response() throws InvalidProtocolBufferException {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        ResponseOuterClass.Response responseFrom = responseBuilder.buildResponse();
        byte[] bytes = responseFrom.toByteArray();

        ResponseOuterClass.Response responseTo = ResponseOuterClass.Response.parseFrom(bytes);
        if (responseFrom.equals(responseTo)) {
            System.out.println("The objects are equal");
        } else {
            System.out.println("The objects are not equal");
        }

    }   
}