package com.vital_self.view

import AdapterVitalList
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vital_self.R

import com.vital_self.base.BaseActivity

import com.vital_self.model.Model
import com.vital_self.utils.AnimationsHandler
import com.vital_self.databinding.ActivityVitalResultBinding
import com.vital_self.model.VitalListItem
import com.vital_self.utils.DateFormatter
import com.vital_self.utils.PDFGenerator
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.vital_self.view.VitalResultActivity2.Companion.ISTOPBACK
import java.io.File
import java.util.Locale
import kotlin.collections.ArrayList
import kotlin.collections.get

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class VitalResultActivity : BaseActivity() {

    lateinit var binding: ActivityVitalResultBinding
    private var vitals: ArrayList<Model.VitalsData>? = null
    private var subject: Model.SubjectDetails? = null
    private var wellnessIndex: String? = null
    private var wellnessLevel: String? = null
    private var bmiS: String = ""
    private var date: String = ""
    private var time: String = ""
    private var isTitleVisible = false
    private var adapter: AdapterVitalList? = null
    private var prevIndex = 0

    companion object {
        fun startActivity(
            activity: Activity,
            list: ArrayList<Model.VitalsData>,
            subjectDetails: Model.SubjectDetails?,
            wellnessIndex: String?,
            wellnessLevel: String?,
            date: String,
            time: String,
            isTop: Boolean = false
        ) {
            Intent(activity, VitalResultActivity::class.java).apply {
                putExtra(Constant.SUBJECT, subjectDetails)
                putExtra(Constant.WELLNESS_INDEX, wellnessIndex)
                putExtra(Constant.WELLNESS_LEVEL, wellnessLevel)
                putExtra(Constant.DATE, date)
                putExtra(Constant.TIME, time)
                putParcelableArrayListExtra(Constant.VITAL_LIST, list)
                putExtra(ISTOPBACK, isTop)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vital_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        // Setup fade effect for collapsing toolbar
        setupCollapsingToolbar()

        // Initialize UI components
        initUi()

        // Setup TabLayout
        setupTabLayout()

        // Set up listeners
        setupListeners()

        // Handle back press
        setupBackPressHandler()
    }

    private fun setupCollapsingToolbar() {
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange

            if (Math.abs(verticalOffset) >= totalScrollRange - 10) {
                // Fully Collapsed -> Fade In
                if (!isTitleVisible) {
                    binding.pageTitle.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                    isTitleVisible = true
                }
            } else {
                // Not collapsed -> Fade Out
                if (isTitleVisible) {
                    binding.pageTitle.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .start()
                    isTitleVisible = false
                }
            }
        })
    }

    private fun setupTabLayout() {
        binding.tabLayout.removeAllTabs()

        val tabs = listOf(
            VitalCategories.SECTION_RISKS,
            VitalCategories.SECTION_VITAL_SIGNS,
            VitalCategories.SECTION_BLOOD,
            VitalCategories.SECTION_BLOOD_TESTS,
            VitalCategories.SECTION_STRESS,
            VitalCategories.SECTION_HRV,
            VitalCategories.SECTION_ENERGY,
            VitalCategories.SECTION_ADVANCE_HRV
        )

        tabs.forEach { title ->
            val tab = binding.tabLayout.newTab()
            tab.customView = createCustomTab(title)
            binding.tabLayout.addTab(tab)
        }

        binding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val currentSection = tab.position
                val current = getIndexOfVitalBasedOnSection(currentSection)
                prevIndex = current
                scrollToPosition(current)

                val textView = tab.customView?.findViewById<TextView>(R.id.tab_text)
                textView?.setTextColor(ContextCompat.getColor(this@VitalResultActivity, R.color.white))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val textView = tab.customView?.findViewById<TextView>(R.id.tab_text)
                textView?.setTextColor(ContextCompat.getColor(this@VitalResultActivity, R.color.secondary_theme_text_color))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // Add margin between tabs
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            val tabView = tab?.view
            val params = tabView?.layoutParams as? ViewGroup.MarginLayoutParams
            params?.setMargins(12, 0, 12, 0) // Adjust left-right margins
            tabView?.layoutParams = params
        }

        // Select first tab manually
        binding.tabLayout.getTabAt(0)?.select()

        // ALSO manually update first tab text color
        val firstTab = binding.tabLayout.getTabAt(0)
        val firstTextView = firstTab?.customView?.findViewById<TextView>(R.id.tab_text)
        firstTextView?.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun createCustomTab(title: String): View {
        val textView = TextView(this)
        textView.id = R.id.tab_text
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.text = title.lowercase(Locale.getDefault())
        textView.setTextColor(ContextCompat.getColor(this, R.color.secondary_theme_text_color))
        textView.textSize = 14f // Adjust font size
        textView.typeface = ResourcesCompat.getFont(this, R.font.inter_reguler) // Use your Inter-Regular font
        textView.setPadding(20, 8, 20, 8) // Internal padding
        textView.isAllCaps = false
        return textView
    }

    private fun getIndexOfVitalBasedOnSection(position:Int) : Int{
       when (position){
           0 -> return 0
           1 -> return 6
           2 -> return 12
           3 -> return 15
           4 -> return 18
           5 -> return 20
           6 -> return 22
           7 -> return 28
           else -> return 0
       }
    }

    private fun scrollToPosition(position: Int){
        binding.recyclerView.postDelayed({
            val layoutManager = binding.recyclerView.layoutManager as? LinearLayoutManager
            layoutManager?.let {
                it.scrollToPositionWithOffset(position,0) // Scroll to the position with offset
                it.smoothScrollToPosition(binding.recyclerView, RecyclerView.State(), position) // Smooth scroll
            }
        }, 100)
    }


    private fun setupListeners() {
        binding.tool.share.setOnClickListener {
            // share file through all
            sharePdfFileEvery(this)
        }

        binding.tool.btnBack.setOnClickListener {
            ScanHistory.startActivity(this,Constant.HISTORY)
            finish()
        }

    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ScanHistory.startActivity(this@VitalResultActivity,Constant.HISTORY)
            }
        })
    }




    private fun initUi() {
        try {
            // Get vitals data from intent
            vitals = intent.getParcelableArrayListExtra<Model.VitalsData>(Constant.VITAL_LIST) as ArrayList<Model.VitalsData>
            subject = intent.getParcelableExtra(Constant.SUBJECT)
           vitals?.let {
               Log.d("TAG", "initUi: vital size ${it.size}")
           }
            subject?.let {
                Log.d("TAG", "initUi: subject ${it}")
            }
            // Set up RecyclerView with fixed size for better performance
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.isNestedScrollingEnabled = true

            // Use linear layout manager
            val layoutManager = LinearLayoutManager(this)
            binding.recyclerView.layoutManager = layoutManager

            // Organize vitals by category and create the adapter
            val organizedItems = VitalCategories.organizeVitalsByCategory(vitals ?: ArrayList())
            val list =ArrayList<Model.VitalsData>()
            adapter = AdapterVitalList(vitals!!, this@VitalResultActivity)
            binding.recyclerView.adapter = adapter
            wellnessIndex = intent.getStringExtra(Constant.WELLNESS_INDEX) ?: "0"
            wellnessLevel = intent.getStringExtra(Constant.WELLNESS_LEVEL)
            date = intent.getStringExtra(Constant.DATE) ?: ""
            time = intent.getStringExtra(Constant.TIME) ?: ""
            binding.recyclerView.visibility = View.VISIBLE
            Log.d("TAG", "initUi: date ${DateFormatter.convertDateFormat(date)} time  ${DateFormatter.convertDateFormat(date)} ")
            // Set wellness score
            binding.tool.tvDateTime.text = " ${DateFormatter.convertDateFormat(date)} |  ${DateFormatter.convertTimeFormat(time)} | 60 sec"
            binding.pageTitle.text = "$wellnessIndex/10"
            binding.tvScore.text = "$wellnessIndex/10"
            binding.tvScoreLevel.text = "$wellnessLevel"
            createPdf()

        } catch (e: Exception) {
            Log.e("VitalResultActivity", "Error initializing UI: ${e.message}")
            binding.recyclerView.visibility = View.GONE
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
                putExtra(Intent.EXTRA_TEXT, """
                Hello,

                Please see your Vitals Health Report.
                
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



    fun sharePdfFile(context: Context) {

        val fileName = "${subject?.name} VitalsReport.pdf"
        Log.d("TAG", "sharePdfFile: file name $fileName")
        val pdfFile = File(context.getExternalFilesDir("PDFs"), fileName)
        Log.d("TAG", "sharePdfFile: $pdfFile ")

        if (pdfFile.exists()) {
            // Proceed to share the file
            val uri: Uri = FileProvider.getUriForFile(context, "com.briahai.fileprovider", pdfFile)
            val emailBody = """
        Dear ${subject?.name},

        Please find attached your VitalSelf Health Report.

        Need more health and wellness information or support? Contact us: info@powercell.life

        Thank you
    """.trimIndent()
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(""))  // Receiver's email
                putExtra(Intent.EXTRA_BCC, arrayOf("zarkainvest@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Your Health Vital Report")  // Subject of the email
                putExtra(Intent.EXTRA_TEXT, emailBody)  // Body text
                putExtra(Intent.EXTRA_STREAM, uri)  // Attach the PDF file
            }
            val gmailPackage = "com.google.android.gm"
            emailIntent.setPackage(gmailPackage)

            // Check if Gmail is installed
            if (isAppInstalled(context, gmailPackage)) {
                context.startActivity(Intent.createChooser(emailIntent, "Send email using Gmail"))
                val directory = File(context.getExternalFilesDir(null), "PDFs")
                Toast.makeText(this,"File is stored at ${directory}", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("TAG", "Gmail not installed")
                // Optionally, you can fall back to other email clients if Gmail is not installed
                context.startActivity(Intent.createChooser(emailIntent, "Send email"))
            }
        } else {
            Log.d("TAG", "sharePdfFile: file is not present")
            // Handle the error if the file does not exist
            Toast.makeText(this, "Report file not found", Toast.LENGTH_SHORT).show()
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

    private fun createPdf() {
        Log.d("TAG", "createPdf: size ${vitals?.size}")

        vitals?.let {
            PDFGenerator.createVitalPdf(
                context = this,
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
}

object VitalCategories {
    // Define section constants based on your existing com.vitalself.utils.VITAL_CATEGORY enum
    const val SECTION_RISKS = "Risks"
    const val SECTION_VITAL_SIGNS = "My Vital Signs"
    const val SECTION_BLOOD = "Blood"
    const val SECTION_BLOOD_TESTS = "Blood Tests"
    const val SECTION_STRESS = "Stress"
    const val SECTION_HRV = "Heart Rate Variability"
    const val SECTION_ENERGY = "Energy"
    const val SECTION_ADVANCE_HRV = "Advanced HRV"
    const val SECTION_UNCATEGORIZED = "Other Metrics"

    // Map to store which category code belongs to which display name
    private val categoryDisplayNames = mapOf(
        "RISK" to SECTION_RISKS,
        "VITAL_SIGN" to SECTION_VITAL_SIGNS,
        "BLOOD" to SECTION_BLOOD,
        "BLOOD_TEST" to SECTION_BLOOD_TESTS,
        "STRESS" to SECTION_STRESS,
        "HRV" to SECTION_HRV,
        "ENERGY" to SECTION_ENERGY,
        "ADVANCE_HRV" to SECTION_ADVANCE_HRV
    )

    /**
     * Maps a list of vitals data to their appropriate categories and creates a
     * list of VitalListItem objects with headers
     */
    fun organizeVitalsByCategory(vitalsList: ArrayList<Model.VitalsData>): List<VitalListItem> {
        // Group vitals by category
        val categorizedVitals = vitalsList.groupBy { vital ->
            categoryDisplayNames[vital.category] ?: SECTION_UNCATEGORIZED
        }

        // Create the final list with headers and items
        val result = mutableListOf<VitalListItem>()

        // Define the order of categories
        val orderedCategories = listOf(
            SECTION_RISKS,
            SECTION_VITAL_SIGNS,
            SECTION_BLOOD,
            SECTION_BLOOD_TESTS,
            SECTION_STRESS,
            SECTION_HRV,
            SECTION_ENERGY,
            SECTION_ADVANCE_HRV,
            SECTION_UNCATEGORIZED
        )

        // Add sections in order
        for (category in orderedCategories) {
            // Skip categories that don't have any vitals
            val vitalsInCategory = categorizedVitals[category] ?: continue

            if (vitalsInCategory.isNotEmpty()) {
                // Add header
                result.add(VitalListItem.HeaderItem(category))

                // Add all vitals in this category
                vitalsInCategory.forEach { vital ->
                    result.add(VitalListItem.VitalItem(vital))
                }
            }
        }

        return result
    }

    /**
     * Get the position of each tab in the TabLayout
     */

}

// sdk updated
// smoker in profile
// if smoker undefined check profile before scan
//