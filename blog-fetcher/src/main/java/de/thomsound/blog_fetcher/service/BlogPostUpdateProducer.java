package de.thomsound.blog_fetcher.service;

import de.thomsound.blog_fetcher.model.DeleteMessage;
import de.thomsound.blog_fetcher.model.Message;
import de.thomsound.blog_fetcher.model.UpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BlogPostUpdateProducer {

    private static final Logger log = LoggerFactory.getLogger(BlogPostUpdateProducer.class);

    @Value("${spring.kafka.topic-name-blog-post-updates}")
    private String topicName;

    private final KafkaTemplate<String, Message> kafkaTemplate;

    private final BlogPostFetcher blogPostFetcher;

    public BlogPostUpdateProducer(KafkaTemplate<String, Message> kafkaTemplate, BlogPostFetcher blogPostFetcher) {
        this.kafkaTemplate = kafkaTemplate;
        this.blogPostFetcher = blogPostFetcher;
    }

    @Scheduled(fixedRateString = "${app.update-interval-millis}")
    public void fetchLatest() {
        this.blogPostFetcher.fetchPosts()
                .map(post -> new UpdateMessage(post.id(), post.titleText() + " " + post.contentText()))
                .doOnNext(this::sendMessage)
                .blockLast();
    }

    @Scheduled(fixedRateString = "${app.delete-interval-millis}")
    public void fetchDeleted() {
        this.blogPostFetcher.fetchStalePostIds()
                .map(DeleteMessage::new)
                .doOnNext(this::sendMessage)
                .blockLast();
    }

    public void sendMessage(Message msg) {
        this.kafkaTemplate.send(topicName, msg);
    }

}
