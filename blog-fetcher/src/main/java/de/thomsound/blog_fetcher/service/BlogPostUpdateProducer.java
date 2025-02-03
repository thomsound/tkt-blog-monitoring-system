package de.thomsound.blog_fetcher.service;

import de.thomsound.blog_fetcher.model.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BlogPostUpdateProducer {

    @Value("${spring.kafka.topic-name-blog-post-updates}")
    private String topicName;

    private final KafkaTemplate<String, Post> kafkaTemplate;

    private final BlogPostFetcher blogPostFetcher;

    public BlogPostUpdateProducer(KafkaTemplate<String, Post> kafkaTemplate, BlogPostFetcher blogPostFetcher) {
        this.kafkaTemplate = kafkaTemplate;
        this.blogPostFetcher = blogPostFetcher;
    }

    @Scheduled(fixedDelayString = "${app.polling-interval-millis}")
    public void fetchLatest() {
        this.blogPostFetcher.fetchPosts()
                .doOnNext(this::sendMessage)
                .blockLast();
    }

    public void sendMessage(Post post) {
        this.kafkaTemplate.send(topicName, post);
    }
}
