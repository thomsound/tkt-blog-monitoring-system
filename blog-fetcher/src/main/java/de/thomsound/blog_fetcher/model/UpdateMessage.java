package de.thomsound.blog_fetcher.model;

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
}
