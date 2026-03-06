package com.vital_self.features.packages.data.model

import com.google.gson.annotations.SerializedName

// Credit Packages Response
data class CreditPackagesResponse(
    val message: String,
    val status: Boolean,
    val data: CreditPackagesData?
)

data class CreditPackagesData(
    val creditPackages: List<CreditPackage>
)

data class CreditPackage(
    val id: Int,
    val packageName: String,
    val shortDescription: String,
    val longDescription: String,
    val totalCredits: Int,
    val price: Double,
    val durationDays: Int,
    val isActive: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

// User Packages Response
data class UserPackagesResponse(
    val message: String,
    val status: Boolean,
    val data: UserPackagesData?
)

data class UserPackagesData(
    val userCredits: List<UserCredit>
)

data class UserCredit(
    val id: Int,
    val userId: Int,
    val packageId: Int,
    val totalCredits: Int,
    val availableCredits: Int,
    val purchaseDate: String,
    val expiryDate: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    @SerializedName("package")
    val packageInfo: CreditPackageInfo?
)

data class CreditPackageInfo(
    val id: Int,
    val packageName: String,
    val shortDescription: String,
    val longDescription: String,
    val totalCredits: Int,
    val price: Double,
    val durationDays: Int
)

// Purchase Credit Request/Response
data class PurchaseCreditRequest(
    val packageId: Int
)

data class PurchaseCreditResponse(
    val message: String,
    val status: Boolean,
    val data: PurchaseCreditData?
)

data class PurchaseCreditData(
    val userCredit: UserCredit
)

// Available Credits Response
data class AvailableCreditsResponse(
    val message: String,
    val status: Boolean,
    val data: AvailableCreditsData?
)

data class AvailableCreditsData(
    val totalAvailableCredits: Int,
    val freeAvailableCredits: Int,
    val sumofAvailableCredits: Int
)

// Payment Initiate Request/Response
data class PaymentInitiateRequest(
    val packageId: Int
)

data class PaymentInitiateResponse(
    val message: String,
    val status: Boolean,
    val data: PaymentInitiateData?
)

data class PaymentInitiateData(
    @SerializedName("authorization_url")
    val authorizationUrl: String,
    @SerializedName("access_code")
    val accessCode: String,
    val reference: String
)

// Payment Verify Request/Response
data class PaymentVerifyRequest(
    val reference: String
)

data class PaymentVerifyResponse(
    val message: String,
    val status: Boolean,
    val data: PaymentVerifyData?
)

data class PaymentVerifyData(
    @SerializedName("payment_status")
    val paymentStatus: String,
    val reference: String,
    val amount: Double,
    val currency: String,
    @SerializedName("paid_at")
    val paidAt: String,
    @SerializedName("credits_allocated")
    val creditsAllocated: Boolean
)
