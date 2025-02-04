package de.thomsound.blog_fetcher.model;

import java.util.Objects;

public class UpdateMessage extends Message {

    private String content;

    public UpdateMessage(Integer postId, String content) {
        super(MessageType.UPDATE, postId);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateMessage that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getContent(), that.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getContent());
    }
}
