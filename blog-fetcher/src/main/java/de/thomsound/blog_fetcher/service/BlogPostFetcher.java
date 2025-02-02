package de.thomsound.blog_fetcher.service;

import de.thomsound.blog_fetcher.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;


@Service
public class BlogPostFetcher {
    private static final Logger log = LoggerFactory.getLogger(BlogPostFetcher.class);
    private final WebClient webClient;

    public BlogPostFetcher(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Post> fetchPosts() {
        return fetchPages(1);
    }

    private Flux<Post> fetchPages(int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/wp-json/wp/v2/posts")
                        .queryParam("page", page)
                        .build())

                .exchangeToFlux(clientResponse -> {
                    List<String> totalPagesHeader = clientResponse.headers().header("x-wp-totalpages");
                    int totalPages = totalPagesHeader.isEmpty() ? 1 : Integer.parseInt(totalPagesHeader.get(0));

                    Flux<Post> currentPagePosts = clientResponse.bodyToFlux(Post.class);

                    if (page >= totalPages) {
                        return currentPagePosts;
                    }

                    return currentPagePosts.concatWith(fetchPages(page + 1));
                });
    }
}
