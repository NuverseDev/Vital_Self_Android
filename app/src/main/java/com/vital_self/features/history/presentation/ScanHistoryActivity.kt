

package com.vital_self.features.history.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.biosensesignal.sdk.api.session.demographics.Sex
import com.biosensesignal.sdk.api.session.user_info.SmokingStatus
import com.vital_self.R
import com.vital_self.features.history.presentation.adapter.HistoryAdapter
import com.vital_self.features.history.presentation.adapter.HistoryItem
import com.vital_self.features.history.presentation.adapter.HistoryListener
import com.vital_self.core.base.BaseActivity
import com.vital_self.features.qrcode.presentation.components.BottomSheetQRCode
import com.vital_self.databinding.ScanHistoryActivityBinding
import com.vital_self.core.domain.model.Model
import com.vital_self.core.data.remote.model.Status
import com.vital_self.features.history.data.model.toMeasurementResult
import com.vital_self.features.scan.data.repository.ScanRepository
import com.vital_self.features.scan.presentation.viewmodel.ScanViewModelFactory
import com.vital_self.core.utils.helpers.AnimationsHandler
import com.vital_self.features.scan.presentation.scan.ScanResultGenerator
import com.vital_self.features.history.presentation.viewmodel.HistoryViewModel
import com.vital_self.features.scan.presentation.viewmodel.ScanViewModel
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.vital_self.features.scan.presentation.scan.MeasurementResult
import com.vital_self.features.scan.presentation.result.VitalResultActivity2
import java.util.Calendar
import kotlin.getValue


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ScanHistoryActivity : BaseActivity(), HistoryListener {

    private lateinit var binding: ScanHistoryActivityBinding
    private lateinit var historyViewmodel: HistoryViewModel
    private lateinit var historyAdapter: HistoryAdapter

    companion object {
        const val TITLE = ""
        fun startActivity(activity: Activity,title:String) {
            Intent(activity, ScanHistoryActivity::class.java).apply {
                putExtra(TITLE,title)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }
    private val title by lazy {
        intent.getStringExtra(TITLE)
    }
    private val viewModel: ScanViewModel by viewModels {
        ScanViewModelFactory(ScanRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.scan_history_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.tool.tvTitle.text = title
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        historyViewmodel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        setUpToolbar(binding.tool)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        setupAdapter()
        setupObservers()
        initUi()
        listeners()
    }

    private fun setupAdapter() {
        historyAdapter = HistoryAdapter(this@ScanHistoryActivity)

        binding.adapter = historyAdapter
    }

    private fun setupObservers() {
        // Observe scan history by date
        historyViewmodel.scanHistoryByDate.observe(this) { response ->
            when (response.status) {
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    val historyItems = response.data?.data?.scanResults?.map { scanResultItem ->
                        HistoryItem(
                            scanId = scanResultItem.id,
                            measurementResult = scanResultItem.toMeasurementResult()
                        )
                    } ?: emptyList()
                    historyAdapter.setItems(historyItems)
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, response.message ?: "Failed to load history", Toast.LENGTH_SHORT).show()
                    historyAdapter.setItems(emptyList())
                }
            }
        }

        // Observe calendar data
        historyViewmodel.calendarData.observe(this) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    val calendarDays = response.data?.data?.list?.filter { it.score > 0 }?.mapNotNull { item ->
                        val parts = item.date.split("-")
                        if (parts.size == 3) {
                            val y = parts[0].toInt()
                            val m = parts[1].toInt()
                            val d = parts[2].toInt()
                            CalendarDay.from(y, m, d)
                        } else null
                    } ?: emptyList()

                    updateCalendarDecorators(calendarDays)
                }
                Status.ERROR -> {
                    Log.e("ScanHistory", "Failed to load calendar: ${response.message}")
                }
                else -> {}
            }
        }
    }

    private fun updateCalendarDecorators(datesWithRecords: List<CalendarDay>) {
        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val currentMonth = today.get(Calendar.MONTH)
        val currentDay = today.get(Calendar.DAY_OF_MONTH)
        val todayDate = CalendarDay.from(currentYear, currentMonth + 1, currentDay)

        binding.calendarView.post {
            binding.calendarView.removeDecorators()
            val recordedDaysDecorator = RecordedDaysDecorator(this, datesWithRecords)
            binding.calendarView.addDecorator(recordedDaysDecorator)
            binding.calendarView.invalidate()
            binding.calendarView.selectedDate = todayDate
        }
    }

    private fun initUi() {
        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH) + 1
        val day = today.get(Calendar.DAY_OF_MONTH)

        val formattedDate = String.format("%04d-%02d-%02d", year, month, day)

        // Fetch scan history for today from API
        historyViewmodel.fetchScanHistoryByDate(formattedDate)

        // Fetch calendar data for current month from API
        historyViewmodel.fetchCalendar(month, year)
    }

    private fun listeners() {
        binding.tool.btnBack.setOnClickListener {
            finish()
        }
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            val day = date.day
            val month = date.month
            val year = date.year
            val formattedDate = String.format("%04d-%02d-%02d", year, month, day)
            Log.d("TAG", "listeners: formattedDate $formattedDate ")

            // Fetch scan history for selected date from API
            historyViewmodel.fetchScanHistoryByDate(formattedDate)
        }

        binding.calendarView.setOnMonthChangedListener { _, date ->
            // Fetch calendar data for the new month from API
            historyViewmodel.fetchCalendar(date.month, date.year)
        }
    }

    fun generateQrCode(scanId: Int): Bitmap {
        // Only encode the scan ID in QR code
        val qrData = """{"scanId":$scanId}"""

        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 600, 600)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    override fun onQRGenerateClick(scanId: Int?, data: MeasurementResult) {
        if (scanId != null && scanId != -1) {
            val qr = generateQrCode(scanId)
            val dialog = BottomSheetQRCode.newInstance(qr, name = data.name, date = data.date, time = data.time)
            if (dialog?.dialog?.isShowing != true || dialog?.isVisible != true) {
                dialog.show(supportFragmentManager, BottomSheetQRCode::class.java.name)
            }
        } else {
            Toast.makeText(this, "Cannot generate QR code without scan ID", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClicked(scanId: Int?, data: MeasurementResult) {
        // If we have a scanId from API, use the new method that fetches from API
        if (scanId != null && scanId != -1) {
            VitalResultActivity2.startActivityWithScanId(this, scanId)
        }
    }
}

class RecordedDaysDecorator(private val context: Context,private val dates: List<CalendarDay>) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(10f, ContextCompat.getColor(context,R.color.result_screen_wellness_score_color))) // Adds a red dot under the recorded days
        view.addSpan(StyleSpan(Typeface.BOLD))
    }
}