package com.vital_self.room

import android.content.Context
import androidx.room.*
import com.vital_self.model.Converters
import com.vital_self.model.LoginTableModel
import com.vital_self.model.ScanHistory

@Database(entities = [LoginTableModel::class, ScanHistory::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LoginDatabase : RoomDatabase() {

    abstract fun loginDao(): DAOAccess

    companion object {

        @Volatile
        private var INSTANCE: LoginDatabase? = null

        fun getDataseClient(context: Context): LoginDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LoginDatabase::class.java,
                    "LOGIN_DATABASE"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}