package com.allan.javazure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating new messages.
 * Contains validation annotations to ensure data integrity.
 * 
 * @author Allan
 * @version 1.0.0
 * @since 2025-01-01
 */
public class CreateMessageRequest {

    /**
     * The message content. Cannot be null, empty, or exceed 1000 characters.
     */
    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message content cannot exceed 1000 characters")
    private String content;

    /**
     * The message author name. Optional field, but cannot exceed 100 characters if provided.
     */
    @Size(max = 100, message = "Author name cannot exceed 100 characters")
    private String author;

    /**
     * Default constructor for JSON deserialization.
     */
    public CreateMessageRequest() {
    }

    /**
     * Constructs a new CreateMessageRequest with specified content and author.
     *
     * @param content The message content
     * @param author The message author (can be null)
     */
    public CreateMessageRequest(String content, String author) {
        this.content = content;
        this.author = author;
    }

    /**
     * Gets the message content.
     *
     * @return The message content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the message content.
     *
     * @param content The message content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the message author.
     *
     * @return The message author, or null if not specified
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the message author.
     *
     * @param author The message author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns a string representation of this request.
     *
     * @return String representation containing content and author information
     */
    @Override
    public String toString() {
        return "CreateMessageRequest{" +
                "content='" + (content != null ? content.substring(0, Math.min(content.length(), 50)) + "..." : null) + "'" +
                ", author='" + author + "'" +
                '}';
    }
}