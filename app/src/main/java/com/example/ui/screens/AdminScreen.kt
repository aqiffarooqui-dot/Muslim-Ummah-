package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppUser
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MuslimUiState
import com.example.ui.viewmodel.MuslimViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    uiState: MuslimUiState,
    allUsers: List<AppUser>,
    onUpdateSubscription: (email: String, isPremium: Boolean, planType: String, durationDays: Int?) -> Unit,
    onDeleteUser: (email: String) -> Unit,
    onAddNewUser: (email: String, displayName: String, isPremium: Boolean, planType: String) -> Unit,
    onSearchChange: (String) -> Unit,
    onFilterPlanChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddUserDialog by remember { mutableStateOf(false) }
    var selectedUserForEdit by remember { mutableStateOf<AppUser?>(null) }
    var activeTab by remember { mutableStateOf("Users") } // "Users" or "Features"

    val totalUsers = allUsers.size
    val premiumUsersCount = allUsers.count { it.isPremium }
    val freeUsersCount = totalUsers - premiumUsersCount
    val estimatedMRR = allUsers.sumOf {
        when (it.planType) {
            "Monthly Pro" -> 99.0
            "Annual Pro" -> 41.5
            "Lifetime VIP" -> 83.0
            else -> 0.0
        }
    }

    val filteredUsers = remember(allUsers, uiState.adminUserSearchQuery, uiState.adminFilterPlan) {
        val q = uiState.adminUserSearchQuery.trim().lowercase()
        allUsers.filter { user ->
            val matchesQuery = q.isEmpty() ||
                    user.email.lowercase().contains(q) ||
                    user.displayName.lowercase().contains(q)

            val matchesPlan = when (uiState.adminFilterPlan) {
                "Premium" -> user.isPremium
                "Free" -> !user.isPremium
                "Lifetime" -> user.planType == "Lifetime VIP"
                else -> true
            }
            matchesQuery && matchesPlan
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("admin_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Admin Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0F6E56), Color(0xFF053B2E))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFFD54F),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "👑", fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "SUPER ADMIN CONSOLE",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F),
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = uiState.currentUser?.email ?: "aqiffarooqui@gmail.com",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Full Access",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Analytics KPI Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Total Users",
                    value = "$totalUsers",
                    icon = Icons.Default.People,
                    color = EmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Premium Active",
                    value = "$premiumUsersCount",
                    icon = Icons.Default.Verified,
                    color = GoldSecondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Free Users",
                    value = "$freeUsersCount",
                    icon = Icons.Default.PersonOutline,
                    color = Color(0xFF0288D1),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Est. MRR",
                    value = "₹${String.format(Locale.US, "%.0f", estimatedMRR)}",
                    icon = Icons.Default.MonetizationOn,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Selector: User Management vs Premium Features
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = activeTab == "Users",
                    onClick = { activeTab = "Users" },
                    label = { Text("User Management ($totalUsers)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = activeTab == "Features",
                    onClick = { activeTab = "Features" },
                    label = { Text("Premium Roadmap") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (activeTab == "Users") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Registered App Users",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showAddUserDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("admin_add_user_btn")
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Add User")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search & Filter
                OutlinedTextField(
                    value = uiState.adminUserSearchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_user_search"),
                    placeholder = { Text("Search by email or name...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("All", "Premium", "Free", "Lifetime")) { plan ->
                        val isSelected = uiState.adminFilterPlan == plan
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterPlanChange(plan) },
                            label = { Text(plan) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            items(filteredUsers, key = { it.email }) { user ->
                AdminUserRow(
                    user = user,
                    onEditClick = { selectedUserForEdit = user },
                    onQuickToggle = {
                        val newPremium = !user.isPremium
                        val newPlan = if (newPremium) "Annual Pro" else "Free"
                        val duration = if (newPremium) 365 else null
                        onUpdateSubscription(user.email, newPremium, newPlan, duration)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            // Premium Features Catalog & Roadmap
            item {
                Text(
                    text = "Active & Future Premium Features",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Features automatically unlocked for subscribers and admin (${uiState.currentUser?.email})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            items(MuslimViewModel.PREMIUM_FEATURES_CATALOG) { feature ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = feature.title,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = feature.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = feature.status,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = feature.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // Edit User Dialog
    selectedUserForEdit?.let { user ->
        AlertDialog(
            onDismissRequest = { selectedUserForEdit = null },
            title = {
                Column {
                    Text(text = "Manage ${user.displayName}", fontWeight = FontWeight.Bold)
                    Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Current Plan: ${user.planType} (${if (user.isPremium) "Premium" else "Free"})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Change Subscription Tier:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, true, "Lifetime VIP", null)
                            selectedUserForEdit = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Lifetime VIP (Permanent)")
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, true, "Annual Pro", 365)
                            selectedUserForEdit = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Annual Pro (1 Year)")
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, true, "Monthly Pro", 30)
                            selectedUserForEdit = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Monthly Pro (30 Days)")
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, false, "Free", null)
                            selectedUserForEdit = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Downgrade to Free")
                    }

                    if (user.email != "aqiffarooqui@gmail.com") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onDeleteUser(user.email)
                                selectedUserForEdit = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete User Account")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedUserForEdit = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Add New User Dialog
    if (showAddUserDialog) {
        var emailInput by remember { mutableStateOf("") }
        var nameInput by remember { mutableStateOf("") }
        var selectedPlan by remember { mutableStateOf("Monthly Pro") }

        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            title = { Text("Add New App User") },
            text = {
                Column {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Initial Subscription:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Free", "Monthly Pro", "Lifetime VIP").forEach { plan ->
                            FilterChip(
                                selected = selectedPlan == plan,
                                onClick = { selectedPlan = plan },
                                label = { Text(plan) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (emailInput.isNotBlank()) {
                            val isPrem = selectedPlan != "Free"
                            onAddNewUser(emailInput.trim(), nameInput.trim(), isPrem, selectedPlan)
                            showAddUserDialog = false
                        }
                    }
                ) {
                    Text("Create User")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUserDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
private fun AdminUserRow(
    user: AppUser,
    onEditClick: () -> Unit,
    onQuickToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditClick)
            .testTag("admin_user_${user.email}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (user.isPremium) GoldSecondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (user.role == "ADMIN") "👑" else user.displayName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (user.isPremium) GoldSecondary else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (user.role == "ADMIN") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary
                            ) {
                                Text(
                                    text = "ADMIN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (user.isPremium) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = user.planType,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (user.isPremium) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Edit User")
                }
            }
        }
    }
}
