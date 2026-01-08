package com.vital_self.features.scan.presentation.result

import com.vital_self.legacy.adapters.AdapterVitalList
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.biosensesignal.sdk.api.session.demographics.Sex
import com.vital_self.R
import com.vital_self.core.base.BaseActivity
import com.vital_self.databinding.ActivityVitalResult2Binding
import com.vital_self.core.domain.model.Model
import com.vital_self.core.data.remote.model.Status
import com.vital_self.core.utils.helpers.AnimationsHandler
import com.vital_self.core.utils.helpers.DateFormatter
import com.vital_self.core.utils.helpers.PDFGenerator
import com.vital_self.core.data.local.preferences.PreferenceManager
import com.vital_self.core.utils.constants.AppConstants
import com.vital_self.features.history.data.model.toVitalsDataList
import com.vital_self.features.history.presentation.viewmodel.HistoryViewModel
import com.vital_self.features.scan.presentation.scan.VitalScanActivity
import java.io.File

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class VitalResultActivity2 : BaseActivity() {

    lateinit var binding: ActivityVitalResult2Binding

    companion object {
        const val ISTOPBACK = "topback"
        const val SCAN_ID = "scan_id"
        const val IS_FROM_HISTORY = "is_from_history"

        fun startActivity(activity: Activity, list: ArrayList<Model.VitalsData>,
                          subjectDetails: Model.SubjectDetails?,
                          wellnessIndex:String?, wellnessLevel: String?,
                          date: String,
                          time: String,
                          isTop: Boolean) {
            Intent(activity, VitalResultActivity2::class.java).apply {
                putExtra(AppConstants.SUBJECT,subjectDetails)
              putParcelableArrayListExtra(AppConstants.VITAL_LIST, list)
                putExtra(AppConstants.WELLNESS_INDEX,wellnessIndex)
                putExtra(AppConstants.WELLNESS_LEVEL,wellnessLevel)
                putExtra(AppConstants.DATE, date)
                putExtra(AppConstants.TIME, time)
                putExtra(ISTOPBACK, isTop)
                putExtra(IS_FROM_HISTORY, false)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }

        // New entry point for history - fetch data from API by scan ID
        fun startActivityWithScanId(activity: Activity, scanId: Int) {
            Intent(activity, VitalResultActivity2::class.java).apply {
                putExtra(SCAN_ID, scanId)
                putExtra(IS_FROM_HISTORY, true)
                putExtra(ISTOPBACK, false)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private var vitalList : ArrayList<Model.VitalsData>? = null
    private var vitals : ArrayList<Model.VitalsData>? = null
    private var subject : Model.SubjectDetails? = null
    private var wellnessIndex : String? = null
    private var wellnessLevel : String? = null
    private var bmiS : String = ""
    private var date: String = ""
    private var time: String = ""

    private val isTopBack by lazy { intent.getStringExtra(ISTOPBACK) }
    private val isFromHistory by lazy { intent.getBooleanExtra(IS_FROM_HISTORY, false) }
    private val scanId by lazy { intent.getIntExtra(SCAN_ID, -1) }

    private lateinit var historyViewModel: HistoryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vital_result2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        setUpToolbar(binding.tool)
        binding.tool.tvTitle.visibility = View.GONE
        binding.tool.tvDateTime.visibility = View.VISIBLE
        if (PreferenceManager.appVersionMatch) binding.tool.btnShare.visibility = View.GONE else binding.tool.btnShare.visibility = View.VISIBLE

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFromHistory) {
                    finish()
                } else {
                    VitalScanActivity.startActivity(this@VitalResultActivity2)
                    finish()
                }
            }
        })

        binding.tool.btnBack.setOnClickListener {
            if (isFromHistory) {
                finish()
            } else {
                VitalScanActivity.startActivity(this@VitalResultActivity2)
                finish()
            }
        }

        binding.tool.btnShare.setOnClickListener {
            sharePdfFileEvery(this)
        }

        if (isFromHistory && scanId != -1) {
            setupApiObserver()
            historyViewModel.fetchScanHistoryById(scanId)
        } else {
            initUi()
        }
    }


    fun sharePdfFileEvery(context: Context) {
        val fileName = "${subject?.name} VitalsReport.pdf"
        Log.d("TAG", "sharePdfFile: file name $fileName")
        val pdfFile = File(context.getExternalFilesDir("PDFs"), fileName)
        Log.d("TAG", "sharePdfFile: $pdfFile ")

        if (pdfFile.exists()) {
            val uri: Uri = FileProvider.getUriForFile(context, "com.vital_self.fileprovider", pdfFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_SUBJECT, "Your Health Vital Report")
                putExtra(
                    Intent.EXTRA_TEXT, """
                Hello,

                Please see your VitalSelf Health Report.

                Need more health and wellness information or support?

                Contact us: Info@powercell.life

                Report Date : ${DateFormatter.convertDateFormat(date)} |  ${DateFormatter.convertTimeFormat(time)}

                Thank you
            """.trimIndent())
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Show chooser for ALL apps (Gmail, WhatsApp, Teams, etc.)
            context.startActivity(Intent.createChooser(shareIntent, "Share report using"))

            val directory = File(context.getExternalFilesDir(null), "PDFs")
            Toast.makeText(context, "File is stored at $directory", Toast.LENGTH_SHORT).show()

        } else {
            Log.d("TAG", "sharePdfFile: file is not present")
            Toast.makeText(context, "Report file not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupApiObserver() {
        historyViewModel.scanHistoryById.observe(this) { response ->
            when (response.status) {
                Status.LOADING -> {
                    binding.recyclerView.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    response.data?.data?.let { data ->
                        initUiFromApi(data)
                    }
                }
                Status.ERROR -> {
                    binding.recyclerView.visibility = View.GONE
                    Toast.makeText(this, response.message ?: "Failed to load scan history", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initUiFromApi(data: com.vital_self.features.history.data.model.ScanHistoryByIdData) {
        try {
            // Convert API vitals to Model.VitalsData
            val apiVitals = data.vitals?.toVitalsDataList(this) ?: arrayListOf()

            vitalList = apiVitals
            if (!PreferenceManager.appVersionMatch) {
                vitals = vitalList?.filter {
                    it.vitalName.contains(AppConstants.HEART_RATE) || it.vitalName.contains(AppConstants.HRV_SDNN)
                } as ArrayList<Model.VitalsData>?
            } else {
                vitals = vitalList
            }

            // Set user details from API
            val user = data.user
            val scanHistory = data.scanHistory

            if (user != null) {
                val sex = when (user.gender?.lowercase()) {
                    "male" -> Sex.MALE
                    "female" -> Sex.FEMALE
                    else -> Sex.UNSPECIFIED
                }
                subject = Model.SubjectDetails(
                    name = user.name,
                    sex = sex,
                    age = user.age?.toDouble(),
                    weight = user.weight,
                    height = user.height,
                    heightUnit = user.heightUnit,
                    weightUnit = user.weightUnit
                )
            }

            // Set wellness info from scanHistory
            wellnessIndex = scanHistory?.wellnessIndex?.toString() ?: "0"
            wellnessLevel = scanHistory?.wellnessLevel ?: ""

            // Parse date and time from createdAt
            val createdAt = scanHistory?.createdAt ?: ""
            if (createdAt.isNotEmpty()) {
                try {
                    val parts = createdAt.split("T")
                    date = parts.getOrNull(0) ?: ""
                    time = parts.getOrNull(1)?.substringBefore(".") ?: ""
                } catch (e: Exception) {
                    date = ""
                    time = ""
                }
            }

            // Update UI
            binding.userDetail.tvWellnessResult.text = "$wellnessIndex/10"
            binding.userDetail.tvHeaderSub.text = "Your wellness is $wellnessLevel"
            binding.tool.tvDateTime.text = " ${DateFormatter.convertDateFormat(date)} |  ${DateFormatter.convertTimeFormat(time)} | 60 sec"

            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            val adapter = vitals?.let { AdapterVitalList(it, this@VitalResultActivity2) }
            binding.recyclerView.adapter = adapter
            binding.tool.btnShare.visibility = View.VISIBLE

            updateBMI()
            createPdf()

            if (subject != null && subject?.name != null) {
                binding.userDetail.layoutMain.visibility = View.VISIBLE
                binding.userDetail.txtFullName.text = subject?.name

                // Gender - show only if valid
                val sexText = when (subject?.sex) {
                    Sex.MALE -> "Male"
                    Sex.FEMALE -> "Female"
                    else -> null
                }
                if (!sexText.isNullOrEmpty()) {
                    binding.userDetail.layoutGender.visibility = View.VISIBLE
                    binding.userDetail.txtGender.text = sexText
                } else {
                    binding.userDetail.layoutGender.visibility = View.GONE
                }

                // Height - show only if valid
                val heightVal = subject?.height
                if (heightVal != null && heightVal > 0) {
                    binding.userDetail.layoutHeight.visibility = View.VISIBLE
                    binding.userDetail.txtHeight.text = heightVal.toString()
                } else {
                    binding.userDetail.layoutHeight.visibility = View.GONE
                }

                // Weight - show only if valid
                val weightVal = subject?.weight
                if (weightVal != null && weightVal > 0) {
                    binding.userDetail.layoutWeight.visibility = View.VISIBLE
                    binding.userDetail.txtWeight.text = weightVal.toString()
                } else {
                    binding.userDetail.layoutWeight.visibility = View.GONE
                }

                // BMI - show only if both height and weight are valid
                if (heightVal != null && heightVal > 0 && weightVal != null && weightVal > 0) {
                    binding.userDetail.layoutBmi.visibility = View.VISIBLE
                } else {
                    binding.userDetail.layoutBmi.visibility = View.GONE
                }
            } else {
                binding.userDetail.layoutMain.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.e("VitalResultActivity2", "Error initializing from API: ${e.message}")
            binding.recyclerView.visibility = View.GONE
        }
    }


    private fun initUi() {

        try {
            vitalList = intent.getParcelableArrayListExtra<Model.VitalsData>(AppConstants.VITAL_LIST) as ArrayList<Model.VitalsData>
            if (!PreferenceManager.appVersionMatch){
                vitals = vitalList?.filter { it.vitalName.contains(AppConstants.HEART_RATE) || it.vitalName.contains(
                    AppConstants.HRV_SDNN) } as ArrayList<Model.VitalsData>?
            }else{
                vitals = vitalList
            }
            subject = intent.getParcelableExtra(AppConstants.SUBJECT)
            wellnessIndex = intent.getStringExtra(AppConstants.WELLNESS_INDEX) ?: "0"
            wellnessLevel = intent.getStringExtra(AppConstants.WELLNESS_LEVEL)
            binding.userDetail.tvWellnessResult.text = "$wellnessIndex/10"
            binding.userDetail.tvHeaderSub.text = "Your wellness is $wellnessLevel"
            date = intent.getStringExtra(AppConstants.DATE) ?: ""
            time = intent.getStringExtra(AppConstants.TIME) ?: ""
            binding.tool.tvDateTime.text = " ${DateFormatter.convertDateFormat(date)} |  ${DateFormatter.convertTimeFormat(time)} | 60 sec"
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            val adapter = vitals?.let { AdapterVitalList(it,this@VitalResultActivity2) }
            binding.recyclerView.adapter = adapter
            binding.tool.btnShare.visibility = View.VISIBLE
            updateBMI()
            createPdf()

            if (subject != null){
                binding.userDetail.layoutMain.visibility = View.VISIBLE
                binding.userDetail.txtFullName.text = subject?.name

                // Gender - show only if valid
                val sexText = when(subject?.sex){
                    Sex.MALE -> "Male"
                    Sex.FEMALE -> "Female"
                    else -> null
                }
                if (!sexText.isNullOrEmpty()) {
                    binding.userDetail.layoutGender.visibility = View.VISIBLE
                    binding.userDetail.txtGender.text = sexText
                } else {
                    binding.userDetail.layoutGender.visibility = View.GONE
                }

                // Height - show only if valid
                val heightVal = subject?.height
                if (heightVal != null && heightVal > 0) {
                    binding.userDetail.layoutHeight.visibility = View.VISIBLE
                    binding.userDetail.txtHeight.text = heightVal.toString()
                } else {
                    binding.userDetail.layoutHeight.visibility = View.GONE
                }

                // Weight - show only if valid
                val weightVal = subject?.weight
                if (weightVal != null && weightVal > 0) {
                    binding.userDetail.layoutWeight.visibility = View.VISIBLE
                    binding.userDetail.txtWeight.text = weightVal.toString()
                } else {
                    binding.userDetail.layoutWeight.visibility = View.GONE
                }

                // BMI - show only if both height and weight are valid
                if (heightVal != null && heightVal > 0 && weightVal != null && weightVal > 0) {
                    binding.userDetail.layoutBmi.visibility = View.VISIBLE
                } else {
                    binding.userDetail.layoutBmi.visibility = View.GONE
                }
            }else{
                binding.userDetail.layoutMain.visibility = View.GONE
            }
        } catch (e: Exception) {
           binding.recyclerView.visibility = View.GONE
        }

    }

    private fun updateBMI(){
        if (subject?.height != null && subject?.weight != null){
            binding.userDetail.age.visibility = View.VISIBLE
            binding.userDetail.txtAge.visibility = View.VISIBLE
            val height = subject?.height?.div(100)
            val bmi = subject?.weight?.div((height!!.times(height)))
            val bmi_s =  "%.2f".format(bmi)
            bmiS = "$bmi_s  ${getBmiCategory(bmi!!)}"
            binding.userDetail.txtAge.text = bmiS

        }else{
            binding.userDetail.age.visibility = View.GONE
            binding.userDetail.txtAge.visibility = View.GONE
        }
    }


    private fun createPdf(){
        Log.d("TAG", "createPdf: size ${vitals?.size}")
        vitals?.let {
            PDFGenerator.createVitalPdf(context = this,
                healthVitals = it,
                additionalText = getString(R.string.disclaimer_desc),
                bmi = bmiS,
                height = subject?.height.toString(),
                weight = subject?.weight.toString(),
                age = subject?.age.toString(),
                name = subject?.name.toString(),
                wellnessLevel = wellnessLevel.toString(),
                wellnessIndex = wellnessIndex.toString(),
                headertext = getString(R.string.header_text),
                    date = date,
                    time = time
            )
        }
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getBmiCategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight."
            bmi in 18.5..24.9 -> "Normal Weight"
            bmi in 25.0..29.9 -> "Overweight."
            bmi >= 30.0 -> "Obesity"
            else -> "Invalid BMI value."
        }
    }

}