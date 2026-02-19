package com.vital_self.features.scan.data.repository

import com.vital_self.core.data.remote.model.CheckVersionRequest
import com.vital_self.core.data.remote.model.CheckVersionResponse
import com.vital_self.features.packages.data.model.AvailableCreditsResponse
import com.vital_self.features.profile.data.model.GetUserResponse
import com.vital_self.features.profile.data.model.UpdateScanResponse
import com.vital_self.features.profile.data.model.UserLoginRequest
import com.vital_self.features.profile.data.model.UserRequest
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.core.utils.helpers.NetworkUtils
import com.vital_self.core.data.remote.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ScanRepository() {

    suspend fun getUser(requestBody: UserLoginRequest): Flow<ApiResponseState<GetUserResponse>> {     // Flow<ApiResponseState<VerifyOTPResponse>>
        return flow {
            val response = NetworkModule.api.getUser(requestBody)
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
            val response = NetworkModule.api.getUserById(requestBody)
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

    suspend fun checkAppVersion(): Flow<ApiResponseState<CheckVersionResponse>> {     // Flow<ApiResponseState<VerifyOTPResponse>>
        return flow {
            val response = NetworkModule.api.checkAppVersion()
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
            val response = NetworkModule.api.updateScan(request)
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

    suspend fun getAvailableCredits(): Flow<ApiResponseState<AvailableCreditsResponse>> {
        return flow {
            val response = NetworkModule.api.getAvailableCredits()
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