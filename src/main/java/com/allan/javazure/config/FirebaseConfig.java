package com.allan.javazure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.enabled:true}")
    private boolean firebaseEnabled;

    @Value("${firebase.project-id:javazure-portfolio}")
    private String projectId;

    @Value("${firebase.credentials-path:}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        if (!firebaseEnabled) {
            System.out.println("Firebase is disabled via configuration");
            return;
        }
        
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                        .setProjectId(projectId);

                GoogleCredentials credentials = null;

                // Priority 1: Try to load from Azure environment variable (JSON string)
                String credentialsJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS_JSON");
                if (credentialsJson != null && !credentialsJson.isEmpty()) {
                    try {
                        InputStream credentialsStream = new java.io.ByteArrayInputStream(credentialsJson.getBytes());
                        credentials = GoogleCredentials.fromStream(credentialsStream);
                        System.out.println("Firebase credentials loaded from GOOGLE_APPLICATION_CREDENTIALS_JSON environment variable");
                    } catch (Exception e) {
                        System.err.println("Could not load credentials from environment variable: " + e.getMessage());
                    }
                }

                // Priority 2: Try to load from file path if environment variable failed
                if (credentials == null && !credentialsPath.isEmpty()) {
                    try {
                        ClassPathResource resource = new ClassPathResource(credentialsPath);
                        InputStream serviceAccount = resource.getInputStream();
                        credentials = GoogleCredentials.fromStream(serviceAccount);
                        System.out.println("Firebase credentials loaded from file: " + credentialsPath);
                    } catch (Exception e) {
                        System.out.println("Could not load Firebase credentials from file: " + e.getMessage());
                    }
                }

                if (credentials != null) {
                    optionsBuilder.setCredentials(credentials);
                } else {
                    System.out.println("No Firebase credentials found, using default (may fail if not running on GCP)");
                }

                FirebaseApp.initializeApp(optionsBuilder.build());
                System.out.println("Firebase initialized successfully with project: " + projectId);
            }
        } catch (Exception e) {
            System.err.println("Firebase initialization failed: " + e.getMessage());
        }
    }

    @Bean
    @ConditionalOnProperty(value = "firebase.enabled", havingValue = "true", matchIfMissing = true)
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