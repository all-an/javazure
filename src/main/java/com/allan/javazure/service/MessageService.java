package com.allan.javazure.service;

/**
 * Service interface for message-related operations.
 * Defines the contract for message persistence and retrieval.
 * 
 * @author Allan
 * @version 1.0.0
 * @since 2025-01-01
 */
public interface MessageService {

    /**
     * Saves a new message to the data store.
     * Handles both anonymous messages (null author) and named messages.
     * 
     * @param author The message author, can be null for anonymous messages
     * @param content The message content, must not be null or empty
     * @throws IllegalArgumentException if content is null or empty
     * @throws RuntimeException if the message cannot be saved due to technical issues
     */
    void saveMessage(String author, String content);
}