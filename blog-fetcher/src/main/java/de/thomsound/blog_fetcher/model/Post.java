package de.thomsound.blog_fetcher.model;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.Date;

public record Post(int id, Date modified_gmt, Title title, Content content) {

    @JsonGetter("title")
    public String titleText() {
        return this.title.getText();
    }

    @JsonGetter("content")
    public String contentText() {
        return this.content.getText();
    }
}
