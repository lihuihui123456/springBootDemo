package com.example.service;

import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import com.example.grpc.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class SpringBootMyGrpcClient {
    public static void main(String[] args) throws InterruptedException {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8082)
                .usePlaintext()
                .build();

        HelloServiceGrpc.HelloServiceBlockingStub stub =
                HelloServiceGrpc.newBlockingStub(channel);

        HelloResponse helloResponse = stub.hello(
                HelloRequest.newBuilder()
                        .setName("forezp")
                        .setAge(17)
                        .addHobbies("football").putTags( "how?","wonderful" )
                        .build());

        System.out.println(helloResponse);


        channel.shutdown();
    }
}