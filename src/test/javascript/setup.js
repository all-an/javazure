/**
 * Jest setup file for DOM testing
 * Configures the testing environment for browser-like functionality
 */

// Import jest-dom matchers
require('@testing-library/jest-dom');

// Mock localStorage
const localStorageMock = {
  getItem: jest.fn(),
  setItem: jest.fn(),
  removeItem: jest.fn(),
  clear: jest.fn(),
};
global.localStorage = localStorageMock;

// Mock fetch API
global.fetch = jest.fn();

// Mock alert function
global.alert = jest.fn();