package de.thomsound.web_socket_server.service;

import de.thomsound.web_socket_server.WordCountUpdateEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Service
public class WordCountUpdatePublisher implements ApplicationListener<WordCountUpdateEvent>, Consumer<FluxSink<WordCountUpdateEvent>> {

    private final Executor executor;

    private final BlockingQueue<WordCountUpdateEvent> queue = new LinkedBlockingQueue<>();

    public WordCountUpdatePublisher(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void onApplicationEvent(WordCountUpdateEvent event) {
        this.queue.offer(event);
    }

    @Override
    public void accept(FluxSink<WordCountUpdateEvent> sink) {
        this.executor.execute(() -> {
            while (true)
                try {
                    WordCountUpdateEvent event = queue.take();
                    sink.next(event);
                } catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }
}
