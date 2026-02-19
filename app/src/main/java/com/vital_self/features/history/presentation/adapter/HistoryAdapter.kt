package com.vital_self.features.history.presentation.adapter

import com.vital_self.features.history.data.model.ScanResultItem

/**
 * Data class to hold ScanResultItem from API for history list
 */
data class HistoryItem(
    val scanId: Int?,
    val scanResult: ScanResultItem
)
