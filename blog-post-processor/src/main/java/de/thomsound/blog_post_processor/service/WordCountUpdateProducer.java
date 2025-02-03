package de.thomsound.blog_post_processor.service;

import de.thomsound.blog_post_processor.WordCountUpdateEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WordCountUpdateProducer implements ApplicationListener<WordCountUpdateEvent> {

    @Value("${spring.kafka.topic-name-word-count-updates}")
    private String topicName;

    private final KafkaTemplate<String, Map<String, Integer>> kafkaTemplate;

    public WordCountUpdateProducer(KafkaTemplate<String, Map<String, Integer>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void onApplicationEvent(WordCountUpdateEvent event) {
        this.sendMessage((Map<String, Integer>) event.getSource());
    }

    public void sendMessage(Map<String, Integer> wordCountMap) {
        this.kafkaTemplate.send(this.topicName, wordCountMap);
    }
}
