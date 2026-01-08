package com.vital_self.features.history.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.vital_self.core.data.local.database.LoginTableModel
import com.vital_self.features.scan.data.model.ScanHistory
import com.vital_self.core.data.local.database.VitalSelfDatabase
import com.vital_self.core.data.remote.NetworkModule
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.features.history.data.model.CalendarResponse
import com.vital_self.features.history.data.model.ScanHistoryByDateResponse
import com.vital_self.features.history.data.model.ScanHistoryByIdResponse
import com.vital_self.features.history.data.model.SaveScanHistoryRequest
import com.vital_self.features.history.data.model.SaveScanHistoryResponse
import com.vital_self.features.scan.presentation.scan.MeasurementResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class HistoryRepository {

    companion object {

        var loginDatabase: VitalSelfDatabase? = null

        var loginTableModel: LiveData<LoginTableModel>? = null

        fun initializeDB(context: Context): VitalSelfDatabase {
            return VitalSelfDatabase.getDataseClient(context)
        }

        fun insertData(context: Context, username: String, password: String) {

            loginDatabase = initializeDB(context)

            CoroutineScope(IO).launch {
                val loginDetails = LoginTableModel(username, password)
                loginDatabase!!.loginDao().InsertData(loginDetails)
            }

        }

        fun getLoginDetails(context: Context, username: String): LiveData<LoginTableModel>? {

            loginDatabase = initializeDB(context)

            loginTableModel = loginDatabase!!.loginDao().getLoginDetails(username)

            return loginTableModel
        }

        // ------------------ Scan History ------------------

        suspend fun insertOrUpdateScanResult(context: Context, scanResult: MeasurementResult) {
            val dao = VitalSelfDatabase.getDataseClient(context).loginDao()

            val existingHistory = dao.getScanHistoryByDate(scanResult.date)
            if (existingHistory != null) {
                val updatedList = existingHistory.scanResults.toMutableList()
                updatedList.add(scanResult) // appending new scan
                dao.insertOrUpdateScanHistory(existingHistory.copy(scanResults = updatedList))
            } else {
                dao.insertOrUpdateScanHistory(ScanHistory(scanResult.date, listOf(scanResult)))
            }
        }


        suspend fun getScanResultsByDate(context: Context, date: String): List<MeasurementResult> {
            loginDatabase = initializeDB(context)
            return loginDatabase!!.loginDao().getScanHistoryByDate(date)?.scanResults ?: emptyList()
        }

        suspend fun getAllScanResults(context: Context): List<ScanHistory> {
            loginDatabase = initializeDB(context)
            return loginDatabase!!.loginDao().getAllScanHistories()
        }

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
    }
}