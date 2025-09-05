/**
 * Portfolio Message Board Application
 * Handles form submission and custom alert functionality
 * 
 * @author Allan
 * @version 1.0.0
 */

/**
 * Application configuration and constants
 */
const AppConfig = {
    ENDPOINTS: {
        MESSAGES: '/messages'
    },
    TIMEOUTS: {
        FOCUS_DELAY: 100
    },
    ICONS: {
        SUCCESS: '✅',
        ERROR: '❌'
    },
    BUTTON_TEXTS: {
        SUBMIT: 'Post Message',
        LOADING: 'Sending...',
        SUCCESS_ALERT: 'Awesome!',
        ERROR_ALERT: 'Got it'
    }
};

/**
 * DOM element references for better performance and organization
 */
class DOMElements {
    constructor() {
        this.messageForm = document.getElementById('messageForm');
        this.customAlert = document.getElementById('customAlert');
        this.alertIcon = document.getElementById('alertIcon');
        this.alertTitle = document.getElementById('alertTitle');
        this.alertMessage = document.getElementById('alertMessage');
        this.alertButton = document.getElementById('alertButton');
        this.contentInput = document.getElementById('content');
        this.authorInput = document.getElementById('author');
    }

    /**
     * Validates that all required DOM elements exist
     * @returns {boolean} True if all elements are present
     */
    validateElements() {
        const requiredElements = [
            this.customAlert, this.alertIcon, this.alertTitle, 
            this.alertMessage, this.alertButton
        ];
        return requiredElements.every(element => element !== null);
    }
}

/**
 * Custom Alert Manager - Handles all alert-related functionality
 */
class AlertManager {
    constructor(elements) {
        this.elements = elements;
        this.isVisible = false;
    }

    /**
     * Displays a custom alert with specified parameters
     * @param {string} title - Alert title
     * @param {string} message - Alert message content
     * @param {boolean} isSuccess - Whether this is a success or error alert
     */
    show(title, message, isSuccess = true) {
        this.updateAlertContent(title, message, isSuccess);
        this.displayAlert();
        this.focusOnButton();
    }

    /**
     * Updates alert content based on success/error state
     * @param {string} title - Alert title
     * @param {string} message - Alert message
     * @param {boolean} isSuccess - Success state
     * @private
     */
    updateAlertContent(title, message, isSuccess) {
        this.elements.alertIcon.textContent = isSuccess ? AppConfig.ICONS.SUCCESS : AppConfig.ICONS.ERROR;
        this.elements.alertTitle.textContent = title;
        this.elements.alertMessage.textContent = message;
        this.elements.alertButton.textContent = isSuccess ? AppConfig.BUTTON_TEXTS.SUCCESS_ALERT : AppConfig.BUTTON_TEXTS.ERROR_ALERT;
    }

    /**
     * Shows the alert modal
     * @private
     */
    displayAlert() {
        this.elements.customAlert.classList.add('show');
        this.isVisible = true;
    }

    /**
     * Hides the alert modal
     */
    hide() {
        this.elements.customAlert.classList.remove('show');
        this.isVisible = false;
    }

    /**
     * Focuses on alert button for accessibility
     * @private
     */
    focusOnButton() {
        setTimeout(() => {
            if (this.elements.alertButton) {
                this.elements.alertButton.focus();
            }
        }, AppConfig.TIMEOUTS.FOCUS_DELAY);
    }

    /**
     * Sets up event listeners for alert interactions
     */
    setupEventListeners() {
        // Close on button click
        this.elements.alertButton.addEventListener('click', () => this.hide());
        
        // Close on backdrop click
        this.elements.customAlert.addEventListener('click', (e) => {
            if (e.target === this.elements.customAlert) {
                this.hide();
            }
        });
        
        // Close on Escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isVisible) {
                this.hide();
            }
        });
    }
}

/**
 * Form Manager - Handles form validation and submission
 */
class FormManager {
    constructor(elements, alertManager) {
        this.elements = elements;
        this.alertManager = alertManager;
        this.isSubmitting = false;
    }

    /**
     * Validates form input before submission
     * @returns {boolean} True if form is valid
     */
    validateForm() {
        const content = this.elements.contentInput.value.trim();
        if (!content) {
            this.alertManager.show('Oops!', 'Please enter a message before sending.', false);
            return false;
        }
        return true;
    }

    /**
     * Extracts and sanitizes form data
     * @returns {Object} Form data object
     */
    getFormData() {
        return {
            content: this.elements.contentInput.value.trim(),
            author: this.elements.authorInput.value.trim() || null
        };
    }

    /**
     * Clears all form inputs
     */
    clearForm() {
        this.elements.contentInput.value = '';
        this.elements.authorInput.value = '';
    }

    /**
     * Sets button loading state
     * @param {HTMLButtonElement} button - Submit button element
     * @param {boolean} isLoading - Loading state
     * @param {string} originalText - Original button text
     */
    setButtonState(button, isLoading, originalText) {
        if (isLoading) {
            button.textContent = AppConfig.BUTTON_TEXTS.LOADING;
            button.disabled = true;
            this.isSubmitting = true;
        } else {
            button.textContent = originalText || AppConfig.BUTTON_TEXTS.SUBMIT;
            button.disabled = false;
            this.isSubmitting = false;
        }
    }

    /**
     * Handles form submission event
     * @param {Event} event - Form submit event
     */
    async handleSubmit(event) {
        event.preventDefault();
        
        if (this.isSubmitting || !this.validateForm()) {
            return;
        }

        const submitButton = event.target.querySelector('button[type="submit"]');
        const originalText = submitButton.textContent;
        const formData = this.getFormData();

        try {
            this.setButtonState(submitButton, true, originalText);
            
            const response = await this.submitMessage(formData);
            const data = await response.json();
            
            this.clearForm();
            this.alertManager.show('Success!', data.message || 'Message sent successfully!', true);
            
        } catch (error) {
            console.error('Form submission error:', error);
            this.alertManager.show('Error', 'Something went wrong. Please try again.', false);
        } finally {
            this.setButtonState(submitButton, false, originalText);
        }
    }

    /**
     * Submits message data to server
     * @param {Object} formData - Form data to submit
     * @returns {Promise<Response>} Fetch response promise
     */
    async submitMessage(formData) {
        return fetch(AppConfig.ENDPOINTS.MESSAGES, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });
    }

    /**
     * Sets up form event listeners
     */
    setupEventListeners() {
        if (this.elements.messageForm) {
            this.elements.messageForm.addEventListener('submit', (e) => this.handleSubmit(e));
        }
    }
}

/**
 * Main Application Class - Orchestrates all functionality
 */
class PortfolioApp {
    constructor() {
        this.elements = new DOMElements();
        this.alertManager = null;
        this.formManager = null;
    }

    /**
     * Initializes the application
     * @returns {boolean} True if initialization successful
     */
    init() {
        if (!this.elements.validateElements()) {
            console.error('Required DOM elements not found');
            return false;
        }

        this.setupManagers();
        this.setupEventListeners();
        
        console.log('Portfolio application initialized successfully');
        return true;
    }

    /**
     * Sets up manager instances
     * @private
     */
    setupManagers() {
        this.alertManager = new AlertManager(this.elements);
        this.formManager = new FormManager(this.elements, this.alertManager);
    }

    /**
     * Sets up all event listeners
     * @private
     */
    setupEventListeners() {
        this.alertManager.setupEventListeners();
        this.formManager.setupEventListeners();
    }
}

// Export classes for testing
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        DOMElements,
        AlertManager,
        FormManager,
        PortfolioApp,
        AppConfig
    };
}

// Make classes available globally for testing
if (typeof global !== 'undefined') {
    global.DOMElements = DOMElements;
    global.AlertManager = AlertManager;
    global.FormManager = FormManager;
    global.PortfolioApp = PortfolioApp;
    global.AppConfig = AppConfig;
}

/**
 * Application entry point
 * Initializes the application when DOM is ready
 */
document.addEventListener('DOMContentLoaded', function() {
    const app = new PortfolioApp();
    app.init();
});