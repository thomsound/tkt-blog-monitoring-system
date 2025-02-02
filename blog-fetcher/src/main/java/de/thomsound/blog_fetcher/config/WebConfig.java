package de.thomsound.blog_fetcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    @Value("${app.blog-base-url}")
    private String blogBaseUrl;

    @Value("${x-api-key}")
    private String xApiKey;

    @Bean
    public WebClient webClient() {

        return WebClient.builder()
                .baseUrl(blogBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-api-key", xApiKey)
                .build();
    }
}
