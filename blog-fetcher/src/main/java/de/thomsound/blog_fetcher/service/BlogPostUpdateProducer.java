package de.thomsound.blog_fetcher.service;

import de.thomsound.blog_fetcher.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BlogPostUpdateProducer {

    private static final Logger log = LoggerFactory.getLogger(BlogPostUpdateProducer.class);

    @Value("${spring.kafka.topic-name-blog-post-updates}")
    private String topicName;

    private final KafkaTemplate<String, Post> kafkaTemplate;

    private final BlogPostFetcher blogPostFetcher;

    private Date lastUpdated = null;

    public BlogPostUpdateProducer(KafkaTemplate<String, Post> kafkaTemplate, BlogPostFetcher blogPostFetcher) {
        this.kafkaTemplate = kafkaTemplate;
        this.blogPostFetcher = blogPostFetcher;
    }

    @Scheduled(fixedDelay = 60000)
    public void fetchLatest() {
        Date modifiedAfter = lastUpdated;
        this.lastUpdated = new Date();

        this.blogPostFetcher.fetchPosts()
                .doOnNext(this::sendMessage)
                .blockLast();
    }

    public void sendMessage(Post post) {
        this.kafkaTemplate.send(topicName, post);
    }
}
