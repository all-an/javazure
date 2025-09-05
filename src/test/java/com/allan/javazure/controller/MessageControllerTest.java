package com.allan.javazure.controller;

import com.allan.javazure.dto.CreateMessageRequest;
import com.allan.javazure.dto.MessageResponse;
import com.allan.javazure.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for MessageController.
 * Tests HTTP endpoint behavior, validation, and error handling.
 * 
 * @author Allan
 * @version 1.0.0
 */
@WebMvcTest(value = MessageController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateMessageRequest validRequest;
    private CreateMessageRequest invalidRequest;

    @BeforeEach
    void setUp() {
        validRequest = new CreateMessageRequest("Hello, this is a test message!", "Allan");
        invalidRequest = new CreateMessageRequest("", "Allan");
    }

    @Test
    void displayHomePage_ShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void createMessage_WithValidRequest_ShouldReturnSuccess() throws Exception {
        doNothing().when(messageService).saveMessage(eq("Allan"), eq("Hello, this is a test message!"));

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message sent successfully!"))
                .andExpect(jsonPath("$.success").value(true));

        verify(messageService, times(1)).saveMessage("Allan", "Hello, this is a test message!");
    }

    @Test
    void createMessage_WithEmptyContent_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).saveMessage(any(), any());
    }

    @Test
    void createMessage_WithNullContent_ShouldReturnBadRequest() throws Exception {
        CreateMessageRequest nullContentRequest = new CreateMessageRequest(null, "Allan");

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullContentRequest)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).saveMessage(any(), any());
    }

    @Test
    void createMessage_WithTooLongContent_ShouldReturnBadRequest() throws Exception {
        String longContent = "a".repeat(1001); // Exceeds 1000 character limit
        CreateMessageRequest longContentRequest = new CreateMessageRequest(longContent, "Allan");

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longContentRequest)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).saveMessage(any(), any());
    }

    @Test
    void createMessage_WithTooLongAuthor_ShouldReturnBadRequest() throws Exception {
        String longAuthor = "a".repeat(101); // Exceeds 100 character limit
        CreateMessageRequest longAuthorRequest = new CreateMessageRequest("Valid content", longAuthor);

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longAuthorRequest)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).saveMessage(any(), any());
    }

    @Test
    void createMessage_WithNullAuthor_ShouldReturnSuccess() throws Exception {
        CreateMessageRequest nullAuthorRequest = new CreateMessageRequest("Hello, world!", null);
        doNothing().when(messageService).saveMessage(eq(null), eq("Hello, world!"));

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullAuthorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message sent successfully!"))
                .andExpect(jsonPath("$.success").value(true));

        verify(messageService, times(1)).saveMessage(null, "Hello, world!");
    }

    @Test
    void createMessage_WhenServiceThrowsRuntimeException_ShouldReturnInternalServerError() throws Exception {
        doThrow(new RuntimeException("Database error")).when(messageService)
                .saveMessage(eq("Allan"), eq("Hello, this is a test message!"));

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unable to process your message. Please try again later."));

        verify(messageService, times(1)).saveMessage("Allan", "Hello, this is a test message!");
    }

    @Test
    void createMessage_WhenServiceThrowsIllegalArgumentException_ShouldReturnBadRequest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid content")).when(messageService)
                .saveMessage(eq("Allan"), eq("Hello, this is a test message!"));

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid message data: Invalid content"));

        verify(messageService, times(1)).saveMessage("Allan", "Hello, this is a test message!");
    }

    @Test
    void createMessage_WithWhitespaceOnlyContent_ShouldReturnBadRequest() throws Exception {
        CreateMessageRequest whitespaceRequest = new CreateMessageRequest("   \n\t   ", "Allan");

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(whitespaceRequest)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).saveMessage(any(), any());
    }

    @Test
    void createMessage_WithValidContentAndEmptyAuthor_ShouldReturnSuccess() throws Exception {
        CreateMessageRequest emptyAuthorRequest = new CreateMessageRequest("Hello, world!", "");
        doNothing().when(messageService).saveMessage(eq(""), eq("Hello, world!"));

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyAuthorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Message sent successfully!"))
                .andExpect(jsonPath("$.success").value(true));

        verify(messageService, times(1)).saveMessage("", "Hello, world!");
    }
}