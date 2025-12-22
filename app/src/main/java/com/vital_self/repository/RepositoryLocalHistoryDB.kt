package com.vital_self.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.vital_self.model.LoginTableModel
import com.vital_self.model.ScanHistory
import com.vital_self.room.LoginDatabase
import com.vital_self.view.scan.MeasurementResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class RepositoryLocalHistoryDB {

    companion object {

        var loginDatabase: LoginDatabase? = null

        var loginTableModel: LiveData<LoginTableModel>? = null

        fun initializeDB(context: Context): LoginDatabase {
            return LoginDatabase.getDataseClient(context)
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
            val dao = LoginDatabase.getDataseClient(context).loginDao()

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
    }
}