package com.vital_self.features.qrcode.presentation

import com.journeyapps.barcodescanner.CaptureActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.zxing.integration.android.IntentIntegrator
import com.vital_self.features.scan.presentation.result.VitalResultActivity2

// Data class to parse QR code content
data class QrCodeData(
    @SerializedName("scanId")
    val scanId: Int?
)

class QrScanActivity : AppCompatActivity() {

    private val cameraPermission = Manifest.permission.CAMERA

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startQrScanner()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // No need to setContentView - we are directly launching scanner

        checkCameraPermissionAndScan()
    }

    private fun checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
            startQrScanner()
        } else {
            requestPermissionLauncher.launch(cameraPermission)
        }
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setPrompt("Scan a QR Code")
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(true)
        integrator.captureActivity = CaptureActivityPortrait::class.java // Optional: Force portrait mode
        integrator.initiateScan()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // QR Code scanned
                val scannedJson = result.contents
                val gson = Gson()
                try {
                    // Parse QR code to get scan ID
                    val qrData = gson.fromJson(scannedJson, QrCodeData::class.java)

                    if (qrData.scanId != null && qrData.scanId != -1) {
                        // Use the scan ID to fetch data from API
                        VitalResultActivity2.startActivityWithScanId(this, qrData.scanId)
                        finish()
                    } else {
                        Toast.makeText(this, "Invalid QR code: No scan ID found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_SHORT).show()
                    finish()
                }

            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

class CaptureActivityPortrait : CaptureActivity()
