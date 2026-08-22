package com.example.data.repository

import com.example.data.gemini.GeminiJobService
import com.example.data.local.AppDatabase
import com.example.data.local.AutoApplyConfigEntity
import com.example.data.local.AutomationLogEntity
import com.example.data.local.JobApplicationEntity
import com.example.data.local.JobPlatformEntity
import com.example.data.local.JobPostingEntity
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class JobRepository(
    private val database: AppDatabase,
    private val geminiService: GeminiJobService = GeminiJobService()
) {
    private val postingDao = database.jobPostingDao()
    private val platformDao = database.jobPlatformDao()
    private val applicationDao = database.jobApplicationDao()
    private val userProfileDao = database.userProfileDao()
    private val automationLogDao = database.automationLogDao()
    private val configDao = database.autoApplyConfigDao()
    private val userAccountDao = database.userAccountDao()
    private val sentResumeEmailDao = database.sentResumeEmailDao()
    private val notificationReminderDao = database.notificationReminderConfigDao()

    val allJobPostings: Flow<List<JobPostingEntity>> = postingDao.getAllJobPostings()
    val appliedJobPostings: Flow<List<JobPostingEntity>> = postingDao.getAppliedJobPostings()
    val allPlatforms: Flow<List<JobPlatformEntity>> = platformDao.getAllPlatforms()
    val connectedPlatforms: Flow<List<JobPlatformEntity>> = platformDao.getConnectedPlatforms()
    val allApplications: Flow<List<JobApplicationEntity>> = applicationDao.getAllApplications()
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()
    val automationLogs: Flow<List<AutomationLogEntity>> = automationLogDao.getRecentLogs()
    val autoApplyConfig: Flow<AutoApplyConfigEntity?> = configDao.getConfig()
    val totalApplicationsCount: Flow<Int> = applicationDao.getTotalApplicationsCount()
    val allUserAccounts: Flow<List<com.example.data.local.UserAccountEntity>> = userAccountDao.getAllAccounts()
    val allSentResumeEmails: Flow<List<com.example.data.local.SentResumeEmailEntity>> = sentResumeEmailDao.getAllSentEmails()
    val notificationReminderConfig: Flow<com.example.data.local.NotificationReminderConfigEntity?> = notificationReminderDao.getConfig()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentProfile = userProfileDao.getUserProfileSync()
        if (currentProfile == null) {
            userProfileDao.insertOrUpdateProfile(UserProfileEntity())
        }

        val currentConfig = configDao.getConfigSync()
        if (currentConfig == null) {
            configDao.insertOrUpdateConfig(AutoApplyConfigEntity())
        }

        val currentNotificationConfig = notificationReminderDao.getConfigSync()
        if (currentNotificationConfig == null) {
            notificationReminderDao.insertOrUpdateConfig(com.example.data.local.NotificationReminderConfigEntity())
        }

        val accounts = userAccountDao.getAllAccounts().first()
        if (accounts.isEmpty()) {
            val defaultAccounts = listOf(
                com.example.data.local.UserAccountEntity(
                    email = "alex.morgan.dev@gmail.com",
                    fullName = "Alex Morgan",
                    headline = "Senior Android / Mobile Engineer (Kotlin, Compose, Jetpack)",
                    targetRoles = "Android Engineer, Senior Kotlin Developer, Mobile Tech Lead",
                    isCurrent = true,
                    accountType = "EXPERIENCED"
                ),
                com.example.data.local.UserAccountEntity(
                    email = "maya.chen.cs@gmail.com",
                    fullName = "Maya Chen",
                    headline = "Aspiring Android Developer | CS Graduate 2026",
                    targetRoles = "Junior Android Developer, Associate Mobile Software Engineer, Mobile Intern",
                    isCurrent = false,
                    accountType = "FRESHER"
                )
            )
            defaultAccounts.forEach { userAccountDao.insertAccount(it) }
        }

        val platforms = platformDao.getAllPlatforms().first()
        if (platforms.isEmpty()) {
            val initialPlatforms = listOf(
                JobPlatformEntity("linkedin", "LinkedIn", isConnected = true, accountEmail = "alex.morgan.dev@gmail.com", lastSyncedTime = System.currentTimeMillis() - 1000 * 60 * 12, dailyApplyLimit = 35, applicationsToday = 18, statusMessage = "Syncing Easy-Apply active", jobCountAvailable = 142),
                JobPlatformEntity("indeed", "Indeed", isConnected = true, accountEmail = "alex.morgan.dev@gmail.com", lastSyncedTime = System.currentTimeMillis() - 1000 * 60 * 25, dailyApplyLimit = 30, applicationsToday = 12, statusMessage = "Instant-apply webhook ready", jobCountAvailable = 89),
                JobPlatformEntity("glassdoor", "Glassdoor", isConnected = true, accountEmail = "alex.morgan.dev@gmail.com", lastSyncedTime = System.currentTimeMillis() - 1000 * 60 * 45, dailyApplyLimit = 20, applicationsToday = 6, statusMessage = "Aggregated matches synced", jobCountAvailable = 45),
                JobPlatformEntity("wellfound", "Wellfound (AngelList)", isConnected = true, accountEmail = "alex.morgan.dev@gmail.com", lastSyncedTime = System.currentTimeMillis() - 1000 * 60 * 8, dailyApplyLimit = 25, applicationsToday = 9, statusMessage = "Direct founder intros active", jobCountAvailable = 67),
                JobPlatformEntity("ziprecruiter", "ZipRecruiter", isConnected = true, accountEmail = "alex.morgan.dev@gmail.com", lastSyncedTime = System.currentTimeMillis() - 1000 * 60 * 60, dailyApplyLimit = 20, applicationsToday = 5, statusMessage = "1-Click apply enabled", jobCountAvailable = 38),
                JobPlatformEntity("greenhouse", "Greenhouse Board", isConnected = true, accountEmail = "alex.morgan.dev@gmail.com", lastSyncedTime = System.currentTimeMillis() - 1000 * 60 * 18, dailyApplyLimit = 25, applicationsToday = 8, statusMessage = "Direct ATS pipeline connected", jobCountAvailable = 52),
                JobPlatformEntity("lever", "Lever ATS", isConnected = true, accountEmail = "alex.morgan.dev@gmail.com", lastSyncedTime = System.currentTimeMillis() - 1000 * 60 * 30, dailyApplyLimit = 20, applicationsToday = 4, statusMessage = "ATS form autofill connected", jobCountAvailable = 31),
                JobPlatformEntity("workday", "Workday Portal", isConnected = false, accountEmail = "", lastSyncedTime = 0L, dailyApplyLimit = 15, applicationsToday = 0, statusMessage = "Connect login to auto-fill", jobCountAvailable = 24)
            )
            platformDao.insertOrUpdatePlatforms(initialPlatforms)
        }

        val apps = applicationDao.getAllApplications().first()
        if (apps.isEmpty()) {
            val now = System.currentTimeMillis()
            val initialApps = listOf(
                JobApplicationEntity(
                    jobTitle = "Staff Android Engineer",
                    company = "Stripe",
                    platformId = "linkedin",
                    location = "San Francisco, CA / Hybrid",
                    salaryRange = "$195,000 - $220,000",
                    jobType = "Full-time",
                    matchScore = 98,
                    status = "OFFER",
                    appliedDate = now - 1000L * 60 * 60 * 24 * 9,
                    lastUpdatedDate = now - 1000L * 60 * 60 * 3,
                    tailoredCoverLetter = "Dear Stripe Team, With 6+ years architecting core payment workflows in Kotlin and Compose, I am excited about scaling Stripe Terminal and Mobile SDKs...",
                    recruiterName = "Sarah Jenkins",
                    recruiterEmail = "sjenkins@stripe.com",
                    notes = "Offer package received! Base $205k + $110k equity/yr. Final decision by next Friday.",
                    jobUrl = "https://stripe.com/jobs/staff-android-engineer",
                    jobDescription = "Lead architecture for Stripe Android SDK, optimize payment sheet latency, guide Compose migrations across 40+ engineering pods.",
                    isAutoApplied = true
                ),
                JobApplicationEntity(
                    jobTitle = "Senior Mobile Platform Engineer",
                    company = "Figma",
                    platformId = "wellfound",
                    location = "Remote (US)",
                    salaryRange = "$180,000 - $205,000",
                    jobType = "Remote",
                    matchScore = 95,
                    status = "INTERVIEW",
                    appliedDate = now - 1000L * 60 * 60 * 24 * 6,
                    lastUpdatedDate = now - 1000L * 60 * 60 * 5,
                    tailoredCoverLetter = "Dear Figma Engineering, As an avid Figma user and mobile systems engineer with deep expertise in rendering and coroutines...",
                    recruiterName = "David Chen",
                    recruiterEmail = "dchen@figma.com",
                    interviewDate = now + 1000L * 60 * 60 * 24 * 2, // 2 days from now
                    notes = "System Design Round with Principal Architect scheduled for Thursday 2:00 PM EST.",
                    jobUrl = "https://figma.com/careers/mobile-platform",
                    jobDescription = "Build canvas rendering bridges, optimize memory footprint for complex vector canvases on Android tablets and foldables.",
                    isAutoApplied = true
                ),
                JobApplicationEntity(
                    jobTitle = "Lead Android Developer",
                    company = "Airbnb",
                    platformId = "greenhouse",
                    location = "Remote (US)",
                    salaryRange = "$185,000 - $215,000",
                    jobType = "Remote",
                    matchScore = 96,
                    status = "INTERVIEW",
                    appliedDate = now - 1000L * 60 * 60 * 24 * 8,
                    lastUpdatedDate = now - 1000L * 60 * 60 * 12,
                    tailoredCoverLetter = "Dear Airbnb Hiring Team, I have closely followed Airbnb's open-source contributions and design systems. My work leading Compose adoption...",
                    recruiterName = "Elena Rostova",
                    recruiterEmail = "elena.r@airbnb.com",
                    interviewDate = now + 1000L * 60 * 60 * 24 * 4,
                    notes = "Round 2 Technical Deep Dive (MVI architecture & custom Canvas animations).",
                    jobUrl = "https://careers.airbnb.com/lead-android",
                    jobDescription = "Drive next-gen listing experience and guest checkout flows using Server-Driven UI and Jetpack Compose.",
                    isAutoApplied = true
                ),
                JobApplicationEntity(
                    jobTitle = "Android Architect - Jetpack & Core",
                    company = "Datadog",
                    platformId = "indeed",
                    location = "New York, NY / Remote",
                    salaryRange = "$175,000 - $198,000",
                    jobType = "Hybrid",
                    matchScore = 92,
                    status = "SCREENING",
                    appliedDate = now - 1000L * 60 * 60 * 24 * 4,
                    lastUpdatedDate = now - 1000L * 60 * 60 * 18,
                    tailoredCoverLetter = "Dear Datadog Team, Monitoring, telemetry, and low-overhead crash reporting are areas I care deeply about...",
                    recruiterName = "Marcus Vance",
                    recruiterEmail = "marcus.v@datadog.com",
                    notes = "Recruiter screening call completed. Positive feedback; moving to hiring manager technical review.",
                    jobUrl = "https://datadoghq.com/careers/android-architect",
                    jobDescription = "Build lightweight RUM (Real User Monitoring) SDKs with zero main-thread overhead, NDK crash hooks, and network diagnostics.",
                    isAutoApplied = true
                ),
                JobApplicationEntity(
                    jobTitle = "Senior Kotlin Mobile Engineer",
                    company = "Uber",
                    platformId = "lever",
                    location = "San Francisco, CA / Remote",
                    salaryRange = "$170,000 - $190,000",
                    jobType = "Remote",
                    matchScore = 91,
                    status = "SCREENING",
                    appliedDate = now - 1000L * 60 * 60 * 24 * 3,
                    lastUpdatedDate = now - 1000L * 60 * 60 * 20,
                    tailoredCoverLetter = "Dear Uber Team, Having built real-time tracking, maps SDK integrations, and reactive event streams...",
                    recruiterName = "Rachel Kim",
                    recruiterEmail = "rkim@uber.com",
                    notes = "Application reviewed by autonomous screening bot; recruiter reached out to schedule introductory sync.",
                    jobUrl = "https://uber.com/careers/kotlin-engineer",
                    jobDescription = "Power real-time rider dispatch state machines, location telemetry batching, and RIBs architecture modernization.",
                    isAutoApplied = true
                ),
                JobApplicationEntity(
                    jobTitle = "Principal Mobile Engineer",
                    company = "Notion",
                    platformId = "linkedin",
                    location = "San Francisco, CA / Remote",
                    salaryRange = "$190,000 - $210,000",
                    jobType = "Full-time",
                    matchScore = 97,
                    status = "APPLIED",
                    appliedDate = now - 1000L * 60 * 60 * 14,
                    lastUpdatedDate = now - 1000L * 60 * 60 * 14,
                    tailoredCoverLetter = "Dear Notion Team, Notion's editor performance and multiplatform architecture are world-class. My background in local-first caching with Room and Kotlin Flow...",
                    recruiterName = "Talent Acquisition",
                    recruiterEmail = "jobs@makenotion.com",
                    notes = "Auto-applied via LinkedIn Easy-Apply integration with AI custom cover letter.",
                    jobUrl = "https://notion.so/careers/principal-mobile",
                    jobDescription = "Optimize mobile rich-text block editor, offline document sync engine, and native Android widgets.",
                    isAutoApplied = true
                ),
                JobApplicationEntity(
                    jobTitle = "Senior Android Systems Engineer",
                    company = "Square (Block)",
                    platformId = "ziprecruiter",
                    location = "Remote (US)",
                    salaryRange = "$175,000 - $200,000",
                    jobType = "Remote",
                    matchScore = 94,
                    status = "APPLIED",
                    appliedDate = now - 1000L * 60 * 60 * 8,
                    lastUpdatedDate = now - 1000L * 60 * 60 * 8,
                    tailoredCoverLetter = "Dear Square Team, I have extensive experience building mission-critical POS and hardware peripheral communication stacks...",
                    recruiterName = "Square Recruiting",
                    recruiterEmail = "careers@squareup.com",
                    notes = "Auto-applied via ZipRecruiter 1-Click with tailored Android hardware profile.",
                    jobUrl = "https://block.xyz/careers/square-android",
                    jobDescription = "Build reliable point-of-sale Android experiences, NFC card reader protocols, and instant deposit flows.",
                    isAutoApplied = true
                ),
                JobApplicationEntity(
                    jobTitle = "Mobile Core Experience Engineer",
                    company = "Discord",
                    platformId = "greenhouse",
                    location = "San Francisco, CA",
                    salaryRange = "$165,000 - $185,000",
                    jobType = "Full-time",
                    matchScore = 89,
                    status = "APPLIED",
                    appliedDate = now - 1000L * 60 * 60 * 5,
                    lastUpdatedDate = now - 1000L * 60 * 60 * 5,
                    tailoredCoverLetter = "Dear Discord Team, Building fast, fluid voice/video and chat interfaces with Jetpack Compose is what I thrive on...",
                    recruiterName = "Discord Talent",
                    recruiterEmail = "recruiting@discord.com",
                    notes = "Auto-applied via Greenhouse integration with direct resume injection.",
                    jobUrl = "https://discord.com/jobs/mobile-core",
                    jobDescription = "Scale chat messaging lists to 60fps scrolling, WebRTC audio pipeline tuning, and real-time push subscriptions.",
                    isAutoApplied = true
                ),
                JobApplicationEntity(
                    jobTitle = "Staff UI/UX Android Developer",
                    company = "Spotify",
                    platformId = "glassdoor",
                    location = "Remote (US)",
                    salaryRange = "$180,000 - $205,000",
                    jobType = "Remote",
                    matchScore = 93,
                    status = "APPLIED",
                    appliedDate = now - 1000L * 60 * 60 * 2,
                    lastUpdatedDate = now - 1000L * 60 * 60 * 2,
                    tailoredCoverLetter = "Dear Spotify Team, Audio experiences and delightful micro-interactions are where great apps shine...",
                    recruiterName = "Spotify Talent",
                    recruiterEmail = "talent@spotify.com",
                    notes = "Auto-applied via Glassdoor sync. Custom media player questions answered automatically.",
                    jobUrl = "https://spotify.com/careers/staff-android",
                    jobDescription = "Lead UI modernization for playback controls, lyric visualizers, and Material 3 transitions.",
                    isAutoApplied = true
                )
            )
            applicationDao.insertApplications(initialApps)

            // Seed initial automation logs
            val initialLogs = listOf(
                AutomationLogEntity(timestamp = now - 1000L * 60 * 5, platformId = "greenhouse", company = "Discord", jobTitle = "Mobile Core Experience Engineer", action = "SUBMITTED", details = "Custom answers generated & submitted successfully.", matchPercentage = 89),
                AutomationLogEntity(timestamp = now - 1000L * 60 * 8, platformId = "ziprecruiter", company = "Square (Block)", jobTitle = "Senior Android Systems Engineer", action = "SUBMITTED", details = "Easy-Apply executed in 1.4s.", matchPercentage = 94),
                AutomationLogEntity(timestamp = now - 1000L * 60 * 14, platformId = "linkedin", company = "Notion", jobTitle = "Principal Mobile Engineer", action = "SUBMITTED", details = "Tailored AI cover letter attached; score 97%.", matchPercentage = 97),
                AutomationLogEntity(timestamp = now - 1000L * 60 * 35, platformId = "linkedin", company = "Google", jobTitle = "Android System Lead", action = "SKIPPED", details = "Skipped: Missing US Clearance keyword filter.", matchPercentage = 71),
                AutomationLogEntity(timestamp = now - 1000L * 60 * 55, platformId = "wellfound", company = "Figma", jobTitle = "Senior Mobile Platform Engineer", action = "INTERVIEW_DETECTED", details = "Recruiter invite received for System Design round!", matchPercentage = 95)
            )
            initialLogs.forEach { automationLogDao.insertLog(it) }
        }
    }

    // Platform methods
    suspend fun connectPlatform(platformId: String, email: String, applyLimit: Int) = withContext(Dispatchers.IO) {
        platformDao.updateConnectionStatus(
            id = platformId,
            connected = true,
            email = email,
            time = System.currentTimeMillis(),
            status = "Connected & Active"
        )
        automationLogDao.insertLog(
            AutomationLogEntity(
                timestamp = System.currentTimeMillis(),
                platformId = platformId,
                company = platformId.replaceFirstChar { it.uppercase() },
                jobTitle = "Platform Sync",
                action = "CONNECTED",
                details = "Platform connected for $email with daily quota $applyLimit",
                matchPercentage = 100
            )
        )
    }

    suspend fun disconnectPlatform(platformId: String) = withContext(Dispatchers.IO) {
        platformDao.updateConnectionStatus(
            id = platformId,
            connected = false,
            email = "",
            time = 0L,
            status = "Disconnected"
        )
    }

    suspend fun togglePlatformAutoApply(platform: JobPlatformEntity, enabled: Boolean) = withContext(Dispatchers.IO) {
        platformDao.updatePlatform(platform.copy(autoApplyEnabled = enabled))
    }

    // Application methods
    suspend fun updateApplicationStatus(id: Long, newStatus: String) = withContext(Dispatchers.IO) {
        applicationDao.updateStatus(id, newStatus)
    }

    suspend fun updateInterviewDetails(id: Long, interviewDate: Long?, notes: String) = withContext(Dispatchers.IO) {
        applicationDao.updateInterviewDetails(id, interviewDate, notes)
    }

    suspend fun updateApplication(application: JobApplicationEntity) = withContext(Dispatchers.IO) {
        applicationDao.updateApplication(application)
    }

    suspend fun deleteApplication(id: Long) = withContext(Dispatchers.IO) {
        applicationDao.deleteApplicationById(id)
    }

    // Profile & Config
    suspend fun updateProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        userProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun updateConfig(config: AutoApplyConfigEntity) = withContext(Dispatchers.IO) {
        configDao.insertOrUpdateConfig(config)
    }

    suspend fun toggleAutoApply(isActive: Boolean) = withContext(Dispatchers.IO) {
        configDao.toggleAutoApply(isActive)
    }

    // AI & Auto-Apply Execution Engine
    suspend fun autoApplyToJob(
        jobTitle: String,
        company: String,
        platformId: String,
        location: String,
        salaryRange: String,
        jobType: String,
        jobDescription: String,
        matchScore: Int
    ): JobApplicationEntity = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()

        // Generate tailored cover letter via Gemini
        val coverLetter = geminiService.generateTailoredCoverLetter(
            profile = profile,
            jobTitle = jobTitle,
            company = company,
            jobDescription = jobDescription
        )

        val sampleQuestions = listOf(
            "How many years of Kotlin/Compose experience do you have?",
            "What is your expected compensation?",
            "Are you authorized to work in the target location?",
            "What is your earliest possible start date?"
        )
        val screeningAnswers = geminiService.generateScreeningAnswers(profile, sampleQuestions)

        val newApp = JobApplicationEntity(
            jobTitle = jobTitle,
            company = company,
            platformId = platformId,
            location = location,
            salaryRange = salaryRange,
            jobType = jobType,
            matchScore = matchScore,
            status = "APPLIED",
            appliedDate = System.currentTimeMillis(),
            lastUpdatedDate = System.currentTimeMillis(),
            tailoredCoverLetter = coverLetter,
            recruiterName = "$company Talent Lead",
            recruiterEmail = "careers@${company.lowercase().replace(" ", "")}.com",
            notes = "Auto-applied via $platformId engine with AI-generated tailored materials.",
            jobUrl = "https://${platformId}.com/jobs/${company.lowercase()}",
            jobDescription = jobDescription,
            isAutoApplied = true,
            screeningAnswers = screeningAnswers
        )

        val insertedId = applicationDao.insertApplication(newApp)
        platformDao.incrementApplicationsToday(platformId)

        automationLogDao.insertLog(
            AutomationLogEntity(
                timestamp = System.currentTimeMillis(),
                platformId = platformId,
                company = company,
                jobTitle = jobTitle,
                action = "SUBMITTED",
                details = "Auto-applied with ${matchScore}% AI match score. Cover letter & screening answers submitted.",
                matchPercentage = matchScore
            )
        )

        newApp.copy(id = insertedId)
    }

    suspend fun generateFollowUpEmail(application: JobApplicationEntity): String = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        geminiService.generateFollowUpEmail(profile, application)
    }

    fun getApplicationsCountToday(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return applicationDao.getApplicationsCountToday(calendar.timeInMillis)
    }

    // Job Posting CRUD & Status/Timestamp Tracking
    suspend fun insertJobPosting(posting: JobPostingEntity): Long = withContext(Dispatchers.IO) {
        postingDao.insertJobPosting(posting)
    }

    suspend fun updateJobPosting(posting: JobPostingEntity) = withContext(Dispatchers.IO) {
        postingDao.updateJobPosting(posting)
    }

    suspend fun updateJobPostingStatus(
        id: Long,
        status: String,
        appliedTimestamp: Long? = null
    ) = withContext(Dispatchers.IO) {
        postingDao.updateStatusAndTimestamp(
            id = id,
            status = status,
            appliedTimestamp = appliedTimestamp,
            lastUpdatedTimestamp = System.currentTimeMillis()
        )
    }

    suspend fun deleteJobPosting(id: Long) = withContext(Dispatchers.IO) {
        postingDao.deleteJobPostingById(id)
    }

    fun getJobPostingsByStatus(status: String): Flow<List<JobPostingEntity>> =
        postingDao.getJobPostingsByStatus(status)

    fun getJobPostingsBySource(source: String): Flow<List<JobPostingEntity>> =
        postingDao.getJobPostingsBySource(source)

    // Career Toolkit Services (ATS Scan, Interview Prep, Cold Outreach)
    suspend fun analyzeAtsResume(
        targetRole: String,
        targetCompany: String,
        jobDescription: String
    ): com.example.data.gemini.AtsAnalysisResult = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        geminiService.analyzeAtsResumeMatch(profile, targetRole, targetCompany, jobDescription)
    }

    suspend fun generateInterviewPrep(
        role: String,
        company: String
    ): com.example.data.gemini.InterviewPrepResult = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        geminiService.generateInterviewPrep(profile, role, company)
    }

    suspend fun generateOutreachTemplates(
        role: String,
        company: String
    ): List<com.example.data.gemini.OutreachTemplate> = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        geminiService.generateOutreachTemplates(profile, role, company)
    }

    suspend fun addManualApplication(
        company: String,
        jobTitle: String,
        platformId: String,
        location: String,
        salary: String,
        status: String,
        matchScore: Int,
        notes: String
    ): Long = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        val coverLetter = geminiService.generateTailoredCoverLetter(
            profile = profile,
            jobTitle = jobTitle,
            company = company,
            jobDescription = "$jobTitle at $company - $location"
        )
        val entity = JobApplicationEntity(
            company = company,
            jobTitle = jobTitle,
            platformId = platformId.lowercase(),
            status = status.uppercase(),
            matchScore = matchScore,
            salaryRange = salary,
            location = location,
            appliedDate = System.currentTimeMillis(),
            lastUpdatedDate = System.currentTimeMillis(),
            tailoredCoverLetter = coverLetter,
            isAutoApplied = false,
            notes = notes
        )
        applicationDao.insertApplication(entity)
    }

    // ----------------------------------------------------
    // User Authentication & Account Management
    // ----------------------------------------------------
    suspend fun login(email: String, password: String): Boolean = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) return@withContext false

        var account = userAccountDao.getAccountByEmail(cleanEmail)
        if (account == null) {
            // Auto-provision account so login is seamless and never fails on any email
            val rawName = cleanEmail.substringBefore("@").replace(".", " ").replace("_", " ")
            val formattedName = rawName.split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                .ifBlank { "Mobile Engineer" }

            account = com.example.data.local.UserAccountEntity(
                email = cleanEmail,
                fullName = formattedName,
                headline = "Senior Android / Mobile Engineer",
                targetRoles = "Android Developer, Mobile Engineer, Kotlin Specialist",
                passwordHash = password,
                isCurrent = true,
                accountType = "EXPERIENCED"
            )
            userAccountDao.insertAccount(account)
        }

        userAccountDao.clearCurrentAccountFlag()
        userAccountDao.setCurrentAccountFlag(account.email)
        val currentProfile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        val updated = currentProfile.copy(
            email = account.email,
            fullName = account.fullName,
            headline = account.headline,
            targetRoles = account.targetRoles,
            isLoggedIn = true,
            accountType = account.accountType
        )
        userProfileDao.insertOrUpdateProfile(updated)
        true
    }

    suspend fun loginWithGoogle(
        googleEmail: String,
        displayName: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        val cleanEmail = googleEmail.trim().lowercase()
        if (cleanEmail.isBlank()) return@withContext false

        var account = userAccountDao.getAccountByEmail(cleanEmail)
        val derivedName = if (displayName.isNotBlank()) {
            displayName
        } else {
            val rawName = cleanEmail.substringBefore("@").replace(".", " ").replace("_", " ")
            rawName.split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                .ifBlank { "Google User" }
        }

        if (account == null) {
            account = com.example.data.local.UserAccountEntity(
                email = cleanEmail,
                fullName = derivedName,
                headline = "Software Engineer | Kotlin & Android",
                targetRoles = "Android Developer, Mobile Systems Engineer, Kotlin Developer",
                passwordHash = "GOOGLE_OAUTH_TOKEN",
                isCurrent = true,
                accountType = "EXPERIENCED"
            )
            userAccountDao.insertAccount(account)
        } else if (displayName.isNotBlank() && account.fullName != displayName) {
            account = account.copy(fullName = displayName, isCurrent = true)
            userAccountDao.insertAccount(account)
        }

        userAccountDao.clearCurrentAccountFlag()
        userAccountDao.setCurrentAccountFlag(account.email)
        val currentProfile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        val updated = currentProfile.copy(
            email = account.email,
            fullName = account.fullName,
            headline = account.headline,
            targetRoles = account.targetRoles,
            isLoggedIn = true,
            accountType = account.accountType
        )
        userProfileDao.insertOrUpdateProfile(updated)
        true
    }

    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        headline: String,
        targetRoles: String,
        accountType: String = "EXPERIENCED"
    ): Boolean = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val existing = userAccountDao.getAccountByEmail(cleanEmail)
        val newAccount = com.example.data.local.UserAccountEntity(
            email = cleanEmail,
            fullName = fullName.trim(),
            headline = headline.trim(),
            targetRoles = targetRoles.trim(),
            passwordHash = password,
            isCurrent = true,
            accountType = accountType
        )
        userAccountDao.clearCurrentAccountFlag()
        userAccountDao.insertAccount(newAccount)

        val currentProfile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        val updated = currentProfile.copy(
            email = cleanEmail,
            fullName = fullName.trim(),
            headline = headline.trim(),
            targetRoles = targetRoles.trim(),
            isLoggedIn = true,
            accountType = accountType
        )
        userProfileDao.insertOrUpdateProfile(updated)
        true
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userAccountDao.clearCurrentAccountFlag()
        val currentProfile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        userProfileDao.insertOrUpdateProfile(currentProfile.copy(isLoggedIn = false))
    }

    suspend fun switchAccount(email: String) = withContext(Dispatchers.IO) {
        login(email, "")
    }

    // ----------------------------------------------------
    // Resume Upload & AI Resume Parsing
    // ----------------------------------------------------
    suspend fun updateResume(
        fileName: String,
        fileSize: String,
        rawText: String
    ): com.example.data.gemini.ParsedResumeData = withContext(Dispatchers.IO) {
        val parsed = geminiService.parseResumeText(rawText)
        val currentProfile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        val updated = currentProfile.copy(
            resumeFileName = fileName,
            resumeFileSize = fileSize,
            resumeUploadedDate = System.currentTimeMillis(),
            resumeRawText = rawText,
            resumeAtsScore = parsed.atsScore,
            fullName = if (parsed.fullName.isNotBlank()) parsed.fullName else currentProfile.fullName,
            headline = if (parsed.headline.isNotBlank()) parsed.headline else currentProfile.headline,
            targetRoles = if (parsed.targetRoles.isNotBlank()) parsed.targetRoles else currentProfile.targetRoles,
            skills = if (parsed.skills.isNotBlank()) parsed.skills else currentProfile.skills,
            yearsOfExperience = parsed.yearsOfExperience,
            bioSummary = if (parsed.bioSummary.isNotBlank()) parsed.bioSummary else currentProfile.bioSummary
        )
        userProfileDao.insertOrUpdateProfile(updated)
        parsed
    }

    // ----------------------------------------------------
    // Resume Email / Mail Dispatch
    // ----------------------------------------------------
    suspend fun generateResumeEmailDraft(
        company: String,
        jobTitle: String,
        recruiterName: String,
        recruiterEmail: String,
        notes: String
    ): com.example.data.gemini.ResumeEmailDraft = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        geminiService.generateResumeEmailDraft(profile, company, jobTitle, recruiterName, recruiterEmail, notes)
    }

    suspend fun recordSentResumeEmail(
        recipientEmail: String,
        recipientName: String,
        company: String,
        jobTitle: String,
        subject: String,
        body: String,
        resumeFileName: String
    ): Long = withContext(Dispatchers.IO) {
        val entity = com.example.data.local.SentResumeEmailEntity(
            recipientEmail = recipientEmail,
            recipientName = recipientName,
            company = company,
            jobTitle = jobTitle,
            subject = subject,
            body = body,
            resumeFileName = resumeFileName,
            timestamp = System.currentTimeMillis(),
            status = "SENT"
        )
        sentResumeEmailDao.insertSentEmail(entity)
    }

    // ----------------------------------------------------
    // Notification & Daily Reminder Scheduling
    // ----------------------------------------------------
    suspend fun setNotificationReminderEnabled(isEnabled: Boolean) = withContext(Dispatchers.IO) {
        notificationReminderDao.setEnabled(isEnabled)
    }

    suspend fun updateNotificationReminderTime(hour: Int, minute: Int) = withContext(Dispatchers.IO) {
        notificationReminderDao.updateTime(hour, minute)
    }

    suspend fun updateNotificationPreferences(
        includeStats: Boolean,
        notifyInterviews: Boolean,
        notifyJobMatches: Boolean
    ) = withContext(Dispatchers.IO) {
        notificationReminderDao.updatePreferences(includeStats, notifyInterviews, notifyJobMatches)
    }

    suspend fun getNotificationConfigSync(): com.example.data.local.NotificationReminderConfigEntity? = withContext(Dispatchers.IO) {
        notificationReminderDao.getConfigSync()
    }

    suspend fun updateLastNotification(timestamp: Long, message: String) = withContext(Dispatchers.IO) {
        notificationReminderDao.updateLastNotified(timestamp, message)
    }
}

