package de.thomsound.blog_post_processor.service;

import de.thomsound.blog_post_processor.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BlogPostUpdateConsumer {
    private final Logger logger = LoggerFactory.getLogger(BlogPostUpdateConsumer.class);

    @KafkaListener(topics = "${spring.kafka.topic-name-blog-post-updates}", groupId = "group_id")

    public void consume(Post post) throws IOException {
        logger.info(String.format("#### -> Consumed message -> [%s]: %s\n%s", post.modified_gmt(), post.id(), post.title()));
    }
}
