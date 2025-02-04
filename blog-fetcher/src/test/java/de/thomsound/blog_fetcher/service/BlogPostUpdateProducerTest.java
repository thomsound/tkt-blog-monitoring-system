package de.thomsound.blog_fetcher.service;

import de.thomsound.blog_fetcher.domain.Content;
import de.thomsound.blog_fetcher.domain.Post;
import de.thomsound.blog_fetcher.domain.Title;
import de.thomsound.blog_fetcher.model.DeleteMessage;
import de.thomsound.blog_fetcher.model.Message;
import de.thomsound.blog_fetcher.model.UpdateMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogPostUpdateProducerTest {

    @Mock
    private BlogPostFetcher blogPostFetcher;

    @Mock
    private KafkaTemplate<String, Message> kafkaTemplate;

    @InjectMocks
    private BlogPostUpdateProducer blogPostUpdateProducer;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @BeforeEach
    void setup() {
        blogPostUpdateProducer = new BlogPostUpdateProducer("test-topic", kafkaTemplate, blogPostFetcher);
    }

    @Test
    void testFetchLatest_withoutUpdates() {

        when(blogPostFetcher.fetchPosts()).thenReturn(Flux.empty());

        blogPostUpdateProducer.fetchLatest();

        verify(kafkaTemplate, times(0)).send(eq("test-topic"), messageCaptor.capture());

        List<Message> sentMessages = messageCaptor.getAllValues();
        assertThat(sentMessages).isEmpty();
    }

    @Test
    void testFetchLatest_withUpdates() {
        Post post1 = new Post(1, new Title("Title1"), new Content("<div class=\"elementor-widget-heading\">Content1</div>"));
        Post post2 = new Post(2, new Title("Title2"), new Content("<div class=\"elementor-widget-text-editor\">Content2</div>"));

        when(blogPostFetcher.fetchPosts()).thenReturn(Flux.just(post1, post2));

        blogPostUpdateProducer.fetchLatest();

        verify(kafkaTemplate, times(2)).send(eq("test-topic"), messageCaptor.capture());

        List<Message> sentMessages = messageCaptor.getAllValues();
        assertThat(sentMessages.size()).isEqualTo(2);
        assertThat(sentMessages.get(0)).isEqualTo(new UpdateMessage(1, "Title1 Content1"));
        assertThat(sentMessages.get(1)).isEqualTo(new UpdateMessage(2, "Title2 Content2"));
    }

    @Test
    void testFetchDeleted_withoutDeletions() {
        when(blogPostFetcher.fetchStalePostIds()).thenReturn(Flux.empty());

        blogPostUpdateProducer.fetchDeleted();

        verify(kafkaTemplate, times(0)).send(eq("test-topic"), messageCaptor.capture());

        List<Message> sentMessages = messageCaptor.getAllValues();
        assertThat(sentMessages).isEmpty();
    }

    @Test
    void testFetchDeleted_withDeletions() {
        when(blogPostFetcher.fetchStalePostIds()).thenReturn(Flux.just(3, 4));

        blogPostUpdateProducer.fetchDeleted();

        verify(kafkaTemplate, times(2)).send(eq("test-topic"), messageCaptor.capture());

        List<Message> sentMessages = messageCaptor.getAllValues();
        assertThat(sentMessages.size()).isEqualTo(2);
        assertThat(sentMessages.get(0)).isEqualTo(new DeleteMessage(3));
        assertThat(sentMessages.get(1)).isEqualTo(new DeleteMessage(4));
    }
}
