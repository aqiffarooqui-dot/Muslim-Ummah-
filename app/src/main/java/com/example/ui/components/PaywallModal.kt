package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallModal(
    featureTrigger: String,
    onDismiss: () -> Unit,
    onSubscribe: (String) -> Unit,
    onLoginAsAdmin: () -> Unit
) {
    var selectedPlan by remember { mutableStateOf("Annual Pro") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
                .testTag("paywall_modal"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFD54F).copy(alpha = 0.2f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Premium",
                        tint = GoldSecondary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Unlock Muslim Pro Premium",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            if (featureTrigger.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Unlock: $featureTrigger",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Highlights
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(14.dp)
            ) {
                BenefitItem("100% Ad-Free Sacred Spiritual Experience")
                BenefitItem("5 Elite Reciters (Mishary, Abdul Basit, Sudais, Shuraim)")
                BenefitItem("All Historic Adhans (Makkah, Madinah, Al-Aqsa, Cairo)")
                BenefitItem("Ramadan & Qada Missed Fast / Prayer Trackers")
                BenefitItem("Audio Ruqyah & Hisn al-Muslim Audio Supplications")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pricing Plans
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PricingCard(
                    title = "Monthly",
                    price = "$3.99/mo",
                    period = "Billed monthly",
                    isSelected = selectedPlan == "Monthly Pro",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedPlan = "Monthly Pro" }
                )
                PricingCard(
                    title = "Annual Pro",
                    badge = "SAVE 37%",
                    price = "$29.99/yr",
                    period = "$2.50/month",
                    isSelected = selectedPlan == "Annual Pro",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedPlan = "Annual Pro" }
                )
                PricingCard(
                    title = "Lifetime",
                    badge = "BEST VALUE",
                    price = "$59.99",
                    period = "Pay once",
                    isSelected = selectedPlan == "Lifetime VIP",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedPlan = "Lifetime VIP" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subscribe CTA
            Button(
                onClick = { onSubscribe(selectedPlan) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("subscribe_cta_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(
                    text = "Activate $selectedPlan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Admin Fast-Track Button
            TextButton(
                onClick = onLoginAsAdmin,
                modifier = Modifier.testTag("admin_fast_login_btn")
            ) {
                Text(
                    text = "👑 Login as Admin (aqiffarooqui@gmail.com)",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BenefitItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp)
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
private fun PricingCard(
    title: String,
    price: String,
    period: String,
    badge: String? = null,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = GoldSecondary
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                color = EmeraldPrimary
            )
            Text(
                text = period,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
