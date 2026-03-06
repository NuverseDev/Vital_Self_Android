package com.vital_self.features.packages.data.repository

import com.vital_self.features.packages.data.model.CreditPackagesResponse
import com.vital_self.features.packages.data.model.PaymentInitiateRequest
import com.vital_self.features.packages.data.model.PaymentInitiateResponse
import com.vital_self.features.packages.data.model.PaymentVerifyRequest
import com.vital_self.features.packages.data.model.PaymentVerifyResponse
import com.vital_self.features.packages.data.model.PurchaseCreditRequest
import com.vital_self.features.packages.data.model.PurchaseCreditResponse
import com.vital_self.features.packages.data.model.UserPackagesResponse
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.core.utils.helpers.NetworkUtils
import com.vital_self.core.data.remote.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PackagesRepository {

    suspend fun getCreditPackages(): Flow<ApiResponseState<CreditPackagesResponse>> {
        return flow {
            val response = NetworkModule.api.getCreditPackages()
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

    suspend fun getUserPackages(): Flow<ApiResponseState<UserPackagesResponse>> {
        return flow {
            val response = NetworkModule.api.getUserPackages()
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

    suspend fun purchaseCredit(packageId: Int): Flow<ApiResponseState<PurchaseCreditResponse>> {
        return flow {
            val response = NetworkModule.api.purchaseCredit(PurchaseCreditRequest(packageId))
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

    suspend fun initiatePayment(packageId: Int): Flow<ApiResponseState<PaymentInitiateResponse>> {
        return flow {
            val response = NetworkModule.api.initiatePayment(PaymentInitiateRequest(packageId))
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

    suspend fun verifyPayment(reference: String): Flow<ApiResponseState<PaymentVerifyResponse>> {
        return flow {
            val response = NetworkModule.api.verifyPayment(PaymentVerifyRequest(reference))
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
