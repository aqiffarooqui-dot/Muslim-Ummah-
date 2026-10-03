package com.example.data.subscription

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Clean Subscription Tier Architecture for Muslim Ummah
 */
enum class SubscriptionTier {
    FREE,
    SEVEN_DAY,
    MONTHLY,
    QUARTERLY,
    NINE_MONTH,
    YEARLY,
    ADMIN_GRANTED
}

typealias EntitlementState = SubscriptionTier

enum class EntitlementSource {
    DEFAULT,
    GOOGLE_PLAY,
    ADMIN_GRANT
}

/**
 * Comprehensive Entitlement Model representing user's current subscription rights
 */
data class PremiumEntitlement(
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val isActive: Boolean = false,
    val source: EntitlementSource = EntitlementSource.DEFAULT,
    val planId: String = "free",
    val planName: String = "Free Member",
    val startDateMillis: Long? = null,
    val expiresAtMillis: Long? = null,
    val isCancelled: Boolean = false,
    val adminNotes: String? = null,
    // Capability flags
    val isAdFree: Boolean = false,
    val canAccessAiAssistant: Boolean = false,
    val canAccessOfflineDownloads: Boolean = false,
    val canAccessAdvancedReciters: Boolean = false,
    val canAccessHadithStudyTools: Boolean = false,
    val canAccessKhatamPlanner: Boolean = false,
    val canAccessMemorization: Boolean = false,
    val canAccessThemes: Boolean = false,
    val canAccessRuqyahAudio: Boolean = false,
    val canAccessAdvancedQada: Boolean = false,
    val dailyAiQueriesLimit: Int = 3,
    val dailyAiQueriesUsed: Int = 0
) {
    val state: SubscriptionTier get() = tier

    constructor(state: SubscriptionTier = SubscriptionTier.FREE, isAdFree: Boolean = false) : this(
        tier = state,
        isActive = (state != SubscriptionTier.FREE),
        isAdFree = isAdFree
    )
    /**
     * Calculates whether the user currently has an active Premium session.
     * Automatically returns false if the subscription has passed its expiry date.
     */
    val isPremiumActive: Boolean
        get() {
            if (!isActive) return false
            if (tier == SubscriptionTier.FREE) return false
            val expiry = expiresAtMillis
            if (expiry != null && expiry < System.currentTimeMillis()) {
                return false
            }
            return true
        }

    val isExpired: Boolean
        get() = expiresAtMillis != null && expiresAtMillis < System.currentTimeMillis()
}

/**
 * Representation of an active subscription plan product
 */
data class SubscriptionPlan(
    val id: String,
    val title: String,
    val durationDays: Int,
    val priceInr: Int,
    val priceFormatted: String,
    val periodDescription: String,
    val badge: String? = null,
    val isEnabled: Boolean = true
)

interface SubscriptionRepository {
    val entitlementFlow: StateFlow<PremiumEntitlement>
    suspend fun checkEntitlements(): PremiumEntitlement
    suspend fun purchasePlan(planId: String): Result<PremiumEntitlement>
    suspend fun restorePurchases(): Result<PremiumEntitlement>
    suspend fun cancelSubscription(): Result<Unit>
}

object SubscriptionManager : SubscriptionRepository {

    // Default configuration for the 5 official Muslim Ummah plans
    val DEFAULT_PLANS = listOf(
        SubscriptionPlan(
            id = "plan_7_days",
            title = "7 Days",
            durationDays = 7,
            priceInr = 49,
            priceFormatted = "₹49",
            periodDescription = "7 days access"
        ),
        SubscriptionPlan(
            id = "plan_1_month",
            title = "1 Month",
            durationDays = 30,
            priceInr = 129,
            priceFormatted = "₹129",
            periodDescription = "Billed monthly"
        ),
        SubscriptionPlan(
            id = "plan_3_months",
            title = "3 Months",
            durationDays = 90,
            priceInr = 299,
            priceFormatted = "₹299",
            periodDescription = "Save 23% • ₹99/mo",
            badge = "POPULAR"
        ),
        SubscriptionPlan(
            id = "plan_9_months",
            title = "9 Months",
            durationDays = 270,
            priceInr = 649,
            priceFormatted = "₹649",
            periodDescription = "Save 44% • ₹72/mo"
        ),
        SubscriptionPlan(
            id = "plan_1_year",
            title = "1 Year",
            durationDays = 365,
            priceInr = 799,
            priceFormatted = "₹799",
            periodDescription = "Save 48% • ₹66/mo",
            badge = "BEST VALUE"
        )
    )

    // DEFAULT ENTITLEMENT MUST ALWAYS INITIALIZE AS FREE
    private val _entitlementFlow = MutableStateFlow(createFreeEntitlement())
    override val entitlementFlow: StateFlow<PremiumEntitlement> = _entitlementFlow.asStateFlow()

    fun createFreeEntitlement(): PremiumEntitlement {
        return PremiumEntitlement(
            tier = SubscriptionTier.FREE,
            isActive = false,
            source = EntitlementSource.DEFAULT,
            planId = "free",
            planName = "Free Member",
            expiresAtMillis = null,
            isAdFree = false,
            canAccessAiAssistant = true, // Free trial allowance
            canAccessOfflineDownloads = false,
            canAccessAdvancedReciters = false,
            canAccessHadithStudyTools = false,
            canAccessKhatamPlanner = false,
            canAccessMemorization = false,
            canAccessThemes = false,
            canAccessRuqyahAudio = false,
            canAccessAdvancedQada = false,
            dailyAiQueriesLimit = 3
        )
    }

    fun createActiveEntitlement(
        tier: SubscriptionTier,
        planId: String,
        planName: String,
        source: EntitlementSource,
        expiresAt: Long?,
        adminNotes: String? = null
    ): PremiumEntitlement {
        return PremiumEntitlement(
            tier = tier,
            isActive = true,
            source = source,
            planId = planId,
            planName = planName,
            startDateMillis = System.currentTimeMillis(),
            expiresAtMillis = expiresAt,
            adminNotes = adminNotes,
            isAdFree = true,
            canAccessAiAssistant = true,
            canAccessOfflineDownloads = true,
            canAccessAdvancedReciters = true,
            canAccessHadithStudyTools = true,
            canAccessKhatamPlanner = true,
            canAccessMemorization = true,
            canAccessThemes = true,
            canAccessRuqyahAudio = true,
            canAccessAdvancedQada = true,
            dailyAiQueriesLimit = 100
        )
    }

    /**
     * Updates local in-memory entitlement state.
     * Evaluates expiry timestamp and safely downgrades to FREE if expired.
     */
    fun updateEntitlementForUser(
        email: String,
        isPremium: Boolean,
        planType: String,
        expiresAt: Long?,
        source: EntitlementSource = EntitlementSource.DEFAULT
    ) {
        val now = System.currentTimeMillis()
        val isExpired = expiresAt != null && expiresAt < now

        if (isPremium && !isExpired) {
            val tier = mapPlanNameToTier(planType)
            _entitlementFlow.value = createActiveEntitlement(
                tier = tier,
                planId = planType.lowercase().replace(" ", "_"),
                planName = planType,
                source = source,
                expiresAt = expiresAt
            )
        } else {
            // Not premium or expired -> downgrade to FREE.
            _entitlementFlow.value = createFreeEntitlement()
        }
    }

    fun mapPlanNameToTier(planName: String): SubscriptionTier {
        return when {
            planName.contains("7 Day", ignoreCase = true) -> SubscriptionTier.SEVEN_DAY
            planName.contains("1 Month", ignoreCase = true) || planName.contains("Monthly", ignoreCase = true) -> SubscriptionTier.MONTHLY
            planName.contains("3 Month", ignoreCase = true) || planName.contains("Quarterly", ignoreCase = true) -> SubscriptionTier.QUARTERLY
            planName.contains("9 Month", ignoreCase = true) -> SubscriptionTier.NINE_MONTH
            planName.contains("1 Year", ignoreCase = true) || planName.contains("Annual", ignoreCase = true) || planName.contains("Yearly", ignoreCase = true) -> SubscriptionTier.YEARLY
            planName.contains("Admin", ignoreCase = true) -> SubscriptionTier.ADMIN_GRANTED
            else -> SubscriptionTier.MONTHLY
        }
    }

    override suspend fun checkEntitlements(): PremiumEntitlement {
        val current = _entitlementFlow.value
        if (current.isExpired && current.tier != SubscriptionTier.ADMIN_GRANTED) {
            _entitlementFlow.value = createFreeEntitlement()
        }
        return _entitlementFlow.value
    }

    override suspend fun purchasePlan(planId: String): Result<PremiumEntitlement> {
        // Entitlements must never be granted by this local repository method.
        // Real purchases are initiated and confirmed by PlayBillingManager.
        // This method remains for repository compatibility but deliberately fails
        // instead of creating a locally fabricated Premium entitlement.
        val plan = DEFAULT_PLANS.find { it.id == planId }
        return Result.failure(
            IllegalStateException(
                if (plan == null) "Unknown subscription plan: $planId"
                else "Google Play checkout must be used to purchase ${plan.title}."
            )
        )
    }

    override suspend fun restorePurchases(): Result<PremiumEntitlement> {
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
            _entitlementFlow.value = current.copy(isCancelled = true)
        }
        return Result.success(Unit)
    }
}
