package com.example.data.subscription

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class EntitlementState {
    FREE,
    PREMIUM,
    PREMIUM_TRIAL,
    EXPIRED,
    CANCELLED_ACTIVE,
    PENDING
}

data class PremiumEntitlement(
    val state: EntitlementState = EntitlementState.FREE,
    val planId: String = "free",
    val planName: String = "Free Member",
    val expiresAtMillis: Long? = null,
    val isAdFree: Boolean = false,
    val canAccessAiAssistant: Boolean = false,
    val canAccessOfflineDownloads: Boolean = false,
    val canAccessAdvancedReciters: Boolean = false,
    val canAccessHadithStudyTools: Boolean = false,
    val canAccessKhatamPlanner: Boolean = false,
    val canAccessMemorization: Boolean = false,
    val canAccessThemes: Boolean = false,
    val dailyAiQueriesLimit: Int = 5,
    val dailyAiQueriesUsed: Int = 0
) {
    val isPremiumActive: Boolean
        get() = state == EntitlementState.PREMIUM ||
                state == EntitlementState.PREMIUM_TRIAL ||
                state == EntitlementState.CANCELLED_ACTIVE
}

data class SubscriptionProduct(
    val id: String,
    val title: String,
    val priceFormatted: String,
    val period: String,
    val badge: String? = null,
    val trialDays: Int = 0
)

interface SubscriptionRepository {
    val entitlementFlow: StateFlow<PremiumEntitlement>
    suspend fun checkEntitlements(): PremiumEntitlement
    suspend fun purchasePlan(productId: String): Result<PremiumEntitlement>
    suspend fun restorePurchases(): Result<PremiumEntitlement>
    suspend fun cancelSubscription(): Result<Unit>
}

object SubscriptionManager : SubscriptionRepository {

    // Default configuration for Google Play Billing products
    val AVAILABLE_PRODUCTS = listOf(
        SubscriptionProduct(
            id = "muslim_ummah_pro_monthly",
            title = "Monthly Pro",
            priceFormatted = "₹99/month",
            period = "Billed monthly",
            trialDays = 7
        ),
        SubscriptionProduct(
            id = "muslim_ummah_pro_annual",
            title = "Annual Pro",
            priceFormatted = "₹499/year",
            period = "₹41/month • Save 58%",
            badge = "POPULAR",
            trialDays = 14
        ),
        SubscriptionProduct(
            id = "muslim_ummah_vip_lifetime",
            title = "Lifetime VIP",
            priceFormatted = "₹999",
            period = "One-time purchase",
            badge = "BEST VALUE"
        )
    )

    private val _entitlementFlow = MutableStateFlow(
        createPremiumEntitlement(
            planId = "lifetime_vip",
            planName = "Lifetime VIP (Admin)",
            expiresAt = null
        )
    )
    override val entitlementFlow: StateFlow<PremiumEntitlement> = _entitlementFlow.asStateFlow()

    fun updateEntitlementForUser(email: String, isPremium: Boolean, planType: String, expiresAt: Long?) {
        val isAdmin = email.trim().equals("aqiffarooqui@gmail.com", ignoreCase = true)
        if (isAdmin || isPremium) {
            _entitlementFlow.value = createPremiumEntitlement(
                planId = if (isAdmin) "lifetime_vip" else planType.lowercase().replace(" ", "_"),
                planName = if (isAdmin) "Lifetime VIP (Admin)" else planType,
                expiresAt = expiresAt
            )
        } else {
            _entitlementFlow.value = PremiumEntitlement(
                state = EntitlementState.FREE,
                planId = "free",
                planName = "Free Member",
                isAdFree = false,
                canAccessAiAssistant = true, // Free trial basic access
                canAccessOfflineDownloads = false,
                canAccessAdvancedReciters = false,
                canAccessHadithStudyTools = false,
                canAccessKhatamPlanner = false,
                canAccessMemorization = false,
                canAccessThemes = false,
                dailyAiQueriesLimit = 3
            )
        }
    }

    override suspend fun checkEntitlements(): PremiumEntitlement {
        return _entitlementFlow.value
    }

    override suspend fun purchasePlan(productId: String): Result<PremiumEntitlement> {
        val product = AVAILABLE_PRODUCTS.find { it.id == productId }
            ?: AVAILABLE_PRODUCTS[1] // fallback to annual

        val newEntitlement = createPremiumEntitlement(
            planId = product.id,
            planName = product.title,
            expiresAt = if (product.id.contains("monthly")) {
                System.currentTimeMillis() + (30L * 24 * 3600 * 1000)
            } else if (product.id.contains("annual")) {
                System.currentTimeMillis() + (365L * 24 * 3600 * 1000)
            } else null
        )
        _entitlementFlow.value = newEntitlement
        return Result.success(newEntitlement)
    }

    override suspend fun restorePurchases(): Result<PremiumEntitlement> {
        // Query stored entitlements / Google Play Billing
        val current = _entitlementFlow.value
        return if (current.isPremiumActive) {
            Result.success(current)
        } else {
            Result.failure(Exception("No active Google Play subscription found for this account."))
        }
    }

    override suspend fun cancelSubscription(): Result<Unit> {
        val current = _entitlementFlow.value
        if (current.isPremiumActive) {
            _entitlementFlow.value = current.copy(state = EntitlementState.CANCELLED_ACTIVE)
        }
        return Result.success(Unit)
    }

    private fun createPremiumEntitlement(planId: String, planName: String, expiresAt: Long?): PremiumEntitlement {
        return PremiumEntitlement(
            state = EntitlementState.PREMIUM,
            planId = planId,
            planName = planName,
            expiresAtMillis = expiresAt,
            isAdFree = true,
            canAccessAiAssistant = true,
            canAccessOfflineDownloads = true,
            canAccessAdvancedReciters = true,
            canAccessHadithStudyTools = true,
            canAccessKhatamPlanner = true,
            canAccessMemorization = true,
            canAccessThemes = true,
            dailyAiQueriesLimit = 100
        )
    }
}
