package de.thomsound.blog_post_processor.service;

import de.thomsound.blog_post_processor.domain.Post;
import de.thomsound.blog_post_processor.model.Message;
import de.thomsound.blog_post_processor.model.MessageType;
import de.thomsound.blog_post_processor.model.UpdateMessage;
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
    public void consume(Message msg) {
        if (msg.getType() == MessageType.UPDATE) {
            Post post = new Post(msg.getPostId(), ((UpdateMessage) msg).getContent());
            this.processor.process(post);
        } else if(msg.getType() == MessageType.DELETE) {
            this.processor.delete(msg.getPostId());
        }
    }
}
