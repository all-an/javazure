package com.allan.javazure.controller;

import com.allan.javazure.entity.Message;
import com.allan.javazure.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Don't load existing messages for portfolio demo
        model.addAttribute("messages", List.of());
        model.addAttribute("newMessage", new Message());
        return "index";
    }

    @PostMapping("/messages")
    @ResponseBody
    public ResponseEntity<Message> createMessage(@RequestBody CreateMessageRequest request) {
        Message message = new Message(request.getContent(), request.getAuthor());
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/messages")
    @ResponseBody
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(messages);
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
}