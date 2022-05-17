package com.example.controller;

import com.example.common.UploadConstants;
import com.example.common.UploadStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

@Controller
//不使用rest
@Slf4j
@Data
public class MessageFluxController {
    //@Value("${rsocketoutput.file.path.upload}")

    @MessageMapping("message.upload")
    public Flux<UploadStatus> upload(@Headers Map<String,Object> metadata, @Payload Flux<DataBuffer> content) throws  Exception{
        long startTime = System.currentTimeMillis();
        Path outputPath = Paths.get("/Users/lizhenghui/tmp/");
        log.info("【上传后路径】outputPaht={}",outputPath);
        Object fileName = metadata.get(UploadConstants.FILE_NAME);
        Object fileExt = metadata.get(UploadConstants.MINE_FILE_EXTENSION);
        String path = Paths.get(fileName+"."+fileExt).toString();
        log.info("【文件上传】fileName={}、fileExt = {},path={}",fileName,fileExt,path);
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                outputPath.resolve(path),
                StandardOpenOption.CREATE, //文件创建
                StandardOpenOption.WRITE  //文件写入
        );//异步文件通道
        return Flux.concat(DataBufferUtils.write(content,channel)
                        .map(s-> {
                            return UploadStatus.CHUNK_COMPLETED;
                        }),Mono.just(UploadStatus.COMPLETED))
                .doOnComplete(()->{
                    try {
                        long endTime = System.currentTimeMillis();
                        System.out.println("rsocket.uploadStream:耗时："+(endTime-startTime));
                        channel.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                })
                .onErrorReturn(UploadStatus.FAILED);

    }
}