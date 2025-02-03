package de.thomsound.web_socket_server;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

public class WordCountUpdateEvent extends ApplicationEvent {

    public WordCountUpdateEvent(Map<String, Integer> source) {
        super(source);
    }
}
