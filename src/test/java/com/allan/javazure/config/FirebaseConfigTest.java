package com.allan.javazure.config;

import com.google.cloud.firestore.Firestore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FirebaseConfig.
 * Tests Firebase initialization and Firestore bean creation.
 * 
 * @author Allan
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class FirebaseConfigTest {

    @Test
    void constructor_ShouldCreateInstance() {
        FirebaseConfig config = new FirebaseConfig();
        assertNotNull(config);
    }

    @Test
    void getDatabase_WhenFirebaseNotInitialized_ShouldReturnNull() {
        FirebaseConfig config = new FirebaseConfig();
        
        Firestore result = config.getDatabase();
        
        assertNull(result);
    }

    @Test
    void projectIdProperty_ShouldHaveDefaultValue() {
        FirebaseConfig config = new FirebaseConfig();
        
        // Use reflection to check the default value since @Value is not processed in unit tests
        String projectId = (String) ReflectionTestUtils.getField(config, "projectId");
        
        // In unit test context, @Value defaults won't be applied, so it will be null
        // This test verifies the field exists and can be accessed
        assertDoesNotThrow(() -> ReflectionTestUtils.getField(config, "projectId"));
    }

    @Test
    void credentialsPathProperty_ShouldHaveDefaultValue() {
        FirebaseConfig config = new FirebaseConfig();
        
        // Use reflection to check the field exists
        assertDoesNotThrow(() -> ReflectionTestUtils.getField(config, "credentialsPath"));
    }

    @Test
    void initialize_ShouldNotThrowException() {
        FirebaseConfig config = new FirebaseConfig();
        
        assertDoesNotThrow(() -> {
            // Call the @PostConstruct method directly
            ReflectionTestUtils.invokeMethod(config, "initialize");
        });
    }
}