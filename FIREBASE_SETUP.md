# Firebase Setup Instructions

## Getting Started

This application uses Firebase Firestore for storing messages. The Firebase credentials are not included in the repository for security reasons.

## Local Development Setup

### Option 1: Using Firebase Service Account Key (Recommended for Development)

1. **Get Firebase Service Account Key:**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or select your existing project
   - Go to Project Settings → Service Accounts
   - Click "Generate new private key"
   - Download the JSON file

2. **Add Credentials to Your Local Project:**
   - Rename the downloaded file to `firebase-credentials.json`
   - Place it in `src/main/resources/firebase-credentials.json`
   - Update `application.properties`:
     ```properties
     firebase.credentials-path=firebase-credentials.json
     ```

### Option 2: Mock Mode (No Firebase Required)

For local development without Firebase:
- Leave `firebase.credentials-path` empty in `application.properties`
- The app will run in mock mode and log messages to console
- Perfect for testing the UI without setting up Firebase

## Production Deployment (Azure App Service)

### Environment Variables

Set these in Azure App Service Configuration:

```
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CREDENTIALS_PATH=firebase-credentials.json
```

### Upload Credentials File

1. Upload `firebase-credentials.json` to your App Service using:
   - Kudu console (`https://your-app.scm.azurewebsites.net`)
   - Azure CLI
   - Or include in your deployment package

## Configuration

The app works in this priority order:

1. **File-based credentials**: If `firebase-credentials.json` exists at the specified path
2. **Mock mode**: Falls back to console logging for development

## Security Notes

- **NEVER commit Firebase credentials to Git**
- Add `firebase-credentials.json` to `.gitignore`
- Use environment variables in production
- Enable Firebase security rules
- Rotate credentials regularly

## Firebase Project Setup

1. Create a new Firebase project at [console.firebase.google.com](https://console.firebase.google.com/)
2. Enable Firestore Database
3. Set up security rules (for public write access to messages):
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /messages/{document} {
         allow write: if true;  // Allow anyone to write messages
         allow read: if false;  // Don't allow reading (portfolio owner only)
       }
     }
   }
   ```
4. Generate service account key as described above