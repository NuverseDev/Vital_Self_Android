package com.vital_self.core.data.remote.api


import com.vital_self.features.auth.data.model.AuthResponse
import com.vital_self.core.data.remote.model.CheckVersionRequest
import com.vital_self.core.data.remote.model.CheckVersionResponse
import com.vital_self.features.packages.data.model.AvailableCreditsResponse
import com.vital_self.features.packages.data.model.CreditPackagesResponse
import com.vital_self.features.packages.data.model.PaymentInitiateRequest
import com.vital_self.features.packages.data.model.PaymentInitiateResponse
import com.vital_self.features.packages.data.model.PaymentVerifyRequest
import com.vital_self.features.packages.data.model.PaymentVerifyResponse
import com.vital_self.features.packages.data.model.PurchaseCreditRequest
import com.vital_self.features.packages.data.model.PurchaseCreditResponse
import com.vital_self.features.auth.data.model.ForgotPasswordRequest
import com.vital_self.features.auth.data.model.ForgotPasswordResponse
import com.vital_self.features.profile.data.model.GetUserResponse
import com.vital_self.features.auth.data.model.LoginRequest
import com.vital_self.features.auth.data.model.ProfileUpdateRequest
import com.vital_self.features.auth.data.model.ProfileUpdateResponse
import com.vital_self.features.auth.data.model.SignupRequest
import com.vital_self.features.profile.data.model.UpdateScanResponse
import com.vital_self.features.profile.data.model.UserLoginRequest
import com.vital_self.features.packages.data.model.UserPackagesResponse
import com.vital_self.features.profile.data.model.UserRequest
import com.vital_self.features.history.data.model.ScanHistoryByDateResponse
import com.vital_self.features.history.data.model.ScanHistoryByIdResponse
import com.vital_self.features.history.data.model.CalendarResponse
import com.vital_self.features.history.data.model.SaveScanHistoryRequest
import com.vital_self.features.history.data.model.SaveScanHistoryResponse
import com.vital_self.features.scan.data.model.PdfDownloadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body requestBody: LoginRequest): Response<AuthResponse>

    @POST("/api/auth/signup")
    suspend fun signup(@Body requestBody: SignupRequest): Response<AuthResponse>

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body requestBody: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @PATCH("/api/user/profile")
    suspend fun updateProfile(@Body requestBody: ProfileUpdateRequest): Response<ProfileUpdateResponse>

    @GET("/api/credit-packages")
    suspend fun getCreditPackages(): Response<CreditPackagesResponse>

    @GET("/api/user/packages")
    suspend fun getUserPackages(): Response<UserPackagesResponse>

    @POST("/api/user/credits")
    suspend fun purchaseCredit(@Body requestBody: PurchaseCreditRequest): Response<PurchaseCreditResponse>

    @POST("/api/demo/login")
    suspend fun getUser(@Body requestBody: UserLoginRequest): Response<GetUserResponse?>

    @POST("/api/demo/decrement")
    suspend fun updateScan(@Body requestBody: UserRequest): Response<UpdateScanResponse>

    @GET("/api/demo/user")
    suspend fun getUserById(@Query("userId") userId: Int): Response<GetUserResponse?>

    @GET("/api/device-version")
    suspend fun checkAppVersion(): Response<CheckVersionResponse>

    @GET("/api/user/scan-history-by-date")
    suspend fun getScanHistoryByDate(@Query("date") date: String): Response<ScanHistoryByDateResponse>

    @GET("/api/user/calendar")
    suspend fun getCalendar(@Query("month") month: Int, @Query("year") year: Int): Response<CalendarResponse>

    @GET("/api/user/scan-history/{id}")
    suspend fun getScanHistoryById(@retrofit2.http.Path("id") id: Int): Response<ScanHistoryByIdResponse>

    @POST("/api/user/scan-history")
    suspend fun saveScanHistory(@Body requestBody: SaveScanHistoryRequest): Response<SaveScanHistoryResponse>

    @GET("/api/user/available-credits")
    suspend fun getAvailableCredits(): Response<AvailableCreditsResponse>

    @POST("/api/payment/initiate")
    suspend fun initiatePayment(@Body requestBody: PaymentInitiateRequest): Response<PaymentInitiateResponse>

    @POST("/api/payment/verify")
    suspend fun verifyPayment(@Body requestBody: PaymentVerifyRequest): Response<PaymentVerifyResponse>

    @GET("/api/scan-history/pdf/{id}")
    suspend fun getScanHistoryPdf(@retrofit2.http.Path("id") id: Int): Response<PdfDownloadResponse>

}