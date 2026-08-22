<div align="center">

# 🚀 AutoApply

### **Autonomous AI Career Intelligence & Software Engineering Job Automation Suite**

*An enterprise-grade, offline-first Android application orchestrating autonomous job matching, ATS resume optimization, rate-paced multi-platform submissions, and intelligent recruiter outreach.*

<br/>

**Lead Architect & Author: [ASISH RANJAN SAHU](mailto:asishranjansahu2003@gmail.com)**

<br/>

[![Author: ASISH RANJAN SAHU](https://img.shields.io/badge/Author-ASISH%20RANJAN%20SAHU-blue.svg?style=for-the-badge&logo=github)](mailto:asishranjansahu2003@gmail.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android SDK](https://img.shields.io/badge/Platform-Android%20API%2026%2B-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Google Gemini](https://img.shields.io/badge/AI-Gemini%202.5%20Flash-8E75FF.svg?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev)
[![Room Database](https://img.shields.io/badge/Storage-Room%20SQLite-F9AB00.svg?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![License: MIT](https://img.shields.io/badge/License-MIT-00C853.svg?style=for-the-badge)](LICENSE)

<br/>

[Overview](#-overview) • [Software Engineering Tracks](#-software-engineering-career-tracks) • [Core Capabilities](#-core-capabilities) • [System Architecture](#-system-architecture) • [Getting Started](#-getting-started) • [Author](#-author--lead-architect) • [License](#-license)

---

</div>

## 📖 Overview

**AutoApply** is an autonomous mobile software suite engineered to eliminate job search fatigue and streamline career advancement for modern software professionals. 

Built with **Kotlin 2.0**, **Jetpack Compose (Material Design 3)**, and **Google Gemini 2.5 Flash**, AutoApply runs a deterministic background application agent. It autonomously parses resumes, calculates multi-vector ATS relevance scores, synchronizes credentials across 6+ top employment portals, autofills complex job application questionnaires with human-like rate pacing, and logs full lifecycle statuses directly to an offline-first SQLite database.

```
┌─────────────────────────┐      ┌─────────────────────────┐      ┌─────────────────────────┐
│   Resume & Skills OCR   │ ───► │  Gemini 2.5 ATS Engine  │ ───► │  Autonomous Dispatcher  │
│ (Custom Domain Presets) │      │ (Keyword Matrix & Match)│      │  (Rate-Paced Bot Queue) │
└─────────────────────────┘      └─────────────────────────┘      └────────────┬────────────┘
                                                                               │
┌─────────────────────────┐      ┌─────────────────────────┐                   │
│ Real-Time Kanban Track  │ ◄─── │   Room SQLite Cache     │ ◄─────────────────┘
│ (Applied ➔ Offer Stage) │      │ (Offline-First Storage) │
└─────────────────────────┘      └─────────────────────────┘
```

---

## 🎯 Software Engineering Career Tracks

AutoApply provides **1-Click Engineering Role Presets**, instantly configuring resume heuristics, technical keywords, ATS matching vectors, and expected compensation:

| Role Preset | Target Titles | Core Tech Stack & Heuristics | Default Target Range |
| :--- | :--- | :--- | :--- |
| 🚀 **Senior Software Engineer** | Senior SWE, Staff Engineer, SDE II / III, Lead Engineer | Distributed Systems, Microservices, Python, Go, Kotlin, TypeScript, AWS, Docker, Kubernetes, CI/CD, Gemini AI | $160,000 – $220,000 |
| 💻 **Full-Stack Software Engineer** | Full Stack Engineer, Web Architect, Lead Frontend/Backend | React, Next.js, TypeScript, Node.js, Express, Kotlin, Spring Boot, PostgreSQL, MongoDB, GraphQL, WebSockets | $150,000 – $195,000 |
| ⚙️ **Backend & Cloud Systems** | Backend Developer, Systems Architect, Platform Engineer | Go, Java, Kotlin, Spring Boot, gRPC, Kafka, Redis, PostgreSQL, MySQL, Terraform, Event-Driven Architecture | $165,000 – $210,000 |
| 🧠 **AI / Machine Learning Engineer** | AI Engineer, ML Systems Engineer, Generative AI Architect | Python, PyTorch, TensorFlow, Google Gemini API, LangChain, LlamaIndex, Vector DBs, RAG, FastAPI, HuggingFace | $170,000 – $230,000 |
| 📱 **Mobile & Android Architect** | Android Engineer, Senior Mobile Dev, Mobile Tech Lead | Kotlin, Jetpack Compose, Coroutines, Flow, Room DB, Retrofit, MVVM/MVI, M3, Performance Profiling | $150,000 – $190,000 |
| 🎓 **Graduate Software Engineer** | Junior SWE, Associate Developer, Software Intern | Data Structures & Algorithms, OOP, Java, Kotlin, Python, JavaScript, SQL, REST APIs, Git, Unit Testing | $75,000 – $110,000 |

---

## 🌟 Core Capabilities

### 1. 🤖 Autonomous Auto-Apply Agent
- **Concurrent & Batch Dispatch**: Queue hundreds of qualified roles across multiple portals simultaneously.
- **Smart Human Pacing Engine**: Employs dynamic jitter delays and throttle intervals to safeguard accounts against anti-bot triggers.
- **Live Terminal Execution Log**: Streaming operational console providing transparent step-by-step insight into form parsing, ATS alignment, and confirmation receipts.
- **Strict Relevance Thresholds**: Configurable ATS match-score gating (e.g. `≥ 80% Match`) ensuring high-precision submissions.

### 2. 📄 ATS Resume Scanner & OCR Analyzer
- **Automated Text Extraction**: Ingests resume PDFs and parses experience, technical certifications, and project links.
- **Granular ATS Diagnostics**: Evaluates keyword density, formatting compliance, contact accessibility, and hard skill coverage.
- **Autofill Form Validator**: Automatically verifies candidate metadata (notice period, visa status, salary expectations, GitHub, LinkedIn).

### 3. 📊 Centralized Application Kanban
- **Real-Time Kanban & List Modes**: Track applications across the software engineering hiring funnel:
  $$\text{Applied} \longrightarrow \text{In Review} \longrightarrow \text{Technical Interview} \longrightarrow \text{Offered} \longrightarrow \text{Archived}$$
- **Multi-Vector Filtering**: Filter instantly by platform source, experience level, remote/hybrid status, and salary bracket.
- **Deep Application Inspector**: Inspect individual job descriptions, submitted resume versions, recruiter contacts, and activity timelines.

### 4. 🌐 Multi-Platform Credential Sync
- **Unified Portal Cockpit**: Centralized session manager supporting:
  - 🔗 **LinkedIn** (Easy Apply integration)
  - 💼 **Indeed** (Instant Apply sync)
  - 🏢 **Glassdoor** (Aggregated job matches)
  - 🚀 **Wellfound / AngelList** (Direct startup founder intros)
  - 👾 **Monster** & 🎯 **ZipRecruiter** (1-Click submissions)
  - 📋 **Greenhouse & Lever** (Direct enterprise ATS pipelines)

### 5. 📧 AI Recruiter Cold Outreach
- **Tailored Cover Letters & Cold Emails**: Employs Google Gemini 2.5 to synthesize context-aware outreach messages aligned with hiring managers' specific requirements.
- **1-Tap Direct Mail Intent**: Seamlessly opens drafts in native email clients (Gmail, Outlook) with pre-populated recipient addresses, custom subjects, and tailored pitches.

### 6. 🔐 1-Tap Google Sign-In & Offline Persistence
- **Rapid Google Authentication**: Instant 1-tap Google Sign-In with multi-account switching capabilities.
- **Zero Cloud Leakage**: Sensitive user credentials and application records remain cached safely in local Room SQLite storage.

---

## 🏗️ System Architecture

AutoApply adheres strictly to **Clean Architecture** and the **Android Jetpack MVVM (Model-View-ViewModel)** reactive pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                     Jetpack Compose UI                      │
│   (Dashboard, Auto-Apply Hub, Kanban Tracker, Profile)      │
└──────────────────────────────┬──────────────────────────────┘
                               │ Observes UI State (StateFlow)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    JobViewModel (MVVM)                      │
│   (Business Logic, Bot State Machine, Filtering, Coroutines)│
└──────────────────────────────┬──────────────────────────────┘
                               │ Dispatches Commands
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   JobRepository (Repository)                │
│    (Single Source of Truth, Query Logic, Type Converters)   │
└───────────────────┬─────────────────────────────────┬───────┘
                    │                                 │
                    ▼                                 ▼
┌──────────────────────────────────────┐ ┌────────────────────┐
│          Room SQLite Database        │ │  Gemini AI Engine  │
│ (Jobs, Applications, Logs, Profiles) │ │ (Ktor / REST API)  │
└──────────────────────────────────────┘ └────────────────────┘
```

### Technology Breakdown
- **Core Language**: Kotlin 2.0 (Coroutines, StateFlow, Serialization)
- **UI Framework**: Jetpack Compose with Material Design 3 (Dynamic Color)
- **AI Core**: Google Gemini 2.5 Flash via high-efficiency Ktor REST Client
- **Local Persistence**: Room SQLite with custom Type Converters
- **Image Pipeline**: Coil 3 for Compose
- **Architecture**: MVVM + Clean Architecture + Repository Pattern

---

## 📂 Project Structure

```
AutoApply/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/
│   │   │   ├── data/
│   │   │   │   ├── gemini/           # Gemini API client & AI prompt orchestrators
│   │   │   │   ├── local/            # Room Database, Entities (Jobs, Logs, Profiles) & DAOs
│   │   │   │   └── repository/       # Single-Source-of-Truth Repository
│   │   │   ├── ui/
│   │   │   │   ├── screens/          # Modular Jetpack Compose Screens
│   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   ├── AutoApplyScreen.kt
│   │   │   │   │   ├── ApplicationsTrackerScreen.kt
│   │   │   │   │   ├── PlatformsScreen.kt
│   │   │   │   │   ├── ProfileScreen.kt
│   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   ├── ResumePreviewDialog.kt
│   │   │   │   │   └── AuthDialog.kt
│   │   │   │   ├── theme/            # M3 Color, Typography, Elevation, Shapes
│   │   │   │   └── viewmodel/        # JobViewModel (Reactive StateFlow Engine)
│   │   │   └── MainActivity.kt       # Single-Activity Navigation Host
│   │   └── res/                      # Vector Drawables, Strings, Icons
│   └── build.gradle.kts              # App-level Gradle build configuration
├── gradle/
│   └── libs.versions.toml            # Centralized Version Catalog
├── .env.example                      # API Credentials Template
└── README.md                         # Comprehensive Documentation
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2+) or higher
- **JDK**: Java 17 or higher
- **Android Target**: Android 8.0 (API Level 26) or newer
- **Gemini API Key**: Free key from [Google AI Studio](https://aistudio.google.com/) *(optional for AI synthesis)*

---

### Step-by-Step Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/auto-apply-android.git
   cd auto-apply-android
   ```

2. **Configure API Secrets**:
   Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
   Add your Google Gemini API Key:
   ```env
   GEMINI_API_KEY="your_gemini_api_key_here"
   ```

3. **Open in Android Studio**:
   - Launch Android Studio ➔ **Open...** ➔ Select the project folder.
   - Let Gradle resolve dependencies and sync the catalog.

4. **Build & Execute**:
   - Press **Run ▶** or execute via terminal:
     ```bash
     gradle :app:installDebug
     ```

---

### 📦 Building Release APK

To generate a standalone release APK:

```bash
gradle :app:assembleRelease
```
The resulting package will be stored at:
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## 🔒 Security & Privacy

- **On-Device Encrypted Storage**: All credentials and application logs remain strictly within local SQLite storage on your device.
- **Zero Third-Party Telemetry**: Your resume data is never shared with third-party tracking or advertising networks.
- **Secure Secret Ingestion**: Secrets are passed at build time via `BuildConfig` and omitted from version control.

---

## 👨‍💻 Author & Lead Architect

<div align="center">

### **ASISH RANJAN SAHU**

*Lead Mobile Systems Architect & AI Engineer*

[![Email](https://img.shields.io/badge/Email-asishranjansahu2003%40gmail.com-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:asishranjansahu2003@gmail.com)
[![GitHub](https://img.shields.io/badge/GitHub-Profile-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/asishranjansahu2003)

Architected and developed with modern Android standards, reactive state streams, and autonomous AI automation.

</div>

---

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for complete details.

<div align="center">

<br/>

**Designed & Engineered with ❤️ by ASISH RANJAN SAHU**  
*Powered by Kotlin 2.0 • Jetpack Compose • Google Gemini 2.5*

[⭐ Star on GitHub](https://github.com/your-username/auto-apply-android) • [🐛 Report Bug](https://github.com/your-username/auto-apply-android/issues) • [💡 Request Feature](https://github.com/your-username/auto-apply-android/issues)

</div>
