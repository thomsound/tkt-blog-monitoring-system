package de.thomsound.blog_fetcher.service;

import de.thomsound.blog_fetcher.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;


@Service
public class BlogPostFetcher {
    private static final Logger log = LoggerFactory.getLogger(BlogPostFetcher.class);

    private final WebClient webClient;

    private Date lastPoll = null;

    public BlogPostFetcher(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Post> fetchPosts() {
        Optional<String> after = this.lastPoll == null ? Optional.empty() : Optional.of(getISODateString(this.lastPoll));
        this.lastPoll = new Date();

        return fetchPages(1, after, List.of("id", "modified_after", "title", "content"));
    }

    private String getISODateString(Date date) {
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    private Flux<Post> fetchPages(int page, Optional<String> after, List<String> fields) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/wp-json/wp/v2/posts")
                        .queryParam("page", page)
                        .queryParam("_fields", String.join(",",fields))
                        .queryParamIfPresent("modified_after", after)
                        .build())

                .exchangeToFlux(clientResponse -> {
                    List<String> totalPagesHeader = clientResponse.headers().header("x-wp-totalpages");
                    int totalPages = totalPagesHeader.isEmpty() ? 1 : Integer.parseInt(totalPagesHeader.get(0));

                    Flux<Post> currentPagePosts = clientResponse.bodyToFlux(Post.class);

                    if (page >= totalPages) {
                        return currentPagePosts;
                    }

                    return currentPagePosts.concatWith(fetchPages(page + 1, after, fields));
                });
    }
}
