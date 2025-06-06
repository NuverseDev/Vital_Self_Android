package com.vitalself.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalself.model.LoginTableModel
import com.vitalself.model.ScanHistory
import com.vitalself.repository.LoginRepository
import com.vitalself.view.MeasurementResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    var liveDataLogin: LiveData<LoginTableModel>? = null

    fun insertData(context: Context, username: String, password: String) {
       LoginRepository.insertData(context, username, password)
    }

    fun getLoginDetails(context: Context, username: String) : LiveData<LoginTableModel>? {
        liveDataLogin = LoginRepository.getLoginDetails(context, username)
        return liveDataLogin
    }

    // ------------------- Scan History -------------------

    fun insertOrUpdateScanResult(context: Context, scanResult: MeasurementResult) {
        viewModelScope.launch(Dispatchers.IO) {
            LoginRepository.insertOrUpdateScanResult(context, scanResult)
        }
    }

    fun getScanResultByDate(context: Context, date: String, callback: (List<MeasurementResult>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = LoginRepository.getScanResultsByDate(context, date)
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
    }

    fun getAllScanResults(context: Context, callback: (List<ScanHistory>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = LoginRepository.getAllScanResults(context)
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
    }


}