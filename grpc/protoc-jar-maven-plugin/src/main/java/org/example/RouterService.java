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
