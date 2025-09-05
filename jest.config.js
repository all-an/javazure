module.exports = {
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['<rootDir>/src/test/javascript/setup.js'],
  testMatch: ['<rootDir>/src/test/javascript/**/*.test.js'],
  collectCoverageFrom: [
    'src/main/resources/static/js/**/*.js'
  ],
  coverageDirectory: 'coverage',
  coverageReporters: ['text', 'lcov', 'html'],
  moduleFileExtensions: ['js', 'json'],
  verbose: true,
  collectCoverage: false
};