package com.allan.javazure.controller;

import com.allan.javazure.service.FirebaseMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MessageController {

    @Autowired
    private FirebaseMessageService firebaseMessageService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/messages")
    @ResponseBody
    public ResponseEntity<MessageResponse> createMessage(@RequestBody CreateMessageRequest request) {
        try {
            firebaseMessageService.saveMessage(request.getAuthor(), request.getContent());
            return ResponseEntity.ok(new MessageResponse("Message sent successfully!", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new MessageResponse("Error sending message", false));
        }
    }

    public static class CreateMessageRequest {
        private String content;
        private String author;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }

    public static class MessageResponse {
        private String message;
        private boolean success;

        public MessageResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}