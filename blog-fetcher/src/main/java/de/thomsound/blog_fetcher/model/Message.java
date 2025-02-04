package de.thomsound.blog_fetcher.model;

import java.util.Objects;

public abstract class Message {
    private MessageType type;
    private Integer postId;

    public Message(MessageType type, Integer postId) {
        this.type = type;
        this.postId = postId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return getType() == message.getType() && Objects.equals(getPostId(), message.getPostId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getPostId());
    }
}
