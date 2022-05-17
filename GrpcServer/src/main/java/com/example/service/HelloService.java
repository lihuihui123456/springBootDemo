package com.example.service;

import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import com.example.grpc.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class HelloService extends HelloServiceGrpc.HelloServiceImplBase{
    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        System.out.println( request );

        String greeting = "Hi " + request.getName() + " you are " + request.getAge() + " years old" +
                " your hoby is " + (request.getHobbiesList()) + " your tags " + request.getTagsMap();

        HelloResponse response = HelloResponse.newBuilder().setGreeting( greeting ).build();
        responseObserver.onNext( response );
        responseObserver.onCompleted();
    }
}