package de.thomsound.blog_fetcher.model;

public class DeleteMessage extends Message {
    public DeleteMessage(Integer postId) {
        super(MessageType.DELETE, postId);
    }
}
