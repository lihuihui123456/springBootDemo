package com.example.service;

import com.example.grpc.*;
import com.example.util.FileUtils;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class FileGrpcService {
    @Autowired
    private ManagedChannel channel;

    private FileOperateServiceGrpc.FileOperateServiceBlockingStub stub;

    private FileOperateServiceGrpc.FileOperateServiceStub stub1;


    @PostConstruct
    public void init() {
        stub = FileOperateServiceGrpc.newBlockingStub(channel);
        stub1 = FileOperateServiceGrpc.newStub(channel);

    }

    /**
     * 上传文件
     * @param name 保存到服务端的文件名
     * @param path 要上传的文件路径
     * @throws IOException
     */
    public void uploadAll(String name, String path) throws IOException {
        UploadFileRequest request = UploadFileRequest.newBuilder()
                .setFileName(name)
                // 文件 -> 字节码数据 -> ByteString
                .setContent(ByteString.copyFrom(FileUtils.getContent(path)))
                .build();
        UploadFileResponse response=null;
        try {
            response = stub.uploadFile(request);
            System.out.println(response.getFilePath());
        } catch (StatusRuntimeException ex) {

        }
    }
    /**
     * 上传文件
     * @param name 保存到服务端的文件名
     * @param path 要上传的文件路径
     * @throws IOException
     */
    public void uploadBatch(String name, String path) throws IOException {
        UploadFileResponse response=null;
        try {
            File serverFile = new File(path);
            FileInputStream fileInputStream = new FileInputStream(serverFile);
            int length=0;
            byte[] buffer = new byte[1000*1000*80];//grpc限制数据大小最大4m
            while ((length = fileInputStream.read(buffer)) != -1) {
                UploadFileRequest requestParam = UploadFileRequest.newBuilder()
                        .setFileName(name)
                        // 文件 -> 字节码数据 -> ByteString
                        .setContent(ByteString.copyFrom(buffer))
                        .build();
                response = stub.uploadFile(requestParam);
            }
        } catch (StatusRuntimeException ex) {

        }
    }
    /**
     * 流式上传文件
     * @param name 保存到服务端的文件名
     * @param path 要上传的文件路径
     * @throws IOException
     */
    public String uploadStream(String name, String path) throws IOException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        long startTime = System.currentTimeMillis();

        // responseObserver的onNext和onCompleted会在另一个线程中被执行，
        // ExtendResponseObserver继承自StreamObserver
        ExtendResponseObserver<UploadFileResponse> responseObserver = new ExtendResponseObserver<UploadFileResponse>() {

            String extraStr;

            @Override
            public String getExtra() {
                return extraStr;
            }

            private String filePath;

            @Override
            public void onNext(UploadFileResponse value) {
                filePath = value.getFilePath();
            }

            @Override
            public void onError(Throwable t) {
                extraStr = "gRPC error, " + t.getMessage();
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                long endTime = System.currentTimeMillis();
                System.out.println("总uploadStream:耗时："+(endTime-startTime));
                extraStr = filePath;
                countDownLatch.countDown();
            }
        };

        // 远程调用，此时数据还没有给到服务端

        StreamObserver<UploadFileRequest> requestObserver = stub1.uploadFileStream(responseObserver);
        File serverFile = new File(path);
        FileInputStream fileInputStream = new FileInputStream(serverFile);
        int length=0;
        byte[] buffer = new byte[1000*1000*32];//grpc限制数据大小最大4m
        while ((length = fileInputStream.read(buffer)) != -1) {
            UploadFileRequest requestParam = UploadFileRequest.newBuilder()
                .setFileName(name)
                // 文件 -> 字节码数据 -> ByteString
                .setContent(ByteString.copyFrom(buffer))
                .build();
            requestObserver.onNext(requestParam);
        }
        // 客户端告诉服务端：数据已经发完了
        requestObserver.onCompleted();

        try {
            // 开始等待，如果服务端处理完成，那么responseObserver的onCompleted方法会在另一个线程被执行，
            // 那里会执行countDownLatch的countDown方法，一但countDown被执行，下面的await就执行完毕了，
            // await的超时时间设置为2秒
            countDownLatch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
        // 服务端返回的内容被放置在requestObserver中，从getExtra方法可以取得
        return responseObserver.getExtra();
    }
}