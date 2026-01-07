package com.vital_self.network


import com.vital_self.model.AuthResponse
import com.vital_self.model.CheckVersionRequest
import com.vital_self.model.CheckVersionResponse
import com.vital_self.model.CreditPackagesResponse
import com.vital_self.model.PurchaseCreditRequest
import com.vital_self.model.PurchaseCreditResponse
import com.vital_self.model.ForgotPasswordRequest
import com.vital_self.model.ForgotPasswordResponse
import com.vital_self.model.GetUserResponse
import com.vital_self.model.LoginRequest
import com.vital_self.model.ProfileUpdateRequest
import com.vital_self.model.ProfileUpdateResponse
import com.vital_self.model.SignupRequest
import com.vital_self.model.UpdateScanResponse
import com.vital_self.model.UserLoginRequest
import com.vital_self.model.UserPackagesResponse
import com.vital_self.model.UserRequest
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

    @POST("/api/demo/check-version")
    suspend fun checkAppVersion(@Body requestBody: CheckVersionRequest): Response<CheckVersionResponse>

}