package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_postings")
data class JobPostingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val company: String,
    val source: String, // e.g. "LinkedIn", "Indeed", "Glassdoor", "Wellfound", "ZipRecruiter", "Greenhouse", "Lever", "Workday"
    val status: String = "DISCOVERED", // DISCOVERED, SAVED, APPLYING, APPLIED, SCREENING, INTERVIEW, OFFER, REJECTED, ARCHIVED
    val location: String = "Remote",
    val salaryRange: String = "",
    val jobType: String = "Full-time", // Full-time, Remote, Hybrid, Contract, Internship
    val experienceLevel: String = "Fresher / Entry-Level",
    val matchScore: Int = 85,
    val description: String = "",
    val requirements: String = "",
    val jobUrl: String = "",
    val postedTimestamp: Long = System.currentTimeMillis(),
    val appliedTimestamp: Long? = null, // Application timestamp
    val lastUpdatedTimestamp: Long = System.currentTimeMillis(),
    val isAutoApplied: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "job_platforms")
data class JobPlatformEntity(
    @PrimaryKey val id: String, // e.g. "linkedin", "indeed", "glassdoor", "wellfound", "ziprecruiter", "greenhouse", "lever", "workday"
    val name: String,
    val isConnected: Boolean = false,
    val accountEmail: String = "",
    val lastSyncedTime: Long = 0L,
    val dailyApplyLimit: Int = 25,
    val applicationsToday: Int = 0,
    val statusMessage: String = "Ready to connect",
    val autoApplyEnabled: Boolean = true,
    val jobCountAvailable: Int = 0
)

@Entity(tableName = "job_applications")
data class JobApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val jobTitle: String,
    val company: String,
    val platformId: String,
    val location: String,
    val salaryRange: String,
    val jobType: String = "Full-time", // Full-time, Remote, Hybrid, Contract
    val matchScore: Int = 85, // 0 - 100
    val status: String = "APPLIED", // SAVED, AUTO_APPLYING, APPLIED, SCREENING, INTERVIEW, OFFER, REJECTED
    val appliedDate: Long = System.currentTimeMillis(),
    val lastUpdatedDate: Long = System.currentTimeMillis(),
    val tailoredCoverLetter: String = "",
    val recruiterName: String = "",
    val recruiterEmail: String = "",
    val interviewDate: Long? = null,
    val notes: String = "",
    val jobUrl: String = "",
    val jobDescription: String = "",
    val isAutoApplied: Boolean = true,
    val screeningAnswers: String = "" // Question: Answer pairs formatted
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Alex Morgan",
    val email: String = "alex.morgan.dev@gmail.com",
    val phone: String = "+1 (555) 382-9012",
    val headline: String = "Senior Android / Mobile Engineer (Kotlin, Compose, Jetpack)",
    val targetRoles: String = "Android Engineer, Senior Kotlin Developer, Mobile Tech Lead, Staff Android Architect",
    val targetLocations: String = "Remote (US/Global), San Francisco, CA, New York, NY, Austin, TX",
    val minSalary: Int = 145000,
    val yearsOfExperience: Int = 6,
    val skills: String = "Kotlin, Jetpack Compose, Coroutines, Flow, Room, Retrofit, MVVM, CI/CD, Unit Testing, M3, Performance Optimization",
    val bioSummary: String = "Passionate mobile engineer with 6+ years specializing in high-performance Android apps, declarative UI architectures, and scalable cloud client integrations.",
    val portfolioUrl: String = "https://alexmorgan.dev",
    val githubUrl: String = "https://github.com/alexmorgandev",
    val linkedinUrl: String = "https://linkedin.com/in/alexmorgan-dev",
    val workAuthorization: String = "US Citizen (No sponsorship required)",
    val noticePeriod: String = "2 Weeks",
    val resumeFileName: String = "Alex_Morgan_Android_Resume.pdf",
    val resumeFileSize: String = "245 KB",
    val resumeUploadedDate: Long = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 2,
    val resumeRawText: String = "Alex Morgan | Senior Android Engineer\nSkills: Kotlin, Compose, Coroutines, Flow, Room, Retrofit, MVVM, CI/CD\nExperience: 6+ years building mobile applications with 10M+ downloads.",
    val resumeAtsScore: Int = 94,
    val isLoggedIn: Boolean = true,
    val accountType: String = "EXPERIENCED" // "EXPERIENCED", "FRESHER"
)

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val headline: String,
    val targetRoles: String,
    val passwordHash: String = "password123",
    val isCurrent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val accountType: String = "EXPERIENCED"
)

@Entity(tableName = "sent_resume_emails")
data class SentResumeEmailEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val recipientEmail: String,
    val recipientName: String = "",
    val company: String,
    val jobTitle: String,
    val subject: String,
    val body: String,
    val resumeFileName: String = "Resume.pdf",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SENT" // "SENT", "OPENED_IN_MAIL_APP", "DRAFT"
)

@Entity(tableName = "automation_logs")
data class AutomationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val platformId: String,
    val company: String,
    val jobTitle: String,
    val action: String, // "MATCHED", "TAILORED", "SUBMITTED", "INTERVIEW_DETECTED", "SKIPPED"
    val details: String,
    val matchPercentage: Int = 0
)

@Entity(tableName = "auto_apply_config")
data class AutoApplyConfigEntity(
    @PrimaryKey val id: Int = 1,
    val isAutoApplyActive: Boolean = true,
    val minMatchScore: Int = 80,
    val maxDailyApplications: Int = 30,
    val applySpeed: String = "SMART_BATCH", // INSTANT, SMART_BATCH, SCHEDULED
    val autoTailorCoverLetter: Boolean = true,
    val autoAnswerScreening: Boolean = true,
    val onlyEasyApply: Boolean = true,
    val notifyOnSubmission: Boolean = true,
    val filterOutKeywords: String = "Unpaid, Clearance Required, Staffing Agency"
)

@Entity(tableName = "notification_reminder_config")
data class NotificationReminderConfigEntity(
    @PrimaryKey val id: Int = 1,
    val isEnabled: Boolean = true,
    val reminderHour: Int = 9, // 24-hr format: 9 = 9:00 AM
    val reminderMinute: Int = 0,
    val includeApplicationStats: Boolean = true,
    val notifyOnInterviewAlerts: Boolean = true,
    val notifyOnJobMatches: Boolean = true,
    val customTitle: String = "🎯 Daily Job Application Digest",
    val customMessage: String = "Check your application tracker for new interview requests and recruiter updates!",
    val lastNotifiedTimestamp: Long = 0L,
    val lastNotifiedMessage: String = ""
)

