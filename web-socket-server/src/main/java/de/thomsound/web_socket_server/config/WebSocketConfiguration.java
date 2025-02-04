package de.thomsound.web_socket_server.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thomsound.web_socket_server.service.WordCountUpdatePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class WebSocketConfiguration {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfiguration.class);

    private volatile String lastPreparedMessage;

    @Bean
    Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    HandlerMapping handlerMapping(WebSocketHandler wsh) {
        return new SimpleUrlHandlerMapping() {
            {
                setUrlMap(Collections.singletonMap("/ws/word-counts", wsh));
                setOrder(10);
            }
        };
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(
            ObjectMapper objectMapper,
            WordCountUpdatePublisher eventPublisher
    ) {

        Flux<String> publish = Flux
                .create(eventPublisher)
                .sample(Duration.ofMillis(500))
                .map(evt -> {
                    try {
                        return objectMapper.writeValueAsString(evt.getSource());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doOnNext(message -> {
                    lastPreparedMessage = message;
                })
                .share();

        return session -> {

            if (lastPreparedMessage != null) {
                session.send(Mono.just(session.textMessage(lastPreparedMessage)))
                        .subscribe(null, throwable -> log.error("Error sending message", throwable));
            }

            Flux<WebSocketMessage> messageFlux = publish.map(session::textMessage);

            return session.send(messageFlux);
        };
    }
}
