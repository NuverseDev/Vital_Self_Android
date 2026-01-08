package com.vital_self.core.data.local.database

import android.content.Context
import androidx.room.*
import com.vital_self.features.scan.data.model.Converters
import com.vital_self.core.data.local.database.dao.DAOAccess
import com.vital_self.features.scan.data.model.ScanHistory

@Database(entities = [LoginTableModel::class, ScanHistory::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class VitalSelfDatabase : RoomDatabase() {

    abstract fun loginDao(): DAOAccess

    companion object {

        @Volatile
        private var INSTANCE: VitalSelfDatabase? = null

        fun getDataseClient(context: Context): VitalSelfDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    VitalSelfDatabase::class.java,
                    "LOGIN_DATABASE"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}