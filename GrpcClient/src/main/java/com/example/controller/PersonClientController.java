package com.example.controller;

import com.example.common.UploadConstants;
import com.example.common.UploadStatus;
import com.example.entity.Person;
import com.google.common.io.Resources;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.UUID;
@RestController
@RequestMapping("/people")
public class PersonClientController {
    @Autowired
    RSocketRequester rSocketRequester;

    @Value("classpath:./Users/lizhenghui/tmp/httpdocx.docx")
    private Resource resource1;


    public PersonClientController(RSocketRequester rSocketRequester) {
            this.rSocketRequester = rSocketRequester;
        }
    @Autowired
    private Mono<RSocketRequester> requesterMonos; //来进行服务调用

    @GetMapping("/{id}")
        public Mono<Person> getOne(@PathVariable String id){
            return this.rSocketRequester
                    .route("people.findById") //1
                    .data(new Person()) //2
                    .retrieveMono(Person.class); //3
        }
        @GetMapping
        public Flux<Person> getAll(){
            return this.rSocketRequester
                    .route("people.findAll")
                    .data(new Person())
                    .retrieveFlux(Person.class);//4
        }
    @GetMapping("/fluxUpload")
    public void testUpload(){
        Resource resource= new FileSystemResource("/Users/lizhenghui/Downloads/ideaIC-2022.1.dmg");
        String fileName = "pshdhx"+ UUID.randomUUID();
        String fileExt = resource.getFilename().substring(resource.getFilename().lastIndexOf(".")+1);
        Flux<DataBuffer> resourceFlux = DataBufferUtils.read(resource,new DefaultDataBufferFactory(),1024*1024)
                .doOnNext(s-> System.out.println("文件上传:"+s));
        Flux<UploadStatus> uploadStatusFlux = this.requesterMonos
                .map(r->r.route("message.upload")
                        .metadata(metadataSpec -> {
                            System.out.println("【上传测试:】文件名称"+fileName+"."+fileExt);
                            metadataSpec.metadata(fileName, MimeType.valueOf(UploadConstants.MINE_FILE_NAME));
                            metadataSpec.metadata(fileExt, MimeType.valueOf(UploadConstants.MINE_FILE_EXTENSION));
                        }).data(resourceFlux)).flatMapMany(r->r.retrieveFlux(UploadStatus.class))

                .doOnNext(o-> System.out.println("上传进度:"+o));
        uploadStatusFlux.blockLast();
    }

    }

