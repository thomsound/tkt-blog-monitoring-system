package de.thomsound.blog_post_processor.service;

import de.thomsound.blog_post_processor.WordCountUpdateEvent;
import de.thomsound.blog_post_processor.model.Post;
import de.thomsound.blog_post_processor.repository.WordCountRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BlogPostProcessor {

    private final WordCountRepository repository;
    private final ApplicationEventPublisher publisher;

    public BlogPostProcessor(WordCountRepository repository, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public void process(Post post) {
        Map<String, Integer> wordCountDelta = repository.getWordCounts(post.id());
        wordCountDelta.forEach((key, value) -> wordCountDelta.put(key, -value));

        String text = post.title() + " " + post.content();
        List<String> words = getWords(text);

        for(String w : words) {
            wordCountDelta.merge(w, 1, (c1, c2) -> c1 + 1);
        }

        repository.applyDelta(post, wordCountDelta);

        this.publisher.publishEvent(new WordCountUpdateEvent(this.repository.getWordCountsTotal()));
    }

    private static List<String> getWords(String text) {
        String regex = "\\b[^\\s\\d\\W-]*[a-zA-ZäöüÄÖÜß]+(?:-[a-zA-ZäöüÄÖÜß]+)*\\b";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        List<String> words = new ArrayList<>();
        while (matcher.find()) {
            words.add(matcher.group());
        }
        return words;
    }
}
