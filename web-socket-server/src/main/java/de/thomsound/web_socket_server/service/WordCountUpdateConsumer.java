package de.thomsound.web_socket_server.service;

import de.thomsound.web_socket_server.WordCountUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WordCountUpdateConsumer {

    private final Logger logger = LoggerFactory.getLogger(WordCountUpdateConsumer.class);

    private final ApplicationEventPublisher publisher;

    public WordCountUpdateConsumer(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @KafkaListener(topics = "${spring.kafka.topic-name-word-count-updates}", groupId = "group_id")
    public void consume(Map<String, Integer> wordCounts) {

        publisher.publishEvent(new WordCountUpdateEvent(wordCounts));

        logger.info(String.format("Consumed message -> Total word counts:\n%s", wordCounts.toString()));
    }

}
