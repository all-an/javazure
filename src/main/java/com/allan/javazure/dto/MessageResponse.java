package com.allan.javazure.dto;

/**
 * Data Transfer Object for message operation responses.
 * Provides standardized response format for API operations.
 * 
 * @author Allan
 * @version 1.0.0
 * @since 2025-01-01
 */
public class MessageResponse {

    /**
     * The response message describing the operation result.
     */
    private String message;

    /**
     * Indicates whether the operation was successful.
     */
    private boolean success;

    /**
     * Default constructor for JSON serialization.
     */
    public MessageResponse() {
    }

    /**
     * Constructs a new MessageResponse with specified message and success status.
     *
     * @param message The response message
     * @param success The success status of the operation
     */
    public MessageResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    /**
     * Creates a success response with the specified message.
     *
     * @param message The success message
     * @return A new MessageResponse indicating success
     */
    public static MessageResponse success(String message) {
        return new MessageResponse(message, true);
    }

    /**
     * Creates an error response with the specified message.
     *
     * @param message The error message
     * @return A new MessageResponse indicating failure
     */
    public static MessageResponse error(String message) {
        return new MessageResponse(message, false);
    }

    /**
     * Gets the response message.
     *
     * @return The response message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     *
     * @param message The response message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the success status of the operation.
     *
     * @return true if the operation was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the operation.
     *
     * @param success The success status to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Returns a string representation of this response.
     *
     * @return String representation containing message and success status
     */
    @Override
    public String toString() {
        return "MessageResponse{" +
                "message='" + message + "'" +
                ", success=" + success +
                '}';
    }
}