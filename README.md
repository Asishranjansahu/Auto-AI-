# AutoApply

AutoApply is an Android application that helps candidates organize and automate parts of their job search workflow. It provides a unified interface to manage platforms, discover opportunities, track applications, and use AI-assisted tools for resume and outreach tasks.

## Key Features

- **Dashboard-first workflow** for quick visibility into your job search activity
- **Platform connection management** for storing target platforms and auto-apply limits
- **Auto-apply controls** for single-job and batch apply flows
- **Application tracker** with status updates, filters, and interview notes
- **Profile and authentication dialogs** for account management
- **Resume and outreach support** including resume preview and recruiter email drafts
- **Daily notification reminders** with configurable reminder settings
- **Gemini-powered assistance** (when API key is configured)

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** ViewModel + Repository pattern
- **Local Storage:** Room
- **Async:** Kotlin Coroutines / Flows
- **Networking:** Retrofit + OkHttp + Moshi
- **Cloud/AI:** Firebase AI (Gemini), Firebase App Check
- **Build System:** Gradle Kotlin DSL

## Project Structure

```text
app/src/main/java/com/example
├── data/            # Database, repository, Gemini service
├── notifications/   # Reminder scheduling and receiver
└── ui/              # Screens, components, theme, viewmodel
```

## Getting Started

### Prerequisites

- Android Studio (latest stable recommended)
- JDK 11
- Android SDK (min SDK 24, target SDK 36)

### 1) Clone and open

Open the repository in Android Studio:

`/home/runner/work/Auto-AI-/Auto-AI-`

### 2) Configure environment variables

Copy `.env.example` values into `.env` and set:

- `GEMINI_API_KEY=your_real_key`

> Do not commit real secrets to source control.

### 3) Build and run

From Android Studio, sync Gradle and run the `app` module, or use CLI:

```bash
./gradlew assembleDebug
```

## Testing

Run unit and instrumentation test tasks with Gradle:

```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Notes

- `google-services.json` is optional in this project setup and missing-file behavior is set to warning.
- Some optional dependencies in `app/build.gradle.kts` are intentionally commented for future enablement.

## License

No license file is currently defined in this repository.
