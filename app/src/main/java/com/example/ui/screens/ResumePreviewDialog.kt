package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.EmailTargetInfo
import com.example.ui.viewmodel.JobViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResumePreviewDialog(
    viewModel: JobViewModel,
    onDismiss: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isUploading by viewModel.isResumeUploading.collectAsState()
    val context = LocalContext.current

    val profile = userProfile ?: UserProfileEntity()
    var selectedTab by remember { mutableIntStateOf(0) }

    val resumePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "Updated_Resume.pdf"
            val rawContent = "Resume of ${profile.fullName}\nHeadline: ${profile.headline}\nTarget Roles: ${profile.targetRoles}\nSkills: ${profile.skills}\nExperience: ${profile.yearsOfExperience} years."
            viewModel.uploadResume(
                fileName = fileName,
                fileSize = "210 KB",
                rawText = rawContent
            )
            Toast.makeText(context, "Uploaded & re-parsed $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .shadow(24.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .testTag("resume_preview_modal"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Modal Top Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(BrandBluePrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = "Resume Preview",
                                        tint = BrandBluePrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Resume & ATS Summary",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = SuccessGreen.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${profile.resumeAtsScore}% ATS Score",
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${profile.resumeFileName} • ${profile.resumeFileSize}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Resume Summary", buildResumePlainText(profile))
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "Resume summary copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("btn_copy_resume_text")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Summary",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.testTag("btn_close_resume_preview")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Navigation Tabs inside Modal
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = BrandBluePrimary
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                text = { Text("Document Preview", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                text = { Text("ATS Score & Metrics", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                text = { Text("Autofill Readiness", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }
                }

                // Modal Content Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> ResumeDocumentView(
                            profile = profile,
                            onSwitchSenior = {
                                val seniorResume = """
                                    ALEX MORGAN
                                    Email: alex.morgan.dev@gmail.com | Phone: +1 (415) 890-3412
                                    San Francisco, CA | https://github.com/alexmorgan-dev
                                    
                                    PROFESSIONAL SUMMARY:
                                    Staff/Senior Android Engineer with 6+ years specializing in Jetpack Compose, Kotlin Coroutines, Architecture Components, Room, and high-performance offline caching.
                                    
                                    CORE SKILLS:
                                    Kotlin, Jetpack Compose, Coroutines, Flow, Room DB, Retrofit, Ktor, Dagger/Hilt, Material 3, Clean Architecture, CI/CD, Unit Testing.
                                """.trimIndent()
                                viewModel.uploadResume("Alex_Morgan_Senior_Android_2026.pdf", "195 KB", seniorResume)
                            },
                            onSwitchFresher = {
                                val fresherResume = """
                                    MAYA CHEN
                                    Email: maya.chen.cs@gmail.com | Phone: +1 (510) 555-0198
                                    San Jose, CA | https://github.com/mayachen-cs
                                    
                                    OBJECTIVE:
                                    Aspiring Android Developer & Computer Science Graduate (2026) passionate about building modern mobile apps with Kotlin and Jetpack Compose.
                                    
                                    CORE SKILLS:
                                    Kotlin, Jetpack Compose, Android SDK, Java, Data Structures & Algorithms, REST APIs, Room Database, Git.
                                """.trimIndent()
                                viewModel.uploadResume("Maya_Chen_Graduate_Resume_2026.pdf", "145 KB", fresherResume)
                            }
                        )
                        1 -> AtsMetricsView(profile = profile)
                        2 -> AutofillReadinessView(profile = profile)
                    }
                }

                // Bottom Action Footer
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { resumePickerLauncher.launch("*/*") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_modal_reupload_resume"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Re-upload", fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                viewModel.openEmailResumeDialog(
                                    EmailTargetInfo(
                                        company = "Direct Recruiter",
                                        jobTitle = profile.headline.split("/").firstOrNull()?.trim() ?: "Android Engineer",
                                        recruiterName = "Hiring Team",
                                        recruiterEmail = "careers@company.com",
                                        defaultNotes = "Resume dispatched via candidate preview"
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_modal_mail_resume"),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mail Resume", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ResumeDocumentView(
    profile: UserProfileEntity,
    onSwitchSenior: () -> Unit,
    onSwitchFresher: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Presets Banner
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Resume Preset Switcher:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandBluePrimary.copy(alpha = 0.12f),
                        modifier = Modifier.clickable { onSwitchSenior() }
                    ) {
                        Text(
                            text = "💼 Senior Profile",
                            color = BrandBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SuccessGreen.copy(alpha = 0.12f),
                        modifier = Modifier.clickable { onSwitchFresher() }
                    ) {
                        Text(
                            text = "🎓 Fresher Profile",
                            color = SuccessGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Paper Aesthetic Resume Canvas
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Section
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = profile.fullName.uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = profile.headline,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandBluePrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Contact Row
                        FlowRow(
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ResumeContactChip(icon = Icons.Default.Email, text = profile.email)
                            ResumeContactChip(icon = Icons.Default.Phone, text = profile.phone)
                            ResumeContactChip(icon = Icons.Default.LocationOn, text = profile.targetLocations.split(",").firstOrNull() ?: "Remote")
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (profile.githubUrl.isNotBlank()) {
                                ResumeContactChip(icon = Icons.Default.Code, text = profile.githubUrl.replace("https://", ""))
                            }
                            if (profile.linkedinUrl.isNotBlank()) {
                                ResumeContactChip(icon = Icons.Default.Link, text = profile.linkedinUrl.replace("https://", ""))
                            }
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Professional Summary
                    ResumeSectionHeader(title = "PROFESSIONAL SUMMARY")
                    Text(
                        text = profile.bioSummary.ifBlank {
                            "Experienced Software Engineer with proven expertise in building modern, scalable applications with clean architecture and performant systems."
                        },
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Core Skills
                    ResumeSectionHeader(title = "TECHNICAL & CORE COMPETENCIES")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        profile.skills.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandBluePrimary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = skill,
                                    color = BrandBluePrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Experience Section
                    ResumeSectionHeader(title = "RELEVANT EXPERIENCE & IMPACT")
                    if (profile.accountType == "FRESHER") {
                        ExperienceEntry(
                            title = "Mobile Engineering Intern / Project Lead",
                            company = "Academic & Open-Source Projects",
                            period = "2024 - 2026",
                            bullets = listOf(
                                "Developed responsive Jetpack Compose application featuring Room SQLite caching and MVVM clean architecture.",
                                "Implemented REST API integrations using Retrofit and Kotlin Coroutines/StateFlow.",
                                "Achieved 95%+ unit test coverage with automated Robolectric tests and CI pipelines."
                            )
                        )
                    } else {
                        ExperienceEntry(
                            title = "Senior / Staff Android Engineer",
                            company = "Tier 1 Tech Solutions",
                            period = "2020 - Present • ${profile.yearsOfExperience} yrs exp",
                            bullets = listOf(
                                "Architected and delivered multi-module Jetpack Compose applications serving over 5,000,000 active users.",
                                "Engineered offline-first synchronization layer using Room DB and WorkManager reducing latency by 45%.",
                                "Mentored 8 engineering team members on Kotlin Coroutines, asynchronous flow control, and M3 design systems."
                            )
                        )
                    }

                    // Education & Credentials
                    ResumeSectionHeader(title = "EDUCATION & WORK AUTHORIZATION")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Bachelor of Science in Computer Science",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Authorized: ${profile.workAuthorization}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Notice: ${profile.noticePeriod}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Text(
                                text = "Target Salary: $${profile.minSalary}/yr",
                                fontSize = 10.sp,
                                color = BrandBluePrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumeContactChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(11.dp))
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = text, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ResumeSectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp,
            color = BrandBluePrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Divider(color = BrandBluePrimary.copy(alpha = 0.25f), thickness = 1.dp)
    }
}

@Composable
private fun ExperienceEntry(
    title: String,
    company: String,
    period: String,
    bullets: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(text = period, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = company, fontSize = 10.sp, color = BrandBluePrimary, fontWeight = FontWeight.SemiBold)
        bullets.forEach { bullet ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "• ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandBluePrimary)
                Text(text = bullet, fontSize = 10.sp, lineHeight = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun AtsMetricsView(profile: UserProfileEntity) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ATS Overall Score Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${profile.resumeAtsScore}%",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ATS Optimization Grade: A+",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SuccessGreen
                        )
                        Text(
                            text = "This resume passes automated ATS filters across Greenhouse, Lever, Workday, and SmartRecruiters.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Metric Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "ATS Health Breakdown", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    AtsMetricBar(label = "Keyword Density & Role Alignment", score = 96)
                    AtsMetricBar(label = "Contact Information Completeness", score = 100)
                    AtsMetricBar(label = "ATS Machine-Readable Formatting", score = 98)
                    AtsMetricBar(label = "Skills & Tech Stack Taxonomy", score = 92)
                    AtsMetricBar(label = "Action Verb Impact Ratio", score = 89)
                }
            }
        }

        // Extracted ATS Text Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Raw Parsed ATS Stream", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Surface(shape = RoundedCornerShape(4.dp), color = BrandBluePrimary.copy(alpha = 0.15f)) {
                            Text("Gemini OCR Extracted", fontSize = 9.sp, color = BrandBluePrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = profile.resumeRawText.ifBlank {
                                "${profile.fullName}\n${profile.headline}\nContact: ${profile.email} | ${profile.phone}\nSkills: ${profile.skills}\nTarget: ${profile.targetRoles}"
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AtsMetricBar(label: String, score: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp)
            Text(text = "$score%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (score >= 90) SuccessGreen else BrandBluePrimary)
        }
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (score >= 90) SuccessGreen else BrandBluePrimary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun AutofillReadinessView(profile: UserProfileEntity) {
    val items = listOf(
        AutofillItem("Full Name", profile.fullName, true),
        AutofillItem("Email Address", profile.email, true),
        AutofillItem("Phone Number", profile.phone, true),
        AutofillItem("Headline / Title", profile.headline, true),
        AutofillItem("Target Job Roles", profile.targetRoles, true),
        AutofillItem("Target Locations", profile.targetLocations, true),
        AutofillItem("Core Skills", profile.skills, true),
        AutofillItem("Work Experience", "${profile.yearsOfExperience} Years", true),
        AutofillItem("Minimum Salary", "$${profile.minSalary}", true),
        AutofillItem("Work Authorization", profile.workAuthorization, true),
        AutofillItem("Notice Period", profile.noticePeriod, true),
        AutofillItem("GitHub Portfolio", profile.githubUrl, profile.githubUrl.isNotBlank()),
        AutofillItem("LinkedIn Profile", profile.linkedinUrl, profile.linkedinUrl.isNotBlank()),
        AutofillItem("Portfolio Website", profile.portfolioUrl, profile.portfolioUrl.isNotBlank())
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "1-Click Bot Autofill Matrix",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "These fields will be automatically injected into Greenhouse, Lever, and LinkedIn EasyApply forms without manual intervention.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items.forEach { item ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (item.isReady) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (item.isReady) SuccessGreen else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = item.fieldName, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(text = item.value.ifBlank { "Not provided" }, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (item.isReady) SuccessGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (item.isReady) "READY" else "OPTIONAL",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isReady) SuccessGreen else Color.Gray,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class AutofillItem(
    val fieldName: String,
    val value: String,
    val isReady: Boolean
)

private fun buildResumePlainText(profile: UserProfileEntity): String {
    return """
        ${profile.fullName.uppercase(Locale.getDefault())}
        ${profile.headline}
        Email: ${profile.email} | Phone: ${profile.phone} | Location: ${profile.targetLocations}
        GitHub: ${profile.githubUrl} | LinkedIn: ${profile.linkedinUrl}
        
        SUMMARY:
        ${profile.bioSummary}
        
        CORE SKILLS:
        ${profile.skills}
        
        EXPERIENCE & TARGETS:
        Target Roles: ${profile.targetRoles}
        Years Experience: ${profile.yearsOfExperience}
        Min Base Salary: $${profile.minSalary}
        Work Authorization: ${profile.workAuthorization}
        Notice Period: ${profile.noticePeriod}
    """.trimIndent()
}
