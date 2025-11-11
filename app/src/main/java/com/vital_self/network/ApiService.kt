package com.vital_self.network


import com.vital_self.model.CheckVersionRequest
import com.vital_self.model.CheckVersionResponse
import com.vital_self.model.GetUserResponse
import com.vital_self.model.UpdateScanResponse
import com.vital_self.model.UserLoginRequest
import com.vital_self.model.UserRequest
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