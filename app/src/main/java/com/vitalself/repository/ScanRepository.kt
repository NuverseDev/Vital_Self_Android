package com.vitalself.repository

import com.vitalself.model.CheckVersionRequest
import com.vitalself.model.CheckVersionResponse
import com.vitalself.model.GetUserResponse
import com.vitalself.model.UpdateScanResponse
import com.vitalself.model.UserLoginRequest
import com.vitalself.model.UserRequest
import com.vitalself.network.ApiResponseState
import com.vitalself.network.NetworkUtils
import com.vitalself.network.RetrofitInstance
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