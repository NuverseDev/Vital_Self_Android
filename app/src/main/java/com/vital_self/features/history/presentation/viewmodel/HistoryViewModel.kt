package com.vital_self.features.history.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital_self.core.data.remote.model.ApiResponseState
import com.vital_self.core.data.remote.model.Status
import com.vital_self.features.history.data.model.CalendarResponse
import com.vital_self.features.history.data.model.ScanHistoryByDateResponse
import com.vital_self.features.history.data.model.ScanHistoryByIdResponse
import com.vital_self.features.history.data.model.SaveScanHistoryRequest
import com.vital_self.features.history.data.model.SaveScanHistoryResponse
import com.vital_self.features.history.data.repository.HistoryRepository
import com.vital_self.features.history.presentation.adapter.HistoryItem
import com.vital_self.features.history.presentation.state.ScanHistoryScreenEvent
import com.vital_self.features.history.presentation.state.ScanHistoryScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class HistoryViewModel : ViewModel() {

    // Compose Screen State
    private val _screenState = MutableStateFlow(ScanHistoryScreenState())
    val screenState: StateFlow<ScanHistoryScreenState> = _screenState.asStateFlow()

    // API LiveData (kept for backward compatibility with other screens)
    private val _scanHistoryByDate = MutableLiveData<ApiResponseState<ScanHistoryByDateResponse>>()
    val scanHistoryByDate: LiveData<ApiResponseState<ScanHistoryByDateResponse>> = _scanHistoryByDate

    private val _calendarData = MutableLiveData<ApiResponseState<CalendarResponse>>()
    val calendarData: LiveData<ApiResponseState<CalendarResponse>> = _calendarData

    private val _scanHistoryById = MutableLiveData<ApiResponseState<ScanHistoryByIdResponse>>()
    val scanHistoryById: LiveData<ApiResponseState<ScanHistoryByIdResponse>> = _scanHistoryById

    private val _saveScanHistory = MutableLiveData<ApiResponseState<SaveScanHistoryResponse>>()
    val saveScanHistory: LiveData<ApiResponseState<SaveScanHistoryResponse>> = _saveScanHistory

    init {
        // Initialize with today's data
        val today = LocalDate.now()
        loadDataForDate(today)
        loadCalendarForMonth(today.monthValue, today.year)
    }

    // ------------------- Compose Event Handler -------------------

    fun onEvent(event: ScanHistoryScreenEvent) {
        when (event) {
            is ScanHistoryScreenEvent.DateSelected -> {
                _screenState.update { it.copy(selectedDate = event.date) }
                loadDataForDate(event.date)
            }
            is ScanHistoryScreenEvent.MonthChanged -> {
                loadCalendarForMonth(event.month, event.year)
            }
            is ScanHistoryScreenEvent.ItemClicked -> {
                // Navigation handled by Activity
            }
            is ScanHistoryScreenEvent.QRCodeClicked -> {
                _screenState.update {
                    it.copy(
                        showQRSheet = true,
                        qrScanId = event.scanId,
                        qrDate = event.date,
                        qrTime = event.time
                    )
                }
            }
            is ScanHistoryScreenEvent.DismissQRSheet -> {
                _screenState.update {
                    it.copy(
                        showQRSheet = false,
                        qrScanId = null,
                        qrDate = "",
                        qrTime = ""
                    )
                }
            }
            is ScanHistoryScreenEvent.DismissError -> {
                _screenState.update { it.copy(error = null) }
            }
        }
    }

    // ------------------- Data Loading Methods -------------------

    private fun loadDataForDate(date: LocalDate) {
        val formattedDate = String.format("%04d-%02d-%02d", date.year, date.monthValue, date.dayOfMonth)

        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }

            HistoryRepository.getScanHistoryByDateFromApi(formattedDate).collect { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        val historyItems = response.data?.data?.scanResults?.map { scanResultItem ->
                            HistoryItem(
                                scanId = scanResultItem.id,
                                scanResult = scanResultItem
                            )
                        } ?: emptyList()

                        _screenState.update {
                            it.copy(
                                historyItems = historyItems,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    Status.ERROR -> {
                        _screenState.update {
                            it.copy(
                                historyItems = emptyList(),
                                isLoading = false,
                                error = response.message ?: "Failed to load history"
                            )
                        }
                    }
                    Status.LOADING -> {
                        _screenState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun loadCalendarForMonth(month: Int, year: Int) {
        viewModelScope.launch {
            HistoryRepository.getCalendarFromApi(month, year).collect { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        val recordedDates = response.data?.data?.list
                            ?.filter { it.score > 0 }
                            ?.mapNotNull { item ->
                                val parts = item.date.split("-")
                                if (parts.size == 3) {
                                    val y = parts[0].toInt()
                                    val m = parts[1].toInt()
                                    val d = parts[2].toInt()
                                    LocalDate.of(y, m, d)
                                } else null
                            } ?: emptyList()

                        _screenState.update {
                            it.copy(recordedDates = recordedDates)
                        }
                    }
                    Status.ERROR -> {
                        // Calendar error doesn't need to show to user
                    }
                    Status.LOADING -> {
                        // No loading state for calendar
                    }
                }
            }
        }
    }

    // ------------------- API Methods (Legacy) -------------------

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
