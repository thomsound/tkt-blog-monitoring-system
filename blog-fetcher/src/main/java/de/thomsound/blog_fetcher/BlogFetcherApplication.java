package de.thomsound.blog_fetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlogFetcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogFetcherApplication.class, args);
    }

}
