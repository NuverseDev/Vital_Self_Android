package com.vitalself.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vitalself.model.LoginTableModel
import com.vitalself.model.ScanHistory

@Dao
interface DAOAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun InsertData(loginTableModel: LoginTableModel)

    @Query("SELECT * FROM Login WHERE Username =:username")
    fun getLoginDetails(username: String?) : LiveData<LoginTableModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateScanHistory(scanHistoryEntity: ScanHistory)

    @Query("SELECT * FROM ScanHistory WHERE date = :date LIMIT 5")
    suspend fun getScanHistoryByDate(date: String): ScanHistory?

    @Query("SELECT * FROM ScanHistory")
    suspend fun getAllScanHistories(): List<ScanHistory>
}