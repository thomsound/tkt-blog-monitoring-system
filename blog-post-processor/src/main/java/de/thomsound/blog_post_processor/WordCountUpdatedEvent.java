package de.thomsound.blog_post_processor;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

public class WordCountUpdatedEvent extends ApplicationEvent {

    public WordCountUpdatedEvent(Map<String, Integer> source) {
        super(source);
    }
}
