/**
 * Unit tests for Portfolio Message Board Application
 * Tests all JavaScript classes and functions
 * 
 * @author Allan
 * @version 1.0.0
 */

// Import the main app file and destructure classes
const {
    DOMElements,
    AlertManager, 
    FormManager,
    PortfolioApp,
    AppConfig
} = require('../../main/resources/static/js/app.js');

// Mock DOM elements for testing
const createMockElement = (id) => {
    const element = document.createElement('div');
    element.id = id;
    return element;
};

const setupMockDOM = () => {
    document.body.innerHTML = `
        <form id="messageForm">
            <input type="text" id="author" />
            <textarea id="content"></textarea>
            <button type="submit">Submit</button>
        </form>
        <div id="customAlert" class="custom-alert">
            <div id="alertIcon"></div>
            <div id="alertTitle"></div>
            <div id="alertMessage"></div>
            <button id="alertButton"></button>
        </div>
    `;
};

describe('DOMElements', () => {
    beforeEach(() => {
        setupMockDOM();
    });

    test('should initialize all DOM elements correctly', () => {
        const elements = new DOMElements();
        
        expect(elements.messageForm).toBeTruthy();
        expect(elements.customAlert).toBeTruthy();
        expect(elements.alertIcon).toBeTruthy();
        expect(elements.alertTitle).toBeTruthy();
        expect(elements.alertMessage).toBeTruthy();
        expect(elements.alertButton).toBeTruthy();
        expect(elements.contentInput).toBeTruthy();
        expect(elements.authorInput).toBeTruthy();
    });

    test('should validate elements correctly when all are present', () => {
        const elements = new DOMElements();
        expect(elements.validateElements()).toBe(true);
    });

    test('should return false when required elements are missing', () => {
        document.getElementById('customAlert').remove();
        const elements = new DOMElements();
        expect(elements.validateElements()).toBe(false);
    });
});

describe('AlertManager', () => {
    let alertManager;
    let elements;

    beforeEach(() => {
        setupMockDOM();
        elements = new DOMElements();
        alertManager = new AlertManager(elements);
    });

    test('should show success alert correctly', () => {
        alertManager.show('Success', 'Test message', true);
        
        expect(elements.alertIcon.textContent).toBe('✅');
        expect(elements.alertTitle.textContent).toBe('Success');
        expect(elements.alertMessage.textContent).toBe('Test message');
        expect(elements.alertButton.textContent).toBe('Awesome!');
        expect(elements.customAlert.classList.contains('show')).toBe(true);
        expect(alertManager.isVisible).toBe(true);
    });

    test('should show error alert correctly', () => {
        alertManager.show('Error', 'Test error', false);
        
        expect(elements.alertIcon.textContent).toBe('❌');
        expect(elements.alertTitle.textContent).toBe('Error');
        expect(elements.alertMessage.textContent).toBe('Test error');
        expect(elements.alertButton.textContent).toBe('Got it');
        expect(elements.customAlert.classList.contains('show')).toBe(true);
        expect(alertManager.isVisible).toBe(true);
    });

    test('should hide alert correctly', () => {
        alertManager.show('Test', 'Test', true);
        alertManager.hide();
        
        expect(elements.customAlert.classList.contains('show')).toBe(false);
        expect(alertManager.isVisible).toBe(false);
    });

    test('should setup event listeners correctly', () => {
        const clickSpy = jest.spyOn(alertManager, 'hide');
        alertManager.setupEventListeners();
        
        // Test button click
        elements.alertButton.click();
        expect(clickSpy).toHaveBeenCalled();
    });

    test('should close on backdrop click', () => {
        const hideSpy = jest.spyOn(alertManager, 'hide');
        alertManager.setupEventListeners();
        
        const clickEvent = new MouseEvent('click', {
            bubbles: true,
            target: elements.customAlert
        });
        
        elements.customAlert.dispatchEvent(clickEvent);
        expect(hideSpy).toHaveBeenCalled();
    });

    test('should close on Escape key', () => {
        const hideSpy = jest.spyOn(alertManager, 'hide');
        alertManager.isVisible = true;
        alertManager.setupEventListeners();
        
        const escapeEvent = new KeyboardEvent('keydown', { key: 'Escape' });
        document.dispatchEvent(escapeEvent);
        
        expect(hideSpy).toHaveBeenCalled();
    });
});

describe('FormManager', () => {
    let formManager;
    let elements;
    let alertManager;

    beforeEach(() => {
        setupMockDOM();
        elements = new DOMElements();
        alertManager = new AlertManager(elements);
        formManager = new FormManager(elements, alertManager);
        
        // Mock fetch
        global.fetch = jest.fn();
    });

    afterEach(() => {
        jest.resetAllMocks();
    });

    test('should validate form correctly with content', () => {
        elements.contentInput.value = 'Test message';
        expect(formManager.validateForm()).toBe(true);
    });

    test('should fail validation with empty content', () => {
        elements.contentInput.value = '';
        const showSpy = jest.spyOn(alertManager, 'show');
        
        expect(formManager.validateForm()).toBe(false);
        expect(showSpy).toHaveBeenCalledWith('Oops!', 'Please enter a message before sending.', false);
    });

    test('should get form data correctly', () => {
        elements.contentInput.value = '  Test message  ';
        elements.authorInput.value = '  Allan  ';
        
        const formData = formManager.getFormData();
        expect(formData.content).toBe('Test message');
        expect(formData.author).toBe('Allan');
    });

    test('should handle empty author correctly', () => {
        elements.contentInput.value = 'Test message';
        elements.authorInput.value = '';
        
        const formData = formManager.getFormData();
        expect(formData.content).toBe('Test message');
        expect(formData.author).toBe(null);
    });

    test('should clear form inputs', () => {
        elements.contentInput.value = 'Test message';
        elements.authorInput.value = 'Allan';
        
        formManager.clearForm();
        
        expect(elements.contentInput.value).toBe('');
        expect(elements.authorInput.value).toBe('');
    });

    test('should set button loading state correctly', () => {
        const button = document.createElement('button');
        button.textContent = 'Submit';
        
        formManager.setButtonState(button, true, 'Submit');
        
        expect(button.textContent).toBe('Sending...');
        expect(button.disabled).toBe(true);
        expect(formManager.isSubmitting).toBe(true);
    });

    test('should reset button state correctly', () => {
        const button = document.createElement('button');
        button.disabled = true;
        
        formManager.setButtonState(button, false, 'Submit');
        
        expect(button.textContent).toBe('Submit');
        expect(button.disabled).toBe(false);
        expect(formManager.isSubmitting).toBe(false);
    });

    test('should submit message successfully', async () => {
        const mockResponse = {
            json: jest.fn().resolve({ message: 'Success!' })
        };
        global.fetch.mockResolvedValue(mockResponse);
        
        const formData = { content: 'Test', author: 'Allan' };
        const response = await formManager.submitMessage(formData);
        
        expect(global.fetch).toHaveBeenCalledWith('/messages', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });
        
        expect(response).toBe(mockResponse);
    });

    test('should handle form submission successfully', async () => {
        const mockResponse = {
            json: jest.fn().resolve({ message: 'Message sent!' })
        };
        global.fetch.mockResolvedValue(mockResponse);
        
        elements.contentInput.value = 'Test message';
        elements.authorInput.value = 'Allan';
        
        const event = new Event('submit');
        const button = elements.messageForm.querySelector('button[type="submit"]');
        event.target = elements.messageForm;
        
        const showSpy = jest.spyOn(alertManager, 'show');
        
        await formManager.handleSubmit(event);
        
        expect(showSpy).toHaveBeenCalledWith('Success!', 'Message sent!', true);
        expect(elements.contentInput.value).toBe('');
        expect(elements.authorInput.value).toBe('');
    });

    test('should handle form submission error', async () => {
        global.fetch.mockRejectedValue(new Error('Network error'));
        
        elements.contentInput.value = 'Test message';
        
        const event = new Event('submit');
        event.target = elements.messageForm;
        
        const showSpy = jest.spyOn(alertManager, 'show');
        const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
        
        await formManager.handleSubmit(event);
        
        expect(showSpy).toHaveBeenCalledWith('Error', 'Something went wrong. Please try again.', false);
        expect(consoleSpy).toHaveBeenCalledWith('Form submission error:', expect.any(Error));
        
        consoleSpy.mockRestore();
    });

    test('should not submit if already submitting', async () => {
        formManager.isSubmitting = true;
        elements.contentInput.value = 'Test message';
        
        const event = new Event('submit');
        event.target = elements.messageForm;
        
        await formManager.handleSubmit(event);
        
        expect(global.fetch).not.toHaveBeenCalled();
    });
});

describe('PortfolioApp', () => {
    let app;

    beforeEach(() => {
        setupMockDOM();
        app = new PortfolioApp();
    });

    test('should initialize successfully with valid DOM', () => {
        const consoleSpy = jest.spyOn(console, 'log').mockImplementation();
        
        const result = app.init();
        
        expect(result).toBe(true);
        expect(app.alertManager).toBeTruthy();
        expect(app.formManager).toBeTruthy();
        expect(consoleSpy).toHaveBeenCalledWith('Portfolio application initialized successfully');
        
        consoleSpy.mockRestore();
    });

    test('should fail initialization with invalid DOM', () => {
        document.getElementById('customAlert').remove();
        
        const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
        
        const result = app.init();
        
        expect(result).toBe(false);
        expect(consoleErrorSpy).toHaveBeenCalledWith('Required DOM elements not found');
        
        consoleErrorSpy.mockRestore();
    });

    test('should setup managers correctly', () => {
        app.setupManagers();
        
        expect(app.alertManager).toBeInstanceOf(AlertManager);
        expect(app.formManager).toBeInstanceOf(FormManager);
    });
});

describe('AppConfig', () => {
    test('should have correct configuration values', () => {
        expect(AppConfig.ENDPOINTS.MESSAGES).toBe('/messages');
        expect(AppConfig.TIMEOUTS.FOCUS_DELAY).toBe(100);
        expect(AppConfig.ICONS.SUCCESS).toBe('✅');
        expect(AppConfig.ICONS.ERROR).toBe('❌');
        expect(AppConfig.BUTTON_TEXTS.SUBMIT).toBe('Post Message');
        expect(AppConfig.BUTTON_TEXTS.LOADING).toBe('Sending...');
        expect(AppConfig.BUTTON_TEXTS.SUCCESS_ALERT).toBe('Awesome!');
        expect(AppConfig.BUTTON_TEXTS.ERROR_ALERT).toBe('Got it');
    });
});