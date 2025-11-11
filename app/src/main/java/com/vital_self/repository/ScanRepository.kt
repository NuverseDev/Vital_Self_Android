package com.vital_self.repository

import com.vital_self.model.CheckVersionRequest
import com.vital_self.model.CheckVersionResponse
import com.vital_self.model.GetUserResponse
import com.vital_self.model.UpdateScanResponse
import com.vital_self.model.UserLoginRequest
import com.vital_self.model.UserRequest
import com.vital_self.network.ApiResponseState
import com.vital_self.network.NetworkUtils
import com.vital_self.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ScanRepository() {

    suspend fun getUser(requestBody: UserLoginRequest): Flow<ApiResponseState<GetUserResponse>> {     // Flow<ApiResponseState<VerifyOTPResponse>>
        return flow {
            val response = RetrofitInstance.api.getUser(requestBody)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        NetworkUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserById(requestBody: Int): Flow<ApiResponseState<GetUserResponse>> {     // Flow<ApiResponseState<VerifyOTPResponse>>
        return flow {
            val response = RetrofitInstance.api.getUserById(requestBody)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        NetworkUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun checkAppVersion(requestBody: CheckVersionRequest): Flow<ApiResponseState<CheckVersionResponse>> {     // Flow<ApiResponseState<VerifyOTPResponse>>
        return flow {
            val response = RetrofitInstance.api.checkAppVersion(requestBody)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        NetworkUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateScan(request: UserRequest): Flow<ApiResponseState<UpdateScanResponse>> {
        return flow {
            val response = RetrofitInstance.api.updateScan(request)
            if (response.isSuccessful) {
                emit(ApiResponseState.success(response.body(), response.code()))
            } else {
                emit(
                    ApiResponseState.error(
                        NetworkUtils.getErrorResponse(response.errorBody()).message, response.code()
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

}