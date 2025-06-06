package com.vitalself.network


import com.vitalself.model.CheckVersionRequest
import com.vitalself.model.CheckVersionResponse
import com.vitalself.model.GetUserResponse
import com.vitalself.model.UpdateScanResponse
import com.vitalself.model.UserLoginRequest
import com.vitalself.model.UserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("/api/demo/login")
    suspend fun getUser(@Body requestBody: UserLoginRequest): Response<GetUserResponse?>

    @POST("/api/demo/decrement")
    suspend fun updateScan(@Body requestBody: UserRequest): Response<UpdateScanResponse>

    @GET("/api/demo/user")
    suspend fun getUserById(@Query("userId") userId: Int): Response<GetUserResponse?>

    @POST("/api/demo/check-version")
    suspend fun checkAppVersion(@Body requestBody: CheckVersionRequest): Response<CheckVersionResponse>

}