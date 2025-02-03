package de.thomsound.blog_fetcher.model;

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
}
