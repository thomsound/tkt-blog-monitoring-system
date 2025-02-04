package de.thomsound.blog_fetcher.domain;

import com.fasterxml.jackson.annotation.JsonGetter;

public record Post(int id, Title title, Content content) {

    @JsonGetter("title")
    public String titleText() {
        return this.title.getText();
    }

    @JsonGetter("content")
    public String contentText() {
        return this.content.getText();
    }
}
