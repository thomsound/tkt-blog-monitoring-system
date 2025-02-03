package de.thomsound.blog_post_processor.model;

public class UpdateMessage extends Message {

    private String content;

    public UpdateMessage() {
        super();
    }

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
