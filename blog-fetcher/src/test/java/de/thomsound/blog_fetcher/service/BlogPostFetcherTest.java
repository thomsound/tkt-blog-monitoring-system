package de.thomsound.blog_fetcher.service;

import de.thomsound.blog_fetcher.domain.Post;
import de.thomsound.blog_fetcher.repository.PostTrackingRepository;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogPostFetcherTest {

    private static MockWebServer mockWebServer;
    private static BlogPostFetcher blogPostFetcher;

    @Mock
    private PostTrackingRepository repository;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void init() {
        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();

        blogPostFetcher = new BlogPostFetcher(webClient, repository);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testFetchPosts_withOnePage() {
        enqueueResponses(createPageOf(2, 1));

        Flux<Post> result = blogPostFetcher.fetchPosts();

        StepVerifier.create(result)
                .expectNextMatches(post -> post.id() == 1 && post.title().getText().equals("Post 1"))
                .expectNextMatches(post -> post.id() == 2 && post.title().getText().equals("Post 2"))
                .expectComplete()
                .verify();
    }

    @Test
    void testFetchPosts_withMultiplePages() {
        enqueueResponses(
                createPageOf(2, 1),
                createPageOf(1, 3)
        );

        Flux<Post> result = blogPostFetcher.fetchPosts();

        StepVerifier.create(result)
                .expectNextMatches(post -> post.id() == 1 && post.title().getText().equals("Post 1"))
                .expectNextMatches(post -> post.id() == 2 && post.title().getText().equals("Post 2"))
                .expectNextMatches(post -> post.id() == 3 && post.title().getText().equals("Post 3"))
                .expectComplete()
                .verify();
    }

    @Test
    void testFetchStalePostIds_withoutStaleIds() {
        enqueueResponses(createPageOf(1, 3));

        when(repository.prune(Set.of(3))).thenReturn(Set.of());

        Flux<Integer> result = blogPostFetcher.fetchStalePostIds();

        StepVerifier.create(result.collectList())
                .expectNext(List.of())
                .expectComplete()
                .verify();
    }

    @Test
    void testFetchStalePostIds_withStaleIds() {
        enqueueResponses(createPageOf(1, 3));

        when(repository.prune(Set.of(3))).thenReturn(Set.of(1, 2, 4, 5, 6));

        Flux<Integer> result = blogPostFetcher.fetchStalePostIds();

        StepVerifier.create(result.collectList())
                .assertNext(list -> assertThat(list).containsExactlyInAnyOrder(1, 2, 4, 5, 6))
                .expectComplete()
                .verify();
    }

    private String createPageOf(int n, int start) {
        return IntStream.iterate(start, i -> i + 1).limit(n)
                .mapToObj(id -> String.format("{\"id\": %1$d, \"title\": { \"rendered\": \"Post %1$d\" }, \"content\": { \"rendered\": \"Content %1$d\" }}", id))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private void enqueueResponses(String... jsonPages) {
        int nPages = jsonPages.length;

        for (String page : jsonPages) {
            mockWebServer.enqueue(
                    new MockResponse.Builder()
                            .body(page)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("x-wp-totalpages", nPages)
                            .build());
        }
    }
}