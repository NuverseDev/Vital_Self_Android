package com.vitalself.view

import AdapterVitalList
import android.Manifest
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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.biosensesignal.sdk.api.session.demographics.Sex
import com.google.gson.Gson
import com.google.zxing.common.StringUtils
import com.vitalself.R
import com.vitalself.base.BaseActivity
import com.vitalself.databinding.ActivityVitalResult2Binding
import com.vitalself.model.Model
import com.vitalself.utils.AnimationsHandler
import com.vitalself.utils.DateFormatter
import com.vitalself.utils.PDFGenerator
import com.vitalself.utils.Pref
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class VitalResultActivity2 : BaseActivity() {

    lateinit var binding: ActivityVitalResult2Binding

    companion object {
        const val ISTOPBACK = "topback"
        fun startActivity(activity: Activity, list: ArrayList<Model.VitalsData>,
            subjectDetails: Model.SubjectDetails?,
            wellnessIndex:String?, wellnessLevel: String?,
            date: String,
            time: String,
            isTop: Boolean) {
            Intent(activity, VitalResultActivity2::class.java).apply {
                putExtra(Constant.SUBJECT,subjectDetails)
              putParcelableArrayListExtra(Constant.VITAL_LIST, list)
                putExtra(Constant.WELLNESS_INDEX,wellnessIndex)
                putExtra(Constant.WELLNESS_LEVEL,wellnessLevel)
                putExtra(Constant.DATE, date)
                putExtra(Constant.TIME, time)
                putExtra(ISTOPBACK, isTop)
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
        initUi()
        setUpToolbar(binding.tool)
        binding.tool.tvTitle.visibility = View.GONE
        binding.tool.tvDateTime.visibility = View.VISIBLE
        if (Pref.appVersionMatch) binding.tool.btnShare.visibility = View.GONE else binding.tool.btnShare.visibility = View.VISIBLE

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                VitalScanActivity.startActivity(this@VitalResultActivity2)
                finish()
            }
        })


        binding.tool.btnBack.setOnClickListener {
            VitalScanActivity.startActivity(this@VitalResultActivity2)
            finish()
        }


        binding.tool.btnShare.setOnClickListener {
            sharePdfFileEvery(this)
        }

    }


    fun sharePdfFileEvery(context: Context) {
        val fileName = "${subject?.name} VitalsReport.pdf"
        Log.d("TAG", "sharePdfFile: file name $fileName")
        val pdfFile = File(context.getExternalFilesDir("PDFs"), fileName)
        Log.d("TAG", "sharePdfFile: $pdfFile ")

        if (pdfFile.exists()) {
            val uri: Uri = FileProvider.getUriForFile(context, "com.vitalself.fileprovider", pdfFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_SUBJECT, "Your Health Vital Report")
                putExtra(Intent.EXTRA_TEXT, """
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


    private fun initUi() {

        try {
            vitalList = intent.getParcelableArrayListExtra<Model.VitalsData>(Constant.VITAL_LIST) as ArrayList<Model.VitalsData>
            if (Pref.appVersionMatch){
                vitals = vitalList?.filter { it.vitalName.contains(Constant.HEART_RATE) || it.vitalName.contains(
                    Constant.HRV_SDNN) } as ArrayList<Model.VitalsData>?
            }else{
                vitals = vitalList
            }
            subject = intent.getParcelableExtra(Constant.SUBJECT)
            wellnessIndex = intent.getStringExtra(Constant.WELLNESS_INDEX) ?: "0"
            wellnessLevel = intent.getStringExtra(Constant.WELLNESS_LEVEL)
            binding.userDetail.tvWellnessResult.text = "$wellnessIndex/10"
            binding.userDetail.tvHeaderSub.text = "Your wellness is $wellnessLevel"
            date = intent.getStringExtra(Constant.DATE) ?: ""
            time = intent.getStringExtra(Constant.TIME) ?: ""
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
                val sex = when(subject?.sex){
                    Sex.MALE -> "Male"
                    Sex.FEMALE -> "Female"
                    else -> "Uknown"
                }
                //modified
                binding.userDetail.txtGender.text = sex
                binding.userDetail.txtAge.text = subject?.age.toString()
                binding.userDetail.txtHeight.text = subject?.height.toString()
                binding.userDetail.txtWeight.text = subject?.weight.toString()

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
