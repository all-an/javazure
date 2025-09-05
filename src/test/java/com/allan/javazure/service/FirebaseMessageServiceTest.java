package com.allan.javazure.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.CollectionReference;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.WriteResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FirebaseMessageService.
 * Tests message validation, Firestore operations, and mock mode functionality.
 * 
 * @author Allan
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class FirebaseMessageServiceTest {

    @Mock
    private Firestore mockFirestore;

    @Mock
    private CollectionReference mockCollection;

    @Mock
    private DocumentReference mockDocument;

    @Mock
    private ApiFuture<WriteResult> mockWriteFuture;

    @Mock
    private WriteResult mockWriteResult;

    private FirebaseMessageService serviceWithFirestore;
    private FirebaseMessageService serviceWithoutFirestore;

    @BeforeEach
    void setUp() {
        serviceWithFirestore = new FirebaseMessageService(mockFirestore);
        serviceWithoutFirestore = new FirebaseMessageService(null);
    }

    @Test
    void constructor_WithFirestore_ShouldInitializeSuccessfully() {
        FirebaseMessageService service = new FirebaseMessageService(mockFirestore);
        assertNotNull(service);
    }

    @Test
    void constructor_WithNullFirestore_ShouldInitializeInMockMode() {
        FirebaseMessageService service = new FirebaseMessageService(null);
        assertNotNull(service);
    }

    @Test
    void saveMessage_WithValidContentAndAuthor_ShouldSaveToFirestore() throws Exception {
        // Arrange
        String author = "Allan";
        String content = "Hello, world!";
        
        when(mockFirestore.collection("messages")).thenReturn(mockCollection);
        when(mockCollection.document()).thenReturn(mockDocument);
        when(mockDocument.set(any(Map.class))).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(mockWriteResult);

        // Act
        serviceWithFirestore.saveMessage(author, content);

        // Assert
        verify(mockFirestore, times(1)).collection("messages");
        verify(mockCollection, times(1)).document();
        verify(mockDocument, times(1)).set(any(Map.class));
        verify(mockWriteFuture, times(1)).get();
    }

    @Test
    void saveMessage_WithNullAuthor_ShouldUseAnonymous() throws Exception {
        // Arrange
        String content = "Hello, world!";
        
        when(mockFirestore.collection("messages")).thenReturn(mockCollection);
        when(mockCollection.document()).thenReturn(mockDocument);
        when(mockDocument.set(any(Map.class))).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(mockWriteResult);

        // Act
        serviceWithFirestore.saveMessage(null, content);

        // Assert
        verify(mockDocument).set(argThat(map -> {
            Map<String, Object> messageMap = (Map<String, Object>) map;
            return "Anonymous".equals(messageMap.get("author")) && 
                   content.equals(messageMap.get("content"));
        }));
    }

    @Test
    void saveMessage_WithEmptyAuthor_ShouldUseAnonymous() throws Exception {
        // Arrange
        String content = "Hello, world!";
        
        when(mockFirestore.collection("messages")).thenReturn(mockCollection);
        when(mockCollection.document()).thenReturn(mockDocument);
        when(mockDocument.set(any(Map.class))).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(mockWriteResult);

        // Act
        serviceWithFirestore.saveMessage("", content);

        // Assert
        verify(mockDocument).set(argThat(map -> {
            Map<String, Object> messageMap = (Map<String, Object>) map;
            return "Anonymous".equals(messageMap.get("author"));
        }));
    }

    @Test
    void saveMessage_WithWhitespaceAuthor_ShouldUseAnonymous() throws Exception {
        // Arrange
        String content = "Hello, world!";
        
        when(mockFirestore.collection("messages")).thenReturn(mockCollection);
        when(mockCollection.document()).thenReturn(mockDocument);
        when(mockDocument.set(any(Map.class))).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(mockWriteResult);

        // Act
        serviceWithFirestore.saveMessage("   \n\t   ", content);

        // Assert
        verify(mockDocument).set(argThat(map -> {
            Map<String, Object> messageMap = (Map<String, Object>) map;
            return "Anonymous".equals(messageMap.get("author"));
        }));
    }

    @Test
    void saveMessage_WithNullContent_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            serviceWithFirestore.saveMessage("Allan", null));
        
        verify(mockFirestore, never()).collection(any());
    }

    @Test
    void saveMessage_WithEmptyContent_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            serviceWithFirestore.saveMessage("Allan", ""));
        
        verify(mockFirestore, never()).collection(any());
    }

    @Test
    void saveMessage_WithWhitespaceContent_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            serviceWithFirestore.saveMessage("Allan", "   \n\t   "));
        
        verify(mockFirestore, never()).collection(any());
    }

    @Test
    void saveMessage_WhenFirestoreThrowsExecutionException_ShouldThrowRuntimeException() throws Exception {
        // Arrange
        when(mockFirestore.collection("messages")).thenReturn(mockCollection);
        when(mockCollection.document()).thenReturn(mockDocument);
        when(mockDocument.set(any(Map.class))).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenThrow(new ExecutionException("Database error", new RuntimeException()));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            serviceWithFirestore.saveMessage("Allan", "Hello, world!"));
        
        assertEquals("Failed to save message to database", exception.getMessage());
    }

    @Test
    void saveMessage_WhenFirestoreThrowsInterruptedException_ShouldThrowRuntimeException() throws Exception {
        // Arrange
        when(mockFirestore.collection("messages")).thenReturn(mockCollection);
        when(mockCollection.document()).thenReturn(mockDocument);
        when(mockDocument.set(any(Map.class))).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenThrow(new InterruptedException("Thread interrupted"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            serviceWithFirestore.saveMessage("Allan", "Hello, world!"));
        
        assertEquals("Message save operation was interrupted", exception.getMessage());
        assertTrue(Thread.currentThread().isInterrupted()); // Verify interrupt status is restored
    }

    @Test
    void saveMessage_WithMockMode_ShouldNotThrowException() {
        // Act - Should not throw any exception
        assertDoesNotThrow(() -> 
            serviceWithoutFirestore.saveMessage("Allan", "Hello, world!"));
    }

    @Test
    void saveMessage_WithMockModeAndNullAuthor_ShouldNotThrowException() {
        // Act - Should not throw any exception
        assertDoesNotThrow(() -> 
            serviceWithoutFirestore.saveMessage(null, "Hello, world!"));
    }

    @Test
    void saveMessage_WithMockModeAndInvalidContent_ShouldThrowException() {
        // Act & Assert - Validation should still work in mock mode
        assertThrows(IllegalArgumentException.class, () -> 
            serviceWithoutFirestore.saveMessage("Allan", ""));
    }

    @Test
    void saveMessage_ShouldCreateProperMessageDocument() throws Exception {
        // Arrange
        String author = "Allan";
        String content = "Test message content";
        
        when(mockFirestore.collection("messages")).thenReturn(mockCollection);
        when(mockCollection.document()).thenReturn(mockDocument);
        when(mockDocument.set(any(Map.class))).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(mockWriteResult);

        // Act
        serviceWithFirestore.saveMessage(author, content);

        // Assert
        verify(mockDocument).set(argThat(map -> {
            Map<String, Object> messageMap = (Map<String, Object>) map;
            return author.equals(messageMap.get("author")) &&
                   content.equals(messageMap.get("content")) &&
                   messageMap.containsKey("createdAt") &&
                   messageMap.containsKey("messageId") &&
                   messageMap.get("messageId") instanceof String;
        }));
    }

    @Test
    void saveMessage_WithAuthorContainingWhitespace_ShouldTrimAuthor() throws Exception {
        // Arrange
        String authorWithWhitespace = "  Allan  ";
        String content = "Test message";
        
        when(mockFirestore.collection("messages")).thenReturn(mockCollection);
        when(mockCollection.document()).thenReturn(mockDocument);
        when(mockDocument.set(any(Map.class))).thenReturn(mockWriteFuture);
        when(mockWriteFuture.get()).thenReturn(mockWriteResult);

        // Act
        serviceWithFirestore.saveMessage(authorWithWhitespace, content);

        // Assert
        verify(mockDocument).set(argThat(map -> {
            Map<String, Object> messageMap = (Map<String, Object>) map;
            return "Allan".equals(messageMap.get("author"));
        }));
    }
}