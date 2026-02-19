package com.vital_self.features.scan.presentation.result

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.vital_self.R
import com.vital_self.core.domain.model.Model
import com.vital_self.core.ui.theme.VitalSelfTheme
import com.vital_self.core.utils.constants.AppConstants
import com.vital_self.core.utils.helpers.AnimationsHandler
import com.vital_self.core.utils.helpers.PDFGenerator
import com.vital_self.features.scan.presentation.result.screen.VitalResultScreenContent
import com.vital_self.features.scan.presentation.result.state.VitalResultScreenEvent
import com.vital_self.features.scan.presentation.result.viewmodel.VitalResultViewModel
import com.vital_self.features.scan.presentation.scan.VitalScanActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class VitalResultActivity2 : ComponentActivity() {

    private val viewModel: VitalResultViewModel by viewModels()

    companion object {

        const val ISTOPBACK = "topback"
        const val SCAN_ID = "scan_id"
        const val IS_FROM_HISTORY = "is_from_history"
        const val IS_FROM_SCAN = "is_from_scan"

        fun startActivity(
            activity: Activity,
            list: ArrayList<Model.VitalsData>,
            subjectDetails: Model.SubjectDetails?,
            wellnessIndex: String?,
            wellnessLevel: String?,
            date: String,
            time: String,
            isTop: Boolean
        ) {
            Intent(activity, VitalResultActivity2::class.java).apply {
                putExtra(AppConstants.SUBJECT, subjectDetails)
                putParcelableArrayListExtra(AppConstants.VITAL_LIST, list)
                putExtra(AppConstants.WELLNESS_INDEX, wellnessIndex)
                putExtra(AppConstants.WELLNESS_LEVEL, wellnessLevel)
                putExtra(AppConstants.DATE, date)
                putExtra(AppConstants.TIME, time)
                putExtra(ISTOPBACK, isTop)
                putExtra(IS_FROM_HISTORY, false)
                putExtra(IS_FROM_SCAN, true)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }

        fun startActivityWithScanId(activity: Activity, scanId: Int, isFromHistory: Boolean = true) {
            Intent(activity, VitalResultActivity2::class.java).apply {
                putExtra(SCAN_ID, scanId)
                putExtra(IS_FROM_HISTORY, isFromHistory)
                putExtra(IS_FROM_SCAN, !isFromHistory)
                putExtra(ISTOPBACK, false)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private val isFromHistory by lazy { intent.getBooleanExtra(IS_FROM_HISTORY, false) }
    private val isFromScan by lazy { intent.getBooleanExtra(IS_FROM_SCAN, false) }
    private val scanId by lazy { intent.getIntExtra(SCAN_ID, -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (scanId != -1) {
            Log.d("TAG", "onCreate: initfrom scan id")
            viewModel.initFromScanId(scanId, this)
        } else {
            Log.d("TAG", "onCreate: initfrom intent")
            initFromIntent()
        }

        // Observe PDF share events
        lifecycleScope.launch {
            viewModel.pdfShareEvent.collectLatest { pdfUrl ->
                downloadAndSharePdf(pdfUrl)
            }
        }

        setContent {
            VitalSelfTheme {

                val state by viewModel.screenState.collectAsState()

                VitalResultScreenContent(
                    state = state,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = { handleBackNavigation() },
                    onSharePdf = { viewModel.onEvent(VitalResultScreenEvent.ShareClicked) }
                )

                if (state.vitals.isNotEmpty() && !state.isLoading) {
                    createPdf(state)
                }

            }
        }
    }

    @Suppress("DEPRECATION")
    private fun initFromIntent() {
        val vitalList = intent.getParcelableArrayListExtra<Model.VitalsData>(AppConstants.VITAL_LIST)
        val subject = intent.getParcelableExtra<Model.SubjectDetails>(AppConstants.SUBJECT)
        val wellnessIndex = intent.getStringExtra(AppConstants.WELLNESS_INDEX)
        val wellnessLevel = intent.getStringExtra(AppConstants.WELLNESS_LEVEL)
        val date = intent.getStringExtra(AppConstants.DATE) ?: ""
        val time = intent.getStringExtra(AppConstants.TIME) ?: ""

        viewModel.initFromIntent(
            vitals = vitalList,
            subject = subject,
            wellnessIndex = wellnessIndex,
            wellnessLevel = wellnessLevel,
            date = date,
            time = time
        )

    }

    private fun handleBackNavigation() {
        if (isFromScan) {
            // Coming from scan activity - go back to scan
            VitalScanActivity.startActivity(this)
            finish()
        } else {
            // Coming from history or other sources - just finish
            finish()
        }
    }

    private fun downloadAndSharePdf(pdfUrl: String) {
        lifecycleScope.launch {
            try {
                val state = viewModel.screenState.value
                val fileName = "${state.subject?.name ?: "User"}_VitalsReport.pdf"

                // Download PDF to cache directory
                val pdfFile = withContext(Dispatchers.IO) {
                    downloadPdfToCache(pdfUrl, fileName)
                }

                if (pdfFile != null && pdfFile.exists()) {
                    val uri: Uri = FileProvider.getUriForFile(
                        this@VitalResultActivity2,
                        "com.vital_self.fileprovider",
                        pdfFile
                    )

                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    startActivity(Intent.createChooser(shareIntent, "Share report using"))

                    // Clear PDF URL from state
                    viewModel.onEvent(VitalResultScreenEvent.ClearPdfUrl)
                } else {
                    Toast.makeText(
                        this@VitalResultActivity2,
                        "Failed to download PDF",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("VitalResultActivity2", "Error sharing PDF: ${e.message}", e)
                Toast.makeText(
                    this@VitalResultActivity2,
                    "Error sharing PDF: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun downloadPdfToCache(pdfUrl: String, fileName: String): File? {
        return try {
            val url = URL(pdfUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                // Use cache directory instead of external files
                val cacheDir = File(cacheDir, "shared_pdfs")
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }

                val pdfFile = File(cacheDir, fileName)
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(pdfFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                pdfFile
            } else {
                Log.e("VitalResultActivity2", "HTTP error: ${connection.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e("VitalResultActivity2", "Download error: ${e.message}", e)
            null
        }
    }

    private var pdfCreated = false

    private fun createPdf(state: com.vital_self.features.scan.presentation.result.state.VitalResultScreenState) {
        if (pdfCreated) return
        pdfCreated = true
        PDFGenerator.createVitalPdf(
            context = this,
            healthVitals = ArrayList(state.vitals),
            additionalText = getString(R.string.disclaimer_desc),
            bmi = state.bmi,
            height = state.subject?.height?.toString() ?: "",
            weight = state.subject?.weight?.toString() ?: "",
            age = state.subject?.age?.toString() ?: "",
            name = state.subject?.name ?: "",
            wellnessLevel = state.wellnessLevel,
            wellnessIndex = state.wellnessIndex,
            headertext = getString(R.string.header_text),
            date = state.date,
            time = state.time
        )
    }
}
