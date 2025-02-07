package de.thomsound.blog_post_processor.model;

public class DeleteMessage extends Message {

    public DeleteMessage() {
        super();
    }

    public DeleteMessage(Integer postId) {
        super(MessageType.DELETE, postId);
    }
}
