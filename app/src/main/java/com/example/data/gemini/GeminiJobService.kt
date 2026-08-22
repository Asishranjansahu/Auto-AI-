package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.JobApplicationEntity
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AtsAnalysisResult(
    val matchScore: Int,
    val summary: String,
    val matchedKeywords: List<String>,
    val missingKeywords: List<String>,
    val suggestedBullets: List<String>,
    val improvementTips: List<String>
)

data class InterviewQuestion(
    val question: String,
    val category: String, // "Technical", "Behavioral (STAR)", "System Design", "Situational"
    val sampleAnswer: String,
    val evaluationTip: String
)

data class InterviewPrepResult(
    val role: String,
    val company: String,
    val questions: List<InterviewQuestion>,
    val interviewStrategy: String
)

data class OutreachTemplate(
    val title: String,
    val type: String, // "LINKEDIN_CONNECT", "COLD_EMAIL", "REFERRAL_REQUEST", "POST_INTERVIEW_THANKS"
    val subject: String,
    val body: String,
    val tips: String
)

data class ParsedResumeData(
    val fullName: String,
    val headline: String,
    val targetRoles: String,
    val skills: String,
    val yearsOfExperience: Int,
    val bioSummary: String,
    val atsScore: Int,
    val education: String = "",
    val suggestedImprovements: List<String> = emptyList()
)

data class ResumeEmailDraft(
    val recipientEmail: String,
    val subject: String,
    val body: String,
    val resumeSummarySnippet: String
)

class GeminiJobService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateTailoredCoverLetter(
        profile: UserProfileEntity,
        jobTitle: String,
        company: String,
        jobDescription: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackCoverLetter(profile, jobTitle, company)
        }

        val prompt = """
            Write a compelling, professional, and tailored 3-paragraph cover letter for:
            Candidate Name: ${profile.fullName}
            Current Headline: ${profile.headline}
            Core Skills: ${profile.skills}
            Years of Experience: ${profile.yearsOfExperience}
            Portfolio: ${profile.portfolioUrl} | GitHub: ${profile.githubUrl}
            
            Applying For: $jobTitle at $company
            Job Description / Requirements: $jobDescription
            
            Format nicely with clear paragraph breaks. Highlight exact matching skills directly relevant to the role. Keep it concise, punchy, and confident without generic fluff.
        """.trimIndent()

        val response = callGeminiApi(apiKey, prompt)
        if (response.isNotBlank()) response else getFallbackCoverLetter(profile, jobTitle, company)
    }

    suspend fun generateScreeningAnswers(
        profile: UserProfileEntity,
        questions: List<String>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackScreeningAnswers(profile, questions)
        }

        val questionsText = questions.joinToString("\n- ")
        val prompt = """
            You are an automated job application agent. Provide concise, professional answers to these screening questions for the candidate:
            Candidate: ${profile.fullName}, ${profile.headline}
            Experience: ${profile.yearsOfExperience} years
            Skills: ${profile.skills}
            Notice Period: ${profile.noticePeriod}
            Work Auth: ${profile.workAuthorization}
            Min Salary: $${profile.minSalary}/year
            
            Questions:
            - $questionsText
            
            Return format:
            Q: [Question]
            A: [Concise high-impact answer]
        """.trimIndent()

        val response = callGeminiApi(apiKey, prompt)
        if (response.isNotBlank()) response else getFallbackScreeningAnswers(profile, questions)
    }

    suspend fun generateFollowUpEmail(
        profile: UserProfileEntity,
        application: JobApplicationEntity
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackFollowUpEmail(profile, application)
        }

        val prompt = """
            Write a polite, impactful follow-up email from candidate ${profile.fullName} to the hiring team at ${application.company} for the role of ${application.jobTitle}.
            Current Status: ${application.status}
            Recruiter/Contact: ${if (application.recruiterName.isNotBlank()) application.recruiterName else "Hiring Manager"}
            Candidate skills: ${profile.skills}
            
            Provide Subject line and Body. Keep it gracious, enthusiastic, and under 150 words.
        """.trimIndent()

        val response = callGeminiApi(apiKey, prompt)
        if (response.isNotBlank()) response else getFallbackFollowUpEmail(profile, application)
    }

    suspend fun analyzeAtsResumeMatch(
        profile: UserProfileEntity,
        targetRole: String,
        targetCompany: String,
        jobDescription: String
    ): AtsAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            val prompt = """
                You are an expert ATS (Applicant Tracking System) scanner and career coach.
                Analyze the match between this Candidate Profile and the target Job Description.
                
                Candidate Profile:
                - Name: ${profile.fullName}
                - Headline: ${profile.headline}
                - Skills: ${profile.skills}
                - Experience: ${profile.yearsOfExperience} years
                - Bio/Summary: ${profile.bioSummary}
                
                Target Job: $targetRole at $targetCompany
                Job Description:
                $jobDescription
                
                Respond in valid JSON format ONLY:
                {
                   "matchScore": 88,
                   "summary": "Strong alignment on Kotlin and Compose, missing CI/CD and Ktor specifics.",
                   "matchedKeywords": ["Kotlin", "Jetpack Compose", "Coroutines", "Room", "MVVM"],
                   "missingKeywords": ["Ktor", "CI/CD GitHub Actions", "GraphQL", "Dagger-Hilt"],
                   "suggestedBullets": [
                      "Architected reactive UI with Jetpack Compose reducing rendering overhead by 25%",
                      "Designed local SQLite caching layer using Room with automated Flow state emissions"
                   ],
                   "improvementTips": [
                      "Incorporate Ktor or Retrofit networking explicitly in your experience summary.",
                      "Highlight test-driven development and Coroutines testing."
                   ]
                }
            """.trimIndent()

            try {
                val rawResponse = callGeminiApi(apiKey, prompt)
                if (rawResponse.isNotBlank()) {
                    val cleanJson = rawResponse.replace("```json", "").replace("```", "").trim()
                    val json = JSONObject(cleanJson)
                    val matchScore = json.optInt("matchScore", 85)
                    val summary = json.optString("summary", "Solid foundational match for this role.")
                    val matched = json.optJSONArray("matchedKeywords")?.let { arr ->
                        (0 until arr.length()).map { arr.getString(it) }
                    } ?: emptyList()
                    val missing = json.optJSONArray("missingKeywords")?.let { arr ->
                        (0 until arr.length()).map { arr.getString(it) }
                    } ?: emptyList()
                    val bullets = json.optJSONArray("suggestedBullets")?.let { arr ->
                        (0 until arr.length()).map { arr.getString(it) }
                    } ?: emptyList()
                    val tips = json.optJSONArray("improvementTips")?.let { arr ->
                        (0 until arr.length()).map { arr.getString(it) }
                    } ?: emptyList()

                    return@withContext AtsAnalysisResult(
                        matchScore = matchScore,
                        summary = summary,
                        matchedKeywords = matched,
                        missingKeywords = missing,
                        suggestedBullets = bullets,
                        improvementTips = tips
                    )
                }
            } catch (e: Exception) {
                Log.e("GeminiJobService", "Failed to parse ATS JSON from Gemini: ${e.message}")
            }
        }
        // Fallback rule-based ATS analysis
        return@withContext getFallbackAtsAnalysis(profile, targetRole, targetCompany, jobDescription)
    }

    suspend fun generateInterviewPrep(
        profile: UserProfileEntity,
        role: String,
        company: String
    ): InterviewPrepResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            val prompt = """
                You are a Staff Software Engineering Hiring Manager preparing interview questions for candidate ${profile.fullName} applying for $role at $company.
                Candidate experience: ${profile.yearsOfExperience} years, Skills: ${profile.skills}.
                
                Generate 4 high-yield interview questions (2 Technical, 1 Behavioral STAR method, 1 Architecture/System Design).
                Respond in valid JSON ONLY:
                {
                   "interviewStrategy": "Focus on state management, declarative UI performance, and structured problem-solving.",
                   "questions": [
                      {
                        "category": "Technical (Kotlin/Compose)",
                        "question": "How does Jetpack Compose handle recomposition skipping and state hoisting?",
                        "sampleAnswer": "Compose skips recomposition when parameters are stable and unchanged. By hoisting state up to callers and passing down immutable states with event lambdas, we preserve unidirectional data flow...",
                        "evaluationTip": "Look for deep knowledge of remember, derivedStateOf, and SnapshotStateList."
                      }
                   ]
                }
            """.trimIndent()

            try {
                val rawResponse = callGeminiApi(apiKey, prompt)
                if (rawResponse.isNotBlank()) {
                    val cleanJson = rawResponse.replace("```json", "").replace("```", "").trim()
                    val json = JSONObject(cleanJson)
                    val strategy = json.optString("interviewStrategy", "Prepare clear STAR anecdotes and explain system trade-offs.")
                    val qArr = json.optJSONArray("questions")
                    val questionsList = mutableListOf<InterviewQuestion>()
                    if (qArr != null) {
                        for (i in 0 until qArr.length()) {
                            val item = qArr.getJSONObject(i)
                            questionsList.add(
                                InterviewQuestion(
                                    question = item.optString("question"),
                                    category = item.optString("category"),
                                    sampleAnswer = item.optString("sampleAnswer"),
                                    evaluationTip = item.optString("evaluationTip")
                                )
                            )
                        }
                    }
                    if (questionsList.isNotEmpty()) {
                        return@withContext InterviewPrepResult(
                            role = role,
                            company = company,
                            questions = questionsList,
                            interviewStrategy = strategy
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("GeminiJobService", "Interview prep parse error: ${e.message}")
            }
        }
        return@withContext getFallbackInterviewPrep(profile, role, company)
    }

    suspend fun generateOutreachTemplates(
        profile: UserProfileEntity,
        role: String,
        company: String
    ): List<OutreachTemplate> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            val prompt = """
                Generate 4 personalized outreach templates for candidate ${profile.fullName} applying for $role at $company:
                1. LinkedIn Connection Request (Strictly under 300 characters)
                2. Cold Recruiter InMail / Email
                3. Employee Referral Request
                4. Post-Interview Thank You Note
                
                Respond in valid JSON array ONLY:
                [
                   {
                     "title": "LinkedIn Connect Note (Under 300 chars)",
                     "type": "LINKEDIN_CONNECT",
                     "subject": "Connecting regarding $role",
                     "body": "Hi [Name], I noticed your work building mobile products at $company. As a developer specializing in ${profile.skills.split(",").take(2).joinToString(",")}, I'd love to connect and follow your team's engineering work!",
                     "tips": "Send within business hours to recruiters or engineering leads."
                   }
                ]
            """.trimIndent()

            try {
                val rawResponse = callGeminiApi(apiKey, prompt)
                if (rawResponse.isNotBlank()) {
                    val cleanJson = rawResponse.replace("```json", "").replace("```", "").trim()
                    val arr = JSONArray(cleanJson)
                    val resultList = mutableListOf<OutreachTemplate>()
                    for (i in 0 until arr.length()) {
                        val item = arr.getJSONObject(i)
                        resultList.add(
                            OutreachTemplate(
                                title = item.optString("title"),
                                type = item.optString("type"),
                                subject = item.optString("subject"),
                                body = item.optString("body"),
                                tips = item.optString("tips")
                            )
                        )
                    }
                    if (resultList.isNotEmpty()) return@withContext resultList
                }
            } catch (e: Exception) {
                Log.e("GeminiJobService", "Outreach parse error: ${e.message}")
            }
        }
        return@withContext getFallbackOutreachTemplates(profile, role, company)
    }

    private fun callGeminiApi(apiKey: String, prompt: String): String {
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val partObj = JSONObject().apply {
                        put("text", prompt)
                    }
                    val parts = JSONArray().apply { put(partObj) }
                    val contentObj = JSONObject().apply {
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("GeminiJobService", "API request failed: ${response.code} - ${response.message}")
                    return ""
                }
                val bodyString = response.body?.string() ?: return ""
                val rootJson = JSONObject(bodyString)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text", "").trim()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("GeminiJobService", "Error calling Gemini: ${e.message}", e)
        }
        return ""
    }

    private fun getFallbackCoverLetter(profile: UserProfileEntity, jobTitle: String, company: String): String {
        val expSnippet = if (profile.yearsOfExperience <= 0) {
            "As an enthusiastic and motivated software engineer specializing in ${profile.skills.split(",").take(3).joinToString(",")}, I have built multiple end-to-end applications demonstrating clean architecture, solid problem-solving fundamentals, and rapid learning capacity."
        } else {
            "With over ${profile.yearsOfExperience} years of experience specializing in ${profile.skills.split(",").take(3).joinToString(",")}, I have built a track record of architecting scalable applications, improving app performance, and collaborating closely with product teams."
        }

        return """
Dear Hiring Team at $company,

I am writing to express my enthusiastic interest in the $jobTitle position. $expSnippet

At my academic and project experiences, I have consistently prioritized clean code standards, modern reactive patterns, and user-centric features. Your team's mission at $company strongly aligns with my passion for building high-quality, impactful products.

I would love the opportunity to discuss how my background in modern development, hands-on project portfolio, and quick ramp-up ability can contribute to $company's engineering goals. Thank you for your time and consideration.

Warm regards,
${profile.fullName}
${profile.email} | ${profile.portfolioUrl}
        """.trimIndent()
    }

    private fun getFallbackScreeningAnswers(profile: UserProfileEntity, questions: List<String>): String {
        return questions.joinToString("\n\n") { q ->
            val answer = when {
                q.contains("experience", ignoreCase = true) -> {
                    if (profile.yearsOfExperience <= 0) "Recent graduate / entry-level developer with strong hands-on project experience in modern Android & Kotlin."
                    else "${profile.yearsOfExperience}+ years of production engineering experience with modern toolchains."
                }
                q.contains("salary", ignoreCase = true) || q.contains("compensation", ignoreCase = true) -> "Target compensation is $${profile.minSalary}+ base salary, open to discussion based on role and total rewards."
                q.contains("notice", ignoreCase = true) || q.contains("start", ignoreCase = true) -> "${profile.noticePeriod}; available immediately upon offer finalization."
                q.contains("sponsorship", ignoreCase = true) || q.contains("authorized", ignoreCase = true) -> "${profile.workAuthorization}."
                q.contains("remote", ignoreCase = true) -> "Comfortable working in remote, hybrid, or on-site collaborative environments."
                else -> "Strong technical grounding in ${profile.skills.split(",").take(4).joinToString(",")}, dedicated to rapid execution, clean architecture, and team growth."
            }
            "Q: $q\nA: $answer"
        }
    }

    private fun getFallbackFollowUpEmail(profile: UserProfileEntity, application: JobApplicationEntity): String {
        val contact = if (application.recruiterName.isNotBlank()) application.recruiterName else "${application.company} Hiring Team"
        return """
Subject: Following up on ${application.jobTitle} Application - ${profile.fullName}

Hi $contact,

I hope you're having a great week!

I wanted to quickly follow up regarding my recent application for the ${application.jobTitle} role at ${application.company}. I remain very enthusiastic about the company's trajectory and the opportunity to bring my background in ${profile.skills.split(",").take(3).joinToString(",")} to your engineering team.

Please let me know if there are any additional portfolio samples or details I can provide. Looking forward to hearing about next steps!

Best regards,
${profile.fullName}
${profile.phone} | ${profile.email}
        """.trimIndent()
    }

    private fun getFallbackAtsAnalysis(
        profile: UserProfileEntity,
        role: String,
        company: String,
        jobDescription: String
    ): AtsAnalysisResult {
        val userSkills = profile.skills.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() }
        val commonKeywords = listOf(
            "kotlin", "jetpack compose", "coroutines", "flow", "room", "retrofit",
            "mvvm", "android sdk", "git", "rest apis", "ci/cd", "unit testing",
            "dagger", "hilt", "ktor", "material 3", "clean architecture"
        )
        val jdLower = jobDescription.lowercase()
        val matched = mutableListOf<String>()
        val missing = mutableListOf<String>()

        for (kw in commonKeywords) {
            val inJd = jdLower.contains(kw) || jobDescription.isBlank()
            val inProfile = userSkills.any { it.contains(kw) || kw.contains(it) }
            if (inJd && inProfile) {
                matched.add(kw.replaceFirstChar { it.uppercase() })
            } else if (inJd && !inProfile) {
                missing.add(kw.replaceFirstChar { it.uppercase() })
            }
        }

        val baseScore = if (matched.isNotEmpty()) {
            (70 + (matched.size * 5)).coerceIn(60, 96)
        } else {
            78
        }

        return AtsAnalysisResult(
            matchScore = baseScore,
            summary = "High keyword alignment with $company's $role profile. Adding a few targeted backend integration keywords will maximize ATS recruiter ranking.",
            matchedKeywords = if (matched.isNotEmpty()) matched else listOf("Kotlin", "Jetpack Compose", "Coroutines", "Room", "MVVM"),
            missingKeywords = if (missing.isNotEmpty()) missing.take(4) else listOf("CI/CD Pipelines", "Hilt / Dependency Injection", "Automated UI Testing"),
            suggestedBullets = listOf(
                "Engineered responsive Android application features using Kotlin and Jetpack Compose following MVVM architecture.",
                "Implemented persistent local caching with Room SQLite DB and reactive Kotlin StateFlow data streams.",
                "Integrated asynchronous network endpoints via Retrofit/Ktor with comprehensive error handling and retry logic."
            ),
            improvementTips = listOf(
                "Include concrete metrics (e.g. 'reduced load time by 30%', '4.8★ user rating').",
                "Ensure your GitHub profile (${profile.githubUrl}) has pinned repositories showing clean architecture.",
                "Add matching tech stack tags in your headline."
            )
        )
    }

    private fun getFallbackInterviewPrep(
        profile: UserProfileEntity,
        role: String,
        company: String
    ): InterviewPrepResult {
        val isFresher = profile.yearsOfExperience <= 0
        val q1 = if (isFresher) {
            InterviewQuestion(
                category = "Core Fundamentals & Kotlin",
                question = "Can you explain how Kotlin Coroutines differ from standard Java threads, and what Dispatchers.IO vs Dispatchers.Main are used for?",
                sampleAnswer = "Coroutines are lightweight user-space threads that don't block the underlying OS thread during suspension. Dispatchers.Main is reserved for UI updates, whereas Dispatchers.IO is optimized for disk I/O and network operations with an expandable pool.",
                evaluationTip = "Look for clear explanation of structured concurrency and non-blocking suspension."
            )
        } else {
            InterviewQuestion(
                category = "System Design & Architecture",
                question = "How would you architect a real-time order tracking or chat screen in Jetpack Compose to ensure smooth 60fps scrolling and offline resilience?",
                sampleAnswer = "I'd use a Single Source of Truth architecture where WebSocket/Network events update a local Room database, emitting reactive Flows to a ViewModel. The Compose UI observes immutable StateFlows with remember and derivedStateOf to prevent unnecessary recompositions.",
                evaluationTip = "Evaluates offline-first caching, Compose rendering optimization, and boundary separation."
            )
        }

        val q2 = InterviewQuestion(
            category = "Jetpack Compose & Reactive UI",
            question = "What is the difference between 'remember' and 'rememberSaveable', and how do you prevent recomposition loops?",
            sampleAnswer = "'remember' preserves state across recompositions, while 'rememberSaveable' survives activity recreation and configuration changes (e.g. screen rotation). Recomposition loops are avoided by keeping Composables side-effect free and hoisting mutable state up to ViewModel or callers.",
            evaluationTip = "Checks understanding of Compose state lifecycle, LaunchedEffect keys, and SnapshotState."
        )

        val q3 = InterviewQuestion(
            category = "Behavioral (STAR Method)",
            question = "Tell me about a challenging bug or technical problem you solved, and how you diagnosed it under a deadline.",
            sampleAnswer = "[Situation] During app development, users reported sporadic UI stuttering on large lists. [Task] I needed to profile frame drops without breaking ongoing feature delivery. [Action] I used Android Studio Profiler and Layout Inspector to identify unstable lambda parameters triggering full list recompositions. I refactored them to method references and immutable data classes. [Result] Frame drops dropped to 0% with 60fps rendering.",
            evaluationTip = "Look for structured Situation -> Task -> Action -> Result narrative with quantifiable outcome."
        )

        val q4 = InterviewQuestion(
            category = "Company & Culture Fit",
            question = "Why are you specifically interested in working at $company on the $role team?",
            sampleAnswer = "I admire $company's commitment to delivering reliable, user-first mobile products. The $role role directly aligns with my passion for building clean Kotlin architectures, and I'm eager to contribute high velocity and craftsmanship to your team.",
            evaluationTip = "Shows company research and genuine alignment with product values."
        )

        return InterviewPrepResult(
            role = role,
            company = company,
            questions = listOf(q1, q2, q3, q4),
            interviewStrategy = "Highlight your hands-on coding craftsmanship, articulate trade-offs clearly, and structure behavioral responses with the STAR method."
        )
    }

    private fun getFallbackOutreachTemplates(
        profile: UserProfileEntity,
        role: String,
        company: String
    ): List<OutreachTemplate> {
        val skillsShort = profile.skills.split(",").take(2).joinToString(" & ")
        return listOf(
            OutreachTemplate(
                title = "LinkedIn Connection Note (Under 300 chars)",
                type = "LINKEDIN_CONNECT",
                subject = "Connecting regarding $role",
                body = "Hi [Name], I admire your work at $company! As an Android engineer specializing in $skillsShort, I'm very excited about the $role role and would love to connect to follow your team's engineering journey.",
                tips = "Keep under 300 characters. Personalize with recipient's name."
            ),
            OutreachTemplate(
                title = "Direct Recruiter InMail / Cold Email",
                type = "COLD_EMAIL",
                subject = "Application Inquiry: $role - ${profile.fullName}",
                body = """
Hi [Recruiter Name],

I hope you're having a productive week!

I recently noticed the open $role position at $company and wanted to introduce myself. I specialize in $skillsShort, with hands-on experience building reactive mobile apps, clean MVVM architectures, and robust local caching.

Given $company's engineering goals, I'd love to chat briefly about how my skill set could add value to your team. My portfolio and code samples are available at ${profile.portfolioUrl}.

Thank you for your time,
${profile.fullName}
${profile.email} | ${profile.phone}
                """.trimIndent(),
                tips = "Send to technical recruiters or engineering managers on LinkedIn or via company email."
            ),
            OutreachTemplate(
                title = "Employee Referral Request",
                type = "REFERRAL_REQUEST",
                subject = "Quick question about engineering at $company",
                body = """
Hi [Name],

I came across your profile and noticed you're an engineer at $company. I've been following $company's mobile products and am preparing an application for the $role position.

Would you be open to a quick 5-minute chat about the team culture, or considering submitting an internal referral if my background in $skillsShort matches what the team is looking for? 

I'd be glad to share my resume and project links. Thank you so much!

Best,
${profile.fullName}
                """.trimIndent(),
                tips = "Reach out to 2nd-degree connections or alumni at the company."
            ),
            OutreachTemplate(
                title = "Post-Interview Thank You Email",
                type = "POST_INTERVIEW_THANKS",
                subject = "Thank you - $role Interview with ${profile.fullName}",
                body = """
Hi [Interviewer Name],

Thank you so much for taking the time to speak with me today about the $role position at $company. I really enjoyed our discussion around [insert specific topic discussed, e.g. Compose performance / team architecture].

Our conversation reinforced my enthusiasm for joining $company and contributing to your team's upcoming milestones.

Please let me know if you need any additional code samples or references.

Warm regards,
${profile.fullName}
                """.trimIndent(),
                tips = "Send within 24 hours of completing your interview round."
            )
        )
    }

    suspend fun parseResumeText(resumeText: String): ParsedResumeData = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            val prompt = """
                You are an expert AI Resume and ATS Parser. Parse this resume text and extract candidate profile details.
                Resume Text:
                \"\"\"
                $resumeText
                \"\"\"

                Return ONLY a JSON object with this structure:
                {
                  "fullName": "Candidate full name",
                  "headline": "Professional headline / title",
                  "targetRoles": "Comma separated target job roles",
                  "skills": "Comma separated top technical and professional skills",
                  "yearsOfExperience": 5,
                  "bioSummary": "2-sentence executive professional summary",
                  "atsScore": 92,
                  "education": "University / Degree",
                  "suggestedImprovements": ["Actionable improvement 1", "Actionable improvement 2"]
                }
            """.trimIndent()

            val rawJson = callGeminiApi(apiKey, prompt)
            val parsed = parseResumeJson(rawJson)
            if (parsed != null) return@withContext parsed
        }
        fallbackParseResume(resumeText)
    }

    private fun parseResumeJson(rawJson: String): ParsedResumeData? {
        return try {
            val clean = rawJson.replace("```json", "").replace("```", "").trim()
            val obj = JSONObject(clean)
            val improvementsList = mutableListOf<String>()
            val tipsArr = obj.optJSONArray("suggestedImprovements")
            if (tipsArr != null) {
                for (i in 0 until tipsArr.length()) {
                    improvementsList.add(tipsArr.optString(i))
                }
            }
            ParsedResumeData(
                fullName = obj.optString("fullName", "Candidate"),
                headline = obj.optString("headline", "Software Engineer"),
                targetRoles = obj.optString("targetRoles", "Software Engineer, Android Developer"),
                skills = obj.optString("skills", "Kotlin, Android, Git, REST API"),
                yearsOfExperience = obj.optInt("yearsOfExperience", 3),
                bioSummary = obj.optString("bioSummary", "Dedicated developer specializing in robust software design."),
                atsScore = obj.optInt("atsScore", 88).coerceIn(50, 99),
                education = obj.optString("education", "B.S. Computer Science"),
                suggestedImprovements = improvementsList
            )
        } catch (e: Exception) {
            Log.e("GeminiJobService", "Failed to parse resume JSON: ${e.message}")
            null
        }
    }

    private fun fallbackParseResume(resumeText: String): ParsedResumeData {
        val lines = resumeText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val name = lines.firstOrNull { !it.contains("http") && !it.contains("@") && it.length < 40 } ?: "Alex Morgan"
        val isFresher = resumeText.contains("Student", ignoreCase = true) || resumeText.contains("Fresher", ignoreCase = true) || resumeText.contains("Graduate", ignoreCase = true)
        val skillsFound = mutableListOf<String>()
        val commonSkills = listOf("Kotlin", "Java", "Jetpack Compose", "Android SDK", "Coroutines", "Flow", "Room", "Retrofit", "Python", "TypeScript", "React", "Node.js", "SQL", "Git", "Docker", "AWS", "CI/CD", "REST APIs", "Unit Testing", "Figma")
        for (skill in commonSkills) {
            if (resumeText.contains(skill, ignoreCase = true)) {
                skillsFound.add(skill)
            }
        }
        if (skillsFound.isEmpty()) {
            skillsFound.addAll(listOf("Kotlin", "Jetpack Compose", "Coroutines", "MVVM", "Room", "Git"))
        }

        return ParsedResumeData(
            fullName = name,
            headline = if (isFresher) "Aspiring Android Developer | Software Engineering Graduate" else "Senior Android & Mobile Systems Engineer",
            targetRoles = if (isFresher) "Associate Android Developer, Junior Software Engineer, Mobile Developer" else "Senior Android Engineer, Lead Mobile Developer, Mobile Architect",
            skills = skillsFound.joinToString(", "),
            yearsOfExperience = if (isFresher) 0 else 5,
            bioSummary = if (isFresher) "Recent Computer Science graduate with hands-on Android Kotlin projects and strong fundamentals in modern application architecture." else "Results-driven mobile engineer with extensive experience developing scalable Jetpack Compose applications.",
            atsScore = if (skillsFound.size >= 5) 92 else 85,
            education = "B.S. Computer Science / Software Engineering",
            suggestedImprovements = listOf(
                "Add quantified metrics (e.g., 'improved render speed by 35%')",
                "Ensure core keyword density for target ATS filters",
                "Include direct links to live GitHub repositories and demo APKs"
            )
        )
    }

    suspend fun generateResumeEmailDraft(
        profile: UserProfileEntity,
        company: String,
        jobTitle: String,
        recruiterName: String,
        recruiterEmail: String,
        notes: String
    ): ResumeEmailDraft = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val cleanRecruiter = if (recruiterName.isNotBlank()) recruiterName else "Hiring Team"
        val targetRole = if (jobTitle.isNotBlank()) jobTitle else "Mobile Software Engineer"
        val targetCompany = if (company.isNotBlank()) company else "your team"

        val subject = "Application & Resume: $targetRole - ${profile.fullName}"

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            val prompt = """
                Write a high-converting, polite direct recruiter email from job applicant ${profile.fullName} applying for $targetRole at $targetCompany.
                Recruiter Name: $cleanRecruiter
                Candidate Headline: ${profile.headline}
                Top Skills: ${profile.skills}
                Years of Experience: ${profile.yearsOfExperience}
                Portfolio/GitHub: ${profile.portfolioUrl} | ${profile.githubUrl}
                Additional Applicant Note: $notes

                Create:
                1. Subject line
                2. Engaging email body (under 160 words) emphasizing direct qualification fit and mentioning attached resume.
                
                Format response as JSON:
                {
                   "subject": "Subject line",
                   "body": "Email body with proper line breaks"
                }
            """.trimIndent()

            val rawJson = callGeminiApi(apiKey, prompt)
            try {
                val clean = rawJson.replace("```json", "").replace("```", "").trim()
                val obj = JSONObject(clean)
                val aiSubject = obj.optString("subject", subject)
                val aiBody = obj.optString("body")
                if (aiBody.isNotBlank()) {
                    return@withContext ResumeEmailDraft(
                        recipientEmail = recruiterEmail,
                        subject = aiSubject,
                        body = aiBody,
                        resumeSummarySnippet = "${profile.fullName} • ${profile.headline} • Skills: ${profile.skills.take(60)}..."
                    )
                }
            } catch (e: Exception) {
                Log.e("GeminiJobService", "Failed to parse email draft: ${e.message}")
            }
        }

        val defaultBody = """
Hi $cleanRecruiter,

I hope you are having a wonderful week!

I am reaching out to express my strong interest in the $targetRole position at $targetCompany. With ${profile.yearsOfExperience}+ years of experience and specialized focus in ${profile.skills.split(",").take(4).joinToString(", ")}, I have built production applications with a strong emphasis on clean architecture and high performance.

I have attached my resume (${profile.resumeFileName}) for your review. You can also view my live projects and code repositories at ${profile.portfolioUrl} and ${profile.githubUrl}.

I would welcome the opportunity to discuss how my background aligns with $targetCompany's upcoming milestones.

Thank you for your consideration!

Best regards,
${profile.fullName}
${profile.email} | ${profile.phone}
${profile.linkedinUrl}
        """.trimIndent()

        ResumeEmailDraft(
            recipientEmail = recruiterEmail,
            subject = subject,
            body = defaultBody,
            resumeSummarySnippet = "${profile.fullName} • ${profile.headline} • Skills: ${profile.skills.take(60)}..."
        )
    }
}
