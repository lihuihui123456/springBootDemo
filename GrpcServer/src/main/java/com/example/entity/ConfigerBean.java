package com.example.entity;

import com.example.common.UploadConstants;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeType;

@Configuration
public class ConfigerBean {

    @Bean
    public RSocketStrategies getRSocketStrategies(){
        return RSocketStrategies.builder()
                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
                .metadataExtractorRegistry(metadataExtractorRegistry -> {
                    metadataExtractorRegistry.metadataToExtract(MimeType.valueOf(UploadConstants.MINE_FILE_NAME),String.class,UploadConstants.FILE_NAME);
                    metadataExtractorRegistry.metadataToExtract(MimeType.valueOf(UploadConstants.MINE_FILE_EXTENSION),String.class,UploadConstants.MINE_FILE_EXTENSION);
                })
                .build();
    }
}
