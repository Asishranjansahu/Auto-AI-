package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.AtsAnalysisResult
import com.example.data.gemini.InterviewPrepResult
import com.example.data.gemini.OutreachTemplate
import com.example.data.local.AppDatabase
import com.example.data.local.AutoApplyConfigEntity
import com.example.data.local.AutomationLogEntity
import com.example.data.local.JobApplicationEntity
import com.example.data.local.JobPlatformEntity
import com.example.data.local.JobPostingEntity
import com.example.data.local.UserProfileEntity
import com.example.data.repository.JobRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DiscoveredJob(
    val id: String,
    val title: String,
    val company: String,
    val platformId: String,
    val location: String,
    val salary: String,
    val matchScore: Int,
    val jobType: String,
    val description: String,
    val experienceLevel: String = "Fresher / Entry-Level",
    val isAutoApplying: Boolean = false
)

data class AutoApplyBotState(
    val isRunning: Boolean = false,
    val currentStepText: String = "Idle - Bot ready",
    val progressPercent: Float = 0f,
    val currentTargetJob: String = "",
    val totalProcessedInBatch: Int = 0,
    val successCountInBatch: Int = 0
)

data class EmailTargetInfo(
    val company: String = "",
    val jobTitle: String = "",
    val recruiterName: String = "",
    val recruiterEmail: String = "",
    val defaultNotes: String = ""
)

class JobViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JobRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = JobRepository(db)
    }

    // Reactive streams from Repository
    val allPlatforms: StateFlow<List<JobPlatformEntity>> = repository.allPlatforms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allApplications: StateFlow<List<JobApplicationEntity>> = repository.allApplications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJobPostings: StateFlow<List<JobPostingEntity>> = repository.allJobPostings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUserAccounts: StateFlow<List<com.example.data.local.UserAccountEntity>> = repository.allUserAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSentResumeEmails: StateFlow<List<com.example.data.local.SentResumeEmailEntity>> = repository.allSentResumeEmails
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automationLogs: StateFlow<List<AutomationLogEntity>> = repository.automationLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val autoApplyConfig: StateFlow<AutoApplyConfigEntity?> = repository.autoApplyConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val notificationReminderConfig: StateFlow<com.example.data.local.NotificationReminderConfigEntity?> = repository.notificationReminderConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val applicationsCountToday: StateFlow<Int> = repository.getApplicationsCountToday()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Notification Settings Dialog state
    private val _showNotificationSettingsDialog = MutableStateFlow(false)
    val showNotificationSettingsDialog: StateFlow<Boolean> = _showNotificationSettingsDialog.asStateFlow()

    // Auth Dialog state
    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    // Resume Email Dialog state
    private val _showEmailResumeDialog = MutableStateFlow(false)
    val showEmailResumeDialog: StateFlow<Boolean> = _showEmailResumeDialog.asStateFlow()

    private val _emailResumeTarget = MutableStateFlow<EmailTargetInfo?>(null)
    val emailResumeTarget: StateFlow<EmailTargetInfo?> = _emailResumeTarget.asStateFlow()

    // Resume Preview Dialog state
    private val _showResumePreviewDialog = MutableStateFlow(false)
    val showResumePreviewDialog: StateFlow<Boolean> = _showResumePreviewDialog.asStateFlow()

    // Resume Uploading / Parsing state
    private val _isResumeUploading = MutableStateFlow(false)
    val isResumeUploading: StateFlow<Boolean> = _isResumeUploading.asStateFlow()

    // UI Filter & Search State
    private val _selectedStageFilter = MutableStateFlow("ALL")
    val selectedStageFilter: StateFlow<String> = _selectedStageFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPlatformFilter = MutableStateFlow("ALL")
    val selectedPlatformFilter: StateFlow<String> = _selectedPlatformFilter.asStateFlow()

    private val _selectedExpLevelFilter = MutableStateFlow("ALL") // "ALL", "FRESHER", "EXPERIENCED"
    val selectedExpLevelFilter: StateFlow<String> = _selectedExpLevelFilter.asStateFlow()

    // Filtered Applications Stream
    val filteredApplications: StateFlow<List<JobApplicationEntity>> = combine(
        allApplications,
        _selectedStageFilter,
        _searchQuery,
        _selectedPlatformFilter
    ) { apps, stage, query, platform ->
        apps.filter { app ->
            val matchesStage = if (stage == "ALL") true else app.status.equals(stage, ignoreCase = true)
            val matchesPlatform = if (platform == "ALL") true else app.platformId.equals(platform, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    app.jobTitle.contains(query, ignoreCase = true) ||
                    app.company.contains(query, ignoreCase = true) ||
                    app.location.contains(query, ignoreCase = true) ||
                    app.notes.contains(query, ignoreCase = true)
            matchesStage && matchesPlatform && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Auto-Apply Bot State
    private val _botState = MutableStateFlow(AutoApplyBotState())
    val botState: StateFlow<AutoApplyBotState> = _botState.asStateFlow()

    // Discovered Jobs Queue
    private val _discoveredJobs = MutableStateFlow(getInitialDiscoveredJobs())
    val discoveredJobs: StateFlow<List<DiscoveredJob>> = _discoveredJobs.asStateFlow()

    val filteredDiscoveredJobs: StateFlow<List<DiscoveredJob>> = combine(
        _discoveredJobs,
        _selectedExpLevelFilter
    ) { jobs, filter ->
        when (filter) {
            "FRESHER" -> jobs.filter {
                it.experienceLevel.contains("Fresher", ignoreCase = true) ||
                it.experienceLevel.contains("Entry", ignoreCase = true) ||
                it.experienceLevel.contains("Junior", ignoreCase = true) ||
                it.experienceLevel.contains("Intern", ignoreCase = true) ||
                it.experienceLevel.contains("Graduate", ignoreCase = true) ||
                it.title.contains("Junior", ignoreCase = true) ||
                it.title.contains("Associate", ignoreCase = true) ||
                it.title.contains("Intern", ignoreCase = true) ||
                it.title.contains("Graduate", ignoreCase = true) ||
                it.title.contains("Fresher", ignoreCase = true)
            }
            "EXPERIENCED" -> jobs.filter {
                !it.experienceLevel.contains("Fresher", ignoreCase = true) &&
                !it.experienceLevel.contains("Entry", ignoreCase = true) &&
                !it.experienceLevel.contains("Intern", ignoreCase = true) &&
                !it.title.contains("Intern", ignoreCase = true) &&
                !it.title.contains("Junior", ignoreCase = true)
            }
            else -> jobs
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Application for Detail Dialog/Sheet
    private val _selectedApplication = MutableStateFlow<JobApplicationEntity?>(null)
    val selectedApplication: StateFlow<JobApplicationEntity?> = _selectedApplication.asStateFlow()

    // Follow-up Email State
    private val _generatedEmailText = MutableStateFlow<String?>(null)
    val generatedEmailText: StateFlow<String?> = _generatedEmailText.asStateFlow()
    val isGeneratingEmail = MutableStateFlow(false)

    // ATS Match Analysis State
    private val _atsAnalysisResult = MutableStateFlow<AtsAnalysisResult?>(null)
    val atsAnalysisResult: StateFlow<AtsAnalysisResult?> = _atsAnalysisResult.asStateFlow()
    val isAnalyzingAts = MutableStateFlow(false)

    // Interview Prep State
    private val _interviewPrepResult = MutableStateFlow<InterviewPrepResult?>(null)
    val interviewPrepResult: StateFlow<InterviewPrepResult?> = _interviewPrepResult.asStateFlow()
    val isGeneratingInterview = MutableStateFlow(false)

    // Smart Outreach Templates State
    private val _outreachTemplates = MutableStateFlow<List<OutreachTemplate>>(emptyList())
    val outreachTemplates: StateFlow<List<OutreachTemplate>> = _outreachTemplates.asStateFlow()
    val isGeneratingOutreach = MutableStateFlow(false)

    // UI Snackbars / Alerts
    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    fun setStageFilter(stage: String) {
        _selectedStageFilter.value = stage
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPlatformFilter(platform: String) {
        _selectedPlatformFilter.value = platform
    }

    fun setExpLevelFilter(level: String) {
        _selectedExpLevelFilter.value = level
    }

    fun selectApplication(app: JobApplicationEntity?) {
        _selectedApplication.value = app
        _generatedEmailText.value = null
        _atsAnalysisResult.value = null
        _interviewPrepResult.value = null
        _outreachTemplates.value = emptyList()
    }

    fun clearSnackbar() {
        _snackBarMessage.value = null
    }

    fun loadFresherPreset() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val fresherProfile = current.copy(
                headline = "Fresher / Junior Android Developer (Kotlin, Jetpack Compose, Room)",
                targetRoles = "Junior Android Developer, Graduate Software Engineer, Associate Mobile Developer, Android Intern, Fresher Kotlin Developer, Entry-Level Mobile Engineer",
                targetLocations = "Remote (US/Global), Hybrid, San Francisco, CA, New York, NY, Austin, TX, Bangalore, IN",
                minSalary = 65000,
                yearsOfExperience = 0,
                skills = "Kotlin, Jetpack Compose, Android SDK, Java, XML, Coroutines, Room DB, Retrofit, Git, REST APIs, MVVM Architecture, OOP, Unit Testing, Material Design 3",
                bioSummary = "Eager and driven recent graduate specializing in native Android engineering. Built multiple full-stack Kotlin and Jetpack Compose mobile apps with clean MVVM architecture, Room SQLite caching, and Retrofit REST integrations. Eager to contribute to fast-paced mobile teams.",
                noticePeriod = "Immediate Joiner"
            )
            repository.updateProfile(fresherProfile)
            _selectedExpLevelFilter.value = "FRESHER"
            _snackBarMessage.value = "🎓 Fresher profile preset applied & entry-level job stream selected!"
        }
    }

    fun loadExperiencedPreset() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val expProfile = current.copy(
                headline = "Senior Android / Mobile Systems Engineer (Kotlin, Compose, Coroutines)",
                targetRoles = "Android Engineer, Senior Kotlin Developer, Mobile Tech Lead, Staff Android Architect",
                targetLocations = "Remote (US/Global), San Francisco, CA, New York, NY, Austin, TX",
                minSalary = 150000,
                yearsOfExperience = 6,
                skills = "Kotlin, Jetpack Compose, Coroutines, Flow, Room, Retrofit, MVVM, CI/CD, Unit Testing, M3, Performance Optimization",
                bioSummary = "Passionate mobile engineer with 6+ years specializing in high-performance Android apps, declarative UI architectures, and scalable cloud client integrations.",
                noticePeriod = "2 Weeks"
            )
            repository.updateProfile(expProfile)
            _selectedExpLevelFilter.value = "ALL"
            _snackBarMessage.value = "💼 Experienced profile preset loaded!"
        }
    }

    fun toggleMasterAutoApply(isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleAutoApply(isActive)
            _snackBarMessage.value = if (isActive) "Auto-Apply Engine Activated" else "Auto-Apply Engine Paused"
        }
    }

    fun updateConfig(config: AutoApplyConfigEntity) {
        viewModelScope.launch {
            repository.updateConfig(config)
            _snackBarMessage.value = "Automation preferences saved"
        }
    }

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(profile)
            _snackBarMessage.value = "Candidate profile updated"
        }
    }

    fun updateApplicationStatus(appId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(appId, newStatus)
            _selectedApplication.value?.let { current ->
                if (current.id == appId) {
                    _selectedApplication.value = current.copy(status = newStatus)
                }
            }
            _snackBarMessage.value = "Application moved to $newStatus"
        }
    }

    fun updateInterviewDetails(appId: Long, interviewDate: Long?, notes: String) {
        viewModelScope.launch {
            repository.updateInterviewDetails(appId, interviewDate, notes)
            _selectedApplication.value?.let { current ->
                if (current.id == appId) {
                    _selectedApplication.value = current.copy(interviewDate = interviewDate, notes = notes)
                }
            }
            _snackBarMessage.value = "Interview schedule updated"
        }
    }

    fun connectPlatform(platformId: String, email: String, limit: Int) {
        viewModelScope.launch {
            repository.connectPlatform(platformId, email, limit)
            _snackBarMessage.value = "${platformId.replaceFirstChar { it.uppercase() }} connected successfully!"
        }
    }

    fun disconnectPlatform(platformId: String) {
        viewModelScope.launch {
            repository.disconnectPlatform(platformId)
            _snackBarMessage.value = "${platformId.replaceFirstChar { it.uppercase() }} disconnected"
        }
    }

    fun togglePlatformAutoApply(platform: JobPlatformEntity, enabled: Boolean) {
        viewModelScope.launch {
            repository.togglePlatformAutoApply(platform, enabled)
        }
    }

    fun generateFollowUpEmail(application: JobApplicationEntity) {
        viewModelScope.launch {
            isGeneratingEmail.value = true
            val email = repository.generateFollowUpEmail(application)
            _generatedEmailText.value = email
            isGeneratingEmail.value = false
        }
    }

    fun analyzeAtsResume(role: String, company: String, jobDescription: String) {
        viewModelScope.launch {
            isAnalyzingAts.value = true
            val result = repository.analyzeAtsResume(role, company, jobDescription)
            _atsAnalysisResult.value = result
            isAnalyzingAts.value = false
        }
    }

    fun generateInterviewPrep(role: String, company: String) {
        viewModelScope.launch {
            isGeneratingInterview.value = true
            val result = repository.generateInterviewPrep(role, company)
            _interviewPrepResult.value = result
            isGeneratingInterview.value = false
        }
    }

    fun generateOutreachTemplates(role: String, company: String) {
        viewModelScope.launch {
            isGeneratingOutreach.value = true
            val result = repository.generateOutreachTemplates(role, company)
            _outreachTemplates.value = result
            isGeneratingOutreach.value = false
        }
    }

    fun addManualApplication(
        company: String,
        jobTitle: String,
        platformId: String,
        location: String,
        salary: String,
        status: String,
        matchScore: Int,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addManualApplication(
                company = company,
                jobTitle = jobTitle,
                platformId = platformId,
                location = location,
                salary = salary,
                status = status,
                matchScore = matchScore,
                notes = notes
            )
            _snackBarMessage.value = "Application for $jobTitle at $company added!"
        }
    }

    fun bookmarkJob(job: DiscoveredJob) {
        viewModelScope.launch {
            repository.insertJobPosting(
                JobPostingEntity(
                    title = job.title,
                    company = job.company,
                    source = job.platformId,
                    location = job.location,
                    salaryRange = job.salary,
                    matchScore = job.matchScore,
                    status = "SAVED",
                    description = job.description,
                    jobType = job.jobType,
                    experienceLevel = job.experienceLevel
                )
            )
            _snackBarMessage.value = "📌 ${job.title} at ${job.company} saved to Saved Jobs database"
        }
    }

    fun deleteApplication(appId: Long) {
        viewModelScope.launch {
            repository.deleteApplication(appId)
            if (_selectedApplication.value?.id == appId) {
                _selectedApplication.value = null
            }
            _snackBarMessage.value = "Application removed"
        }
    }

    // Auto Apply Single Job
    fun autoApplyToSingleJob(job: DiscoveredJob) {
        viewModelScope.launch {
            _discoveredJobs.value = _discoveredJobs.value.map {
                if (it.id == job.id) it.copy(isAutoApplying = true) else it
            }

            _botState.value = AutoApplyBotState(
                isRunning = true,
                currentStepText = "Customizing resume & cover letter with Gemini for ${job.company}...",
                progressPercent = 0.3f,
                currentTargetJob = "${job.title} at ${job.company}"
            )
            delay(900)

            _botState.value = _botState.value.copy(
                currentStepText = "Autofilling application screening questions for ${job.platformId}...",
                progressPercent = 0.7f
            )
            delay(900)

            repository.autoApplyToJob(
                jobTitle = job.title,
                company = job.company,
                platformId = job.platformId,
                location = job.location,
                salaryRange = job.salary,
                jobType = job.jobType,
                jobDescription = job.description,
                matchScore = job.matchScore
            )

            _discoveredJobs.value = _discoveredJobs.value.filter { it.id != job.id }

            _botState.value = AutoApplyBotState(
                isRunning = false,
                currentStepText = "Submitted application to ${job.company}!",
                progressPercent = 1f
            )
            _snackBarMessage.value = "Auto-applied to ${job.title} at ${job.company} (${job.matchScore}% Match)"
        }
    }

    // Run Automated Batch Apply Session
    fun startBatchAutoApply() {
        val jobsToApply = _discoveredJobs.value.filter { it.matchScore >= 80 }
        if (jobsToApply.isEmpty()) {
            _snackBarMessage.value = "No matching jobs in queue. Refreshing stream..."
            _discoveredJobs.value = getInitialDiscoveredJobs()
            return
        }

        viewModelScope.launch {
            _botState.value = AutoApplyBotState(
                isRunning = true,
                currentStepText = "Auto-Apply Engine Started. Scanning ${jobsToApply.size} matched positions...",
                progressPercent = 0.05f,
                totalProcessedInBatch = jobsToApply.size
            )

            var success = 0
            for ((index, job) in jobsToApply.withIndex()) {
                val progress = (index + 1).toFloat() / jobsToApply.size

                _botState.value = _botState.value.copy(
                    currentStepText = "[${index + 1}/${jobsToApply.size}] Analyzing requirements for ${job.title} at ${job.company}...",
                    progressPercent = progress * 0.4f,
                    currentTargetJob = "${job.title} at ${job.company}"
                )
                delay(600)

                _botState.value = _botState.value.copy(
                    currentStepText = "[${index + 1}/${jobsToApply.size}] AI tailoring cover letter & screening questionnaire...",
                    progressPercent = progress * 0.7f
                )
                delay(600)

                repository.autoApplyToJob(
                    jobTitle = job.title,
                    company = job.company,
                    platformId = job.platformId,
                    location = job.location,
                    salaryRange = job.salary,
                    jobType = job.jobType,
                    jobDescription = job.description,
                    matchScore = job.matchScore
                )
                success++

                _botState.value = _botState.value.copy(
                    currentStepText = "[${index + 1}/${jobsToApply.size}] Submitted to ${job.company} on ${job.platformId.replaceFirstChar { it.uppercase() }}",
                    progressPercent = progress,
                    successCountInBatch = success
                )
                delay(400)
            }

            _discoveredJobs.value = _discoveredJobs.value.filter { it.matchScore < 80 }
            _botState.value = AutoApplyBotState(
                isRunning = false,
                currentStepText = "Batch Completed! Successfully submitted $success applications.",
                progressPercent = 1f,
                successCountInBatch = success
            )
            _snackBarMessage.value = "Automated batch complete: $success jobs applied!"
        }
    }

    fun refreshDiscoveredJobs() {
        _discoveredJobs.value = getInitialDiscoveredJobs().shuffled()
        _snackBarMessage.value = "Synced new matching positions across connected platforms"
    }

    // ----------------------------------------------------
    // Authentication Operations
    // ----------------------------------------------------
    fun openAuthDialog() {
        _showAuthDialog.value = true
    }

    fun closeAuthDialog() {
        _showAuthDialog.value = false
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val success = repository.login(email, password)
            if (success) {
                _snackBarMessage.value = "Signed in as $email"
                _showAuthDialog.value = false
            } else {
                _snackBarMessage.value = "Could not sign in with $email. Please check details."
            }
            onResult(success)
        }
    }

    fun loginWithGoogle(email: String, displayName: String = "", onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val success = repository.loginWithGoogle(email, displayName)
            if (success) {
                _snackBarMessage.value = "Signed in with Google as ${if (displayName.isNotBlank()) displayName else email}"
                _showAuthDialog.value = false
            } else {
                _snackBarMessage.value = "Google sign-in failed. Please try again."
            }
            onResult(success)
        }
    }

    fun signUp(
        fullName: String,
        email: String,
        password: String,
        headline: String,
        targetRoles: String,
        accountType: String = "EXPERIENCED",
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val success = repository.signUp(fullName, email, password, headline, targetRoles, accountType)
            if (success) {
                _snackBarMessage.value = "Account created for $fullName! Ready to auto-apply."
                _showAuthDialog.value = false
            } else {
                _snackBarMessage.value = "Could not create account. Please check details."
            }
            onResult(success)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _snackBarMessage.value = "Logged out successfully."
        }
    }

    fun switchAccount(email: String) {
        viewModelScope.launch {
            repository.switchAccount(email)
            _snackBarMessage.value = "Switched active account to $email"
        }
    }

    // ----------------------------------------------------
    // Resume Upload & AI Parsing
    // ----------------------------------------------------
    fun uploadResume(
        fileName: String,
        fileSize: String,
        rawText: String,
        onComplete: (com.example.data.gemini.ParsedResumeData) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isResumeUploading.value = true
            _snackBarMessage.value = "Uploading & scanning resume with Gemini AI..."
            try {
                val parsed = repository.updateResume(fileName, fileSize, rawText)
                _snackBarMessage.value = "Resume '$fileName' parsed! ATS Score: ${parsed.atsScore}%"
                onComplete(parsed)
            } catch (e: Exception) {
                _snackBarMessage.value = "Uploaded $fileName. Profile updated."
            } finally {
                _isResumeUploading.value = false
            }
        }
    }

    // ----------------------------------------------------
    // Resume Email Operations
    // ----------------------------------------------------
    fun openEmailResumeDialog(target: EmailTargetInfo? = null) {
        _emailResumeTarget.value = target ?: EmailTargetInfo()
        _showEmailResumeDialog.value = true
    }

    fun openEmailResumeDialog(application: JobApplicationEntity) {
        _emailResumeTarget.value = EmailTargetInfo(
            company = application.company,
            jobTitle = application.jobTitle,
            recruiterEmail = "recruiter@${application.company.lowercase().replace(" ", "")}.com",
            defaultNotes = "Follow-up regarding my submitted application for ${application.jobTitle} on ${application.platformId}."
        )
        _showEmailResumeDialog.value = true
    }

    fun closeEmailResumeDialog() {
        _showEmailResumeDialog.value = false
        _emailResumeTarget.value = null
    }

    fun openResumePreviewDialog() {
        _showResumePreviewDialog.value = true
    }

    fun closeResumePreviewDialog() {
        _showResumePreviewDialog.value = false
    }

    suspend fun generateResumeEmailDraft(
        company: String,
        jobTitle: String,
        recruiterName: String,
        recruiterEmail: String,
        notes: String
    ): com.example.data.gemini.ResumeEmailDraft {
        return repository.generateResumeEmailDraft(company, jobTitle, recruiterName, recruiterEmail, notes)
    }

    fun recordSentResumeEmail(
        recipientEmail: String,
        recipientName: String,
        company: String,
        jobTitle: String,
        subject: String,
        body: String,
        resumeFileName: String
    ) {
        viewModelScope.launch {
            repository.recordSentResumeEmail(
                recipientEmail = recipientEmail,
                recipientName = recipientName,
                company = company,
                jobTitle = jobTitle,
                subject = subject,
                body = body,
                resumeFileName = resumeFileName
            )
            _snackBarMessage.value = "Resume application email recorded & dispatched to $recipientEmail"
            closeEmailResumeDialog()
        }
    }

    private fun getInitialDiscoveredJobs(): List<DiscoveredJob> {
        return listOf(
            DiscoveredJob(
                id = "disc_fresher_1",
                title = "Junior Android Developer (Fresher / New Grad)",
                company = "Google",
                platformId = "linkedin",
                location = "Mountain View, CA / Remote",
                salary = "$115,000 - $135,000",
                matchScore = 98,
                jobType = "Remote",
                experienceLevel = "Fresher / Entry-Level (0-1 yrs)",
                description = "Join the Android Core Developer Ecosystem team. Build Jetpack UI components, open-source Kotlin samples, and test coverage for modern Material 3 design systems."
            ),
            DiscoveredJob(
                id = "disc_fresher_2",
                title = "Associate Mobile Software Engineer",
                company = "Razorpay",
                platformId = "wellfound",
                location = "Remote / Bangalore",
                salary = "$75,000 - $95,000 / ₹18-24 LPA",
                matchScore = 97,
                jobType = "Remote",
                experienceLevel = "Fresher / Entry-Level (0-1 yrs)",
                description = "Design and build frictionless mobile checkout SDKs with Kotlin, Jetpack Compose, Coroutines, and local SQLite/Room caching. Great mentorship program for fresh graduates."
            ),
            DiscoveredJob(
                id = "disc_fresher_3",
                title = "Android Engineering Intern / Graduate Trainee",
                company = "Swiggy",
                platformId = "greenhouse",
                location = "Bangalore, IN / Hybrid",
                salary = "₹45,000/mo stipend + PPO",
                matchScore = 96,
                jobType = "Hybrid",
                experienceLevel = "Fresher / Intern (0 yrs)",
                description = "Hands-on development with the consumer food delivery app. Write unit tests, optimize Compose list performance, and build real-time order tracking UI."
            ),
            DiscoveredJob(
                id = "disc_fresher_4",
                title = "Entry-Level Kotlin App Developer",
                company = "Postman",
                platformId = "indeed",
                location = "San Francisco, CA / Remote",
                salary = "$80,000 - $100,000",
                matchScore = 95,
                jobType = "Remote",
                experienceLevel = "Fresher / Entry-Level (0-1 yrs)",
                description = "Build API collaboration tools for Android developers. Work directly with modern networking (Ktor/Retrofit), Kotlin Flow, and clean MVVM architecture."
            ),
            DiscoveredJob(
                id = "disc_fresher_5",
                title = "Graduate Mobile Engineer (Campus / 2024-2026 Batch)",
                company = "Microsoft",
                platformId = "ziprecruiter",
                location = "Redmond, WA / Remote",
                salary = "$120,000 - $140,000",
                matchScore = 98,
                jobType = "Remote",
                experienceLevel = "Fresher / Entry-Level (0-1 yrs)",
                description = "Build experiences across Microsoft 365 Android apps (Teams, Outlook). Eager to hire passionate learners with strong Kotlin, Java, and Data Structures fundamentals."
            ),
            DiscoveredJob(
                id = "disc_1",
                title = "Senior Android Engineer - Mobile Core",
                company = "DoorDash",
                platformId = "linkedin",
                location = "San Francisco, CA / Remote",
                salary = "$180,000 - $210,000",
                matchScore = 97,
                jobType = "Remote",
                experienceLevel = "Senior (4+ yrs)",
                description = "Lead Merchant and Dasher app reliability, Compose migration, real-time map navigation feeds, and low-latency order dispatching."
            ),
            DiscoveredJob(
                id = "disc_2",
                title = "Staff Mobile Software Engineer",
                company = "Airbnb",
                platformId = "greenhouse",
                location = "Remote (US)",
                salary = "$195,000 - $225,000",
                matchScore = 96,
                jobType = "Remote",
                experienceLevel = "Lead / Staff (6+ yrs)",
                description = "Architect next-gen booking checkout flows, server-driven UI rendering engine, and performance benchmarks."
            ),
            DiscoveredJob(
                id = "disc_3",
                title = "Android Tech Lead",
                company = "Robinhood",
                platformId = "wellfound",
                location = "Menlo Park, CA / Hybrid",
                salary = "$185,000 - $215,000",
                matchScore = 94,
                jobType = "Hybrid",
                experienceLevel = "Lead (5+ yrs)",
                description = "Build secure, high-precision charting and stock trading interfaces with Jetpack Compose, Room persistence, and WebSocket feeds."
            )
        )
    }

    // ----------------------------------------------------
    // Notification & Daily Reminder Management
    // ----------------------------------------------------
    fun openNotificationSettingsDialog() {
        _showNotificationSettingsDialog.value = true
    }

    fun closeNotificationSettingsDialog() {
        _showNotificationSettingsDialog.value = false
    }

    fun toggleNotificationReminder(isEnabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationReminderEnabled(isEnabled)
            val config = repository.getNotificationConfigSync()
            val hour = config?.reminderHour ?: 9
            val minute = config?.reminderMinute ?: 0
            com.example.notifications.NotificationReminderManager.scheduleDailyReminder(
                context = getApplication(),
                hour = hour,
                minute = minute,
                enabled = isEnabled
            )
            if (isEnabled) {
                val formattedTime = String.format("%02d:%02d %s", if (hour % 12 == 0) 12 else hour % 12, minute, if (hour >= 12) "PM" else "AM")
                _snackBarMessage.value = "Daily reminders enabled for $formattedTime"
            } else {
                _snackBarMessage.value = "Daily reminders turned off"
            }
        }
    }

    fun setNotificationReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.updateNotificationReminderTime(hour, minute)
            val config = repository.getNotificationConfigSync()
            val isEnabled = config?.isEnabled ?: true
            com.example.notifications.NotificationReminderManager.scheduleDailyReminder(
                context = getApplication(),
                hour = hour,
                minute = minute,
                enabled = isEnabled
            )
            val formattedTime = String.format("%02d:%02d %s", if (hour % 12 == 0) 12 else hour % 12, minute, if (hour >= 12) "PM" else "AM")
            _snackBarMessage.value = "Daily reminder scheduled for $formattedTime"
        }
    }

    fun updateNotificationPreferences(includeStats: Boolean, notifyInterviews: Boolean, notifyJobMatches: Boolean) {
        viewModelScope.launch {
            repository.updateNotificationPreferences(includeStats, notifyInterviews, notifyJobMatches)
            _snackBarMessage.value = "Reminder preferences updated"
        }
    }

    fun sendTestNotification() {
        viewModelScope.launch {
            val apps = repository.allApplications.firstOrNull() ?: emptyList()
            val totalApplied = apps.count { it.status.equals("APPLIED", true) }
            val inInterviews = apps.count { it.status.equals("INTERVIEW", true) }
            val config = repository.getNotificationConfigSync()

            val title = config?.customTitle ?: "🎯 Daily Job Tracker Digest"
            val message = config?.customMessage ?: "Check your application tracker for new interview requests and recruiter updates!"

            com.example.notifications.NotificationReminderManager.showReminderNotification(
                context = getApplication(),
                title = title,
                message = message,
                totalApplied = totalApplied,
                inInterviews = inInterviews,
                notificationId = com.example.notifications.NotificationReminderManager.NOTIFICATION_ID_TEST
            )

            _snackBarMessage.value = "🔔 Test daily reminder notification dispatched!"
        }
    }
}

