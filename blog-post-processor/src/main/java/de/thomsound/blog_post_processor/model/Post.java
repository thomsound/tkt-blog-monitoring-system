package de.thomsound.blog_post_processor.model;

import java.util.Date;

public record Post(int id, Date modified_gmt, String title, String content) {
}
