package com.allan.javazure.service;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Firebase implementation of the MessageService interface.
 * Handles message persistence using Google Cloud Firestore.
 * Falls back to mock mode when Firestore is not available.
 * 
 * @author Allan
 * @version 1.0.0
 * @since 2025-01-01
 */
@Service
public class FirebaseMessageService implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseMessageService.class);
    
    /**
     * The name of the Firestore collection where messages are stored.
     */
    private static final String MESSAGES_COLLECTION = "messages";
    
    /**
     * Default author name for anonymous messages.
     */
    private static final String ANONYMOUS_AUTHOR = "Anonymous";

    private final Firestore firestore;

    /**
     * Constructs a new FirebaseMessageService with the specified Firestore instance.
     *
     * @param firestore The Firestore database instance (can be null for mock mode)
     */
    @Autowired
    public FirebaseMessageService(Firestore firestore) {
        this.firestore = firestore;
        
        if (firestore == null) {
            logger.warn("Firestore instance is null - running in mock mode");
        } else {
            logger.info("Firebase Message Service initialized with Firestore");
        }
    }

    /**
     * {@inheritDoc}
     * 
     * If Firestore is available, saves the message to the 'messages' collection.
     * If Firestore is not available, logs the message in mock mode.
     */
    @Override
    public void saveMessage(String author, String content) {
        validateMessageContent(content);
        
        final String effectiveAuthor = resolveAuthorName(author);
        
        if (isFirestoreAvailable()) {
            saveToFirestore(effectiveAuthor, content);
        } else {
            saveMockMessage(effectiveAuthor, content);
        }
    }

    /**
     * Validates that the message content is not null or empty.
     * 
     * @param content The message content to validate
     * @throws IllegalArgumentException if content is null or empty
     */
    private void validateMessageContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }
    }

    /**
     * Resolves the author name, providing a default for null or empty values.
     * 
     * @param author The provided author name
     * @return The resolved author name
     */
    private String resolveAuthorName(String author) {
        return (author != null && !author.trim().isEmpty()) ? author.trim() : ANONYMOUS_AUTHOR;
    }

    /**
     * Checks if Firestore is available for database operations.
     * 
     * @return true if Firestore is available, false otherwise
     */
    private boolean isFirestoreAvailable() {
        return firestore != null;
    }

    /**
     * Saves the message to Firestore database.
     * 
     * @param author The message author
     * @param content The message content
     * @throws RuntimeException if the save operation fails
     */
    private void saveToFirestore(String author, String content) {
        try {
            Map<String, Object> messageData = createMessageDocument(author, content);
            
            DocumentReference docRef = firestore.collection(MESSAGES_COLLECTION).document();
            docRef.set(messageData).get(); // Wait for completion to ensure consistency
            
            logger.info("Message saved successfully to Firestore for author: '{}'", author);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupt status
            logger.error("Firestore save operation was interrupted for author: '{}'", author, e);
            throw new RuntimeException("Message save operation was interrupted", e);
            
        } catch (ExecutionException e) {
            logger.error("Failed to save message to Firestore for author: '{}'", author, e);
            throw new RuntimeException("Failed to save message to database", e.getCause());
        }
    }

    /**
     * Creates a message document for Firestore storage.
     * 
     * @param author The message author
     * @param content The message content
     * @return Map representing the message document
     */
    private Map<String, Object> createMessageDocument(String author, String content) {
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("author", author);
        messageData.put("content", content);
        messageData.put("createdAt", Timestamp.now());
        messageData.put("messageId", java.util.UUID.randomUUID().toString());
        return messageData;
    }

    /**
     * Logs the message in mock mode when Firestore is not available.
     * Useful for development and testing environments.
     * 
     * @param author The message author
     * @param content The message content
     */
    private void saveMockMessage(String author, String content) {
        logger.info("MOCK MODE - Message received: Author='{}', Content='{}', Timestamp='{}'", 
                   author, 
                   content.length() > 100 ? content.substring(0, 100) + "..." : content,
                   java.time.LocalDateTime.now());
    }
}