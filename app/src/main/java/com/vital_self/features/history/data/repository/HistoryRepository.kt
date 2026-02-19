package com.vital_self.features.history.data.repository

import com.vital_self.core.data.remote.NetworkModule
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.features.history.data.model.CalendarResponse
import com.vital_self.features.history.data.model.ScanHistoryByDateResponse
import com.vital_self.features.history.data.model.ScanHistoryByIdResponse
import com.vital_self.features.history.data.model.SaveScanHistoryRequest
import com.vital_self.features.history.data.model.SaveScanHistoryResponse
import com.vital_self.features.scan.data.model.PdfDownloadResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class HistoryRepository {

    companion object {

        // ------------------ API Methods ------------------

        fun getScanHistoryByDateFromApi(date: String): Flow<ApiResponseState<ScanHistoryByDateResponse>> {
            return flow {
                emit(ApiResponseState.loading())
                try {
                    val response = NetworkModule.api.getScanHistoryByDate(date)
                    if (response.isSuccessful && response.body() != null) {
                        emit(ApiResponseState.success(response.body()!!, response.code()))
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Failed to fetch scan history"
                        emit(ApiResponseState.error(errorMessage, response.code()))
                    }
                } catch (e: Exception) {
                    emit(ApiResponseState.error(e.message ?: "Network error", -1))
                }
            }.flowOn(IO)
        }

        fun getCalendarFromApi(month: Int, year: Int): Flow<ApiResponseState<CalendarResponse>> {
            return flow {
                emit(ApiResponseState.loading())
                try {
                    val response = NetworkModule.api.getCalendar(month, year)
                    if (response.isSuccessful && response.body() != null) {
                        emit(ApiResponseState.success(response.body()!!, response.code()))
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Failed to fetch calendar"
                        emit(ApiResponseState.error(errorMessage, response.code()))
                    }
                } catch (e: Exception) {
                    emit(ApiResponseState.error(e.message ?: "Network error", -1))
                }
            }.flowOn(IO)
        }

        fun getScanHistoryByIdFromApi(id: Int): Flow<ApiResponseState<ScanHistoryByIdResponse>> {
            return flow {
                emit(ApiResponseState.loading())
                try {
                    val response = NetworkModule.api.getScanHistoryById(id)
                    if (response.isSuccessful && response.body() != null) {
                        emit(ApiResponseState.success(response.body()!!, response.code()))
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Failed to fetch scan history"
                        emit(ApiResponseState.error(errorMessage, response.code()))
                    }
                } catch (e: Exception) {
                    emit(ApiResponseState.error(e.message ?: "Network error", -1))
                }
            }.flowOn(IO)
        }

        fun saveScanHistoryToApi(request: SaveScanHistoryRequest): Flow<ApiResponseState<SaveScanHistoryResponse>> {
            return flow {
                emit(ApiResponseState.loading())
                try {
                    val response = NetworkModule.api.saveScanHistory(request)
                    if (response.isSuccessful && response.body() != null) {
                        emit(ApiResponseState.success(response.body()!!, response.code()))
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Failed to save scan history"
                        emit(ApiResponseState.error(errorMessage, response.code()))
                    }
                } catch (e: Exception) {
                    emit(ApiResponseState.error(e.message ?: "Network error", -1))
                }
            }.flowOn(IO)
        }

        fun getScanHistoryPdfUrl(scanId: Int): Flow<ApiResponseState<PdfDownloadResponse>> {
            return flow {
                emit(ApiResponseState.loading())
                try {
                    val response = NetworkModule.api.getScanHistoryPdf(scanId)
                    if (response.isSuccessful && response.body() != null) {
                        emit(ApiResponseState.success(response.body()!!, response.code()))
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Failed to get PDF"
                        emit(ApiResponseState.error(errorMessage, response.code()))
                    }
                } catch (e: Exception) {
                    emit(ApiResponseState.error(e.message ?: "Network error", -1))
                }
            }.flowOn(IO)
        }
    }
}
