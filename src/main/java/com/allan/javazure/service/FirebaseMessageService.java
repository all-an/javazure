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

@Service
public class FirebaseMessageService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseMessageService.class);

    @Autowired(required = false)
    private Firestore firestore;

    public void saveMessage(String author, String content) {
        try {
            if (firestore == null) {
                logger.info("Mock Firebase - message saved for author '{}': {}", author, content);
                return;
            }

            Map<String, Object> messageData = new HashMap<>();
            messageData.put("author", author != null ? author : "Anonymous");
            messageData.put("content", content);
            messageData.put("createdAt", Timestamp.now());

            DocumentReference docRef = firestore.collection("messages").document();
            docRef.set(messageData).get(); // Wait for completion

            logger.info("Message saved successfully for author '{}'", author);
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error saving message for author '{}'", author, e);
            throw new RuntimeException("Failed to save message", e);
        }
    }
}