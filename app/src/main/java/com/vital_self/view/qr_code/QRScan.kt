package com.vital_self.view.qr_code

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
import com.biosensesignal.sdk.api.session.demographics.Sex
import com.biosensesignal.sdk.api.session.user_info.SmokingStatus
import com.vital_self.model.Model
import com.vital_self.utils.ScanResultGenrator
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import com.vital_self.view.scan.MeasurementResult
import com.vital_self.view.scan.VitalResultActivity2

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
                    val data = gson.fromJson(scannedJson, MeasurementResult::class.java)
                    val sex = when (data.gender) {
                        "Male" -> Sex.MALE
                        "Female" -> Sex.FEMALE
                        else -> Sex.UNSPECIFIED
                    }
                    val subjectData = Model.SubjectDetails(
                        sex = sex,
                        age = data.age.toDouble() ?: 0.0,
                        weight = data.weight.toDouble() ?: 0.0,
                        height = data.height.toDouble() ?: 0.0,
                        isSmoker = SmokingStatus.UNSPECIFIED,
                        name = data.name ?: ""
                    )
                    val list = ScanResultGenrator.createScanResult(
                        context = this,
                        result = data
                    )

                    VitalResultActivity2.Companion.startActivity(
                        this,
                        list,
                        subjectData,
                        data.wellnessIndex,
                        data.wellnessLevel,
                        date = data.date,
                        time = data.time,
                        false
                    )

                    // You can navigate or open detail screen here
                    finish()


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
