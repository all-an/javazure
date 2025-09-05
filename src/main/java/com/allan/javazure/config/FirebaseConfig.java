package com.allan.javazure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id:javazure-portfolio}")
    private String projectId;

    @Value("${firebase.credentials-path:}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                        .setProjectId(projectId);

                // Try to load credentials if path is provided
                if (!credentialsPath.isEmpty()) {
                    try {
                        ClassPathResource resource = new ClassPathResource(credentialsPath);
                        InputStream serviceAccount = resource.getInputStream();
                        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                        optionsBuilder.setCredentials(credentials);
                    } catch (Exception e) {
                        System.out.println("Could not load Firebase credentials, using default: " + e.getMessage());
                    }
                }

                FirebaseApp.initializeApp(optionsBuilder.build());
                System.out.println("Firebase initialized successfully with project: " + projectId);
            }
        } catch (Exception e) {
            System.err.println("Firebase initialization failed: " + e.getMessage());
        }
    }

    @Bean
    public Firestore getDatabase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                System.out.println("No Firebase app initialized - returning null for mock mode");
                return null;
            }
            return FirestoreClient.getFirestore();
        } catch (Exception e) {
            System.err.println("Could not get Firestore instance: " + e.getMessage());
            return null; // Will be handled gracefully in service
        }
    }
}