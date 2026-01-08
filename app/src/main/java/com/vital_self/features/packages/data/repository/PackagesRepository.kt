package com.vital_self.features.packages.data.repository

import com.vital_self.features.packages.data.model.CreditPackagesResponse
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
}
