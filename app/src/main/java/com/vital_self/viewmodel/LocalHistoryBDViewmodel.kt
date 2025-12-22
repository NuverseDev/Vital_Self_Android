package com.vital_self.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vital_self.model.LoginTableModel
import com.vital_self.model.ScanHistory
import com.vital_self.repository.RepositoryLocalHistoryDB
import com.vital_self.view.scan.MeasurementResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalHistoryBDViewmodel : ViewModel() {

    var liveDataLogin: LiveData<LoginTableModel>? = null

    fun insertData(context: Context, username: String, password: String) {
       RepositoryLocalHistoryDB.insertData(context, username, password)
    }

    fun getLoginDetails(context: Context, username: String) : LiveData<LoginTableModel>? {
        liveDataLogin = RepositoryLocalHistoryDB.getLoginDetails(context, username)
        return liveDataLogin
    }

    // ------------------- Scan History -------------------

    fun insertOrUpdateScanResult(context: Context, scanResult: MeasurementResult) {
        viewModelScope.launch(Dispatchers.IO) {
            RepositoryLocalHistoryDB.insertOrUpdateScanResult(context, scanResult)
        }
    }

    fun getScanResultByDate(context: Context, date: String, callback: (List<MeasurementResult>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = RepositoryLocalHistoryDB.getScanResultsByDate(context, date)
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
    }

    fun getAllScanResults(context: Context, callback: (List<ScanHistory>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = RepositoryLocalHistoryDB.getAllScanResults(context)
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
    }


}