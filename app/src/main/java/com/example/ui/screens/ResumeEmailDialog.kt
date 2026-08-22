package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeEmailDialog(
    target: EmailTargetInfo?,
    viewModel: JobViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val profile = userProfile ?: UserProfileEntity()
    val scope = rememberCoroutineScope()

    var recipientEmail by remember(target) { mutableStateOf(target?.recruiterEmail ?: "") }
    var recipientName by remember(target) { mutableStateOf(target?.recruiterName ?: "") }
    var company by remember(target) { mutableStateOf(target?.company ?: "") }
    var jobTitle by remember(target) { mutableStateOf(target?.jobTitle ?: "") }
    var subject by remember(target) {
        mutableStateOf(
            if (jobTitle.isNotBlank()) "Application & Resume: $jobTitle - ${profile.fullName}"
            else "Application & Resume - ${profile.fullName}"
        )
    }
    var body by remember(target) { mutableStateOf("") }
    var isGeneratingAiDraft by remember { mutableStateOf(false) }

    // Auto-generate AI draft when opened if body is blank
    LaunchedEffect(target, profile) {
        if (body.isBlank()) {
            isGeneratingAiDraft = true
            val draft = viewModel.generateResumeEmailDraft(
                company = company.ifBlank { "Hiring Team" },
                jobTitle = jobTitle.ifBlank { "Software Engineer" },
                recruiterName = recipientName.ifBlank { "Recruiter" },
                recruiterEmail = recipientEmail,
                notes = target?.defaultNotes ?: ""
            )
            subject = draft.subject
            body = draft.body
            isGeneratingAiDraft = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("resume_email_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BrandBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Mail Resume to Recruiter",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AI-tailored pitch with attached resume summary",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            // Attached Resume Badge Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = BrandBluePrimary.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attached Resume",
                        tint = BrandBluePrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Attached: ${profile.resumeFileName}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = BrandBluePrimary
                        )
                        Text(
                            text = "${profile.resumeFileSize} • Uploaded by ${profile.fullName}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SuccessGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "ATS ${profile.resumeAtsScore}%",
                            color = SuccessGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Target Recruiter Details
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("email_company_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = jobTitle,
                    onValueChange = { jobTitle = it },
                    label = { Text("Job Title") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("email_job_title_input"),
                    singleLine = true
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    label = { Text("Recruiter Name") },
                    placeholder = { Text("e.g. Sarah Jenkins") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("email_recruiter_name_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = recipientEmail,
                    onValueChange = { recipientEmail = it },
                    label = { Text("Recruiter Email") },
                    placeholder = { Text("recruiter@company.com") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("email_recruiter_email_input"),
                    singleLine = true
                )
            }

            // AI Regenerate Pitch Button
            OutlinedButton(
                onClick = {
                    scope.launch {
                        isGeneratingAiDraft = true
                        val draft = viewModel.generateResumeEmailDraft(
                            company = company.ifBlank { "your team" },
                            jobTitle = jobTitle.ifBlank { "Software Engineer" },
                            recruiterName = recipientName.ifBlank { "Hiring Manager" },
                            recruiterEmail = recipientEmail,
                            notes = ""
                        )
                        subject = draft.subject
                        body = draft.body
                        isGeneratingAiDraft = false
                        Toast.makeText(context, "AI Pitch generated!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_ai_pitch_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isGeneratingAiDraft) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Crafting AI Pitch with Gemini...")
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = BrandBluePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Regenerate Custom Pitch with Gemini AI", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Email Subject Field
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject Line") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_subject_input"),
                singleLine = true
            )

            // Email Body Field
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                label = { Text("Email Message & Application Pitch") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("email_body_input"),
                minLines = 6,
                maxLines = 12
            )

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Open In Device Email Client (Gmail, Outlook, etc.)
                Button(
                    onClick = {
                        val targetMail = recipientEmail.ifBlank { "recruiter@${company.lowercase().replace(" ", "")}.com" }
                        val mailUri = Uri.parse("mailto:$targetMail?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
                        val emailIntent = Intent(Intent.ACTION_SENDTO, mailUri)
                        try {
                            context.startActivity(Intent.createChooser(emailIntent, "Send Resume Email Via"))
                            viewModel.recordSentResumeEmail(
                                recipientEmail = targetMail,
                                recipientName = recipientName,
                                company = company.ifBlank { "Target Company" },
                                jobTitle = jobTitle.ifBlank { "Software Engineer" },
                                subject = subject,
                                body = body,
                                resumeFileName = profile.resumeFileName
                            )
                        } catch (e: Exception) {
                            Toast.makeText(context, "No email client found. Draft recorded in app history.", Toast.LENGTH_SHORT).show()
                            viewModel.recordSentResumeEmail(
                                recipientEmail = targetMail,
                                recipientName = recipientName,
                                company = company.ifBlank { "Target Company" },
                                jobTitle = jobTitle.ifBlank { "Software Engineer" },
                                subject = subject,
                                body = body,
                                resumeFileName = profile.resumeFileName
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("send_email_app_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open in Email App & Dispatch", fontWeight = FontWeight.Bold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Copy to Clipboard
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Application Email", "Subject: $subject\n\n$body")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Email pitch copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_email_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Pitch", fontSize = 12.sp)
                    }

                    // Record as Dispatched in local DB
                    Button(
                        onClick = {
                            val targetMail = recipientEmail.ifBlank { "recruiter@${company.lowercase().replace(" ", "")}.com" }
                            viewModel.recordSentResumeEmail(
                                recipientEmail = targetMail,
                                recipientName = recipientName,
                                company = company.ifBlank { "Target Company" },
                                jobTitle = jobTitle.ifBlank { "Software Engineer" },
                                subject = subject,
                                body = body,
                                resumeFileName = profile.resumeFileName
                            )
                            Toast.makeText(context, "Resume email recorded to sent history!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("record_sent_email_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Sent", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
