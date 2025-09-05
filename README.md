# Allan's Portfolio - Message Board

[![CI/CD Pipeline](https://github.com/all-an/javazure/actions/workflows/ci.yml/badge.svg)](https://github.com/all-an/javazure/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/all-an/javazure/branch/main/graph/badge.svg)](https://codecov.io/gh/all-an/javazure)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A simple portfolio message board built with Java Spring Boot and deployed on Azure App Service.

## 🚀 Technologies Used

- **Java 21** - Spring Boot 3.5
- **Azure App Service** - Cloud deployment
- **Firebase Firestore** - Database for storing messages
- **Thymeleaf** - Template engine
- **HTML/CSS/JavaScript** - Frontend with custom modal alerts
- **Maven** - Build tool
- **JaCoCo** - Code coverage reporting
- **Jest** - JavaScript testing framework
- **JUnit 5** - Java testing framework

## 🌐 Live Demo

Visit my portfolio: [http://all-an.space]

## 📝 Features

- Send messages with validation
- Responsive design
- Firebase integration for message storage
- Comprehensive test coverage (87% overall)
- Automated CI/CD pipeline
- Code quality monitoring

## 🛠️ Local Development

1. Clone the repository
2. Add Firebase credentials (see `FIREBASE_SETUP.md`)
3. Install dependencies: `npm install`
4. Run tests: `mvn clean test` (Java) or `npm test` (JavaScript)
5. Run application: `mvn spring-boot:run`
6. Visit `http://localhost:8080`

## 🧪 Testing

### Running Tests
```bash
# Java tests with coverage
mvn clean test

# JavaScript tests with coverage  
npm test -- --coverage

# View coverage reports
open target/site/jacoco/index.html  # Java coverage
open coverage/lcov-report/index.html  # JavaScript coverage
```

### Test Documentation
- **[Complete Test Documentation](TEST_DOCUMENTATION.md)** - Comprehensive guide to testing strategy, coverage metrics, and best practices
- **Current Coverage**: 87% instruction coverage, 73% branch coverage
- **Quality Gates**: 80% minimum line coverage per package

### CI/CD Pipeline
- **Automated testing** on every push and pull request
- **Coverage reporting** with Codecov integration
- **Build verification** for deployment readiness
- **Quality gates** prevent deployment of untested code

## 📊 Project Status

- ✅ **Build Status**: Passing
- ✅ **Test Coverage**: 87% overall
- ✅ **Code Quality**: Monitored with JaCoCo
- ✅ **CI/CD**: Automated with GitHub Actions

---

Made with ☕ by Allan