<div align="center">

# 🚀 AutoApply

### **Autonomous AI Job Application & Career Intelligence Suite for Android**

*Effortlessly orchestrate job discovery, ATS resume matching, autonomous application submission, and recruiter outreach across 6+ top employment platforms.*

<br/>

**Created & Maintained by [ASISH RANJAN SAHU](mailto:asishranjansahu2003@gmail.com)**

<br/>

[![Author: ASISH RANJAN SAHU](https://img.shields.io/badge/Author-ASISH%20RANJAN%20SAHU-blue.svg?style=for-the-badge&logo=github)](mailto:asishranjansahu2003@gmail.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-4285F4.svg?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android SDK](https://img.shields.io/badge/Android-API%2026%2B-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Google Gemini](https://img.shields.io/badge/AI-Gemini%202.5%20Flash-8E75FF.svg?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev)
[![Room Database](https://img.shields.io/badge/Storage-Room%20SQLite-F9AB00.svg?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![License: MIT](https://img.shields.io/badge/License-MIT-00C853.svg?style=for-the-badge)](LICENSE)

<br/>

[Key Features](#-key-features) • [Architecture](#-architecture--tech-stack) • [Installation](#-getting-started) • [Security & Privacy](#-security--privacy) • [Author](#-author--lead-developer) • [Roadmap](#-roadmap)

---

</div>

## 📖 Overview

**AutoApply** is an intelligent, offline-first native Android application designed to eliminate job search fatigue. Powered by **Google Gemini AI** and built entirely in **Jetpack Compose (Material 3)**, AutoApply acts as your personal autonomous agent. It crawls, ranks, tailors, autofills, and tracks applications across major platforms including **LinkedIn, Indeed, Glassdoor, Wellfound, Monster, and ZipRecruiter**.

```
[ Resume OCR & Profile ] ────► [ Gemini AI ATS Matcher ] ────► [ Autonomous Bot Dispatch ]
                                                                       │
[ Real-Time Tracking ] ◄─── [ Room Database Cache ] ◄──────────────────┘
```

---

## 📑 Table of Contents

- [Key Features](#-key-features)
  - [1. Autonomous AI Auto-Apply Engine](#1-autonomous-ai-auto-apply-engine)
  - [2. ATS Resume Scanner & Inspector](#2-ats-resume-scanner--inspector)
  - [3. Centralized Application Tracker (Kanban & List)](#3-centralized-application-tracker)
  - [4. Multi-Platform Credential Sync](#4-multi-platform-credential-sync)
  - [5. AI Recruiter Cold Outreach](#5-ai-recruiter-cold-outreach)
  - [6. Account Management & 1-Tap Google Sign-In](#6-account-management--1-tap-google-sign-in)
- [Architecture & Tech Stack](#-architecture--tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Build and Run](#build-and-run)
- [Permissions](#-permissions)
- [Security & Privacy](#-security--privacy)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Key Features

### 1. Autonomous AI Auto-Apply Engine
- **Multi-Platform Batching**: Run concurrent or sequential application dispatches across all connected job boards.
- **Natural Pacing Safeguards**: Incorporates intelligent throttle intervals (rate-limiting) to mimic human interactions and safeguard account health.
- **Live Terminal Execution Log**: Streaming operational console with step-by-step visibility into keyword matching, question parsing, and submission states.
- **Relevance Gating**: Set custom ATS score thresholds (e.g. `≥ 80% Match`) to ensure high-quality, targeted submissions.

### 2. ATS Resume Scanner & Inspector
- **OCR Text Extraction**: Parses skills, experience, contact metadata, and technical certifications directly from uploaded resumes.
- **Interactive Resume Modal**: Live candidate preview with instant preset switching (*Senior Mobile Engineer* vs. *Fresher / New Grad*).
- **ATS Match Matrix**: Granular diagnostic scores evaluating keyword saturation, layout readability, and contact clarity.
- **1-Click Autofill Verification**: Audits all required form data (e.g., notice period, expected compensation, portfolio links) prior to queueing.

### 3. Centralized Application Tracker
- **Dynamic Kanban & Tabular Views**: Organize applications across 5 distinct stages:
  - 📥 `Applied` → 🔍 `In Review` → 💬 `Interviewing` → 🏆 `Offered` → 📁 `Archived/Rejected`
- **Multi-Vector Search & Filters**: Filter dynamically by platform, compensation brackets, date ranges, and work arrangement (*Remote*, *Hybrid*, *Onsite*).
- **Deep Application Dossier**: View comprehensive job descriptions, submitted resume versions, recruiter contacts, and activity timelines.

### 4. Multi-Platform Credential Sync
- **Unified Platform Cockpit**: Centralized hub managing connections for:
  - 🔗 **LinkedIn** • 💼 **Indeed** • 🏢 **Glassdoor** • 🚀 **Wellfound (AngelList)** • 👾 **Monster** • 🎯 **ZipRecruiter**
- **Live Diagnostic Indicators**: Real-time heartbeat checks, active sync status, and synchronized job role counters.

### 5. AI Recruiter Cold Outreach
- **Context-Aware Email Drafter**: Leverages Google Gemini to synthesize bespoke cold outreach emails tailored to hiring managers and job descriptions.
- **Native Android Email Dispatch**: One-tap export to your device's native email client (Gmail, Outlook) with pre-filled subject lines, recruiter addresses, and customized cover letters.

### 6. Account Management & 1-Tap Google Sign-In
- **Fast Google Authentication**: Seamless 1-tap Google Sign-In modal supporting multiple accounts and instant session provisioning.
- **Multi-Profile Switching**: Switch effortlessly between distinct career personas without losing submitted histories.
- **Offline-First Persistence**: Powered by SQLite Room Database with reactive Kotlin StateFlow pipelines.

---

## 🏗️ Architecture & Tech Stack

AutoApply follows **Clean Architecture** principles combined with the official **Android MVVM (Model-View-ViewModel)** pattern.

```
┌─────────────────────────────────────────────────────────────┐
│                     Jetpack Compose UI                      │
│   (Screens, Components, Material Design 3, Color Tokens)     │
└──────────────────────────────┬──────────────────────────────┘
                               │ Observes UI State (StateFlow)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    JobViewModel (MVVM)                      │
│   (Business Logic, Bot State Machine, Filtering, Coroutines)│
└──────────────────────────────┬──────────────────────────────┘
                               │ Calls
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   JobRepository (Repository)                │
│    (Single Source of Truth, Data Routing, Type Converters)  │
└───────────────────┬─────────────────────────────────┬───────┘
                    │                                 │
                    ▼                                 ▼
┌──────────────────────────────────────┐ ┌────────────────────┐
│          Room SQLite Database        │ │  Gemini AI Engine  │
│ (Jobs, Applications, Logs, Profiles) │ │ (Ktor / REST API)  │
└──────────────────────────────────────┘ └────────────────────┘
```

### Core Technologies
| Category | Technology |
| :--- | :--- |
| **Language** | [Kotlin 2.0+](https://kotlinlang.org/) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3) |
| **Asynchronous Engine** | Kotlin Coroutines & Flow (`StateFlow`, `SharedFlow`) |
| **Local Database** | [Room Database](https://developer.android.com/training/data-storage/room) (SQLite) + Type Converters |
| **AI Integration** | [Google Gemini 2.5 Flash](https://ai.google.dev/) via direct Ktor REST Client |
| **Networking & Serialization** | Ktor Client, OkHttp, Kotlinx Serialization |
| **Image Loading** | [Coil 3](https://coil-kt.github.io/coil/) (Compose) |
| **Dependency Injection** | Constructor Injection & ViewModel Provider Factory |

---

## 📂 Project Structure

```
AutoApply/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/
│   │   │   ├── data/
│   │   │   │   ├── gemini/           # Gemini API client & AI prompt builders
│   │   │   │   ├── local/            # Room DB, Entity models, and DAOs
│   │   │   │   └── repository/       # Unified Single-Source-of-Truth Repository
│   │   │   ├── ui/
│   │   │   │   ├── screens/          # Compose Screen composables & dialogs
│   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   ├── AutoApplyScreen.kt
│   │   │   │   │   ├── ApplicationsTrackerScreen.kt
│   │   │   │   │   ├── PlatformsScreen.kt
│   │   │   │   │   ├── ProfileScreen.kt
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── ResumePreviewDialog.kt
│   │   │   │   │   └── AuthDialog.kt
│   │   │   │   ├── theme/            # M3 ColorScheme, Typography, Shapes & Tokens
│   │   │   │   └── viewmodel/        # JobViewModel and state orchestrators
│   │   │   └── MainActivity.kt       # Single-Activity Navigation Host
│   │   └── res/                      # Vector drawables, mipmaps, strings, colors
│   └── build.gradle.kts              # App-level Gradle build script
├── gradle/
│   └── libs.versions.toml            # Centralized Version Catalog
├── .env.example                      # Environment variables template
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2+) or higher
- **JDK**: Java 17 or higher
- **Android Device / Emulator**: Running Android 8.0 (API Level 26) or above
- **Gemini API Key**: Obtain a free key from [Google AI Studio](https://aistudio.google.com/) *(optional for AI features)*

---

### Installation Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/auto-apply-android.git
   cd auto-apply-android
   ```

2. **Configure API Secrets**:
   Copy the example environment file:
   ```bash
   cp .env.example .env
   ```
   Add your Google Gemini API Key inside `.env`:
   ```env
   GEMINI_API_KEY="your_actual_gemini_api_key_here"
   ```

3. **Open and Sync in Android Studio**:
   - Open Android Studio, click **File** ➔ **Open...**, and select the project root directory.
   - Allow Gradle to download dependencies and sync the Version Catalog (`libs.versions.toml`).

4. **Build and Run**:
   - Select your target device or emulator in Android Studio.
   - Click the green **Run (▶)** button or execute via CLI:
     ```bash
     gradle :app:installDebug
     ```

---

### 📦 Release APK Generation

To generate an optimized release APK:

```bash
gradle :app:assembleRelease
```
The generated APK artifact will be output to:
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## 🔒 Permissions & Security

| Permission | Purpose |
| :--- | :--- |
| `android.permission.INTERNET` | Required for Gemini AI prompt processing, live platform sync, and job fetching. |
| `android.permission.POST_NOTIFICATIONS` | Required on Android 13+ (API 33+) to dispatch background application status notifications. |

### Security Architecture
- **On-Device Storage**: Credentials and application histories are persisted securely in local Room SQLite storage.
- **Zero Third-Party Telemetry**: Your resumes and contact details are processed strictly between your device and direct API endpoints.
- **Secret Isolation**: Secrets and API keys are injected at build-time via Gradle `BuildConfig` and excluded from source control.

---

## 🗺️ Roadmap

- [x] Autonomous Auto-Apply State Machine with human-like rate pacing.
- [x] Multi-Platform Hub (LinkedIn, Indeed, Wellfound, Glassdoor, Monster, ZipRecruiter).
- [x] Gemini 2.5 Flash AI cover letter & cold outreach generator.
- [x] Interactive ATS Resume Viewer with granular keyword score diagnostics.
- [x] 1-Tap Google Sign-In & Multi-Profile Switcher.
- [ ] Direct LinkedIn & Indeed OAuth2 Cloud Synchronization.
- [ ] Export Applications Tracker to CSV / Google Sheets.
- [ ] Push Notification Webhook for incoming recruiter responses.

---

## 🤝 Contributing

Contributions are warmly welcomed! If you'd like to improve AutoApply:

1. **Fork the Repository**
2. **Create a Feature Branch**:
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your changes**:
   ```bash
   git commit -m "Add AmazingFeature"
   ```
4. **Push to the branch**:
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

---

## 👨‍💻 Author & Lead Developer

<div align="center">

### **ASISH RANJAN SAHU**

*Lead Mobile Systems Architect & AI Engineer*

[![Email](https://img.shields.io/badge/Email-asishranjansahu2003%40gmail.com-D14836?style=flat&logo=gmail&logoColor=white)](mailto:asishranjansahu2003@gmail.com)
[![GitHub](https://img.shields.io/badge/GitHub-Profile-181717?style=flat&logo=github&logoColor=white)](https://github.com/)

Designed and built with modern Android engineering standards, reactive unidirectional architecture, and intelligent automated agents.

</div>

---

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

<div align="center">

<br/>

**Designed & Developed with ❤️ by ASISH RANJAN SAHU**  
*Built using Kotlin, Jetpack Compose

[⭐ Star on GitHub](https://github.com/your-username/auto-apply-android) • [🐛 Report Bug](https://github.com/your-username/auto-apply-android/issues) • [💡 Request Feature](https://github.com/your-username/auto-apply-android/issues)

</div>
