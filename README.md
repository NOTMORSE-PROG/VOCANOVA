# VocaNova

A modern Android vocabulary learning application built with Kotlin and Jetpack Compose. VocaNova makes learning new words fun and engaging through interactive features, games, and a gamification system.

## Features

### Core Learning
- **Daily Word** - Learn a new word every day with definitions, part of speech, and examples
- **Lessons** - Structured vocabulary lessons with video content
- **Quizzes** - Test your knowledge with various quiz formats
- **Saved Words** - Review and manage words you want to remember

### Games
- **Flying Words** - Catch the correct words as they fly across the screen
- **Green Light Red Light** - A vocabulary twist on the classic game
- **Swipe Game** - Swipe to match words with their meanings

### Gamification
- **Achievements** - Unlock achievements as you progress
- **In-App Currency** - Earn currency by completing activities
- **Shop** - Spend your earned currency on power-ups and rewards

### User Features
- **User Authentication** - Sign up and login with Firebase
- **Profile** - Track your progress and stats
- **About** - Learn more about the app

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt (Dagger)
- **Backend**: Firebase (Authentication & Firestore)
- **Media**: Media3 ExoPlayer for video content
- **Navigation**: Jetpack Navigation Compose
- **Async**: Kotlin Coroutines & Flow

## Requirements

- Android SDK 31+ (Android 12)
- JDK 17
- Android Studio Hedgehog or newer

## Project Structure

```
app/src/main/java/com/example/vocanova/
├── data/
│   ├── model/          # Data classes (User, Word, Quiz, Achievement, etc.)
│   └── repository/     # Data repositories (Auth, Quiz, Word, etc.)
├── di/                 # Dependency injection modules
├── ui/
│   ├── components/     # Reusable UI components
│   ├── screens/        # Composable screens
│   ├── theme/          # App theme (colors, typography)
│   └── viewmodels/     # ViewModels for each screen
├── utils/              # Utility classes (Audio, Video, Game utilities)
├── MainActivity.kt     # Main entry point
└── VocaNovaApplication.kt
```

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/NOTMORSE-PROG/VOCANOVA.git
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Set up Firebase:
   - The `google-services.json` file is included for development
   - For production, replace with your own Firebase configuration

5. Build and run on an Android device or emulator (API 31+)

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## License

This project is for educational purposes.
