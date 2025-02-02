package de.thomsound.blog_fetcher.model;

public record Title(String rendered) {

    public String getText() {
        return rendered;
    }
}
