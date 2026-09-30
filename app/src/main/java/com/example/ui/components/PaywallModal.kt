package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.subscription.SubscriptionPlanConfig
import com.example.data.subscription.SubscriptionPricingManager
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallModal(
    featureTrigger: String,
    onDismiss: () -> Unit,
    onSubscribe: (String) -> Unit
) {
    val livePlans by SubscriptionPricingManager.plansState.collectAsState()
    val availablePlans = remember(livePlans) { livePlans.filter { it.isEnabled } }

    var selectedPlanId by remember(availablePlans) {
        mutableStateOf(availablePlans.find { it.id == "plan_1_year" }?.id ?: availablePlans.firstOrNull()?.id ?: "plan_1_month")
    }

    val selectedPlan = availablePlans.find { it.id == selectedPlanId } ?: availablePlans.firstOrNull()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
                .testTag("paywall_modal"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Surface(
                shape = CircleShape,
                color = GoldSecondary.copy(alpha = 0.2f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Premium",
                        tint = GoldSecondary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Muslim Ummah Premium",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Elevate your sacred worship with verified tools, audio downloads and an ad-free experience.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (featureTrigger.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Feature: $featureTrigger",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7 Real Feature Capabilities
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BenefitItem("1. 100% Ad-Free Sacred Atmosphere")
                BenefitItem("2. Muslim Ummah AI: Source-verified Islamic assistant with citations")
                BenefitItem("3. Advanced Quran Tools: Memorization mode, Khatam planner & Ayah repeat loops")
                BenefitItem("4. Holy Sanctuary Recitations: Mishary, Sudais, Shuraim & Al-Ghamdi")
                BenefitItem("5. Complete Hadith Library: Kutub al-Sittah & cross-language search")
                BenefitItem("6. Cloud Sync: Reading positions, notes and bookmarks across devices")
                BenefitItem("7. Premium Themes: Imperial Gold, Sacred Green, OLED Dark & Paper White")
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Select a Subscription Plan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // The 5 Official Plans in a Horizontal Selector
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(availablePlans, key = { it.id }) { plan ->
                    val isSelected = selectedPlanId == plan.id
                    PlanSelectionCard(
                        plan = plan,
                        isSelected = isSelected,
                        onClick = { selectedPlanId = plan.id }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subscribe Button
            Button(
                onClick = {
                    if (selectedPlan != null) {
                        onSubscribe(selectedPlan.id)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("subscribe_cta_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(
                    text = "Subscribe for ${selectedPlan?.formattedPrice ?: "₹129"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

        }
    }
}

@Composable
private fun BenefitItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Included",
            tint = EmeraldPrimary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PlanSelectionCard(
    plan: SubscriptionPlanConfig,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (plan.badge != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldSecondary
                ) {
                    Text(
                        text = plan.badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = plan.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = plan.formattedPrice,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldPrimary
            )

            Text(
                text = plan.periodDescription,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
