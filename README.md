# Word PDF Parser - Clean Architecture

This Android application follows Clean Architecture principles to parse PDF files and extract words for translation.

## Architecture Overview

The application is structured using Clean Architecture with the following layers:

### 1. Domain Layer (`domain/`)
- **Entities**: Core business objects (`Word`, `Filename`)
- **Repository Interfaces**: Define contracts for data operations
- **Use Cases**: Business logic operations
  - `GetAllFilenamesUseCase`
  - `GetWordsForFilenameUseCase`
  - `InsertWordsUseCase`
  - `DeleteAllWordsUseCase`

### 2. Data Layer (`data/`)
- **Local Entities**: Room database entities (`WordEntity`, `FilenameEntity`)
- **DAO**: Data Access Objects for database operations
- **Repository Implementation**: Concrete implementation of repository interfaces
- **Mappers**: Convert between domain and data entities

### 3. Presentation Layer (`presentation/`)
- **ViewModels**: Handle UI state and business logic
  - `MainViewModel`: Manages main screen state
  - `WordsViewModel`: Manages words list state
- **Screens**: Compose UI components
  - `MainScreen`: Main screen with PDF selection
  - `WordsScreen`: Displays words from selected PDF
  - `WordDetailScreen`: Shows word translation

### 4. Dependency Injection (`di/`)
- **AppModule**: Provides dependencies using Hilt
- **WordApplication**: Application class with Hilt setup

## Key Features

1. **PDF Parsing**: Extract words from PDF files
2. **Word Storage**: Store words in local Room database
3. **Translation**: Translate words using ML Kit
4. **Clean Architecture**: Separation of concerns with proper layering
5. **Dependency Injection**: Using Hilt for dependency management

## Dependencies

- **Jetpack Compose**: Modern UI toolkit
- **Room**: Local database
- **Hilt**: Dependency injection
- **Navigation Compose**: Navigation between screens
- **ML Kit**: Translation services
- **PDFBox**: PDF parsing

## Project Structure

```
app/src/main/java/com/shan/word/
├── domain/
│   ├── entity/
│   │   ├── Word.kt
│   │   └── Filename.kt
│   ├── repository/
│   │   └── WordRepository.kt
│   └── usecase/
│       ├── GetAllFilenamesUseCase.kt
│       ├── GetWordsForFilenameUseCase.kt
│       ├── InsertWordsUseCase.kt
│       └── DeleteAllWordsUseCase.kt
├── data/
│   ├── local/
│   │   ├── entity/
│   │   │   ├── WordEntity.kt
│   │   │   └── FilenameEntity.kt
│   │   ├── dao/
│   │   │   └── WordDao.kt
│   │   └── WordDatabase.kt
│   ├── mapper/
│   │   └── WordMapper.kt
│   └── repository/
│       └── WordRepositoryImpl.kt
├── presentation/
│   ├── viewmodel/
│   │   ├── MainViewModel.kt
│   │   └── WordsViewModel.kt
│   └── screen/
│       ├── MainScreen.kt
│       ├── WordsScreen.kt
│       ├── WordDetailScreen.kt
│       └── Preview.kt
├── di/
│   └── AppModule.kt
├── ui/theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
├── MainActivity.kt
└── WordApplication.kt
```

## Benefits of Clean Architecture

1. **Separation of Concerns**: Each layer has a specific responsibility
2. **Testability**: Easy to unit test business logic
3. **Maintainability**: Clear structure makes code easier to maintain
4. **Scalability**: Easy to add new features or modify existing ones
5. **Dependency Inversion**: High-level modules don't depend on low-level modules

## Usage

1. Launch the app
2. Tap "Select PDF" to choose a PDF file
3. Wait for parsing to complete
4. Select a file from the dropdown to view extracted words
5. Tap on any word to see its translation

## Building the Project

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run the project

The app requires Android API level 34+ and uses modern Android development practices with Jetpack Compose and Clean Architecture. 