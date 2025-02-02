package de.thomsound.blog_fetcher.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.stream.Collectors;

public record Content(String rendered) {

    public String getText() {
        Document doc = Jsoup.parse(rendered);
        return doc.select(".elementor-widget-heading,.elementor-widget-text-editor")
                .stream()
                .map(Element::text)
                .collect(Collectors.joining("\n"));
    }
}
