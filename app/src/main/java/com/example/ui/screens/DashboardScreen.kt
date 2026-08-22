package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.JobApplicationEntity
import com.example.data.local.JobPlatformEntity
import com.example.ui.components.AutomationLiveIndicator
import com.example.ui.components.MatchScoreBadge
import com.example.ui.components.MetricCard
import com.example.ui.components.PlatformBadge
import com.example.ui.components.StatusPill
import com.example.ui.components.SwipeableJobItemCard
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.StatusApplied
import com.example.ui.theme.StatusInterview
import com.example.ui.theme.StatusOffer
import com.example.ui.theme.StatusScreening
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.JobViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: JobViewModel,
    onNavigateToPlatforms: () -> Unit,
    onNavigateToAutoApply: () -> Unit,
    onNavigateToApplications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onConnectPlatformClick: (JobPlatformEntity?) -> Unit
) {
    val applications by viewModel.allApplications.collectAsState()
    val platforms by viewModel.allPlatforms.collectAsState()
    val config by viewModel.autoApplyConfig.collectAsState()
    val notifConfig by viewModel.notificationReminderConfig.collectAsState()
    val botState by viewModel.botState.collectAsState()
    val logs by viewModel.automationLogs.collectAsState()
    val appliedTodayCount by viewModel.applicationsCountToday.collectAsState()

    val userProfile by viewModel.userProfile.collectAsState()
    val totalApplied = applications.count { it.status.equals("APPLIED", true) }
    val inScreening = applications.count { it.status.equals("SCREENING", true) }
    val inInterview = applications.count { it.status.equals("INTERVIEW", true) }
    val offers = applications.count { it.status.equals("OFFER", true) }

    val dailyLimit = config?.maxDailyApplications ?: 30
    val isAutoApplyActive = config?.isAutoApplyActive ?: true
    val progressToday = (appliedTodayCount.toFloat() / dailyLimit.toFloat()).coerceIn(0f, 1f)

    val isNotifEnabled = notifConfig?.isEnabled ?: true
    val notifHour = notifConfig?.reminderHour ?: 9
    val notifMinute = notifConfig?.reminderMinute ?: 0
    val formattedHour = if (notifHour % 12 == 0) 12 else notifHour % 12
    val amPm = if (notifHour >= 12) "PM" else "AM"
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, notifMinute, amPm)

    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // User Profile & Authentication Quick Banner
        item {
            userProfile?.let { prof ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { viewModel.openAuthDialog() }
                        .testTag("dashboard_user_auth_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (prof.accountType == "FRESHER") SuccessGreen else BrandBluePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prof.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = prof.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (prof.isLoggedIn) SuccessGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (prof.isLoggedIn) "Logged In" else "Demo Mode",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (prof.isLoggedIn) SuccessGreen else Color.Gray,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${prof.email} • ${prof.headline}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandBluePrimary.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .clickable { viewModel.openResumePreviewDialog() }
                                    .testTag("dashboard_quick_resume_preview")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Resume Preview",
                                        tint = BrandBluePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Resume",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandBluePrimary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { viewModel.openAuthDialog() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Switch Account / Login",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Auth",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Master Bot Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("bot_status_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAutoApplyActive) Color(0xFF0F172A) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Auto-Apply Engine",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAutoApplyActive) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                AutomationLiveIndicator(isActive = isAutoApplyActive)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isAutoApplyActive) "Connecting 8 platforms • AI Matching active" else "Automation paused",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isAutoApplyActive) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = isAutoApplyActive,
                            onCheckedChange = { viewModel.toggleMasterAutoApply(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreen,
                                uncheckedThumbColor = Color.Gray
                            ),
                            modifier = Modifier.testTag("master_auto_apply_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily Apply Quota Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Target Progress",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isAutoApplyActive) Color(0xFFCBD5E1) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$appliedTodayCount / $dailyLimit applied",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isAutoApplyActive) Color(0xFF38BDF8) else BrandBluePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressToday },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (progressToday >= 1f) SuccessGreen else BrandBluePrimary,
                        trackColor = if (isAutoApplyActive) Color(0xFF1E293B) else MaterialTheme.colorScheme.surface
                    )

                    // Active Bot Step Banner
                    AnimatedVisibility(
                        visible = botState.isRunning || botState.currentStepText.isNotBlank(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isAutoApplyActive) Color(0xFF1E293B) else MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (botState.isRunning) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color(0xFF38BDF8),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = botState.currentStepText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isAutoApplyActive) Color(0xFFE2E8F0) else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToAutoApply,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("run_auto_apply_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBluePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch Bot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.openEmailResumeDialog(
                                    com.example.ui.viewmodel.EmailTargetInfo(
                                        company = "Google",
                                        jobTitle = "Senior Android Engineer",
                                        recruiterName = "Tech Talent Recruiter",
                                        recruiterEmail = "recruiter@google.com",
                                        defaultNotes = "Direct application & resume mail dispatch"
                                    )
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dashboard_email_pitch_quick_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAutoApplyActive) Color(0xFF334155) else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (isAutoApplyActive) Color.White else MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Mail Resume",
                                fontSize = 12.sp,
                                color = if (isAutoApplyActive) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Daily Reminder Digest Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { viewModel.openNotificationSettingsDialog() }
                    .testTag("dashboard_reminder_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isNotifEnabled) BrandBlueLight.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isNotifEnabled) BrandBluePrimary.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
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
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Daily Tracker Reminder",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isNotifEnabled) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = if (isNotifEnabled) timeFormatted else "Paused",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isNotifEnabled) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isNotifEnabled) "Scheduled daily digest to review applications & interview requests" else "Tap to schedule automated local reminders",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.sendTestNotification() },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("dashboard_test_notif_icon_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Test Notification",
                                tint = BrandBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Configure Reminder",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Metrics Grid (2x2)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Application Pipeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Submitted",
                        value = totalApplied.toString(),
                        subtitle = "Auto-applied via AI",
                        icon = Icons.Default.Send,
                        accentColor = StatusApplied,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToApplications
                    )
                    MetricCard(
                        title = "Screening",
                        value = inScreening.toString(),
                        subtitle = "Autonomous review",
                        icon = Icons.Default.Work,
                        accentColor = StatusScreening,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToApplications
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Interviews",
                        value = inInterview.toString(),
                        subtitle = "Upcoming rounds",
                        icon = Icons.Default.Event,
                        accentColor = StatusInterview,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToApplications
                    )
                    MetricCard(
                        title = "Offers Received",
                        value = offers.toString(),
                        subtitle = "High Match packages",
                        icon = Icons.Default.CheckCircle,
                        accentColor = StatusOffer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToApplications
                    )
                }
            }
        }

        // Connected Platforms Carousel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Connected Job Platforms",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToPlatforms) {
                        Text("Manage All", color = BrandBluePrimary, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(platforms) { platform ->
                        Card(
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { onConnectPlatformClick(platform) }
                                .testTag("platform_carousel_card_${platform.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PlatformBadge(platformId = platform.id)
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (platform.isConnected) SuccessGreen else Color.Gray)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (platform.isConnected) "${platform.jobCountAvailable} matched jobs" else "Disconnected",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (platform.isConnected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (platform.isConnected) "${platform.applicationsToday}/${platform.dailyApplyLimit} today" else "Tap to connect",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (platform.isConnected) BrandBluePrimary else Color.Gray,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Applications Stream Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recent Applications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Swipe card or tap ⋯ to quickly update status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
                TextButton(onClick = onNavigateToApplications) {
                    Text("View All (${applications.size})", color = BrandBluePrimary, fontSize = 12.sp)
                }
            }
        }

        // Recent Applications List with Swipe-to-Update & Quick Action Menu
        items(applications.take(5), key = { it.id }) { app ->
            SwipeableJobItemCard(
                application = app,
                onClick = { viewModel.selectApplication(app) },
                onStatusChange = { newStatus ->
                    viewModel.updateApplicationStatus(app.id, newStatus)
                },
                onFollowUpEmailClick = {
                    viewModel.selectApplication(app)
                    viewModel.openEmailResumeDialog(app)
                }
            )
        }

        // Live Automation Ticker Activity Feed
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Live Automation Feed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        logs.take(3).forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (log.action) {
                                                "SUBMITTED" -> SuccessGreen
                                                "INTERVIEW_DETECTED" -> StatusInterview
                                                else -> BrandBluePrimary
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "[${log.platformId.uppercase()}] ${log.jobTitle} at ${log.company}: ${log.details}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
