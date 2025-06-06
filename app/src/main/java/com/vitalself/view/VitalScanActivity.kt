package com.vitalself.view

import com.biosensesignal.sdk.api.images.ImageListener
import com.biosensesignal.sdk.api.session.Session
import com.biosensesignal.sdk.api.session.SessionInfoListener
import com.biosensesignal.sdk.api.vital_signs.VitalSignsListener
import android.Manifest
import android.R.string
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.biosensesignal.sdk.api.HealthMonitorException
import com.biosensesignal.sdk.api.SessionEnabledVitalSigns
import com.biosensesignal.sdk.api.alerts.AlertCodes
import com.biosensesignal.sdk.api.alerts.ErrorData
import com.biosensesignal.sdk.api.alerts.WarningData
import com.biosensesignal.sdk.api.images.ImageData
import com.biosensesignal.sdk.api.images.ImageValidity
import com.biosensesignal.sdk.api.license.LicenseDetails
import com.biosensesignal.sdk.api.license.LicenseInfo
import com.biosensesignal.sdk.api.session.SessionState
import com.biosensesignal.sdk.api.session.demographics.Sex
import com.biosensesignal.sdk.api.session.user_info.UserInformation
import com.biosensesignal.sdk.api.vital_signs.VitalSign
import com.biosensesignal.sdk.api.vital_signs.VitalSignTypes
import com.biosensesignal.sdk.api.vital_signs.VitalSignsResults
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignASCVDRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignBloodPressure
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignHeartAge
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignHighFastingGlucoseRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignHighTotalCholesterolRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignLFHF
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignLowHemoglobinRisk
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignMeanRRI
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignPNSIndex
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignPRQ
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignPulseRate
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignRMSSD
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignRespirationRate
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignSD1
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignSD2
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignSDNN
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignSNSIndex
import com.biosensesignal.sdk.session.FaceSessionBuilder
import com.vitalself.R
import com.vitalself.base.BaseActivity
import com.vitalself.databinding.ActivityVitalScanBinding
import com.vitalself.dialog.DialogHowToScan
import com.vitalself.model.Model
import com.vitalself.model.UserRequest
import com.vitalself.network.NetworkErrorCode
import com.vitalself.network.NetworkUtils
import com.vitalself.network.Status
import com.vitalself.repository.ScanRepository
import com.vitalself.repository.factory.ScanViewModelFactory
import com.vitalself.utils.AlertDialogManager
import com.vitalself.utils.AnimationsHandler
import com.vitalself.utils.BinahErrorMessage
import com.vitalself.utils.DialogClickListener
import com.vitalself.utils.Pref
import com.vitalself.utils.ScanResultGenrator
import com.vitalself.viewmodel.LoginViewModel
import com.vitalself.viewmodel.ScanViewModel
import com.google.android.material.navigation.NavigationView
import com.vitalself.BuildConfig
import com.vitalself.model.CheckVersionRequest
import com.vitalself.utils.DateFormatter
import com.vitalself.view.ActivityUserLogin
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.String
import kotlin.getValue

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class VitalScanActivity : BaseActivity(),
    ImageListener, VitalSignsListener, SessionInfoListener {

    lateinit var binding: ActivityVitalScanBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val scanDuration = 60L
    private var mWarningDialogTimeoutHandler: Handler? = null
    private var session: Session? = null

    private var userImage: Bitmap? = null

    lateinit var loginViewModel: LoginViewModel

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navCustomLayout: LinearLayout

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, VitalScanActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private var subject : Model.SubjectDetails? = null

    private val faceDetectionNormal: Bitmap? by lazy {
        ContextCompat.getDrawable(this, R.drawable.ic_correct_frame)?.toBitmap()
    }

    private val faceDetectionError: Bitmap? by lazy {
        ContextCompat.getDrawable(this, R.drawable.ic_error_frame)?.toBitmap()
    }

    private val TAG = VitalScanActivity::class.java.name

    private var isDetected = false

    private var isTerminated = false

    private var mTime = 0
    private var mPercentage = 0
    private var mTimeCountHandler: Handler? = null
    private var progressPercent: Double = 0.0

    private val viewModel: ScanViewModel by viewModels {
        ScanViewModelFactory(ScanRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vital_scan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        subject = Pref.subjectDetails
        Log.d(TAG, "onCreate: scan activity $subject")
        if (subject == null && Pref.isFirstTime == true && !Pref.appVersionMatch) {
            AlertDialogManager.showConfirmationDialog(this,
                title = "Update Profile",
                message = "Please update profile for full report",
                buttonMessage = "Ok",
                cancelable = true,
                dialogClickListener = object : DialogClickListener {
                    override fun onButton1Clicked() {
                        ActivityUserDetail.startActivity(this@VitalScanActivity,Constant.PROFILE)
                        Pref.isFirstTime = false
                    }
                })
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        initUI()
        setListener()
        observable()
        setUpToolbar(binding.tool)
        setSupportActionBar(binding.tool.toolbar)
        binding.tool.llToolbarImg.visibility = View.VISIBLE
        binding.tool.llToolbarTitle.visibility = View.GONE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // hamburger icon
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        navCustomLayout = binding.navCustomLayout

        val menuProfile = navigationView.findViewById<TextView>(R.id.menu_profile)
        val menuHistory = navigationView.findViewById<TextView>(R.id.menu_history)
        val menuHelp = navigationView.findViewById<TextView>(R.id.menu_help)
        val menuScanQR = navigationView.findViewById<TextView>(R.id.menu_scan_qr)
        val menuLogout = navigationView.findViewById<TextView>(R.id.menu_logout)
        val closeDrawer = navigationView.findViewById<ImageView>(R.id.close_icon)

        val menuProfiledv = navigationView.findViewById<View>(R.id.dv_profile)
        val menuHistorydv = navigationView.findViewById<View>(R.id.dv_history)
        val menuScandv = navigationView.findViewById<View>(R.id.dv_best_practices)

        if (Pref.appVersionMatch){
            menuHistory.visibility = View.GONE
            menuScanQR.visibility = View.GONE
            menuProfile.visibility = View.GONE
            menuProfiledv.visibility = View.GONE
            menuHistorydv.visibility = View.GONE
            menuScandv.visibility = View.GONE
        }

        menuProfile.setOnClickListener {
            ActivityUserDetail.startActivity(this,Constant.PROFILE)
            drawerLayout.closeDrawers()
        }
        closeDrawer.setOnClickListener {
            drawerLayout.closeDrawers()
        }
        menuHistory.setOnClickListener {
            ScanHistory.startActivity(this,Constant.HISTORY)
            drawerLayout.closeDrawers()
        }

        menuHelp.setOnClickListener {
            ActivityHowToScan.startActivity(this,Constant.BEST_PRACTICES)
            drawerLayout.closeDrawers()
        }

        menuScanQR.setOnClickListener {
            val intent = Intent(this, QrScanActivity::class.java)
            startActivity(intent)
            drawerLayout.closeDrawers()
        }

        menuLogout.setOnClickListener {
            AlertDialogManager.showConfirmationDialog(this,
                title = "Logout",
                message = "Are you sure you want to logout?",
                buttonMessage = "Logout",
                cancelable = true,
                dialogClickListener = object : DialogClickListener {
                    override fun onButton1Clicked() {
                        Pref.subjectDetails = null
                        Pref.isLoggedIn = false
                        Pref.user = null
                        drawerLayout.closeDrawers()
                        ActivityUserLogin.startActivity(this@VitalScanActivity)
                        finish()
                    }

                })

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observable() {
        lifecycleScope.launch {
            viewModel.checkAppVersion.observe(this@VitalScanActivity, Observer {
                when (it?.status) {
                    Status.LOADING -> {
//                            showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        try {
                            if (it.data?.version_match == true){
                                Log.d(TAG, "observable: under review")
                                Pref.appVersionMatch = true
                            }else{
                                Log.d(TAG, "observable: normal user")
                                Pref.appVersionMatch = false
                            }
                        }catch (e: Exception){

                        }
                    }

                    Status.ERROR -> {

                    }

                    else -> {
                    }
                }
            })
        }

        lifecycleScope.launch {
            viewModel.updateData.observe(this@VitalScanActivity, Observer {
                Log.d(TAG, "observable: observer called")
                when (it?.status) {
                    Status.LOADING -> {
//                            showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        Log.d(TAG, "observable: user data ${Pref.user?.availableScan}")
                        showHideProgress(false)
                        try {
                            if (it.data?.user?.availableScan!! > 0 && it.data.user.status == true){
                                Pref.user = it.data.user
                                Log.d(TAG, "observable: user data ${Pref.user?.availableScan}")
                            }else{
                                AlertDialogManager.showConfirmationDialog(this@VitalScanActivity,
                                    title = getString(R.string.license_expired),
                                    message = getString(R.string.license_expired_message),
                                    buttonMessage = getString(R.string.ok),
                                    cancelable = true,
                                    isDissable = true,
                                    dialogClickListener = object : DialogClickListener {
                                        override fun onButton1Clicked() {
                                            Pref.subjectDetails = null
                                            Pref.isLoggedIn = false
                                            Pref.user = null
                                            drawerLayout.closeDrawers()
                                            ActivityUserLogin.startActivity(this@VitalScanActivity)
                                            finish()
                                        }
                                    })
                            }
                        }catch (e: Exception){
                            Log.d("TAG", "getUserById:catch ")
                            Toast.makeText(this@VitalScanActivity,e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.ERROR -> {
                        Log.d("TAG", "getUserById:error ")
                        showHideProgress(false)
                        AlertDialogManager.showConfirmationDialog(this@VitalScanActivity,
                            title = getString(R.string.error),
                            message = NetworkErrorCode.getNetworkError(it.code,null),
                            buttonMessage = getString(string.ok),
                            cancelable = true,
                            dialogClickListener = object : DialogClickListener {
                                override fun onButton1Clicked() {

                                }
                            })
                    }

                    else -> {
                    }
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (session != null) {
            session?.terminate()
        }
    }

    private fun initUI() {
        binding.tool.btnBack.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        subject = Pref.subjectDetails
        val checkAppVersion = CheckVersionRequest(
            platform = Constant.PLATFORM,
            app_version = BuildConfig.APP_VERSION,
            app_name =  Constant.APP_NAME
        )
        viewModel.checkAppVersion(checkAppVersion,this)
        viewModel.getUserById(Pref.user?.userId!!,this)
    }

    override fun onStart() {
        super.onStart()
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission granted, proceed with camera functionality
                if (session == null) {
                    createSession()
                }
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // Show a rationale to the user (why the app needs the permission)
                showPermissionRationale()
            }

            else -> {
                // Ask for permission
                requestCameraPermission()
            }
        }
    }

    private fun createSession() {

        try {
            Log.d(TAG, "createSession: user ${Pref.user}")
            val key = if (Pref.user?.userKey.isNullOrBlank()) Pref.user?.licenseKey else Pref.user?.userKey
            val licenseDetails = LicenseDetails(key)
            Log.d(TAG, "createSession: key $key")
            if (subject != null){
                Log.d(TAG, "createSession: subject detail $subject")
                val sex = when(subject?.sex){
                    Sex.MALE -> Sex.MALE
                    Sex.FEMALE -> Sex.FEMALE
                    else  -> Sex.UNSPECIFIED
                }
                val userInformation = UserInformation.Builder().setSex(sex).setAge(subject!!.age!!.toDouble()).setWeight(subject!!.weight!!.toDouble()).setHeight(subject!!.height!!.toDouble()).setSmokingStatus(subject!!.isSmoker).build()
                Log.d(TAG, "createSession: userInfo $userInformation")
                session = FaceSessionBuilder(this).apply {
                    withUserInformation(userInformation)
                    withImageListener(this@VitalScanActivity)
                    withDetectionAlwaysOn(true)
                    withVitalSignsListener(this@VitalScanActivity)
                    withSessionInfoListener(this@VitalScanActivity)
                }.run { build(licenseDetails) }
            }else{
                Log.d(TAG, "createSession: subject detail null")
                session = FaceSessionBuilder(this).apply {
                    withImageListener(this@VitalScanActivity)
                    withDetectionAlwaysOn(true)
                    withVitalSignsListener(this@VitalScanActivity)
                    withSessionInfoListener(this@VitalScanActivity)
                }.run { build(licenseDetails) }
            }
        } catch (e: HealthMonitorException) {
            showError(e.errorCode)
        }
    }

    private fun setListener() {
        binding.measurementsLayout.btnStartStop.setOnClickListener {
            handleStartStopButtonClicked()
        }
        binding.tvStop.setOnClickListener {
            handleStartStopButtonClicked()
        }
    }

    override fun onSessionStateChange(sessionState: SessionState?) {
        runOnUiThread {
            when (sessionState) {
                SessionState.READY -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    setScanUi(0)
                }

                SessionState.PROCESSING -> {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    setScanUi(1)
                }

                else -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun setScanUi(scanState: Int){
        when (scanState){
            0 -> {
                // scan ready
                binding.tvStop.visibility = View.GONE
                binding.measurementsLayout.constraintLayout2.visibility = View.INVISIBLE
                binding.measurementsLayout.tvBtnStartStop.text = "Measure Now"
                binding.measurementsLayout.btnStartStop.visibility = View.VISIBLE
                binding.measurementsLayout.tvScanningMsg.visibility = View.VISIBLE
                binding.measurementsLayout.tvScanningErrorDesc.visibility = View.GONE
                if (subject?.name == null){
                     binding.measurementsLayout.tvScanningMsg.text = "Hello, Ready to measure your Vital Signs? "
                }else{
                      binding.measurementsLayout.tvScanningMsg.text = "${subject?.name}, Ready to measure your Vital Signs? "
                }

            }
            1 -> {
                // scan progress
                binding.tvStop.visibility = View.VISIBLE
                binding.measurementsLayout.constraintLayout2.visibility = View.VISIBLE
                binding.measurementsLayout.btnStartStop.visibility = View.GONE
                binding.measurementsLayout.tvScanningMsg.visibility = View.GONE
                binding.measurementsLayout.tvScanningErrorDesc.visibility = View.VISIBLE
            }
            2 -> {
                // error
                binding.tvStop.visibility = View.GONE
                binding.measurementsLayout.constraintLayout2.visibility = View.INVISIBLE
                binding.measurementsLayout.btnStartStop.visibility = View.INVISIBLE
                binding.measurementsLayout.tvScanningMsg.visibility = View.VISIBLE
                binding.measurementsLayout.tvScanningErrorDesc.visibility = View.VISIBLE
               

            }
            else -> {}
        }
    }

    override fun onError(errorData: ErrorData?) {
        runOnUiThread {
            showError(errorData?.code)
            clearCanvas()
            stopTimeCount()
        }
    }

    override fun onLicenseInfo(licenseInfo: LicenseInfo?) {
        runOnUiThread {
            licenseInfo?.licenseActivationInfo?.activationID.takeIf { it!!.isNotEmpty() }
                ?.let { id ->
                }

            licenseInfo?.licenseOfflineMeasurements?.let { offlineMeasurements ->
            }
        }
    }

    override fun onEnabledVitalSigns(sessionEnabledVitalSigns: SessionEnabledVitalSigns?) {
        runOnUiThread {
        }
    }

    override fun onImage(imageData: ImageData) {
        isDetected = imageData.imageValidity == ImageValidity.VALID
        runOnUiThread {
            if (!isDetected) {
                binding.measurementsLayout.tvScanningMsg.visibility = View.VISIBLE
                when (imageData.imageValidity) {
                    ImageValidity.INVALID_DEVICE_ORIENTATION -> getString(R.string.invalid_orientation)
                    ImageValidity.INVALID_ROI -> getString(R.string.face_not_detected)
                    ImageValidity.TILTED_HEAD -> getString(R.string.tilted_head)
                    ImageValidity.FACE_TOO_FAR -> getString(R.string.you_are_too_far)
                    ImageValidity.UNEVEN_LIGHT -> getString(R.string.uneven_lightning)
                    else -> null
                }?.let {
                    binding.faceFrame.setImageResource(R.drawable.face_frame_error)
                    if (session?.state == SessionState.PROCESSING) {
                        binding.measurementsLayout.tvScanningMsg.setText(it)
                        val errorDisc = getErrorDiscription(imageData.imageValidity)
                        binding.measurementsLayout.tvScanningErrorDesc.setText(errorDisc)
                        setScanUi(2)
                    }else if (session?.state == SessionState.READY){
                        setScanUi(0)
                    }
                }
            } else {
//                if (session?.state == SessionState.READY
//                    && session?.state != SessionState.PROCESSING){
//                    session?.start(scanDuration)
//                }
                // setting image bitmap
                if (session?.state == SessionState.PROCESSING) {
                    setScanUi(1)
                    updateReadingProgressMsg()
                }else if (session?.state == SessionState.READY){
                    setScanUi(0)
                }
                userImage = imageData.image

                binding.faceFrame.setImageResource(R.drawable.face_frame_normal)
                val normalColor = ContextCompat.getColor(this, R.color.theme_color)
                val progNormColor = ContextCompat.getColor(this, R.color.theme_color)
                binding.measurementsLayout.tvScanTime.setTextColor(normalColor)
                binding.measurementsLayout.tvBpm.setTextColor(normalColor)
                binding.measurementsLayout.tvScanBpmValue.setTextColor(normalColor)
//                binding.measurementsLayout.readingProgressBar.progressTintList =
//                    ColorStateList.valueOf(progNormColor)
            }

            binding.cameraView.lockCanvas()?.let { canvas ->
                // Drawing the bitmap on the TextureView canvas
                val image = imageData.image
                canvas.drawBitmap(
                    image,
                    null,
                    Rect(
                        0,
                        0,
                        binding.cameraView.width,
                        binding.cameraView.bottom - binding.cameraView.top
                    ),
                    null
                )

                //Drawing the face detection (if not null..)
                imageData.roi?.let roi@{ faceDetectionRect ->
                    //First we scale the SDK face detection rectangle to fit the TextureView size
                    val targetRect = RectF(faceDetectionRect)
                    val m = Matrix()
                    m.postScale(1f, 1f, image.width / 2f, image.height / 2f)
                    m.postScale(
                        binding.cameraView.width.toFloat() / image.width.toFloat(),
                        binding.cameraView.height.toFloat() / image.height.toFloat()
                    )
                    m.mapRect(targetRect)
                    // Then we draw it on the Canvas
                    if (isDetected) {
                        canvas.drawBitmap(faceDetectionNormal ?: return@roi, null, targetRect, null)
                    } else {
                        canvas.drawBitmap(faceDetectionError ?: return@roi, null, targetRect, null)
                    }
                }

                binding.cameraView.unlockCanvasAndPost(canvas)
            }
        }
    }

    override fun onVitalSign(vitalSign: VitalSign?) {
        runOnUiThread {
            binding.measurementsLayout.tvScanBpmValue.text = vitalSign?.value.toString()
            (vitalSign as? VitalSignBloodPressure).let { bpm ->
            }
        }
    }

    private fun getErrorDiscription(imageValidity: Int):String{
        return when(imageValidity) {
            ImageValidity.INVALID_DEVICE_ORIENTATION -> getString(R.string.invalid_orientation_description)
            ImageValidity.INVALID_ROI -> getString(R.string.face_not_detected_description)
            ImageValidity.TILTED_HEAD -> getString(R.string.tilted_head_description)
            ImageValidity.FACE_TOO_FAR -> getString(R.string.you_are_too_far_description)
            ImageValidity.UNEVEN_LIGHT -> getString(R.string.uneven_lightning_description)
            else -> ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onFinalResults(finalResults: VitalSignsResults?) {

        val heartRate = if (finalResults?.getResult(VitalSignTypes.PULSE_RATE)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.PULSE_RATE).value
        }

        var pulseRateConfidenceOrdinal: String? = ""
        if ((finalResults?.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.ordinal != null) {
            pulseRateConfidenceOrdinal = (finalResults?.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level.toString()
        }


        val breathingRate = if (finalResults?.getResult(VitalSignTypes.RESPIRATION_RATE)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.RESPIRATION_RATE).value
        }

        var respirationRateConfidenceOrdinal: String? = ""
        if ((finalResults?.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.ordinal != null) {
            respirationRateConfidenceOrdinal = (finalResults?.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.toString()
        }

        val prq = if (finalResults?.getResult(VitalSignTypes.PRQ)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.PRQ).value
        }

        var prqConfidenceOrdinal: String? = ""
        if ((finalResults?.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.ordinal != null) {
            prqConfidenceOrdinal = (finalResults?.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.toString()
        }

        val bloodPressureSystolic = if (finalResults?.getResult(VitalSignTypes.BLOOD_PRESSURE)?.value == null) {
            getString(R.string._0)
        } else {
            val systolic = finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as VitalSignBloodPressure
            "" + systolic.value.systolic
        }

        val bloodPressureDiastolic = if (finalResults?.getResult(VitalSignTypes.BLOOD_PRESSURE)?.value == null) {
            getString(R.string._0)
        } else {
            val diastolic = finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as VitalSignBloodPressure
            "" + diastolic.value.diastolic
        }

        val hrv_sdnn = if (finalResults?.getResult(VitalSignTypes.SDNN)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.SDNN).value
        }

        var sdnnConfidenceOrdinal: String? = ""
        if ((finalResults?.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.ordinal != null) {
            sdnnConfidenceOrdinal = (finalResults?.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.toString()
        }

        val oxygenSaturation = if (finalResults?.getResult(VitalSignTypes.OXYGEN_SATURATION)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION).value
        }

        val wellnessIndex =  if (finalResults?.getResult(VitalSignTypes.WELLNESS_INDEX)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.WELLNESS_INDEX).value
        }


        val hemoglobin = if (finalResults?.getResult(VitalSignTypes.HEMOGLOBIN)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN).value
        }

        val hemoglobinA1C = if (finalResults?.getResult(VitalSignTypes.HEMOGLOBIN_A1C)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C)?.value
        }

        val recoveryAbility = if (finalResults?.getResult(VitalSignTypes.PNS_ZONE)?.value == null) {
            getString(R.string.n_a)
        } else {
            "" + finalResults.getResult(VitalSignTypes.PNS_ZONE)?.value
        }

        val stressResp = if (finalResults?.getResult(VitalSignTypes.SNS_ZONE)?.value == null) {
            getString(R.string.n_a)
        } else {
            "" + finalResults.getResult(VitalSignTypes.SNS_ZONE)?.value
        }


        val stressLevel = if (finalResults?.getResult(VitalSignTypes.STRESS_LEVEL)?.value == null) {
            getString(R.string.n_a)

        } else {
            "" + finalResults.getResult(VitalSignTypes.STRESS_LEVEL)?.value
        }


        val stressIndex = if (finalResults?.getResult(VitalSignTypes.STRESS_INDEX)?.value == null) {
            getString(R.string.n_a)

        } else {
            "" + finalResults.getResult(VitalSignTypes.STRESS_INDEX)?.value
        }

        val highHemoglobinA1CRisk = if (finalResults?.getResult(VitalSignTypes.HIGH_HEMOGLOBIN_A1C_RISK)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.HIGH_HEMOGLOBIN_A1C_RISK).value
        }

        val highBloodPressureRisk = if (finalResults?.getResult(VitalSignTypes.HIGH_BLOOD_PRESSURE_RISK)?.value == null) {
            getString(R.string._0)
        } else {
            "" + finalResults.getResult(VitalSignTypes.HIGH_BLOOD_PRESSURE_RISK).value
        }


        var meanRriValue: Int = 0
        var meanRriConfidence: Int = 0
        (finalResults?.getResult(VitalSignTypes.MEAN_RRI) as? VitalSignMeanRRI)?.let { meanRRI ->
            if (meanRRI.value != null) {
                meanRriValue = meanRRI.value
            }
            if (meanRRI.confidence?.level?.ordinal != null) {
                meanRriConfidence = meanRRI.confidence?.level?.ordinal!!
            }
        }

        var rmssdValue: Int = 0
        (finalResults?.getResult(VitalSignTypes.RMSSD) as? VitalSignRMSSD)?.let { rmssd ->
            if (rmssd.value != null) {
                rmssdValue = rmssd.value
            }
        }

        var sd1Value: Int = 0
        (finalResults?.getResult(VitalSignTypes.SD1) as? VitalSignSD1)?.let { sd1 ->
            if (sd1.value != null) {
                sd1Value = sd1.value
            }
        }

        var sd2Value: Int = 0
        (finalResults?.getResult(VitalSignTypes.SD2) as? VitalSignSD2)?.let { sd2 ->
            if (sd2.value != null) {
                sd2Value = sd2.value
            }
        }
        var lfhfValue: Double = 0.0
        (finalResults?.getResult(VitalSignTypes.LFHF) as? VitalSignLFHF)?.let { lfhf ->
            if (lfhf.value != null) {
                lfhfValue = lfhf.value
            }
        }

        var snsIndexValue: Double = 0.0
        (finalResults?.getResult(VitalSignTypes.SNS_INDEX) as? VitalSignSNSIndex)?.let { snsIndex ->
            if (snsIndex.value != null) {
                snsIndexValue = snsIndex.value
            }
        }

        var pnsIndexValue: Double = 0.0
        (finalResults?.getResult(VitalSignTypes.PNS_INDEX) as? VitalSignPNSIndex)?.let { pnsIndex ->
            if (pnsIndex.value != null) {
                pnsIndexValue = pnsIndex.value
            }
        }
        var ascvd =  if(finalResults?.getResult(VitalSignTypes.ASCVD_RISK)?.value == null){
            getString(R.string.n_a)
            //  naCount++
        } else{
                (finalResults.getResult(VitalSignTypes.ASCVD_RISK) as VitalSignASCVDRisk).value.toString()
        }
        Log.d("FinalResult(ScanByFaceActivity) = ascvdRisk ", ""+finalResults?.getResult(VitalSignTypes.ASCVD_RISK)?.value)

        var heartAge = if(finalResults?.getResult(VitalSignTypes.HEART_AGE)?.value == null){
            getString(R.string.n_a)
            //  naCount++
        } else{
                (finalResults.getResult(VitalSignTypes.HEART_AGE) as VitalSignHeartAge).value.toString()
        }
        Log.d("FinalResult(ScanByFaceActivity) = heartAge",""+finalResults?.getResult(VitalSignTypes.HEART_AGE)?.value)

        var lowHemoglobinRisk = if(finalResults?.getResult(VitalSignTypes.LOW_HEMOGLOBIN_RISK)?.value == null){
            getString(R.string._0)
            //  naCount++
        } else{
                (finalResults.getResult(VitalSignTypes.LOW_HEMOGLOBIN_RISK) as VitalSignLowHemoglobinRisk).value.ordinal.toString()
        }
        Log.d("FinalResult(ScanByFaceActivity) = lowHemoglobinRisk",""+finalResults?.getResult(VitalSignTypes.LOW_HEMOGLOBIN_RISK)?.value)

        var highFastingGlucoseRisk  = if(finalResults?.getResult(VitalSignTypes.HIGH_FASTING_GLUCOSE_RISK)?.value == null){
            getString(R.string._0)
            //  naCount++
        } else{
                (finalResults.getResult(VitalSignTypes.HIGH_FASTING_GLUCOSE_RISK) as VitalSignHighFastingGlucoseRisk).value.ordinal.toString()
        }
        Log.d("FinalResult(ScanByFaceActivity) = highFastingGlucoseRisk",""+finalResults?.getResult(VitalSignTypes.HIGH_FASTING_GLUCOSE_RISK)?.value)

        var highTotalCholesterolRisk = if(finalResults?.getResult(VitalSignTypes.HIGH_TOTAL_CHOLESTEROL_RISK)?.value == null){
            getString(R.string._0)
            // naCount++
        } else{

                (finalResults.getResult(VitalSignTypes.HIGH_TOTAL_CHOLESTEROL_RISK) as VitalSignHighTotalCholesterolRisk).value.ordinal.toString()
        }

        val wellnessLevel =  finalResults?.getResult(VitalSignTypes.WELLNESS_LEVEL)?.value.toString()


        Log.d("FinalResult(ScanByFaceActivity) = highFastingGlucoseRisk",""+finalResults?.getResult(VitalSignTypes.HIGH_TOTAL_CHOLESTEROL_RISK)?.value)

        Log.d(TAG, "onFinalResults: meam rri $meanRriValue")

        Log.d(TAG, "onFinalResults: ascvd value  $ascvd")
        Log.d(TAG, "onFinalResults: glucose value $highFastingGlucoseRisk")
        Log.d(TAG, "onFinalResults: heart age value $heartAge")
        Log.d(TAG, "onFinalResults: low hemoglobin value $lowHemoglobinRisk")
        Log.d(TAG, "onFinalResults: total colestrol value $highTotalCholesterolRisk")
        val date = DateFormatter.getCurrentDate()
        val time = DateFormatter.getCurrentTime()
        val scanResultObject = MeasurementResult(
         date = date,
         time = time,
         name = subject?.name.toString(),
         age = subject?.age.toString(),
         gender = subject?.sex.toString(),
         height = subject?.height.toString(),
         weight = subject?.weight.toString(),
         heartRate = heartRate,
         breathingRate =  breathingRate,
         PRQ = prq,
         hrv_sdnn = hrv_sdnn,
         hypertensionRisk = highBloodPressureRisk,
         diabeticRisk = highHemoglobinA1CRisk,
         ascvd = ascvd,
         highFastingGlucose = highFastingGlucoseRisk,
         lowHemoglobinRisk = lowHemoglobinRisk,
         heartAge  = heartAge,
         totalColestrol = highTotalCholesterolRisk,
         oxygenSat = oxygenSaturation,
         bloodPressureSystolic = bloodPressureSystolic,
         bloodPressureDistolic = bloodPressureDiastolic,
         hemoglobin = hemoglobin,
         hemoglobinA1c = hemoglobinA1C,
         stressLevel = stressLevel,
         meanRri = meanRriValue,
         pnsIndex  = pnsIndexValue,
         snsIndex  = snsIndexValue,
         RMMSD = rmssdValue,
         sd1  = sd1Value,
         sd2  = sd2Value,
         lfhf = lfhfValue,
         recoveryRate = recoveryAbility,
         stressResp = stressResp,
         wellnessLevel = wellnessLevel,
         wellnessIndex = wellnessIndex,
         heartRateConfi = pulseRateConfidenceOrdinal,
         breathingRateConfid = respirationRateConfidenceOrdinal,
         prqConfi = prqConfidenceOrdinal,
         sdnCofi = sdnnConfidenceOrdinal)
        val list = ScanResultGenrator.createScanResult(
            context = this,
            result = scanResultObject
        )
        Log.d(TAG, "onFinalResults: list ${list.size}")
        runOnUiThread {
            stopTimeCount()
            if(isTerminated){
            }else if (bloodPressureDiastolic=="0" || bloodPressureSystolic == "0" || heartRate == "0" || oxygenSaturation == "0" || prq == "0" || breathingRate == "0") {
                AlertDialogManager.showConfirmationDialog(this,
                    title = getString(R.string.data_not_collected),
                    message = getString(R.string.required_vitals_not_collected),
                    buttonMessage = getString(string.ok),
                    cancelable = true,
                    dialogClickListener = object : DialogClickListener {
                        override fun onButton1Clicked() {
                        }
                    })
            }else {
                loginViewModel.insertOrUpdateScanResult(this,scanResultObject)
                VitalResultActivity2.startActivity(this,list,subject,wellnessIndex,wellnessLevel,date,time,true)
                finish()
            }
        }


    }


    override fun onWarning(warningData: WarningData) {
        runOnUiThread {
            when (warningData.code) {
                AlertCodes.INITIALIZATION_CODE_ROTATION_AND_ORIENTATION_MISMATCH -> {
                    if (session != null && session?.state == SessionState.PROCESSING) {
                        showWarning("Orientation Mismatch", warningData.code)
                    }
                    binding.faceFrame.setImageResource(R.drawable.face_frame_error)
                }

                else -> {
                    showError(warningData.code)
                }
            }
        }
    }

    private fun handleStartStopButtonClicked() {
        try {
            if (session?.state == SessionState.READY) {
                when {
                    (!NetworkUtils.isNetworkAvailable(this@VitalScanActivity)) -> {
                        Log.d("TAG", "getUserById: 2 ")
                        AlertDialogManager.showConfirmationDialog(this@VitalScanActivity,
                            title = getString(R.string.error),
                            message = NetworkErrorCode.getNetworkError(100,null),
                            buttonMessage = getString(string.ok),
                            cancelable = true,
                            dialogClickListener = object : DialogClickListener {
                                override fun onButton1Clicked() {

                                }
                            })
                    }
                    else -> {
                        // put info dialog
                        val dialog =
                            DialogHowToScan(this@VitalScanActivity,object : DialogHowToScan.OnClick{
                                override fun onDismiss() {
                                    try {
                                        Log.d(TAG, "onDismiss: userid ${ Pref.user?.userId!!.toInt()}")
                                        val request = UserRequest(userId = Pref.user?.userId!!.toInt())
                                        viewModel.updateScan(request, this@VitalScanActivity)
                                        binding.measurementsLayout.tvScanningMsg.visibility =
                                            View.VISIBLE
                                        session?.start(scanDuration)
                                        startTimeCount()
                                        isTerminated = false

                                    } catch (e: HealthMonitorException) {
                                        showError(e.errorCode)
                                    }
                                }
                            })
                        dialog.show(supportFragmentManager, "Info Dialog")
                    }
                }
            } else {
                if (mTime > 0){
                    AlertDialogManager.showConfirmationDialog(this,
                        title = getString(R.string.stop_scan),
                        message = getString(R.string.stop_scan_message),
                        buttonMessage = getString(R.string._stop),
                        cancelable = true,
                        dialogClickListener = object : DialogClickListener {
                            override fun onButton1Clicked() {
                                isTerminated = true
                                session?.stop()
                                stopTimeCount()
                            }

                        })
                }else{
                    session?.stop()
                    stopTimeCount()
                }

            }
        } catch (e: HealthMonitorException) {
            showError(e.errorCode)
        }
    }

    private fun showWarning(text1: String, errorCode: Int?) {
        var text: String? = text1
        if (mWarningDialogTimeoutHandler != null) {
            mWarningDialogTimeoutHandler!!.removeCallbacksAndMessages(null)
        }
        if (errorCode != null) {
            text += " ($errorCode)"
        }
        binding.measurementsLayout.tvScanningMsg.text = text
        mWarningDialogTimeoutHandler = Handler(Looper.getMainLooper())
        mWarningDialogTimeoutHandler!!.postDelayed(
            { }, 2000
        )
    }

    private fun showAlert(title: String? = null, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(string.ok), null)
            .setCancelable(false)
            .show()
    }

    private fun startTimeCount() {
        binding.measurementsLayout.readingProgressBar.visibility = View.VISIBLE
        binding.measurementsLayout.tvScanningMsg.visibility = View.VISIBLE
        if (mTimeCountHandler != null) {
            mTimeCountHandler?.removeCallbacksAndMessages(null)
        }
        mTime = scanDuration.toInt()
        progressPercent = 0.0
        mTimeCountHandler = Handler(Looper.getMainLooper())
        mTimeCountHandler?.post(object : Runnable {
            override fun run() {
                mTime--
                mPercentage++
//                updateReadingProgressMsg()
                try {
                    progressPercent = ((mPercentage.toDouble() / scanDuration) * 100)
                    binding.measurementsLayout.readingProgressBar.setProgressPercentage(progressPercent,true)
                    binding.measurementsLayout.tvScanTime.text = "00:"+mTime.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mTimeCountHandler?.postDelayed(this, 1000)
            }
        })
    }

    private fun stopTimeCount() {
        mTime = scanDuration.toInt()
        mPercentage = 0
//        binding.measurementsLayout.tvScanningMsg.visibility = View.GONE
        binding.measurementsLayout.tvScanTime.text = "00:"+scanDuration.toString()
        binding.measurementsLayout.tvScanBpmValue.text = getString(R.string._0)
        binding.measurementsLayout.readingProgressBar.setProgressPercentage(0.toDouble(),true)
        mTimeCountHandler?.removeCallbacksAndMessages(null)
    }

    private fun clearCanvas() {
        val canvas: Canvas? = binding.cameraView.lockCanvas()
        canvas?.drawColor(ContextCompat.getColor(baseContext, R.color.colorScreenBackground))
        if (canvas != null) binding.cameraView.unlockCanvasAndPost(canvas)
    }

    private fun updateReadingProgressMsg() {
        if (isDetected) {
            when (mPercentage) {
                in 0..5 -> {
                    val string = ContextCompat.getString(this, R.string.face_scanning_msg)
                    binding.measurementsLayout.tvScanningErrorDesc.setText(string)
                }

                6 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text = ContextCompat.getString(
                        this,
                        R.string.sit_still_during_the_measurement
                    )
                }

                in 7..12 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text = ContextCompat.getString(
                        this,
                        R.string.avoid_moving_or_talk
                    )
                }

                in 13..18 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text =
                        getString(R.string.stay_focused_on_screen)
                }

                in 19..24 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text =
                        getString(R.string.wait_until_end_for_best_result)
                }

                in 25..30 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text =
                        getString(R.string.normal_prq_range)
                }

                in 31..36 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text =
                        getString(R.string.normal_resting_heart_rate)
                }

                in 37..42 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text =
                        getString(R.string.tracking_heart_rate_provide)
                }

                in 43..48 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text =
                        getString(R.string.athletes_may_track_hrv)
                }

                in 49..54 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text =
                        getString(R.string.high_levels_of_hrv)
                }

                in 55..60 -> {
                    binding.measurementsLayout.tvScanningErrorDesc.text =
                        getString(R.string.hold_on_all_the_result_will_appear)
                }
            }
        }
    }

    private fun showError(errorCode: Int?) {
        when (errorCode) {
            3004, 3003 -> {
                AlertDialogManager.showConfirmationDialog(this,
                    title = getString(R.string.retry),
                    message = getString(R.string.retry_message),
                    buttonMessage = getString(R.string.try_again_btn),
                    cancelable = false,
                    dialogClickListener = object : DialogClickListener {
                        override fun onButton1Clicked() {
                            stopTimeCount()
                        }
                    })
            }

            else -> {
                showAlert(
                    title = getString(R.string.error),
                    message = BinahErrorMessage.getErrorMessage(errorCode!!)
                )
            }
        }
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("EEE dd MMM yyyy | hh:mm a", Locale.getDefault())
        val cal = Calendar.getInstance()
        return dateFormat.format(cal.time)
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            when {
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted, proceed with camera
                    createSession()
                }

                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                ) -> {
                    // Permission denied (not "Don't ask again"), show rationale again
                    showPermissionRationale()
                }

                else -> {
                    // Permission permanently denied ("Don't ask again")
                    showPermissionDeniedDialog()
                }
            }
        }
    }

    private fun showPermissionRationale() {
        AlertDialogManager.showConfirmationDialog(this,
            title = "Camera Permission Needed",
            message = "This app requires camera access to function properly. Please allow camera access.",
            buttonMessage = getString(string.ok),
            cancelable = false,
            isDissable = false,
            dialogClickListener = object : DialogClickListener {
                override fun onButton1Clicked() {
                    requestCameraPermission()
                }
            })
    }

    private fun showPermissionDeniedDialog() {
        AlertDialogManager.showConfirmationDialog(this,
            title = "Camera Permission Denied",
            message = "Camera access has been permanently denied. Please go to settings to enable the camera permission manually.\n\nApp permissions --> Camera (click)",
            buttonMessage = "Go to Settings",
            cancelable = false,
            isDissable = false,
            dialogClickListener = object : DialogClickListener {
                override fun onButton1Clicked() {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                }
            })
    }

}

@Parcelize
data class MeasurementResult(
    val date: String,
    val time : String,
    val name : String,
    val gender : String,
    val age : String,
    val height : String,
    val weight : String,
    val heartRate: String,
    val breathingRate: String,
    val PRQ: String,
    val hrv_sdnn: String,

    val hypertensionRisk: String,
    val diabeticRisk: String,
    val ascvd: String,
    val highFastingGlucose : String,
    val lowHemoglobinRisk : String,
    val heartAge : String,
    val totalColestrol: String,
    val oxygenSat: String,
    val bloodPressureSystolic: String,
    val bloodPressureDistolic: String,


    val hemoglobin: String,
    val hemoglobinA1c: String,

    val stressLevel: String,
    val meanRri : Int,
    val pnsIndex : Double,
    val snsIndex : Double,
    val RMMSD : Int,
    val sd1 : Int,
    val sd2 : Int,
    val lfhf : Double,
    val recoveryRate: String,
    val stressResp: String,

    val wellnessLevel: String,
    val wellnessIndex: String,

    val heartRateConfi: String?,
    val breathingRateConfid: String?,
    val prqConfi: String?,
    val sdnCofi: String?
) : Parcelable