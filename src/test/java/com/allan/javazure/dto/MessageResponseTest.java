package com.allan.javazure.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MessageResponse DTO.
 * Tests constructors, factory methods, and string representation.
 * 
 * @author Allan
 * @version 1.0.0
 */
class MessageResponseTest {

    @Test
    void defaultConstructor_ShouldCreateEmptyResponse() {
        MessageResponse response = new MessageResponse();
        
        assertNull(response.getMessage());
        assertFalse(response.isSuccess());
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        String message = "Operation successful";
        boolean success = true;
        
        MessageResponse response = new MessageResponse(message, success);
        
        assertEquals(message, response.getMessage());
        assertEquals(success, response.isSuccess());
    }

    @Test
    void parameterizedConstructor_WithFailure_ShouldSetFields() {
        String message = "Operation failed";
        boolean success = false;
        
        MessageResponse response = new MessageResponse(message, success);
        
        assertEquals(message, response.getMessage());
        assertEquals(success, response.isSuccess());
    }

    @Test
    void success_StaticMethod_ShouldCreateSuccessResponse() {
        String message = "Success message";
        
        MessageResponse response = MessageResponse.success(message);
        
        assertEquals(message, response.getMessage());
        assertTrue(response.isSuccess());
    }

    @Test
    void error_StaticMethod_ShouldCreateErrorResponse() {
        String message = "Error message";
        
        MessageResponse response = MessageResponse.error(message);
        
        assertEquals(message, response.getMessage());
        assertFalse(response.isSuccess());
    }

    @Test
    void setMessage_ShouldUpdateMessage() {
        MessageResponse response = new MessageResponse();
        String message = "Updated message";
        
        response.setMessage(message);
        
        assertEquals(message, response.getMessage());
    }

    @Test
    void setSuccess_ShouldUpdateSuccess() {
        MessageResponse response = new MessageResponse();
        
        response.setSuccess(true);
        
        assertTrue(response.isSuccess());
    }

    @Test
    void setSuccess_ToFalse_ShouldUpdateSuccess() {
        MessageResponse response = new MessageResponse("Test", true);
        
        response.setSuccess(false);
        
        assertFalse(response.isSuccess());
    }

    @Test
    void toString_WithSuccessResponse_ShouldIncludeAllFields() {
        String message = "Operation completed";
        MessageResponse response = new MessageResponse(message, true);
        
        String result = response.toString();
        
        assertTrue(result.contains("message='Operation completed'"));
        assertTrue(result.contains("success=true"));
        assertTrue(result.contains("MessageResponse"));
    }

    @Test
    void toString_WithErrorResponse_ShouldIncludeAllFields() {
        String message = "Operation failed";
        MessageResponse response = new MessageResponse(message, false);
        
        String result = response.toString();
        
        assertTrue(result.contains("message='Operation failed'"));
        assertTrue(result.contains("success=false"));
        assertTrue(result.contains("MessageResponse"));
    }

    @Test
    void toString_WithNullMessage_ShouldHandleNull() {
        MessageResponse response = new MessageResponse(null, true);
        
        String result = response.toString();
        
        assertTrue(result.contains("message='null'"));
        assertTrue(result.contains("success=true"));
    }

    @Test
    void success_WithEmptyMessage_ShouldCreateValidResponse() {
        MessageResponse response = MessageResponse.success("");
        
        assertEquals("", response.getMessage());
        assertTrue(response.isSuccess());
    }

    @Test
    void error_WithEmptyMessage_ShouldCreateValidResponse() {
        MessageResponse response = MessageResponse.error("");
        
        assertEquals("", response.getMessage());
        assertFalse(response.isSuccess());
    }

    @Test
    void success_WithNullMessage_ShouldCreateValidResponse() {
        MessageResponse response = MessageResponse.success(null);
        
        assertNull(response.getMessage());
        assertTrue(response.isSuccess());
    }

    @Test
    void error_WithNullMessage_ShouldCreateValidResponse() {
        MessageResponse response = MessageResponse.error(null);
        
        assertNull(response.getMessage());
        assertFalse(response.isSuccess());
    }
}