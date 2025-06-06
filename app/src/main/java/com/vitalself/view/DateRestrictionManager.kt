//package com.vitalself.view
//
//import android.content.Context
//import android.content.SharedPreferences
//
//
//class ScanRestrictionManager(private val context: Context) {
//
//    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//
//    companion object {
//        private const val SCAN_COUNT_KEY = "scan_count"
//        private const val MAX_SCANS = 100 // Maximum allowed scans
//    }
//
//    fun initialize() {
//        val scanCount = prefs.getInt(SCAN_COUNT_KEY, -1)
//        if (scanCount == -1) {
//            // First time run, set scan count to 0
//            prefs.edit().putInt(SCAN_COUNT_KEY, 0).apply()
//        }
//    }
//
//    fun incrementScanCount() {
//        val currentScanCount = prefs.getInt(SCAN_COUNT_KEY, 0)
//        prefs.edit().putInt(SCAN_COUNT_KEY, currentScanCount + 1).apply()
//    }
//
//    fun checkAccess(): Boolean {
//        val currentScanCount = prefs.getInt(SCAN_COUNT_KEY, 0)
//
//        // Check if the current scan count is within the allowed limit
//        return currentScanCount < MAX_SCANS
//    }
//
//    fun getRemainingScans(): Int {
//        val currentScanCount = prefs.getInt(SCAN_COUNT_KEY, 0)
//        return if (currentScanCount <= MAX_SCANS) MAX_SCANS - currentScanCount else 0
//    }
//}