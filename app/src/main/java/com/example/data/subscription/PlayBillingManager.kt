package com.example.data.subscription

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BillingConnectionState {
    data object Disconnected : BillingConnectionState()
    data object Connecting : BillingConnectionState()
    data object Connected : BillingConnectionState()
    data class Unavailable(val message: String) : BillingConnectionState()
}

data class PlayProductInfo(
    val productId: String,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val offerToken: String?,
    val productDetails: ProductDetails
)

/**
 * Production Google Play Billing Architecture for Muslim Ummah.
 * Strictly adheres to Play Billing v7:
 * - Real BillingClient lifecycle
 * - Product loading for the 5 official subscription tiers
 * - Verified purchase acknowledgment & token handling
 * - Purchase restoration
 * - Secure entitlement synchronization via SubscriptionManager
 * - Graceful handling of unavailable billing states
 * - Never fakes purchases; genuine entitlement comes only from verified transactions or admin grants.
 */
object PlayBillingManager : PurchasesUpdatedListener {
    private const val TAG = "PlayBillingManager"

    private val scope = CoroutineScope(Dispatchers.IO)
    private var billingClient: BillingClient? = null
    private var appContext: Context? = null

    private val _connectionState = MutableStateFlow<BillingConnectionState>(BillingConnectionState.Disconnected)
    val connectionState: StateFlow<BillingConnectionState> = _connectionState.asStateFlow()

    private val _availableProducts = MutableStateFlow<Map<String, PlayProductInfo>>(emptyMap())
    val availableProducts: StateFlow<Map<String, PlayProductInfo>> = _availableProducts.asStateFlow()

    private val _billingStatusMessage = MutableStateFlow<String?>(null)
    val billingStatusMessage: StateFlow<String?> = _billingStatusMessage.asStateFlow()

    private val _isPurchasing = MutableStateFlow(false)
    val isPurchasing: StateFlow<Boolean> = _isPurchasing.asStateFlow()

    // Configured official subscription product IDs corresponding to the 5 tiers
    val OFFICIAL_SUBSCRIPTION_PRODUCT_IDS = listOf(
        "muslim_ummah_sub_7d",
        "muslim_ummah_sub_1m",
        "muslim_ummah_sub_3m",
        "muslim_ummah_sub_9m",
        "muslim_ummah_sub_1y"
    )

    fun initialize(context: Context) {
        if (billingClient != null && _connectionState.value == BillingConnectionState.Connected) {
            return
        }
        appContext = context.applicationContext

        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        billingClient = BillingClient.newBuilder(context.applicationContext)
            .setListener(this)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()

        startConnection()
    }

    private fun startConnection() {
        val client = billingClient ?: return
        _connectionState.value = BillingConnectionState.Connecting

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    Log.i(TAG, "Google Play Billing Client successfully connected.")
                    _connectionState.value = BillingConnectionState.Connected
                    _billingStatusMessage.value = null
                    querySubscriptionProducts()
                    restorePurchases { _, _ -> }
                } else {
                    val errorMsg = "Billing unavailable: ${billingResult.debugMessage} (code: ${billingResult.responseCode})"
                    Log.w(TAG, errorMsg)
                    _connectionState.value = BillingConnectionState.Unavailable(errorMsg)
                    _billingStatusMessage.value = "Google Play Store unavailable on this device or session"
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected. Will retry connection on demand.")
                _connectionState.value = BillingConnectionState.Disconnected
            }
        })
    }

    /**
     * Query Play Console subscription products for the 5 tiers
     */
    fun querySubscriptionProducts() {
        val client = billingClient
        if (client == null || !client.isReady) {
            Log.w(TAG, "Cannot query products: BillingClient is not ready.")
            return
        }

        val productList = OFFICIAL_SUBSCRIPTION_PRODUCT_IDS.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(ProductType.SUBS)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, queryProductDetailsList ->
            if (billingResult.responseCode == BillingResponseCode.OK && queryProductDetailsList != null) {
                val detailsMap = mutableMapOf<String, PlayProductInfo>()
                for (details in queryProductDetailsList) {
                    val offer = details.subscriptionOfferDetails?.firstOrNull()
                    val pricingPhase = offer?.pricingPhases?.pricingPhaseList?.firstOrNull()
                    val priceFormatted = pricingPhase?.formattedPrice ?: details.name

                    detailsMap[details.productId] = PlayProductInfo(
                        productId = details.productId,
                        title = details.title,
                        description = details.description,
                        formattedPrice = priceFormatted,
                        offerToken = offer?.offerToken,
                        productDetails = details
                    )
                }
                _availableProducts.value = detailsMap
                Log.i(TAG, "Loaded ${detailsMap.size} subscription products from Google Play.")
            } else {
                Log.w(TAG, "Failed to query products from Google Play: ${billingResult.debugMessage}")
            }
        }
    }

    /**
     * Initiate genuine Google Play subscription purchase flow.
     * Does NOT fake purchases or grant entitlement before confirmation.
     */
    fun launchPurchaseFlow(
        activity: Activity,
        googlePlayProductId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val client = billingClient
        if (client == null || !client.isReady) {
            onResult(false, "Google Play Billing is currently not connected or unavailable.")
            return
        }

        val productInfo = _availableProducts.value[googlePlayProductId]
        if (productInfo == null) {
            val note = "Product '$googlePlayProductId' is not yet configured in Google Play Console. External setup in Play Console is required."
            Log.w(TAG, note)
            onResult(false, note)
            return
        }

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productInfo.productDetails)
            .apply {
                productInfo.offerToken?.let { setOfferToken(it) }
            }
            .build()

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        _isPurchasing.value = true
        val billingResult = client.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode != BillingResponseCode.OK) {
            _isPurchasing.value = false
            onResult(false, "Unable to launch checkout: ${billingResult.debugMessage}")
        }
    }

    /**
     * Handles purchases returned by Google Play.
     */
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        _isPurchasing.value = false
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handleVerifiedPurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "User canceled Google Play purchase.")
            _billingStatusMessage.value = "Purchase canceled."
        } else {
            Log.w(TAG, "Purchase error: ${billingResult.debugMessage} (code ${billingResult.responseCode})")
            _billingStatusMessage.value = "Purchase error: ${billingResult.debugMessage}"
        }
    }

    /**
     * Acknowledge and securely sync genuine purchase with SubscriptionManager
     */
    private fun handleVerifiedPurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
            Log.d(TAG, "Purchase not completed yet. State: ${purchase.purchaseState}")
            return
        }

        val client = billingClient ?: return
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            client.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    Log.i(TAG, "Purchase acknowledged successfully: ${purchase.orderId}")
                    applyEntitlementFromPurchase(purchase)
                } else {
                    Log.e(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                }
            }
        } else {
            // Already acknowledged, ensure entitlement is in sync
            applyEntitlementFromPurchase(purchase)
        }
    }

    private fun applyEntitlementFromPurchase(purchase: Purchase) {
        scope.launch {
            val purchasedProduct = purchase.products.firstOrNull() ?: return@launch
            val plan = SubscriptionPricingManager.plansState.value.find {
                it.googlePlayProductId == purchasedProduct
            }

            // Never fall back to an unrelated plan: an unknown Play product must
            // not accidentally grant the wrong entitlement.
            if (plan == null) {
                Log.e(TAG, "Unknown Google Play product '${purchasedProduct}'; entitlement not granted.")
                return@launch
            }

            // Use the Play purchase timestamp rather than "now". This prevents a
            // restored purchase from receiving a fresh full-duration entitlement
            // every time the app starts.
            val purchaseTimeMillis = purchase.purchaseTime.takeIf { it > 0L }
                ?: System.currentTimeMillis()
            val expiryMillis = purchaseTimeMillis +
                (plan.durationDays * 24L * 60L * 60L * 1000L)

            if (expiryMillis <= System.currentTimeMillis()) {
                Log.w(TAG, "Google Play purchase for '${plan.name}' is already expired; entitlement not granted.")
                return@launch
            }

            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email
            if (userEmail.isNullOrBlank()) {
                Log.w(TAG, "No authenticated Firebase user; entitlement not granted.")
                return@launch
            }

            SubscriptionManager.updateEntitlementForUser(
                email = userEmail,
                isPremium = true,
                planType = plan.name,
                expiresAt = expiryMillis,
                source = EntitlementSource.GOOGLE_PLAY
            )
            Log.i(TAG, "Entitlement granted via Google Play purchase: ${plan.name}, expires at: $expiryMillis")
        }
    }

    /**
     * Restore existing active purchases from Google Play
     */
    fun restorePurchases(onComplete: (Int, String) -> Unit) {
        val client = billingClient
        if (client == null || !client.isReady) {
            onComplete(0, "Billing client not connected.")
            return
        }

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.SUBS)
            .build()

        client.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                var restoredCount = 0
                for (purchase in purchasesList) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        handleVerifiedPurchase(purchase)
                        restoredCount++
                    }
                }
                onComplete(
                    restoredCount,
                    if (restoredCount > 0) "Successfully restored $restoredCount purchase(s)." else "No active Google Play subscriptions found."
                )
            } else {
                onComplete(0, "Could not check purchases: ${billingResult.debugMessage}")
            }
        }
    }

    fun clearStatusMessage() {
        _billingStatusMessage.value = null
    }
}
