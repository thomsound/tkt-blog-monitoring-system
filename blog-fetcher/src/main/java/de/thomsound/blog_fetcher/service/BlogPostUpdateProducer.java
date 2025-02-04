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


    private final KafkaTemplate<String, Message> kafkaTemplate;
    private final String topicName;

    private final BlogPostFetcher blogPostFetcher;

    public BlogPostUpdateProducer(
            @Value("${spring.kafka.topic-name-blog-post-updates}") String topicName,
            KafkaTemplate<String, Message> kafkaTemplate,
            BlogPostFetcher blogPostFetcher
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
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

    private void sendMessage(Message msg) {
        this.kafkaTemplate.send(topicName, msg);
    }

}
