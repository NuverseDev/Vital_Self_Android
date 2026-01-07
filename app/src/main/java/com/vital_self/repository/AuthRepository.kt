package com.vital_self.repository

import com.vital_self.model.AuthResponse
import com.vital_self.model.CheckVersionRequest
import com.vital_self.model.CheckVersionResponse
import com.vital_self.model.ForgotPasswordRequest
import com.vital_self.model.ForgotPasswordResponse
import com.vital_self.model.GetUserResponse
import com.vital_self.model.LoginRequest
import com.vital_self.model.ProfileUpdateRequest
import com.vital_self.model.ProfileUpdateResponse
import com.vital_self.model.SignupRequest
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

class AuthRepository() {

    suspend fun login(requestBody: LoginRequest): Flow<ApiResponseState<AuthResponse>> {
        return flow {
            val response = RetrofitInstance.api.login(requestBody)
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
            val response = RetrofitInstance.api.signup(requestBody)
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
            val response = RetrofitInstance.api.forgotPassword(requestBody)
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
            val response = RetrofitInstance.api.updateProfile(requestBody)
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

    suspend fun getUserById(requestBody: Int): Flow<ApiResponseState<GetUserResponse>> {
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

    suspend fun checkAppVersion(requestBody: CheckVersionRequest): Flow<ApiResponseState<CheckVersionResponse>> {
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

}