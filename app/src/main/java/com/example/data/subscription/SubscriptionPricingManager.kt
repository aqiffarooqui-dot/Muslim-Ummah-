package com.example.data.subscription

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

data class SubscriptionPlanConfig(
    val id: String,
    val name: String,
    val durationDays: Int,
    val priceInr: Int,
    val isEnabled: Boolean = true,
    val badge: String? = null,
    val googlePlayProductId: String? = null
) {
    val formattedPrice: String
        get() = "₹$priceInr"

    val periodDescription: String
        get() = when (id) {
            "plan_7_days" -> "7 days access"
            "plan_1_month" -> "Billed monthly"
            "plan_3_months" -> "Save 23% • ₹${(priceInr / 3)}/mo"
            "plan_9_months" -> "Save 44% • ₹${(priceInr / 9)}/mo"
            "plan_1_year" -> "Save 48% • ₹${(priceInr / 12)}/mo"
            else -> "$durationDays days"
        }
}

object SubscriptionPricingManager {
    private const val TAG = "SubscriptionPricing"
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    // Built-in hardcoded defaults representing the 5 official plans
    private val DEFAULT_CONFIGS = listOf(
        SubscriptionPlanConfig(
            id = "plan_7_days",
            name = "7 Days",
            durationDays = 7,
            priceInr = 49,
            isEnabled = true,
            badge = null,
            googlePlayProductId = "muslim_ummah_sub_7d"
        ),
        SubscriptionPlanConfig(
            id = "plan_1_month",
            name = "1 Month",
            durationDays = 30,
            priceInr = 129,
            isEnabled = true,
            badge = null,
            googlePlayProductId = "muslim_ummah_sub_1m"
        ),
        SubscriptionPlanConfig(
            id = "plan_3_months",
            name = "3 Months",
            durationDays = 90,
            priceInr = 299,
            isEnabled = true,
            badge = "POPULAR",
            googlePlayProductId = "muslim_ummah_sub_3m"
        ),
        SubscriptionPlanConfig(
            id = "plan_9_months",
            name = "9 Months",
            durationDays = 270,
            priceInr = 649,
            isEnabled = true,
            badge = null,
            googlePlayProductId = "muslim_ummah_sub_9m"
        ),
        SubscriptionPlanConfig(
            id = "plan_1_year",
            name = "1 Year",
            durationDays = 365,
            priceInr = 799,
            isEnabled = true,
            badge = "BEST VALUE",
            googlePlayProductId = "muslim_ummah_sub_1y"
        )
    )

    private val _plansState = MutableStateFlow(DEFAULT_CONFIGS)
    val plansState: StateFlow<List<SubscriptionPlanConfig>> = _plansState.asStateFlow()

    init {
        // Start listening to live Firestore pricing
        listenToCloudPricing()
    }

    private fun listenToCloudPricing() {
        try {
            firestore.collection("configuration").document("pricing")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Error listening to cloud pricing: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val plansList = mutableListOf<SubscriptionPlanConfig>()
                        for (defaultPlan in DEFAULT_CONFIGS) {
                            val priceFromCloud = snapshot.getLong("${defaultPlan.id}_price")?.toInt()
                            val enabledFromCloud = snapshot.getBoolean("${defaultPlan.id}_enabled") ?: defaultPlan.isEnabled
                            val badgeFromCloud = snapshot.getString("${defaultPlan.id}_badge") ?: defaultPlan.badge

                            plansList.add(
                                defaultPlan.copy(
                                    priceInr = priceFromCloud ?: defaultPlan.priceInr,
                                    isEnabled = enabledFromCloud,
                                    badge = badgeFromCloud
                                )
                            )
                        }
                        if (plansList.isNotEmpty()) {
                            _plansState.value = plansList
                            Log.d(TAG, "Applied live cloud pricing configuration successfully")
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to initialize cloud pricing listener: ${e.message}")
        }
    }

    /**
     * Pricing Calculator helper for Admin Panel:
     * Given a base plan and its newly proposed price, generates sensible proportional price suggestions
     * for all other plans based on duration and progressive bulk discounts.
     * Ends figures cleanly in 9 or 0.
     */
    fun calculateSuggestedPrices(basePlanId: String, basePrice: Int): Map<String, Int> {
        val basePlan = DEFAULT_CONFIGS.find { it.id == basePlanId } ?: DEFAULT_CONFIGS[0]
        if (basePrice <= 0 || basePlan.priceInr <= 0) return emptyMap()

        // Proportional ratio relative to the standard base pricing model
        val ratio = basePrice.toDouble() / basePlan.priceInr
        val suggestions = mutableMapOf<String, Int>()

        for (plan in DEFAULT_CONFIGS) {
            if (plan.id == basePlanId) {
                suggestions[plan.id] = basePrice
            } else {
                val scaled = plan.priceInr * ratio
                suggestions[plan.id] = roundToPricePoint(scaled)
            }
        }

        return suggestions
    }

    private fun roundToPricePoint(cost: Double): Int {
        val roundedTen = (cost / 10.0).roundToInt() * 10
        val candidate = roundedTen - 1
        return if (candidate < 9) 9 else candidate
    }

    /**
     * Publishes modified subscription pricing directly to Firestore (/configuration/pricing).
     * Protected by Firestore security rules so only authorized admins can publish.
     */
    suspend fun publishPricingToCloud(
        updatedPlans: List<SubscriptionPlanConfig>,
        adminEmail: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docRef = firestore.collection("configuration").document("pricing")
            val payload = hashMapOf<String, Any>(
                "updatedAt" to FieldValue.serverTimestamp(),
                "updatedBy" to adminEmail
            )

            for (plan in updatedPlans) {
                payload["${plan.id}_price"] = plan.priceInr
                payload["${plan.id}_enabled"] = plan.isEnabled
                if (plan.badge != null) {
                    payload["${plan.id}_badge"] = plan.badge
                }
            }

            docRef.set(payload, SetOptions.merge()).await()
            _plansState.value = updatedPlans
            Log.d(TAG, "Successfully published updated pricing configuration to Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to publish pricing to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun getPlanById(planId: String): SubscriptionPlanConfig {
        return _plansState.value.find { it.id == planId } ?: DEFAULT_CONFIGS[1]
    }
}
