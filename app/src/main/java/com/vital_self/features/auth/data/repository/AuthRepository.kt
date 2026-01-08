package com.vital_self.features.auth.data.repository

import com.vital_self.features.auth.data.model.AuthResponse
import com.vital_self.core.data.remote.model.CheckVersionRequest
import com.vital_self.core.data.remote.model.CheckVersionResponse
import com.vital_self.features.auth.data.model.ForgotPasswordRequest
import com.vital_self.features.auth.data.model.ForgotPasswordResponse
import com.vital_self.features.profile.data.model.GetUserResponse
import com.vital_self.features.auth.data.model.LoginRequest
import com.vital_self.features.auth.data.model.ProfileUpdateRequest
import com.vital_self.features.auth.data.model.ProfileUpdateResponse
import com.vital_self.features.auth.data.model.SignupRequest
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

class AuthRepository() {

    suspend fun login(requestBody: LoginRequest): Flow<ApiResponseState<AuthResponse>> {
        return flow {
            val response = NetworkModule.api.login(requestBody)
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

    suspend fun signup(requestBody: SignupRequest): Flow<ApiResponseState<AuthResponse>> {
        return flow {
            val response = NetworkModule.api.signup(requestBody)
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

    suspend fun forgotPassword(requestBody: ForgotPasswordRequest): Flow<ApiResponseState<ForgotPasswordResponse>> {
        return flow {
            val response = NetworkModule.api.forgotPassword(requestBody)
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

    suspend fun updateProfile(requestBody: ProfileUpdateRequest): Flow<ApiResponseState<ProfileUpdateResponse>> {
        return flow {
            val response = NetworkModule.api.updateProfile(requestBody)
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

    suspend fun getUser(requestBody: UserLoginRequest): Flow<ApiResponseState<GetUserResponse>> {
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

    suspend fun getUserById(requestBody: Int): Flow<ApiResponseState<GetUserResponse>> {
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

    suspend fun checkAppVersion(requestBody: CheckVersionRequest): Flow<ApiResponseState<CheckVersionResponse>> {
        return flow {
            val response = NetworkModule.api.checkAppVersion(requestBody)
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