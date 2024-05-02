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