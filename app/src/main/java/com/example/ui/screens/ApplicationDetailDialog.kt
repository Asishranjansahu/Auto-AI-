package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.gemini.AtsAnalysisResult
import com.example.data.gemini.InterviewPrepResult
import com.example.data.gemini.OutreachTemplate
import com.example.data.local.JobApplicationEntity
import com.example.ui.components.MatchScoreBadge
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.StatusInterview
import com.example.ui.theme.StatusOffer
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.JobViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplicationDetailDialog(
    application: JobApplicationEntity,
    viewModel: JobViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val generatedEmail by viewModel.generatedEmailText.collectAsState()
    val isGeneratingEmail by viewModel.isGeneratingEmail.collectAsState()

    val atsResult by viewModel.atsAnalysisResult.collectAsState()
    val isAnalyzingAts by viewModel.isAnalyzingAts.collectAsState()

    val interviewPrep by viewModel.interviewPrepResult.collectAsState()
    val isGeneratingInterview by viewModel.isGeneratingInterview.collectAsState()

    val outreachTemplates by viewModel.outreachTemplates.collectAsState()
    val isGeneratingOutreach by viewModel.isGeneratingOutreach.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Overview, 1: ATS Scan, 2: Cover Letter, 3: Interview Prep, 4: Outreach, 5: Follow-Up & Notes

    var notesText by remember { mutableStateOf(application.notes) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .heightIn(max = 740.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("application_detail_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PlatformBadge(platformId = application.platformId)
                            Spacer(modifier = Modifier.width(8.dp))
                            MatchScoreBadge(score = application.matchScore)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = application.jobTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = application.company,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandBluePrimary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_detail_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pipeline Status Fast Selector
                Text(
                    text = "Application Pipeline Stage:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("APPLIED", "SCREENING", "INTERVIEW", "OFFER", "REJECTED").forEach { stage ->
                        val isSelected = application.status.equals(stage, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) BrandBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .testTag("status_selector_$stage")
                        ) {
                            TextButton(
                                onClick = { viewModel.updateApplicationStatus(application.id, stage) },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (stage) {
                                        "APPLIED" -> "Applied"
                                        "SCREENING" -> "Screen"
                                        "INTERVIEW" -> "Interview"
                                        "OFFER" -> "Offer"
                                        else -> "Closed"
                                    },
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Navigation Tabs
                val tabItems = listOf(
                    "Overview",
                    "ATS Scan",
                    "Cover Letter",
                    "Interview Prep",
                    "Outreach",
                    "Follow-Up & Notes"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .horizontalScroll(rememberScrollState())
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tabItems.forEachIndexed { index, title ->
                        val isTabSelected = selectedTab == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isTabSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(
                                onClick = { selectedTab = index },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = title,
                                    color = if (isTabSelected) BrandBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        0 -> {
                            // Overview Tab
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                DetailRow(Icons.Default.LocationOn, "Location", application.location)
                                DetailRow(Icons.Default.MonetizationOn, "Salary", application.salaryRange)
                                DetailRow(Icons.Default.Work, "Employment Type", application.jobType)
                                DetailRow(Icons.Default.Event, "Applied On", dateFormat.format(Date(application.appliedDate)))
                                if (application.interviewDate != null) {
                                    DetailRow(Icons.Default.Event, "Interview Scheduled", dateFormat.format(Date(application.interviewDate)))
                                }
                                if (application.recruiterName.isNotBlank()) {
                                    DetailRow(Icons.Default.Person, "Recruiter", "${application.recruiterName} (${application.recruiterEmail})")
                                }

                                // Quick Mail Resume Action Button
                                Button(
                                    onClick = {
                                        viewModel.openEmailResumeDialog(
                                            com.example.ui.viewmodel.EmailTargetInfo(
                                                company = application.company,
                                                jobTitle = application.jobTitle,
                                                recruiterName = application.recruiterName,
                                                recruiterEmail = application.recruiterEmail,
                                                defaultNotes = application.notes
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("dialog_mail_resume_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Mail Resume Directly to Recruiter / Company", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Job Description / Requirements:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = application.jobDescription.ifBlank { "High-priority position matched against your candidate profile skills and auto-applied with optimized resume." },
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(12.dp),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                        1 -> {
                            // ATS Scan Tab
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Gemini ATS Match Scanner",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandBluePrimary
                                        )
                                        Text(
                                            text = "Analyze keyword alignment & recruiter ranking for your resume.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.analyzeAtsResume(
                                                role = application.jobTitle,
                                                company = application.company,
                                                jobDescription = application.jobDescription
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                                        enabled = !isAnalyzingAts
                                    ) {
                                        if (isAnalyzingAts) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                        } else {
                                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Scan ATS", fontSize = 12.sp)
                                        }
                                    }
                                }

                                if (atsResult == null && !isAnalyzingAts) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = BrandBluePrimary, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Tap 'Scan ATS' to evaluate your profile keywords against ${application.company}'s requirements.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                atsResult?.let { res ->
                                    // Match Score Card
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = "ATS Match Score", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(text = "${res.matchScore}% Compatibility", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                                Text(text = res.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                            }
                                        }
                                    }

                                    // Matched Keywords
                                    Text(text = "Matched Keywords (${res.matchedKeywords.size})", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        res.matchedKeywords.forEach { kw ->
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = SuccessGreen.copy(alpha = 0.12f),
                                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(SuccessGreen.copy(alpha = 0.5f), SuccessGreen.copy(alpha = 0.2f))))
                                            ) {
                                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(text = kw, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SuccessGreen)
                                                }
                                            }
                                        }
                                    }

                                    // Missing / Recommended Keywords
                                    if (res.missingKeywords.isNotEmpty()) {
                                        Text(text = "Missing High-Value Keywords", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = StatusOffer)
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            res.missingKeywords.forEach { kw ->
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = StatusOffer.copy(alpha = 0.12f)
                                                ) {
                                                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = StatusOffer, modifier = Modifier.size(12.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(text = kw, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Suggested Resume Bullets
                                    Text(text = "Suggested Resume Bullets (Tailored for ATS)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    res.suggestedBullets.forEach { bullet ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = "• $bullet", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), lineHeight = 16.sp)
                                                IconButton(
                                                    onClick = {
                                                        copyToClipboard(context, "Resume Bullet", bullet)
                                                        Toast.makeText(context, "Bullet point copied!", Toast.LENGTH_SHORT).show()
                                                    }
                                                ) {
                                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Cover Letter Tab
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tailored Cover Letter",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandBluePrimary
                                    )
                                    IconButton(
                                        onClick = {
                                            copyToClipboard(context, "Cover Letter", application.tailoredCoverLetter)
                                            Toast.makeText(context, "Cover letter copied!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy")
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = application.tailoredCoverLetter.ifBlank { "No tailored cover letter recorded for this application." },
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(12.dp),
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                        3 -> {
                            // Interview Prep Tab
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "AI Interview Prep & Questions",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandBluePrimary
                                        )
                                        Text(
                                            text = "Technical, STAR behavioral, & architecture questions for ${application.company}.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.generateInterviewPrep(application.jobTitle, application.company) },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                                        enabled = !isGeneratingInterview
                                    ) {
                                        if (isGeneratingInterview) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                        } else {
                                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Prep AI", fontSize = 12.sp)
                                        }
                                    }
                                }

                                if (interviewPrep == null && !isGeneratingInterview) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = BrandBluePrimary, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Tap 'Prep AI' to generate predicted questions and model STAR answers for ${application.jobTitle}.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                interviewPrep?.let { prep ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = BrandBluePrimary.copy(alpha = 0.08f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "💡 Strategy: ${prep.interviewStrategy}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BrandBluePrimary,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }

                                    prep.questions.forEachIndexed { idx, q ->
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = BrandBlueDark.copy(alpha = 0.15f)
                                                    ) {
                                                        Text(
                                                            text = q.category,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = BrandBluePrimary,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            copyToClipboard(context, "Interview Question", "Q: ${q.question}\n\nAnswer: ${q.sampleAnswer}")
                                                            Toast.makeText(context, "Question & answer copied!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(text = "Q${idx + 1}: ${q.question}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(text = "Sample Answer:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                                Text(text = q.sampleAnswer, style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
                                                if (q.evaluationTip.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(text = "Recruiter Tip: ${q.evaluationTip}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        4 -> {
                            // Cold Outreach Tab
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Smart Outreach & InMail",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandBluePrimary
                                        )
                                        Text(
                                            text = "Personalized templates for LinkedIn connect, recruiter cold email, and referrals.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.generateOutreachTemplates(application.jobTitle, application.company) },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                                        enabled = !isGeneratingOutreach
                                    ) {
                                        if (isGeneratingOutreach) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                        } else {
                                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Generate", fontSize = 12.sp)
                                        }
                                    }
                                }

                                if (outreachTemplates.isEmpty() && !isGeneratingOutreach) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = BrandBluePrimary, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Tap 'Generate' to create high-conversion messages to send directly to engineers and recruiters at ${application.company}.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                outreachTemplates.forEach { tpl ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = tpl.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BrandBluePrimary)
                                                IconButton(
                                                    onClick = {
                                                        val fullText = if (tpl.subject.isNotBlank()) "Subject: ${tpl.subject}\n\n${tpl.body}" else tpl.body
                                                        copyToClipboard(context, tpl.title, fullText)
                                                        Toast.makeText(context, "${tpl.title} copied!", Toast.LENGTH_SHORT).show()
                                                    }
                                                ) {
                                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                                }
                                            }
                                            if (tpl.subject.isNotBlank()) {
                                                Text(text = "Subject: ${tpl.subject}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surface,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(text = tpl.body, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp), lineHeight = 16.sp)
                                            }
                                            if (tpl.tips.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(text = "💡 Tip: ${tpl.tips}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        5 -> {
                            // Follow-Up & Notes Tab
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "AI Recruiter Follow-up Assistant",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBluePrimary
                                )
                                Button(
                                    onClick = { viewModel.generateFollowUpEmail(application) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("generate_followup_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                                    enabled = !isGeneratingEmail
                                ) {
                                    if (isGeneratingEmail) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Drafting with Gemini AI...")
                                    } else {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Draft Recruiter Follow-Up Email")
                                    }
                                }

                                if (generatedEmail != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Generated Draft:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        IconButton(
                                            onClick = {
                                                copyToClipboard(context, "Follow Up Email", generatedEmail ?: "")
                                                Toast.makeText(context, "Draft email copied!", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy")
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = SuccessGreen.copy(alpha = 0.08f),
                                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(SuccessGreen.copy(alpha = 0.5f), Color.Transparent))),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = generatedEmail ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(12.dp),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }

                                if (application.screeningAnswers.isNotBlank()) {
                                    Text(
                                        text = "Autofilled Screening Answers",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = application.screeningAnswers,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(12.dp),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "Interview & Recruiter Notes",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                OutlinedTextField(
                                    value = notesText,
                                    onValueChange = { notesText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("application_notes_field"),
                                    placeholder = { Text("Add notes about interview questions, tech round feedback, offer details...") },
                                    minLines = 3,
                                    maxLines = 6
                                )
                                Button(
                                    onClick = {
                                        viewModel.updateInterviewDetails(application.id, application.interviewDate, notesText)
                                        Toast.makeText(context, "Notes saved!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save Notes")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = StatusRejected)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Remove")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("Done", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Application?") },
            text = { Text("Are you sure you want to remove ${application.jobTitle} at ${application.company} from your tracker?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteApplication(application.id)
                        showDeleteConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = StatusRejected)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = BrandBluePrimary)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}
