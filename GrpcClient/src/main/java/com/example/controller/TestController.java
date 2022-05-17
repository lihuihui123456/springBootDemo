package com.example.controller;

import com.example.service.FileGrpcService;
import com.example.service.FileHttpService;
import com.example.service.MyGrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class TestController {

    @Autowired
    MyGrpcService myGrpcService;

    @Autowired
    FileGrpcService fileGrpcService;

    @Autowired
    FileHttpService fileHttpService;

    @GetMapping("/grpc")
    public String setGrpc(String name) {
        String result = myGrpcService.getUser(name);
        if ("".equals(result)) {
            return "Result is blank";
        }
        return result;
    }

    @GetMapping("/upload")
    public String upload(String name,String path) throws IOException {
        long startTime = System.currentTimeMillis();
        fileGrpcService.uploadAll(name,path);
        long endTime = System.currentTimeMillis();
        System.out.println("客户端uploadAll:耗时："+(endTime-startTime));
        return "success";
    }

    @GetMapping("/uploadBatch")
    public String uploadBatch(String name,String path) throws IOException {
        long startTime = System.currentTimeMillis();
        fileGrpcService.uploadBatch(name,path);
        long endTime = System.currentTimeMillis();
        System.out.println("客户端uploadBatch:耗时："+(endTime-startTime));
        return "success";
    }

    @GetMapping("/uploadStream")
    public String uploadStream(String name,String path) throws IOException {
        long startTime = System.currentTimeMillis();
        fileGrpcService.uploadStream(name,path);
        long endTime = System.currentTimeMillis();
        System.out.println("客户端uploadStream:耗时："+(endTime-startTime));
        return "success";
    }

    @GetMapping("/uploadHttpClient")
    public String uploadHttpClient(String name,String path) throws IOException {
        long startTime = System.currentTimeMillis();
        fileHttpService.uploadFile(name,path);
        long endTime = System.currentTimeMillis();
        System.out.println("客户端uploadHttpClient:耗时："+(endTime-startTime));
        return "success";
    }
}
