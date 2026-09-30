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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppUser
import com.example.data.subscription.SubscriptionPlanConfig
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MuslimUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    uiState: MuslimUiState,
    allUsers: List<AppUser>,
    subscriptionPlans: List<SubscriptionPlanConfig>,
    onUpdateSubscription: (email: String, isPremium: Boolean, planType: String, durationDays: Int?, expiresAtOverride: Long?) -> Unit,
    onDeleteUser: (email: String) -> Unit,
    onAddNewUser: (email: String, displayName: String, isPremium: Boolean, planType: String) -> Unit,
    onPublishPricing: (List<SubscriptionPlanConfig>) -> Unit,
    onCalculateSuggestions: (basePlanId: String, basePrice: Int) -> Map<String, Int>,
    onSearchChange: (String) -> Unit,
    onFilterPlanChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddUserDialog by remember { mutableStateOf(false) }
    var selectedUserForEdit by remember { mutableStateOf<AppUser?>(null) }
    var activeTab by remember { mutableStateOf("Users") } // "Users", "Pricing", "Features"

    // Editable state for pricing
    var editablePlans by remember(subscriptionPlans) { mutableStateOf(subscriptionPlans) }
    var calculatorBasePlanId by remember { mutableStateOf("plan_7_days") }
    var calculatorBasePriceInput by remember { mutableStateOf("49") }

    val totalUsers = allUsers.size
    val premiumUsersCount = allUsers.count { it.isPremium && (it.expiresAt == null || it.expiresAt >= System.currentTimeMillis()) }
    val freeUsersCount = totalUsers - premiumUsersCount

    val filteredUsers = remember(allUsers, uiState.adminUserSearchQuery, uiState.adminFilterPlan) {
        val q = uiState.adminUserSearchQuery.trim().lowercase()
        allUsers.filter { user ->
            val matchesQuery = q.isEmpty() ||
                    user.email.lowercase().contains(q) ||
                    user.displayName.lowercase().contains(q)

            val isUserActivePrem = user.isPremium && (user.expiresAt == null || user.expiresAt >= System.currentTimeMillis())
            val matchesPlan = when (uiState.adminFilterPlan) {
                "Premium" -> isUserActivePrem
                "Free" -> !isUserActivePrem
                "Expired" -> user.isPremium && user.expiresAt != null && user.expiresAt < System.currentTimeMillis()
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
                    title = "Active Plans",
                    value = "${subscriptionPlans.count { it.isEnabled }} Plans",
                    icon = Icons.Default.Payments,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3-Tab Selector: Users, Pricing, Features
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeTab == "Users",
                    onClick = { activeTab = "Users" },
                    label = { Text("Users ($totalUsers)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = activeTab == "Pricing",
                    onClick = { activeTab = "Pricing" },
                    label = { Text("Pricing (5 Plans)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.1f)
                )
                FilterChip(
                    selected = activeTab == "Features",
                    onClick = { activeTab = "Features" },
                    label = { Text("Capabilities") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // ==========================================
        // TAB 1: USERS & PREMIUM MANAGEMENT
        // ==========================================
        if (activeTab == "Users") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "App Users & Subscriptions",
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

                // Search Bar
                OutlinedTextField(
                    value = uiState.adminUserSearchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_search_input"),
                    placeholder = { Text("Search by email or name...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (uiState.adminUserSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filterOptions = listOf("All", "Premium", "Free", "Expired")
                    items(filterOptions) { filter ->
                        FilterChip(
                            selected = uiState.adminFilterPlan == filter,
                            onClick = { onFilterPlanChange(filter) },
                            label = { Text(filter) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (filteredUsers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.PersonSearch, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No users found matching query", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Try adjusting your search or filter criteria", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(filteredUsers, key = { it.email }) { user ->
                    AdminUserCard(
                        user = user,
                        onManageClick = { selectedUserForEdit = user },
                        onQuickToggle = {
                            val willBePremium = !user.isPremium
                            val plan = if (willBePremium) "1 Month" else "Free"
                            val duration = if (willBePremium) 30 else null
                            onUpdateSubscription(user.email, willBePremium, plan, duration, null)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        // ==========================================
        // TAB 2: SUBSCRIPTION PRICING MANAGEMENT & CALCULATOR
        // ==========================================
        if (activeTab == "Pricing") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = EmeraldPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Smart Pricing Calculator",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Set a base plan price. The calculator generates sensible proportional price suggestions for the other plans with progressive discounts.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = calculatorBasePriceInput,
                                onValueChange = { calculatorBasePriceInput = it.filter { ch -> ch.isDigit() } },
                                label = { Text("7 Days Base (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    val basePrice = calculatorBasePriceInput.toIntOrNull() ?: 49
                                    val suggestions = onCalculateSuggestions(calculatorBasePlanId, basePrice)
                                    if (suggestions.isNotEmpty()) {
                                        editablePlans = editablePlans.map { plan ->
                                            val suggested = suggestions[plan.id]
                                            if (suggested != null) plan.copy(priceInr = suggested) else plan
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(54.dp)
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Calculate")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Plans (${editablePlans.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = {
                            onPublishPricing(editablePlans)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publish to Cloud")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            items(editablePlans, key = { it.id }) { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = plan.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (plan.badge != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GoldSecondary.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = plan.badge,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = GoldSecondary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Duration: ${plan.durationDays} days • ${plan.periodDescription}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var priceText by remember(plan.priceInr) { mutableStateOf(plan.priceInr.toString()) }
                            OutlinedTextField(
                                value = priceText,
                                onValueChange = {
                                    priceText = it.filter { ch -> ch.isDigit() }
                                    val newPrice = priceText.toIntOrNull() ?: plan.priceInr
                                    editablePlans = editablePlans.map { p ->
                                        if (p.id == plan.id) p.copy(priceInr = newPrice) else p
                                    }
                                },
                                prefix = { Text("₹") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.width(110.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // ==========================================
        // TAB 3: PREMIUM ROADMAP & CAPABILITIES
        // ==========================================
        if (activeTab == "Features") {
            item {
                Text(
                    text = "Feature Capability Matrix & Gating",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                val capabilities = listOf(
                    "Ad-Free Sacred Experience" to "Free users see clean banner, Premium is 100% ad-free",
                    "Muslim Ummah AI Assistant" to "Free: 3 daily queries • Premium: 100 daily verified citations",
                    "Advanced Quran Tools" to "Memorization, Khatam Planner, Reciter selection, Ayah repeat loops",
                    "Complete Hadith Study Suite" to "Kutub al-Sittah collection, search by narrator & cross-translations",
                    "Advanced Adhan & Makkah Audio" to "Holy Sanctuary reciters and high-fidelity audio downloads",
                    "Cloud Sync & Offline Storage" to "Cross-device sync for bookmarks, reading positions & notes",
                    "Custom Themes" to "Imperial Gold, Sacred Green, OLED Dark & Paper White"
                )

                capabilities.forEach { (title, desc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // ==========================================
    // DIALOG: USER PREMIUM MANAGEMENT
    // ==========================================
    if (selectedUserForEdit != null) {
        val user = selectedUserForEdit!!
        val isNowExpired = user.expiresAt != null && user.expiresAt < System.currentTimeMillis()
        val isActive = user.isPremium && !isNowExpired

        AlertDialog(
            onDismissRequest = { selectedUserForEdit = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GoldSecondary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Premium Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // User Overview Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = user.displayName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Status: ",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = if (isActive) "ACTIVE PREMIUM" else if (isNowExpired) "EXPIRED" else "FREE MEMBER",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) EmeraldPrimary else if (isNowExpired) MaterialTheme.colorScheme.error else Color.Gray
                                )
                            }
                            if (user.expiresAt != null) {
                                val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
                                val expiryFormatted = sdf.format(Date(user.expiresAt))
                                Text(
                                    text = "Expiry: $expiryFormatted",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Text(
                        text = "Grant Official Subscription Plan:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 5 Official Plans
                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, true, "7 Days", 7, null)
                            selectedUserForEdit = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant 7 Days (₹49)")
                    }

                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, true, "1 Month", 30, null)
                            selectedUserForEdit = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant 1 Month (₹129)")
                    }

                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, true, "3 Months", 90, null)
                            selectedUserForEdit = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant 3 Months (₹299)")
                    }

                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, true, "9 Months", 270, null)
                            selectedUserForEdit = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant 9 Months (₹649)")
                    }

                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, true, "1 Year", 365, null)
                            selectedUserForEdit = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant 1 Year (₹799)")
                    }

                    // Extend if active
                    if (isActive && user.expiresAt != null) {
                        Button(
                            onClick = {
                                val newExpiry = user.expiresAt + (30L * 24 * 3600 * 1000)
                                onUpdateSubscription(user.email, true, user.planType, null, newExpiry)
                                selectedUserForEdit = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.MoreTime, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Extend by +30 Days")
                        }
                    }

                    // Revoke / Downgrade to Free
                    OutlinedButton(
                        onClick = {
                            onUpdateSubscription(user.email, false, "Free", null, null)
                            selectedUserForEdit = null
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Revoke Premium (Set to Free)")
                    }

                    if (user.email != "aqiffarooqui@gmail.com") {
                        Spacer(modifier = Modifier.height(4.dp))
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
        var selectedPlan by remember { mutableStateOf("1 Month") }

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
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val plans = listOf("Free", "7 Days", "1 Month", "3 Months", "1 Year")
                        items(plans) { plan ->
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
private fun AdminUserCard(
    user: AppUser,
    onManageClick: () -> Unit,
    onQuickToggle: () -> Unit
) {
    val isNowExpired = user.expiresAt != null && user.expiresAt < System.currentTimeMillis()
    val isActivePrem = user.isPremium && !isNowExpired

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onManageClick)
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
                    color = if (isActivePrem) GoldSecondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (user.email == "aqiffarooqui@gmail.com") "👑" else user.displayName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isActivePrem) GoldSecondary else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (user.role == "ADMIN") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFD54F)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1B3D34),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
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

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isActivePrem) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (isActivePrem) user.planType else if (isNowExpired) "Expired" else "Free",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (isActivePrem) EmeraldPrimary else if (isNowExpired) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (user.expiresAt != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
                            Text(
                                text = if (isNowExpired) "Expired on ${sdf.format(Date(user.expiresAt))}" else "Until ${sdf.format(Date(user.expiresAt))}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onManageClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Manage",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
