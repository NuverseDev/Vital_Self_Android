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
