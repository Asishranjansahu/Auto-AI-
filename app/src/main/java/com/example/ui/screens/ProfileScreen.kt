package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ProfileScreen(
    viewModel: JobViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val notifConfig by viewModel.notificationReminderConfig.collectAsState()
    val allSentEmails by viewModel.allSentResumeEmails.collectAsState()
    val isResumeUploading by viewModel.isResumeUploading.collectAsState()
    val context = LocalContext.current

    val profile = userProfile ?: UserProfileEntity()
    val isNotifEnabled = notifConfig?.isEnabled ?: true
    val notifHour = notifConfig?.reminderHour ?: 9
    val notifMinute = notifConfig?.reminderMinute ?: 0
    val formattedHour = if (notifHour % 12 == 0) 12 else notifHour % 12
    val amPm = if (notifHour >= 12) "PM" else "AM"
    val timeFormatted = String.format(java.util.Locale.getDefault(), "%02d:%02d %s", formattedHour, notifMinute, amPm)

    var fullName by remember(profile) { mutableStateOf(profile.fullName) }
    var email by remember(profile) { mutableStateOf(profile.email) }
    var phone by remember(profile) { mutableStateOf(profile.phone) }
    var headline by remember(profile) { mutableStateOf(profile.headline) }
    var targetRoles by remember(profile) { mutableStateOf(profile.targetRoles) }
    var targetLocations by remember(profile) { mutableStateOf(profile.targetLocations) }
    var minSalary by remember(profile) { mutableStateOf(profile.minSalary.toString()) }
    var yearsExp by remember(profile) { mutableStateOf(profile.yearsOfExperience.toString()) }
    var skills by remember(profile) { mutableStateOf(profile.skills) }
    var bioSummary by remember(profile) { mutableStateOf(profile.bioSummary) }
    var portfolioUrl by remember(profile) { mutableStateOf(profile.portfolioUrl) }
    var githubUrl by remember(profile) { mutableStateOf(profile.githubUrl) }
    var linkedinUrl by remember(profile) { mutableStateOf(profile.linkedinUrl) }
    var workAuth by remember(profile) { mutableStateOf(profile.workAuthorization) }
    var noticePeriod by remember(profile) { mutableStateOf(profile.noticePeriod) }

    // File picker launcher for Resumes
    val resumePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = queryFileName(context, uri) ?: "My_Resume.pdf"
            val rawContent = "Resume of ${profile.fullName}\nHeadline: ${profile.headline}\nTarget Roles: ${profile.targetRoles}\nSkills: ${profile.skills}\nExperience: ${profile.yearsOfExperience} years."
            viewModel.uploadResume(
                fileName = fileName,
                fileSize = "185 KB",
                rawText = rawContent
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Banner & Account Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(if (profile.accountType == "FRESHER") SuccessGreen else BrandBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (profile.accountType == "FRESHER") SuccessGreen.copy(alpha = 0.15f) else BrandBluePrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (profile.accountType == "FRESHER") "FRESHER" else "EXPERIENCED",
                                        color = if (profile.accountType == "FRESHER") SuccessGreen else BrandBluePrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(text = email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                            Text(text = headline, style = MaterialTheme.typography.bodySmall, color = BrandBluePrimary, fontSize = 11.sp, maxLines = 1)
                        }
                    }

                    // Account Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.openAuthDialog() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_switch_or_login"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (profile.isLoggedIn) "Switch Account" else "Sign In / Sign Up", fontSize = 11.sp)
                        }

                        if (profile.isLoggedIn) {
                            OutlinedButton(
                                onClick = { viewModel.logout() },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sign Out", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // Resume Upload & AI Parsing Section
        // ==========================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BrandBluePrimary.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(BrandBluePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Candidate Resume & ATS Score",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Auto-parsed with Gemini AI for autofill",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreen.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${profile.resumeAtsScore}% ATS Score",
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Uploaded File Info Card & Thumbnail Action
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openResumePreviewDialog() }
                            .testTag("card_resume_thumbnail_preview"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrandBluePrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "Resume Document",
                                    tint = BrandBluePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile.resumeFileName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(profile.resumeUploadedDate))
                                Text(
                                    text = "${profile.resumeFileSize} • Updated $dateStr",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandBluePrimary.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Preview",
                                        tint = BrandBluePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Preview",
                                        color = BrandBluePrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Resume Actions: Preview Resume, Upload File, Mail Resume
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Preview Resume Button
                        Button(
                            onClick = { viewModel.openResumePreviewDialog() },
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("btn_preview_resume_modal"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Preview Resume", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // File picker button
                        OutlinedButton(
                            onClick = {
                                resumePickerLauncher.launch("*/*")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_upload_resume_file"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isResumeUploading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Upload", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Mail Resume Direct
                        Button(
                            onClick = {
                                viewModel.openEmailResumeDialog(
                                    EmailTargetInfo(
                                        company = "Stripe",
                                        jobTitle = "Staff Android Engineer",
                                        recruiterName = "Sarah Jenkins",
                                        recruiterEmail = "sjenkins@stripe.com"
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(0.9f)
                                .testTag("btn_mail_resume_recruiter"),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Mail", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // AI Resume Parser Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "AI Quick Parse Templates:",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandBluePrimary.copy(alpha = 0.1f),
                            modifier = Modifier.clickable {
                                val seniorResume = """
                                    ALEX MORGAN
                                    Email: alex.morgan.dev@gmail.com | Phone: +1 (415) 890-3412
                                    San Francisco, CA | https://github.com/alexmorgan-dev
                                    
                                    PROFESSIONAL SUMMARY:
                                    Staff/Senior Android Engineer with 6+ years specializing in Jetpack Compose, Kotlin Coroutines, Architecture Components, Room, and high-performance offline caching.
                                    
                                    CORE SKILLS:
                                    Kotlin, Jetpack Compose, Coroutines, Flow, Room DB, Retrofit, Ktor, Dagger/Hilt, Material 3, Clean Architecture, CI/CD, Unit Testing.
                                    
                                    EXPERIENCE:
                                    Lead Android Engineer at TechCorp (2021 - Present)
                                    - Led Compose migration across 30+ screens reducing crash rates by 42%.
                                    - Architected real-time background sync engine with Room & WorkManager.
                                """.trimIndent()
                                viewModel.uploadResume("Alex_Morgan_Senior_Android_2026.pdf", "195 KB", seniorResume)
                            }
                        ) {
                            Text("💼 Senior Android", color = BrandBluePrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SuccessGreen.copy(alpha = 0.1f),
                            modifier = Modifier.clickable {
                                val fresherResume = """
                                    MAYA CHEN
                                    Email: maya.chen.cs@gmail.com | Phone: +1 (510) 555-0198
                                    San Jose, CA | https://github.com/mayachen-cs
                                    
                                    OBJECTIVE:
                                    Aspiring Android Developer & Computer Science Graduate (2026) passionate about building modern mobile apps with Kotlin and Jetpack Compose.
                                    
                                    CORE SKILLS:
                                    Kotlin, Jetpack Compose, Android SDK, Java, Data Structures & Algorithms, REST APIs, Room Database, Git.
                                    
                                    PROJECTS:
                                    Campus Connect App (Compose, Room, Coroutines)
                                    - Created student event discovery app with offline caching and Material 3 UI.
                                """.trimIndent()
                                viewModel.uploadResume("Maya_Chen_Graduate_Resume_2026.pdf", "145 KB", fresherResume)
                            }
                        ) {
                            Text("🎓 Fresher / Grad", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }

        // ==========================================
        // Sent Resume Emails History
        // ==========================================
        if (allSentEmails.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.History, contentDescription = null, tint = BrandBluePrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sent Resume Emails (${allSentEmails.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        allSentEmails.take(4).forEach { emailItem ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(SuccessGreen.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "${emailItem.jobTitle} @ ${emailItem.company}", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(text = "To: ${emailItem.recipientEmail} (${emailItem.recipientName})", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        val timeStr = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(emailItem.timestamp))
                                        Text(text = "Sent with ${emailItem.resumeFileName} • $timeStr", fontSize = 9.sp, color = SuccessGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Profile Preset Switcher (Fresher vs Experienced)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = BrandBluePrimary.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1-Click Profile Mode Presets",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandBluePrimary
                    )
                    Text(
                        text = "Quickly switch matching engines and autofill data between Fresher/Graduate and Senior roles.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.loadFresherPreset() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🎓 Fresher / New Grad", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.loadExperiencedPreset() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("💼 Experienced", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Target Roles & Compensation
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Job Search Target Criteria", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = targetRoles,
                        onValueChange = { targetRoles = it },
                        label = { Text("Target Roles (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )
                    OutlinedTextField(
                        value = targetLocations,
                        onValueChange = { targetLocations = it },
                        label = { Text("Target Locations") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = minSalary,
                            onValueChange = { minSalary = it },
                            label = { Text("Min Base Salary ($)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = yearsExp,
                            onValueChange = { yearsExp = it },
                            label = { Text("Years Experience") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Skills Tags & Auto-Matching
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Core Skills & Matching Keywords", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("Skills List (comma separated)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        skills.split(",").map { it.trim() }.filter { it.isNotBlank() }.forEach { skill ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandBluePrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = skill,
                                    color = BrandBluePrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Personal Details & Contact
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Candidate Contact & Links", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = githubUrl, onValueChange = { githubUrl = it }, label = { Text("GitHub URL") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = portfolioUrl, onValueChange = { portfolioUrl = it }, label = { Text("Portfolio Website") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = linkedinUrl, onValueChange = { linkedinUrl = it }, label = { Text("LinkedIn Profile URL") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = workAuth, onValueChange = { workAuth = it }, label = { Text("Work Authorization") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = noticePeriod, onValueChange = { noticePeriod = it }, label = { Text("Notice Period") }, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // Daily Reminder & Notification Settings Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_notification_settings_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isNotifEnabled) BrandBluePrimary else Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isNotifEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Daily Reminder Notifications",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isNotifEnabled) "Scheduled for $timeFormatted daily" else "Notifications currently paused",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isNotifEnabled) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isNotifEnabled,
                            onCheckedChange = { checked ->
                                viewModel.toggleNotificationReminder(checked)
                            },
                            colors = androidx.compose.material3.SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BrandBluePrimary
                            ),
                            modifier = Modifier.testTag("profile_reminder_switch")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.openNotificationSettingsDialog() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_configure_reminders"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Configure Schedule", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.sendTestNotification() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_test_reminder_push")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Push", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    val updated = profile.copy(
                        fullName = fullName,
                        email = email,
                        phone = phone,
                        headline = headline,
                        targetRoles = targetRoles,
                        targetLocations = targetLocations,
                        minSalary = minSalary.toIntOrNull() ?: 140000,
                        yearsOfExperience = yearsExp.toIntOrNull() ?: 5,
                        skills = skills,
                        bioSummary = bioSummary,
                        portfolioUrl = portfolioUrl,
                        githubUrl = githubUrl,
                        linkedinUrl = linkedinUrl,
                        workAuthorization = workAuth,
                        noticePeriod = noticePeriod
                    )
                    viewModel.updateProfile(updated)
                    Toast.makeText(context, "Candidate profile saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_profile_button"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Profile & Update Matching Engine", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helper to query file name from content URI
private fun queryFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    try {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
    } catch (e: Exception) {
        // Fallback
    }
    return name ?: uri.lastPathSegment
}

