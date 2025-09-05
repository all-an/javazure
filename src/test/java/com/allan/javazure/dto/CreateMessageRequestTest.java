package com.allan.javazure.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreateMessageRequest DTO.
 * Tests validation, constructors, and string representation.
 * 
 * @author Allan
 * @version 1.0.0
 */
class CreateMessageRequestTest {

    @Test
    void defaultConstructor_ShouldCreateEmptyRequest() {
        CreateMessageRequest request = new CreateMessageRequest();
        
        assertNull(request.getContent());
        assertNull(request.getAuthor());
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        String content = "Test message";
        String author = "Allan";
        
        CreateMessageRequest request = new CreateMessageRequest(content, author);
        
        assertEquals(content, request.getContent());
        assertEquals(author, request.getAuthor());
    }

    @Test
    void parameterizedConstructor_WithNullValues_ShouldAllowNulls() {
        CreateMessageRequest request = new CreateMessageRequest(null, null);
        
        assertNull(request.getContent());
        assertNull(request.getAuthor());
    }

    @Test
    void setContent_ShouldUpdateContent() {
        CreateMessageRequest request = new CreateMessageRequest();
        String content = "Updated content";
        
        request.setContent(content);
        
        assertEquals(content, request.getContent());
    }

    @Test
    void setAuthor_ShouldUpdateAuthor() {
        CreateMessageRequest request = new CreateMessageRequest();
        String author = "Updated author";
        
        request.setAuthor(author);
        
        assertEquals(author, request.getAuthor());
    }

    @Test
    void toString_WithShortContent_ShouldShowFullContent() {
        String content = "Short message";
        String author = "Allan";
        CreateMessageRequest request = new CreateMessageRequest(content, author);
        
        String result = request.toString();
        
        assertTrue(result.contains("Short message..."));
        assertTrue(result.contains("author='Allan'"));
    }

    @Test
    void toString_WithLongContent_ShouldTruncateContent() {
        String longContent = "a".repeat(100); // 100 character content
        String author = "Allan";
        CreateMessageRequest request = new CreateMessageRequest(longContent, author);
        
        String result = request.toString();
        
        assertTrue(result.contains("..."));
        assertTrue(result.contains("author='Allan'"));
        assertTrue(result.length() < longContent.length() + 50); // Should be truncated
    }

    @Test
    void toString_WithNullContent_ShouldHandleNull() {
        CreateMessageRequest request = new CreateMessageRequest(null, "Allan");
        
        String result = request.toString();
        
        assertTrue(result.contains("content='null'"));
        assertTrue(result.contains("author='Allan'"));
    }

    @Test
    void toString_WithNullAuthor_ShouldHandleNull() {
        CreateMessageRequest request = new CreateMessageRequest("Test content", null);
        
        String result = request.toString();
        
        assertTrue(result.contains("Test content..."));
        assertTrue(result.contains("author='null'"));
    }

    @Test
    void toString_WithBothNull_ShouldHandleBothNulls() {
        CreateMessageRequest request = new CreateMessageRequest(null, null);
        
        String result = request.toString();
        
        assertTrue(result.contains("content='null'"));
        assertTrue(result.contains("author='null'"));
    }

    @Test
    void contentValidation_WithMaxLength_ShouldBeValid() {
        String maxLengthContent = "a".repeat(1000); // Exactly 1000 characters
        CreateMessageRequest request = new CreateMessageRequest(maxLengthContent, "Allan");
        
        assertEquals(maxLengthContent, request.getContent());
        assertEquals(1000, request.getContent().length());
    }

    @Test
    void authorValidation_WithMaxLength_ShouldBeValid() {
        String maxLengthAuthor = "a".repeat(100); // Exactly 100 characters
        CreateMessageRequest request = new CreateMessageRequest("Test", maxLengthAuthor);
        
        assertEquals(maxLengthAuthor, request.getAuthor());
        assertEquals(100, request.getAuthor().length());
    }
}