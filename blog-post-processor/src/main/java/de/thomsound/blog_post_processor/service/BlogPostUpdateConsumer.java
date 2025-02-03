package de.thomsound.blog_post_processor.service;

import de.thomsound.blog_post_processor.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BlogPostUpdateConsumer {
    private final BlogPostProcessor processor;

    public BlogPostUpdateConsumer(BlogPostProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(topics = "${spring.kafka.topic-name-blog-post-updates}", groupId = "group_id")
    public void consume(Post post) throws IOException {
        processor.process(post);
    }
}
