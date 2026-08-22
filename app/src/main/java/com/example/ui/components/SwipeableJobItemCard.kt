package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.JobApplicationEntity
import com.example.ui.theme.BrandBluePrimary
import com.example.ui.theme.StatusApplied
import com.example.ui.theme.StatusInterview
import com.example.ui.theme.StatusOffer
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.StatusScreening
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class StatusOption(
    val status: String,
    val label: String,
    val icon: ImageVector,
    val color: Color
)

val APP_STATUS_OPTIONS = listOf(
    StatusOption("APPLIED", "Applied", Icons.Default.Send, StatusApplied),
    StatusOption("SCREENING", "Screening", Icons.Default.Work, StatusScreening),
    StatusOption("INTERVIEW", "Interview", Icons.Default.Event, StatusInterview),
    StatusOption("OFFER", "Offer Received", Icons.Default.CheckCircle, StatusOffer),
    StatusOption("REJECTED", "Closed / Rejected", Icons.Default.Close, StatusRejected)
)

@Composable
fun SwipeableJobItemCard(
    application: JobApplicationEntity,
    onClick: () -> Unit,
    onStatusChange: (newStatus: String) -> Unit,
    onFollowUpEmailClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showPlatformBadge: Boolean = true
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val dateFormat = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }
    var showMenu by remember { mutableStateOf(false) }

    // Swipe offset state
    val offsetX = remember { Animatable(0f) }
    val maxDragPx = with(density) { 140.dp.toPx() }
    val swipeThresholdPx = with(density) { 75.dp.toPx() }

    // Determine the next logical status to suggest in the swipe right action
    val nextStatusOption = when (application.status.uppercase()) {
        "APPLIED" -> StatusOption("SCREENING", "Screening", Icons.Default.Work, StatusScreening)
        "SCREENING" -> StatusOption("INTERVIEW", "Interview", Icons.Default.Event, StatusInterview)
        "INTERVIEW" -> StatusOption("OFFER", "Offer", Icons.Default.CheckCircle, StatusOffer)
        "OFFER" -> StatusOption("INTERVIEW", "Interview", Icons.Default.Event, StatusInterview)
        else -> StatusOption("INTERVIEW", "Interview", Icons.Default.Event, StatusInterview)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .testTag("job_item_container_${application.id}")
    ) {
        // Swipe Background Action Trays
        Row(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Background: Triggered when swiping RIGHT (Advance Status)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(nextStatusOption.color.copy(alpha = 0.85f))
                    .clickable {
                        coroutineScope.launch {
                            offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                            onStatusChange(nextStatusOption.status)
                        }
                    }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = nextStatusOption.icon,
                        contentDescription = "Advance to ${nextStatusOption.label}",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Move to",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = nextStatusOption.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Right Background: Triggered when swiping LEFT (Quick Options / Reject)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .background(if (application.status.equals("REJECTED", true)) BrandBluePrimary.copy(alpha = 0.85f) else StatusRejected.copy(alpha = 0.85f))
                    .clickable {
                        coroutineScope.launch {
                            offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                            if (application.status.equals("REJECTED", true)) {
                                onStatusChange("APPLIED")
                            } else {
                                onStatusChange("REJECTED")
                            }
                        }
                    }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Mark as",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (application.status.equals("REJECTED", true)) "Re-open" else "Closed",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (application.status.equals("REJECTED", true)) Icons.Default.Send else Icons.Default.Close,
                        contentDescription = "Quick Status",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Foreground Swiping Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                val currentVal = offsetX.value
                                if (currentVal > swipeThresholdPx) {
                                    // Swiped far right -> Advance status
                                    onStatusChange(nextStatusOption.status)
                                } else if (currentVal < -swipeThresholdPx) {
                                    // Swiped far left -> Toggle Rejected / Closed
                                    if (application.status.equals("REJECTED", true)) {
                                        onStatusChange("APPLIED")
                                    } else {
                                        onStatusChange("REJECTED")
                                    }
                                }
                                offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val newOffset = (offsetX.value + dragAmount).coerceIn(-maxDragPx, maxDragPx)
                            coroutineScope.launch {
                                offsetX.snapTo(newOffset)
                            }
                        }
                    )
                }
                .clickable { onClick() }
                .testTag("recent_app_card_${application.id}"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Badges and Titles
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (showPlatformBadge) {
                                PlatformBadge(platformId = application.platformId)
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            // Interactive Status Pill - opens dropdown on tap
                            Box {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = when (application.status.uppercase()) {
                                        "OFFER" -> StatusOffer.copy(alpha = 0.18f)
                                        "INTERVIEW" -> StatusInterview.copy(alpha = 0.18f)
                                        "SCREENING" -> StatusScreening.copy(alpha = 0.18f)
                                        "APPLIED" -> StatusApplied.copy(alpha = 0.18f)
                                        "REJECTED" -> StatusRejected.copy(alpha = 0.18f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { showMenu = true }
                                        .testTag("status_pill_clickable_${application.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val statusColor = when (application.status.uppercase()) {
                                            "OFFER" -> StatusOffer
                                            "INTERVIEW" -> StatusInterview
                                            "SCREENING" -> StatusScreening
                                            "APPLIED" -> StatusApplied
                                            "REJECTED" -> StatusRejected
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(statusColor)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = application.status.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                            color = statusColor,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Icon(
                                            imageVector = Icons.Default.SwapHoriz,
                                            contentDescription = "Change Status",
                                            tint = statusColor.copy(alpha = 0.7f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = application.jobTitle,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = "${application.company} • ${application.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Right Column: Match Score, Date, and 3-dot Quick-Action Menu
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MatchScoreBadge(score = application.matchScore)

                            // Quick Action Dropdown Menu Anchor
                            Box {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("quick_action_menu_btn_${application.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Quick Status Actions",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    modifier = Modifier.testTag("quick_status_dropdown_${application.id}")
                                ) {
                                    Text(
                                        text = "UPDATE STATUS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                    )

                                    APP_STATUS_OPTIONS.forEach { opt ->
                                        val isCurrent = application.status.equals(opt.status, ignoreCase = true)
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(26.dp)
                                                                .clip(CircleShape)
                                                                .background(opt.color.copy(alpha = 0.15f)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(
                                                                imageVector = opt.icon,
                                                                contentDescription = null,
                                                                tint = opt.color,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(10.dp))
                                                        Text(
                                                            text = opt.label,
                                                            fontSize = 13.sp,
                                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (isCurrent) opt.color else MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                    if (isCurrent) {
                                                        Icon(
                                                            imageVector = Icons.Default.CheckCircle,
                                                            contentDescription = "Current Status",
                                                            tint = opt.color,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                showMenu = false
                                                onStatusChange(opt.status)
                                            },
                                            modifier = Modifier.testTag("status_option_${opt.status.lowercase()}_${application.id}")
                                        )
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                                    if (onFollowUpEmailClick != null) {
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Email,
                                                        contentDescription = null,
                                                        tint = BrandBluePrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        text = "Draft Follow-up Email",
                                                        fontSize = 13.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }
                                            },
                                            onClick = {
                                                showMenu = false
                                                onFollowUpEmailClick()
                                            },
                                            modifier = Modifier.testTag("action_follow_up_email_${application.id}")
                                        )
                                    }

                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.OpenInNew,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = "Open Full Details",
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        },
                                        onClick = {
                                            showMenu = false
                                            onClick()
                                        },
                                        modifier = Modifier.testTag("action_open_details_${application.id}")
                                    )
                                }
                            }
                        }

                        Text(
                            text = dateFormat.format(Date(application.appliedDate)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }

                // Quick Status Progress Stepper & Quick Action Chips Row
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Subtle Swipe Hint
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Swipe to update",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Quick 1-Tap Advance Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = nextStatusOption.color.copy(alpha = 0.12f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onStatusChange(nextStatusOption.status) }
                            .testTag("quick_advance_btn_${application.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = nextStatusOption.icon,
                                contentDescription = null,
                                tint = nextStatusOption.color,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Move to ${nextStatusOption.label}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = nextStatusOption.color
                            )
                        }
                    }
                }
            }
        }
    }
}
