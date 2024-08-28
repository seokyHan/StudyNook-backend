package com.studyNook.global.common.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;

@Configuration
@Slf4j
public class WebClientConfiguration {

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .doOnConnected(
                        connection -> connection.addHandlerLast(new ReadTimeoutHandler(5))
                                .addHandlerLast(new WriteTimeoutHandler(60))
                );

        // Memory 조정 2M (default : 256KB)
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2*1024*1024))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filters(filters -> {
                    filters.add(replaceUrlFilter());
                    filters.add(requestFilter());
                    filters.add(responseFilter());
                })
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    private ExchangeFilterFunction requestFilter(){
        return ExchangeFilterFunction.ofRequestProcessor(
                clientRequest -> {
                    StringBuilder requestLog = new StringBuilder();
                    requestLog.append("\n ===========================request begin===========================");
                    requestLog.append(String.format("%n URI         : %s", clientRequest.url()));
                    requestLog.append(String.format("%n Method      : %s", clientRequest.method()));
                    requestLog.append(String.format("%n Headers     : %s", clientRequest.headers()));
                    requestLog.append(String.format("%n Request body: %s", clientRequest.body()));
                    requestLog.append("\n ============================request end============================");
                    log.info(requestLog.toString());

                    return Mono.just(clientRequest);
                }
        );
    }

    private ExchangeFilterFunction responseFilter(){
        return ExchangeFilterFunction.ofResponseProcessor(
                clientResponse -> {
                    StringBuilder responseLog = new StringBuilder();
                    responseLog.append("\n ===========================response begin===========================");
                    responseLog.append(String.format("%n Status code  : %s", clientResponse.statusCode()));
                    responseLog.append(String.format("%n Headers      : %s", clientResponse.headers().asHttpHeaders()));
                    responseLog.append(String.format("%n Response body: %s", clientResponse.bodyToMono(String.class)));
                    responseLog.append("\n ============================response end============================");
                    log.info(responseLog.toString());

                    return Mono.just(clientResponse);
                }
        );
    }

    private ExchangeFilterFunction replaceUrlFilter(){
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            URI originalUri = clientRequest.url();
            String escapedQuery = originalUri.getRawQuery().replace("+", "%2B");
            URI modifiedUri = UriComponentsBuilder.fromUri(originalUri)
                    .replaceQuery(escapedQuery)
                    .build(true).toUri();

            return Mono.just(ClientRequest.from(clientRequest).url(modifiedUri).build());
        });
    }

}
