package com.vital_self.features.history.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital_self.core.data.local.database.LoginTableModel
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.features.history.data.model.CalendarResponse
import com.vital_self.features.history.data.model.ScanHistoryByDateResponse
import com.vital_self.features.history.data.model.ScanHistoryByIdResponse
import com.vital_self.features.history.data.model.SaveScanHistoryRequest
import com.vital_self.features.history.data.model.SaveScanHistoryResponse
import com.vital_self.features.scan.data.model.ScanHistory
import com.vital_self.features.history.data.repository.HistoryRepository
import com.vital_self.features.scan.presentation.scan.MeasurementResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel : ViewModel() {

    var liveDataLogin: LiveData<LoginTableModel>? = null

    // API LiveData
    private val _scanHistoryByDate = MutableLiveData<ApiResponseState<ScanHistoryByDateResponse>>()
    val scanHistoryByDate: LiveData<ApiResponseState<ScanHistoryByDateResponse>> = _scanHistoryByDate

    private val _calendarData = MutableLiveData<ApiResponseState<CalendarResponse>>()
    val calendarData: LiveData<ApiResponseState<CalendarResponse>> = _calendarData

    private val _scanHistoryById = MutableLiveData<ApiResponseState<ScanHistoryByIdResponse>>()
    val scanHistoryById: LiveData<ApiResponseState<ScanHistoryByIdResponse>> = _scanHistoryById

    private val _saveScanHistory = MutableLiveData<ApiResponseState<SaveScanHistoryResponse>>()
    val saveScanHistory: LiveData<ApiResponseState<SaveScanHistoryResponse>> = _saveScanHistory

    fun insertData(context: Context, username: String, password: String) {
       HistoryRepository.insertData(context, username, password)
    }

    fun getLoginDetails(context: Context, username: String) : LiveData<LoginTableModel>? {
        liveDataLogin = HistoryRepository.getLoginDetails(context, username)
        return liveDataLogin
    }

    // ------------------- Scan History -------------------

    fun insertOrUpdateScanResult(context: Context, scanResult: MeasurementResult) {
        viewModelScope.launch(Dispatchers.IO) {
            HistoryRepository.insertOrUpdateScanResult(context, scanResult)
        }
    }

    fun getScanResultByDate(context: Context, date: String, callback: (List<MeasurementResult>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = HistoryRepository.getScanResultsByDate(context, date)
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
    }

    fun getAllScanResults(context: Context, callback: (List<ScanHistory>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = HistoryRepository.getAllScanResults(context)
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
    }

    // ------------------- API Methods -------------------

    fun fetchScanHistoryByDate(date: String) {
        viewModelScope.launch {
            HistoryRepository.getScanHistoryByDateFromApi(date).collect { response ->
                _scanHistoryByDate.postValue(response)
            }
        }
    }

    fun fetchCalendar(month: Int, year: Int) {
        viewModelScope.launch {
            HistoryRepository.getCalendarFromApi(month, year).collect { response ->
                _calendarData.postValue(response)
            }
        }
    }

    fun fetchScanHistoryById(id: Int) {
        viewModelScope.launch {
            HistoryRepository.getScanHistoryByIdFromApi(id).collect { response ->
                _scanHistoryById.postValue(response)
            }
        }
    }

    fun saveScanHistory(request: SaveScanHistoryRequest) {
        viewModelScope.launch {
            HistoryRepository.saveScanHistoryToApi(request).collect { response ->
                _saveScanHistory.postValue(response)
            }
        }
    }

}