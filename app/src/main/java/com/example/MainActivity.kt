package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.JobPlatformEntity
import com.example.notifications.NotificationReminderManager
import com.example.ui.screens.ApplicationDetailDialog
import com.example.ui.screens.ApplicationsTrackerScreen
import com.example.ui.screens.AuthDialog
import com.example.ui.screens.AutoApplyScreen
import com.example.ui.screens.ConnectPlatformDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.NotificationSettingsDialog
import com.example.ui.screens.PlatformsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResumeEmailDialog
import com.example.ui.screens.ResumePreviewDialog
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.JobViewModel

enum class MainNavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "tab_dashboard"),
    PLATFORMS("Platforms", Icons.Filled.Hub, Icons.Outlined.Hub, "tab_platforms"),
    AUTO_APPLY("Auto-Apply", Icons.Filled.FlashOn, Icons.Outlined.FlashOn, "tab_auto_apply"),
    APPLICATIONS("Tracker", Icons.Filled.Work, Icons.Outlined.WorkOutline, "tab_applications"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "tab_profile")
}

class MainActivity : ComponentActivity() {

    private val viewModel: JobViewModel by viewModels()
    private var tabIntentState by mutableStateOf<String?>(null)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create system notification channel on launch
        NotificationReminderManager.createNotificationChannel(this)

        tabIntentState = intent?.getStringExtra(NotificationReminderManager.EXTRA_NAVIGATE_TAB)

        setContent {
            MyApplicationTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val snackbarMessage by viewModel.snackBarMessage.collectAsState()
                val selectedApplication by viewModel.selectedApplication.collectAsState()
                val showAuthDialog by viewModel.showAuthDialog.collectAsState()
                val showEmailResumeDialog by viewModel.showEmailResumeDialog.collectAsState()
                val showResumePreviewDialog by viewModel.showResumePreviewDialog.collectAsState()
                val showNotificationDialog by viewModel.showNotificationSettingsDialog.collectAsState()
                val emailResumeTarget by viewModel.emailResumeTarget.collectAsState()
                val userProfile by viewModel.userProfile.collectAsState()
                val notificationConfig by viewModel.notificationReminderConfig.collectAsState()

                var currentTab by remember { mutableStateOf(MainNavigationTab.DASHBOARD) }
                var platformToConnect by remember { mutableStateOf<JobPlatformEntity?>(null) }
                var showConnectDialog by remember { mutableStateOf(false) }

                // Check for notification deep link tab
                LaunchedEffect(tabIntentState) {
                    tabIntentState?.let { target ->
                        when (target.uppercase()) {
                            "APPLICATIONS" -> currentTab = MainNavigationTab.APPLICATIONS
                            "DASHBOARD" -> currentTab = MainNavigationTab.DASHBOARD
                            "AUTO_APPLY" -> currentTab = MainNavigationTab.AUTO_APPLY
                            "PLATFORMS" -> currentTab = MainNavigationTab.PLATFORMS
                            "PROFILE" -> currentTab = MainNavigationTab.PROFILE
                        }
                        tabIntentState = null
                    }
                }

                LaunchedEffect(snackbarMessage) {
                    snackbarMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearSnackbar()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(BrandBluePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "AutoApply",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            actions = {
                                // Notification Reminder Dialog Launcher
                                val isNotifActive = notificationConfig?.isEnabled ?: true
                                IconButton(
                                    onClick = { viewModel.openNotificationSettingsDialog() },
                                    modifier = Modifier.testTag("topbar_notifications_button")
                                ) {
                                    if (isNotifActive) {
                                        BadgedBox(
                                            badge = {
                                                Badge(
                                                    containerColor = SuccessGreen,
                                                    modifier = Modifier.size(7.dp)
                                                )
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.NotificationsActive,
                                                contentDescription = "Daily Reminders",
                                                tint = BrandBluePrimary
                                            )
                                        }
                                    } else {
                                        Icon(
                                            imageVector = Icons.Outlined.Notifications,
                                            contentDescription = "Daily Reminders",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // User Account Chip / Dialog Launcher
                                userProfile?.let { prof ->
                                    androidx.compose.material3.Surface(
                                        shape = CircleShape,
                                        color = if (prof.isLoggedIn) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .clip(CircleShape)
                                            .clickable { viewModel.openAuthDialog() }
                                            .testTag("topbar_auth_chip")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(if (prof.isLoggedIn) SuccessGreen else Color.Gray)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (prof.isLoggedIn) prof.fullName.split(" ").firstOrNull() ?: "User" else "Sign In",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (prof.isLoggedIn) SuccessGreen else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { currentTab = MainNavigationTab.PROFILE },
                                    modifier = Modifier.testTag("topbar_profile_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Candidate Profile",
                                        tint = if (currentTab == MainNavigationTab.PROFILE) BrandBluePrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("main_navigation_bar")
                        ) {
                            MainNavigationTab.values().forEach { tab ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = BrandBluePrimary,
                                        selectedTextColor = BrandBluePrimary,
                                        indicatorColor = BrandBluePrimary.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            MainNavigationTab.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToPlatforms = { currentTab = MainNavigationTab.PLATFORMS },
                                onNavigateToAutoApply = { currentTab = MainNavigationTab.AUTO_APPLY },
                                onNavigateToApplications = { currentTab = MainNavigationTab.APPLICATIONS },
                                onNavigateToProfile = { currentTab = MainNavigationTab.PROFILE },
                                onConnectPlatformClick = { platform ->
                                    platformToConnect = platform
                                    showConnectDialog = true
                                }
                            )
                            MainNavigationTab.PLATFORMS -> PlatformsScreen(
                                viewModel = viewModel,
                                onConnectPlatformClick = { platform ->
                                    platformToConnect = platform
                                    showConnectDialog = true
                                }
                            )
                            MainNavigationTab.AUTO_APPLY -> AutoApplyScreen(
                                viewModel = viewModel
                            )
                            MainNavigationTab.APPLICATIONS -> ApplicationsTrackerScreen(
                                viewModel = viewModel
                            )
                            MainNavigationTab.PROFILE -> ProfileScreen(
                                viewModel = viewModel
                            )
                        }

                        // Detail Dialog for an Application
                        selectedApplication?.let { app ->
                            ApplicationDetailDialog(
                                application = app,
                                viewModel = viewModel,
                                onDismiss = { viewModel.selectApplication(null) }
                            )
                        }

                        // Connect Platform Dialog
                        if (showConnectDialog) {
                            ConnectPlatformDialog(
                                initialPlatform = platformToConnect,
                                onDismiss = {
                                    showConnectDialog = false
                                    platformToConnect = null
                                },
                                onConnect = { platformId, email, limit ->
                                    viewModel.connectPlatform(platformId, email, limit)
                                }
                            )
                        }

                        // Authentication Dialog (Login / Sign Up / Switch Account)
                        if (showAuthDialog) {
                            AuthDialog(
                                viewModel = viewModel,
                                onDismiss = { viewModel.closeAuthDialog() }
                            )
                        }

                        // Mail Resume Dialog (Compose and dispatch recruiter pitches)
                        if (showEmailResumeDialog) {
                            ResumeEmailDialog(
                                target = emailResumeTarget,
                                viewModel = viewModel,
                                onDismiss = { viewModel.closeEmailResumeDialog() }
                            )
                        }

                        // Resume Preview Modal Overlay
                        if (showResumePreviewDialog) {
                            ResumePreviewDialog(
                                viewModel = viewModel,
                                onDismiss = { viewModel.closeResumePreviewDialog() }
                            )
                        }

                        // Daily Notification Reminder Settings Dialog
                        if (showNotificationDialog) {
                            NotificationSettingsDialog(
                                viewModel = viewModel,
                                onDismiss = { viewModel.closeNotificationSettingsDialog() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val target = intent.getStringExtra(NotificationReminderManager.EXTRA_NAVIGATE_TAB)
        if (!target.isNullOrBlank()) {
            tabIntentState = target
        }
    }
}
