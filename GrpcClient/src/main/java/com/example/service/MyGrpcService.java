package com.example.service;

import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import com.example.grpc.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MyGrpcService {
    @Autowired
    private ManagedChannel channel;

    private HelloServiceGrpc.HelloServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        stub = HelloServiceGrpc.newBlockingStub(channel);
    }

    public String getUser(String name) {
        HelloResponse helloResponse = stub.hello(
                HelloRequest.newBuilder()
                        .setName(name)
                        .setAge(17)
                        .addHobbies("football").putTags( "how?","wonderful" )
                        .build());
        return helloResponse.getGreeting();
    }
}