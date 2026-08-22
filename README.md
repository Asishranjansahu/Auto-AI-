# AutoApply 🚀

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-3DDC84.svg?style=flat&logo=android)](https://android.com)
[![Gemini AI](https://img.shields.io/badge/AI-Gemini%202.5%20Flash-blueviolet.svg?style=flat&logo=google)](https://ai.google.dev)
[![Room Database](https://img.shields.io/badge/Storage-Room%20SQLite-orange.svg?style=flat)](https://developer.android.com/training/data-storage/room)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**AutoApply** is an autonomous AI-powered job application tracking and career automation suite for Android. It bridges major employment platforms (LinkedIn, Indeed, Glassdoor, Wellfound, Monster, ZipRecruiter) into a unified cockpit, automating tailored resume alignment, ATS match scoring, direct recruiter outreach, and real-time application lifecycle tracking.

---

## 🌟 Key Features

### 🤖 1. Autonomous AI Auto-Apply Bot
- **Multi-Platform Dispatch**: Simultaneously queues and executes automated job applications across connected platforms.
- **Smart Pacing Engine**: Rate-limits dispatches to mimic natural user behavior and safeguard platform accounts.
- **Live Execution Console**: Real-time streaming log displaying bot operations (e.g. matching keywords, extracting requirements, autofilling forms, and submitting).
- **Match-Score Filtering**: Only applies to roles meeting a customizable AI relevance threshold (e.g., ≥ 75% ATS match).

### 📄 2. ATS Resume Scanner & Preview
- **OCR Resume Parsing**: Ingests and extracts structured text, work history, skills, and contact data from candidate resumes.
- **Interactive Resume Preview**: Live modal viewer showing formatted experience, education, and ATS readiness breakdown.
- **ATS Compatibility Score**: In-depth breakdown evaluating keyword density, formatting integrity, and contact clarity.
- **1-Click Autofill Matrix**: Verifies all required fields (contact details, portfolio links, salary expectations) prior to submission.

### 📊 3. Centralized Application Tracker
- **Real-Time Kanban & List Views**: Organize applications across stages: *Applied*, *In Review*, *Interviewing*, *Offered*, and *Rejected*.
- **Filter & Search**: Instant multi-criteria filtering by platform, status, salary range, work mode (Remote/Hybrid/Onsite), and date.
- **Application Detail Inspector**: Detailed view featuring job descriptions, submitted resume versions, recruiter contacts, and status timeline.
- **Manual & Auto Logging**: Add external applications or let the bot record them automatically upon submission.

### 🌐 4. Platform Hub & Credential Sync
- **Supported Integrations**: LinkedIn, Indeed, Glassdoor, Wellfound (AngelList), Monster, ZipRecruiter.
- **Multi-Account State**: Connect/disconnect platform sessions with credentials stored securely in encrypted local storage.
- **Sync Diagnostics**: View live connection status, synced active roles, and last sync timestamp for each platform.

### 📧 5. AI Recruiter Cold Outreach & Email
- **AI Cover Letter & Email Generator**: Generates hyper-personalized cold outreach emails matching candidate skills to job specifications using Gemini 2.5.
- **Direct Mail Intent**: Previews and sends tailored emails directly through native Android mail apps or the in-app client.

### 🔐 6. Google Sign-In & Multi-Profile Switcher
- **1-Tap Google Sign-In**: Quick Google account authentication and profile synchronization.
- **Role Presets**: Switch instantly between *Experienced Senior Engineer* and *Fresher / New Graduate* profiles.
- **Offline-First Persistence**: Full local data persistence powered by Room SQLite Database.

---

## 🏗️ Tech Stack & Architecture

- **Architecture**: Modern MVVM (Model-View-ViewModel) + Clean Architecture + Repository Pattern
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3)
- **Language**: Kotlin 100% with Coroutines & StateFlow
- **AI Engine**: Google Gemini API (`gemini-2.5-flash`) via direct REST/Ktor integration
- **Local Persistence**: [Room Database](https://developer.android.com/training/data-storage/room) (SQLite) with Type Converters
- **Networking**: Ktor / OkHttp client with Kotlinx Serialization
- **Image Loading**: Coil 3 (Compose)
- **Theming**: Adaptive Dynamic M3 Color System + Custom Tech-Blue Design Palette

---

## 📂 Project Structure

```
├── app/
│   ├── src/main/
│   │   ├── java/com/example/
│   │   │   ├── data/
│   │   │   │   ├── gemini/           # Gemini API client & AI prompt orchestrators
│   │   │   │   ├── local/            # Room Database, Entities & DAOs
│   │   │   │   └── repository/       # Unified Job & Application Repository
│   │   │   ├── ui/
│   │   │   │   ├── screens/          # Compose screens & overlay dialogs
│   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   ├── AutoApplyScreen.kt
│   │   │   │   │   ├── ApplicationsTrackerScreen.kt
│   │   │   │   │   ├── PlatformsScreen.kt
│   │   │   │   │   ├── ProfileScreen.kt
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── ResumePreviewDialog.kt
│   │   │   │   │   └── AuthDialog.kt
│   │   │   │   ├── theme/            # Material 3 Color, Typography, and Shapes
│   │   │   │   └── viewmodel/        # JobViewModel (StateFlow & business logic)
│   │   │   └── MainActivity.kt       # Single-Activity Jetpack Compose host
│   │   └── res/                      # Vector drawables, strings, and icons
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml            # Version Catalog
├── .env.example                      # Template for Gemini API credentials
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2+) or newer
- JDK 17 or higher
- Android SDK API 34+
- (Optional) [Google Gemini API Key](https://aistudio.google.com/app/apikey) for live AI resume generation and email crafting.

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/auto-apply-android.git
   cd auto-apply-android
   ```

2. **Configure API Key** *(Optional)*:
   Create a `.env` file in the project root:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

3. **Open in Android Studio**:
   - Open Android Studio -> Select **Open** -> Choose the cloned project folder.
   - Let Gradle sync dependencies.

4. **Build & Run**:
   - Connect an Android device or start an emulator (API 26+).
   - Press **Run ▶** or execute via terminal:
     ```bash
     gradle :app:assembleDebug
     ```

---

## 📱 Build & Export APK

To generate an installable APK from the command line:

```bash
gradle :app:assembleRelease
```
The output APK will be located at:
`app/build/outputs/apk/release/app-release-unsigned.apk`

---

## 🔒 Permissions

The app requests the following standard permissions in `AndroidManifest.xml`:
- `android.permission.INTERNET`: For Gemini API calls, platform job fetching, and email dispatch.
- `android.permission.POST_NOTIFICATIONS`: For daily job application status reminders and bot alerts.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
