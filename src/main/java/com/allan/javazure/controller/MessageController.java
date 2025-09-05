package com.allan.javazure.controller;

import com.allan.javazure.dto.CreateMessageRequest;
import com.allan.javazure.dto.MessageResponse;
import com.allan.javazure.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller for handling message-related HTTP requests.
 * Provides endpoints for displaying the portfolio page and handling message submissions.
 * 
 * @author Allan
 * @version 1.0.0
 * @since 2025-01-01
 */
@Controller
@RequestMapping
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    
    private final MessageService messageService;

    /**
     * Constructs a new MessageController with the specified message service.
     *
     * @param messageService The service responsible for handling message operations
     */
    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Displays the main portfolio page.
     * 
     * @return The name of the template to render ("index")
     */
    @GetMapping("/")
    public String displayHomePage() {
        logger.info("Serving portfolio home page");
        return "index";
    }

    /**
     * Handles the creation of new messages submitted through the contact form.
     * Validates the request data and delegates message saving to the service layer.
     * 
     * @param request The message creation request containing content and author information
     * @return ResponseEntity containing the operation result and appropriate HTTP status
     */
    @PostMapping("/messages")
    @ResponseBody
    public ResponseEntity<MessageResponse> createMessage(@Valid @RequestBody CreateMessageRequest request) {
        logger.info("Received message creation request from author: {}", 
                   request.getAuthor() != null ? request.getAuthor() : "Anonymous");
        
        try {
            validateMessageRequest(request);
            
            messageService.saveMessage(request.getAuthor(), request.getContent());
            
            logger.info("Successfully saved message from: {}", 
                       request.getAuthor() != null ? request.getAuthor() : "Anonymous");
            
            return ResponseEntity.ok(createSuccessResponse());
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid message request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Invalid message data: " + e.getMessage()));
                    
        } catch (Exception e) {
            logger.error("Error processing message creation request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Unable to process your message. Please try again later."));
        }
    }

    /**
     * Validates the incoming message request for required fields and business rules.
     * 
     * @param request The request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateMessageRequest(CreateMessageRequest request) {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
        
        if (request.getContent().trim().length() > 1000) {
            throw new IllegalArgumentException("Message content cannot exceed 1000 characters");
        }
        
        if (request.getAuthor() != null && request.getAuthor().length() > 100) {
            throw new IllegalArgumentException("Author name cannot exceed 100 characters");
        }
    }

    /**
     * Creates a standardized success response.
     * 
     * @return MessageResponse indicating successful operation
     */
    private MessageResponse createSuccessResponse() {
        return new MessageResponse("Message sent successfully!", true);
    }

    /**
     * Creates a standardized error response with custom message.
     * 
     * @param errorMessage The error message to include in the response
     * @return MessageResponse indicating failed operation
     */
    private MessageResponse createErrorResponse(String errorMessage) {
        return new MessageResponse(errorMessage, false);
    }
}