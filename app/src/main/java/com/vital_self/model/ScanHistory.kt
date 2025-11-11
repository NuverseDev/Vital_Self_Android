package com.vital_self.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.vital_self.view.MeasurementResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "ScanHistory")
data class ScanHistory(

    @PrimaryKey
    @ColumnInfo(name = "date")
    var date: String,  // date is acting as unique day ID

    @ColumnInfo(name = "scanResults")
    var scanResults: List<MeasurementResult> = emptyList()
)

class Converters {

    @TypeConverter
    fun fromMeasurementList(value: List<MeasurementResult>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toMeasurementList(value: String): List<MeasurementResult> {
        val listType = object : TypeToken<List<MeasurementResult>>() {}.type
        return Gson().fromJson(value, listType)
    }
}