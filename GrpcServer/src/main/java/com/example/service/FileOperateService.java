package com.example.service;

import com.example.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

@GrpcService
public class FileOperateService extends FileOperateServiceGrpc.FileOperateServiceImplBase{

    @Value("${my.file.path}")
    String myFilePath;

    @Override
    public void uploadFile(UploadFileRequest request, StreamObserver<UploadFileResponse> responseObserver){
        //System.out.println(String.format("收到文件%s长度%s", request.getName(), bytes.length));
        String path=myFilePath+ request.getFileName();
        File file = new File(path);
    /*    if (file.exists()) {
            file.delete();
        }*/
        UploadFileResponse response;
        try {
            byte[] bytes = request.getContent().toByteArray();
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            response = UploadFileResponse.newBuilder().setFilePath(path).build();
        } catch (IOException e) {
            response = UploadFileResponse.newBuilder().setFilePath(path).build();
            e.printStackTrace();
        }
        // 返回数据，完成此次请求
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        // return response;
    }

    @Override
    public StreamObserver<UploadFileRequest> uploadFileStream(StreamObserver<UploadFileResponse> responseObserver) {
        String path=myFilePath+ "uploadFileStream.zip";
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        OutputStream os = null;
        try {
             os = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回匿名类，给上层框架使用
        OutputStream finalOs = os;
        return new StreamObserver<UploadFileRequest>() {
            @Override
            public void onNext(UploadFileRequest value) {
                byte[] bytes = value.getContent().toByteArray();
                try {
                    finalOs.write(bytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                //log.error("添加购物车异常", t);
            }

            @Override
            public void onCompleted() {
                try {
                    finalOs.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                responseObserver.onNext(UploadFileResponse.newBuilder()
                        .setFilePath("test")
                        .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void downloadFile(DownloadFileRequest downloadFileRequest,  StreamObserver<DownloadFileResponse> responseObserver){
 /*       File file = new File(downloadFileRequest.getFilePath());
        if (file.exists()) {
            FileStream fs = FileStream(info.fileName, FileMode.Opne, FileAccess.Read)
            BinaryReader br = new BinaryReader(fs);
            byte[] byteArray = br.ReadBytes((int)fs.Length);
            int btSize = byteArray.Length;
            int buffSize = 1024*1024; //1M
            int lastBiteSize = biSize%buffSize;
            int currentTimes = 0;
            int loopTimes = btSize/buffSize;
            BytesContent contentStruct;
            while(currentTimes<=loopTimes){
                ByteString sbytes;
                if(currentTimes == loopTimes){
                    sbytes = ByteString.CopyFrom(byteArray, currentTimes*buffSize, lastBiteSize);
                }
                else{
                    sbytes = ByteString.CopyFrom(byteArray, currentTimes*buffSize, lastBiteSize);
                }
                contentStruct= new BytesContent{
                    fileName = info.fileName;
                    Block = currentTimes;
                    content = sbytes
                };
                await responseStream.WriteAsync(sbytes);
                currentTimes++;
            }
        }*/
    }
}
