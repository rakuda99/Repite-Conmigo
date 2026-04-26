# Repite Conmigo - Language Learning App

Repite Conmigo is a production-ready Android application designed for multilingual pronunciation improvement using a "Repeat After Me" method with AI-powered feedback.

## Features
- **Multilingual Support**: Practice Spanish or English with Arabic translations.
- **Interactive Learning**: Sentence-by-sentence practice with real-time feedback.
- **Smart Scoring**: Accuracy analysis using Levenshtein distance (0-100%).
- **Word Interaction**: Tap any word for translation and individual pronunciation.
- **Gamification**: XP and daily streak system to keep you motivated.
- **Premium Design**: Modern, Duolingo-inspired Jetpack Compose UI.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose (Material 3)
- **Database**: Room
- **Architecture**: MVVM + Repository Pattern
- **Analysis**: Android SpeechRecognizer + Custom Similarity Algorithm
- **Translation**: Google ML Kit On-Device Translation

## Setup Instructions
1. Open this project in **Android Studio (Hedgehog or later)**.
2. Ensure you have the latest **Android SDK** installed.
3. Build and Run the `app` module on an emulator or physical device.
4. (Optional) To use **OpenAI Whisper**, update `WhisperApi.kt` and provide your API key in the `LessonViewModel`.

## Project Structure
- `app/src/main/java/com/repite/conmigo`
  - `data/`: Room Database, DAO, and Repository.
  - `logic/`: TTS, Speech-to-Text, and Analysis logic.
  - `ui/`: Compose Screens, Components, and Themes.
