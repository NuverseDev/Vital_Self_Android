package com.vital_self.view.history

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
import com.vital_self.adapter.HistoryAdapter
import com.vital_self.adapter.HistoryListener
import com.vital_self.base.BaseActivity
import com.vital_self.bottom_sheets.BottomSheetQRCode
import com.vital_self.databinding.ScanHistoryActivityBinding
import com.vital_self.model.Model
import com.vital_self.repository.ScanRepository
import com.vital_self.repository.factory.ScanViewModelFactory
import com.vital_self.utils.AnimationsHandler
import com.vital_self.utils.ScanResultGenrator
import com.vital_self.viewmodel.LocalHistoryBDViewmodel
import com.vital_self.viewmodel.ScanViewModel
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.vital_self.view.scan.MeasurementResult
import com.vital_self.view.scan.VitalResultActivity2
import java.util.Calendar
import kotlin.getValue


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ScanHistory : BaseActivity(), HistoryListener {

    private lateinit var binding: ScanHistoryActivityBinding
    private lateinit var historyViewmodel: LocalHistoryBDViewmodel
    private lateinit var historyAdapter: HistoryAdapter

    companion object {
        const val TITLE = ""
        fun startActivity(activity: Activity,title:String) {
            Intent(activity, ScanHistory::class.java).apply {
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
        historyViewmodel = ViewModelProvider(this).get(LocalHistoryBDViewmodel::class.java)
        setUpToolbar(binding.tool)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        setupAdapter()
        initUi()
        listeners()
        markRecordedDays()
    }

    private fun setupAdapter() {
        historyAdapter = HistoryAdapter(this@ScanHistory)

        binding.adapter = historyAdapter
    }

    private fun initUi() {

        val today = Calendar.getInstance()
        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH) + 1
        val day = today.get(Calendar.DAY_OF_MONTH)

        val formattedDate = String.format("%04d-%02d-%02d", year, month, day)

        historyViewmodel.getScanResultByDate(this, formattedDate) { scanList ->
            historyAdapter.setItems(scanList)
        }

        historyViewmodel.getAllScanResults(this) { scanHistories ->
            for (scanHistory in scanHistories) {
                Log.d("ScanHistory", "Date: ${scanHistory}")
            }
        }

        markRecordedDays()
    }

    private fun listeners() {
        binding.tool.btnBack.setOnClickListener {
            finish()
        }
        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            val day = date.day
            val month = date.month
            val year = date.year
            val formattedDate = String.format("%02d-%02d-%02d", year, month, day)
            Log.d("TAG", "listeners: formattedDate $formattedDate ")

            historyViewmodel.getScanResultByDate(this, formattedDate) { scanList ->
                historyAdapter.setItems(scanList)
            }
        }

        binding.calendarView.setOnMonthChangedListener { _, _ ->
            markRecordedDays()
        }
    }

    private fun markRecordedDays() {
        val today = Calendar.getInstance()
        val currentYear = today.get(Calendar.YEAR)
        val currentMonth = today.get(Calendar.MONTH)
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        val todayDate = CalendarDay.from(currentYear, currentMonth+1, currentDay)

        historyViewmodel.getAllScanResults(this) { scanHistories ->
            val datesWithRecords = scanHistories.mapNotNull { scan ->
                val parts = scan.date.split("-") // Format: YYYY-MM-DD
                if (parts.size == 3) {
                    val y = parts[0].toInt()
                    val m = parts[1].toInt()
                    val d = parts[2].toInt()
                    CalendarDay.from(y, m, d)
                } else null
            }

            binding.calendarView.post {
                binding.calendarView.removeDecorators()
                val recordedDaysDecorator = RecordedDaysDecorator(this, datesWithRecords)
                binding.calendarView.addDecorator(recordedDaysDecorator)
                binding.calendarView.invalidate()

                binding.calendarView.selectedDate = todayDate
            }
        }
    }

    fun generateQrCode(measurementResult: MeasurementResult): Bitmap {
        val gson = Gson()
        val jsonString = gson.toJson(measurementResult)

        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(jsonString, BarcodeFormat.QR_CODE, 600, 600)

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

    override fun onQRGenerateClick(data: MeasurementResult) {
        val qr =  generateQrCode(data)
        val dialog =  BottomSheetQRCode.newInstance(qr, name = data.name,date = data.date, time = data.time)
        if (dialog?.dialog?.isShowing != true || dialog?.isVisible != true) {
            dialog.show(supportFragmentManager, BottomSheetQRCode::class.java.name)
        }
    }

    override fun onItemClicked(data: MeasurementResult) {
        val sex = when (data.gender) {
            "Male" -> Sex.MALE
            "Female" -> Sex.FEMALE
            else -> Sex.UNSPECIFIED
        }
        var subjectData : Model.SubjectDetails? = null
        Log.d("TAG", "onItemClicked: data $data")
        if (data.name == "null" || data.height == "null"){
            subjectData = null
        }else{
            subjectData = Model.SubjectDetails(
                sex = sex,
                age = data?.age?.toDouble() ?: 0.0,
                weight = data?.weight?.toDouble() ?: 0.0,
                height = data?.height?.toDouble() ?: 0.0,
                isSmoker = SmokingStatus.UNSPECIFIED,
                name = data?.name ?: ""
            )
        }

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
//        finish()
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