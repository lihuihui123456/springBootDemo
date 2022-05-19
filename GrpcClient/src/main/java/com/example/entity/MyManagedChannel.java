package com.example.entity;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.channelz.v1.GetServerSocketsRequest;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "grpc.client")
@Data
public class MyManagedChannel {

    private String address;

    private int prot;

    @Bean
    public ManagedChannel myBean() {
        return ManagedChannelBuilder.forAddress(address, prot)
                .usePlaintext()
                .build();
    }

    @Bean
    RSocketRequester rSocketRequester() {
        RSocketStrategies strategies = RSocketStrategies.builder()
//                .encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
//                .decoders(decoders -> decoders.add(new Jackson2CborDecoder()))
                .encoders(encoders -> encoders.add(new Jackson2JsonEncoder()))
                .decoders(decoders -> decoders.add(new Jackson2JsonDecoder()))
                .build();

        RSocketRequester requester = RSocketRequester.builder()
                .rsocketStrategies(strategies)
                .tcp(address, 9899);

        return requester;
    }

    /**
     * 配置RSocket连接策略
     */
    @Bean
    public Mono<RSocketRequester> getRSocketRequesters(RSocketRequester.Builder builder) {
        return Mono.just(
                builder.rsocketConnector(rSocketConnector -> rSocketConnector.reconnect(
                                Retry.fixedDelay(2, Duration.ofSeconds(2))))
                        .dataMimeType(MediaType.APPLICATION_CBOR)
                        .transport(TcpClientTransport.create(address, 9899))
        );
    }

}
