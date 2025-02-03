package de.thomsound.blog_fetcher.domain;

public record Title(String rendered) {

    public String getText() {
        return rendered;
    }
}
