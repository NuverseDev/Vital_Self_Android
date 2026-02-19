package com.vital_self.features.scan.presentation.scan

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
import com.biosensesignal.sdk.api.vital_signs.VitalSignsResults
import com.biosensesignal.sdk.api.vital_signs.vitals.VitalSignBloodPressure
import com.biosensesignal.sdk.session.FaceSessionBuilder
import com.vital_self.R
import com.vital_self.core.base.BaseActivity
import com.vital_self.databinding.ActivityVitalScanBinding
import com.vital_self.features.scan.presentation.dialogs.DialogHowToScan
import com.vital_self.core.domain.model.Model
import com.vital_self.core.data.remote.model.NetworkErrorCode
import com.vital_self.core.utils.helpers.NetworkUtils
import com.vital_self.features.scan.data.repository.ScanRepository
import com.vital_self.features.scan.presentation.viewmodel.ScanViewModelFactory
import com.vital_self.core.utils.helpers.AlertDialogManager
import com.vital_self.core.utils.helpers.AnimationsHandler
import com.vital_self.core.utils.helpers.BinahErrorMessage
import com.vital_self.core.utils.helpers.DialogClickListener
import com.vital_self.core.utils.helpers.TwoButtonDialogClickListener
import com.vital_self.core.data.local.preferences.PreferenceManager
import com.vital_self.features.history.presentation.viewmodel.HistoryViewModel
import com.vital_self.features.scan.presentation.viewmodel.ScanViewModel
import com.google.android.material.navigation.NavigationView
import com.vital_self.BuildConfig
import com.vital_self.features.auth.presentation.login.ActivityUserLogin
import com.vital_self.features.history.presentation.ScanHistoryActivity
import com.vital_self.features.profile.presentation.ActivityProfile
import com.vital_self.features.qrcode.presentation.QrScanActivity
import com.vital_self.features.packages.presentation.PackagesActivity
import com.vital_self.features.onboarding.presentation.ActivityHowToScan
import com.vital_self.features.onboarding.presentation.HowToUseActivity
import com.vital_self.features.scan.presentation.result.VitalResultActivity2
import com.vital_self.core.utils.constants.AppConstants
import com.vital_self.features.scan.presentation.viewmodel.ScanResultState
import com.vital_self.features.scan.presentation.viewmodel.CreditCheckState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.biosensesignal.sdk.api.session.user_info.SmokingStatus
import android.view.ViewGroup
import com.vital_self.core.ui.theme.VitalSelfTheme
import com.vital_self.features.scan.presentation.components.PatientInfoBottomSheet
import com.vital_self.features.scan.presentation.state.PatientFieldError
import com.vital_self.features.scan.presentation.state.PatientInfoEvent
import com.vital_self.features.scan.presentation.state.PatientInfoState
import com.vital_self.features.scan.presentation.state.PatientValidation
import androidx.core.net.toUri

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class VitalScanActivity : BaseActivity(),
    ImageListener, VitalSignsListener, SessionInfoListener {

    lateinit var binding: ActivityVitalScanBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val scanDuration = 60L
    private var mWarningDialogTimeoutHandler: Handler? = null
    private var session: Session? = null
    private var userImage: Bitmap? = null
    lateinit var historyViewmodel: HistoryViewModel
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

    // Doctor mode state
    private var showPatientBottomSheet = mutableStateOf(false)
    private var patientInfoState = mutableStateOf(PatientInfoState())
    private var tempPatientSubject: Model.SubjectDetails? = null
    private var composeView: ComposeView? = null

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

        if (!PreferenceManager.isProfileUpdated) {
            AlertDialogManager.showConfirmationDialog(this,
                title = "Update Profile",
                message = "Please update profile for full report",
                buttonMessage = "Ok",
                cancelable = true,
                dialogClickListener = object : DialogClickListener {
                    override fun onButton1Clicked() {
                        ActivityProfile.startActivity(this@VitalScanActivity,AppConstants.PROFILE)
                    }
            })
        }

        historyViewmodel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        initUI()
        setListener()
        setupComposeBottomSheet()
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

        val menuProfile = navigationView.findViewById<LinearLayout>(R.id.menu_profile)
        val menuHistory = navigationView.findViewById<LinearLayout>(R.id.menu_history)
        val menuHelp = navigationView.findViewById<LinearLayout>(R.id.menu_help)
        val menuScanQR = navigationView.findViewById<LinearLayout>(R.id.menu_scan_qr)
        val menuLogout = navigationView.findViewById<LinearLayout>(R.id.menu_logout)
        val packages = navigationView.findViewById<LinearLayout>(R.id.menu_packages)
        val closeDrawer = navigationView.findViewById<ImageView>(R.id.close_icon)
        val menuMyResetApp = navigationView.findViewById<LinearLayout>(R.id.menu_my_reset_app)
        val appVersion = navigationView.findViewById<TextView>(R.id.app_version)

        appVersion.text = getString(R.string.version, BuildConfig.VERSION)

        if (PreferenceManager.appVersionMatch){
            menuHistory.visibility = View.GONE
            menuScanQR.visibility = View.GONE
            packages.visibility = View.GONE
            menuMyResetApp.visibility = View.GONE
        }else{
            menuHistory.visibility = View.VISIBLE
            menuScanQR.visibility = View.VISIBLE
            packages.visibility = View.VISIBLE
            menuMyResetApp.visibility = View.VISIBLE
        }

        menuProfile.setOnClickListener {
            ActivityProfile.startActivity(this,AppConstants.PROFILE)
            drawerLayout.closeDrawers()
        }

        closeDrawer.setOnClickListener {
            drawerLayout.closeDrawers()
        }

        menuHistory.setOnClickListener {
            ScanHistoryActivity.startActivity(this,AppConstants.HISTORY)
            drawerLayout.closeDrawers()
        }

        packages.setOnClickListener {
            PackagesActivity.startActivity(this)
            drawerLayout.closeDrawers()
        }

        menuHelp.setOnClickListener {
            ActivityHowToScan.startActivity(this,AppConstants.BEST_PRACTICES)
            drawerLayout.closeDrawers()
        }

        val menuHowToUse = navigationView.findViewById<LinearLayout>(R.id.menu_how_to_use)
        menuHowToUse.setOnClickListener {
            HowToUseActivity.startActivity(this)
            drawerLayout.closeDrawers()
        }

        // About us menu - opens URL
        val menuAboutUs = navigationView.findViewById<LinearLayout>(R.id.menu_about_us)
        menuAboutUs.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                "https://vitalself.life/pages/about-vitalself".toUri())
            startActivity(intent)
            drawerLayout.closeDrawers()
        }

        // Privacy policy menu - opens URL
        val menuPrivacyPolicy = navigationView.findViewById<LinearLayout>(R.id.menu_privacy_policy)
        menuPrivacyPolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                "https://vitalself.life/policies/privacy-policy".toUri())
            startActivity(intent)
            drawerLayout.closeDrawers()
        }

        // Terms & condition menu - opens URL
        val menuTerms = navigationView.findViewById<LinearLayout>(R.id.menu_terms)
        menuTerms.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                "https://vitalself.life/policies/terms-of-service".toUri())
            startActivity(intent)
            drawerLayout.closeDrawers()
        }

        menuMyResetApp.setOnClickListener {
            openMyResetApp()
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
                        PreferenceManager.subjectDetails = null
                        PreferenceManager.authToken = null
                        PreferenceManager.authUser = null
                        PreferenceManager.Key = null
                        PreferenceManager.isLoggedIn = false
                        PreferenceManager.user = null
                        drawerLayout.closeDrawers()
                        ActivityUserLogin.startActivity(this@VitalScanActivity)
                        finish()
                    }
                }
            )
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
        viewModel.scanResult.observe(this@VitalScanActivity, Observer { state ->
            when (state) {
                is ScanResultState.Loading -> {
                    showHideProgress(true)
                }
                is ScanResultState.Success -> {
                    showHideProgress(false)
                    viewModel.resetScanResultState()
                    if (state.scanId != -1) {
                        // Navigate to VitalResultActivity2 with scan ID (isFromHistory = false means from scan)
                        VitalResultActivity2.startActivityWithScanId(this@VitalScanActivity, state.scanId, isFromHistory = false)
                        finish()
                    } else {
                        Toast.makeText(this@VitalScanActivity, "Failed to get scan ID", Toast.LENGTH_SHORT).show()
                    }
                }
                is ScanResultState.Error -> {
                    showHideProgress(false)
                    viewModel.resetScanResultState()
                    AlertDialogManager.showConfirmationDialog(this@VitalScanActivity,
                        title = getString(R.string.error),
                        message = state.message ?: "Failed to save scan history",
                        buttonMessage = getString(string.ok),
                        cancelable = true,
                        dialogClickListener = object : DialogClickListener {
                            override fun onButton1Clicked() {
                            }
                        })
                }
                is ScanResultState.ValidationError -> {
                    showHideProgress(false)
                    viewModel.resetScanResultState()
                    AlertDialogManager.showConfirmationDialog(this@VitalScanActivity,
                        title = getString(R.string.data_not_collected),
                        message = getString(R.string.required_vitals_not_collected),
                        buttonMessage = getString(string.ok),
                        cancelable = true,
                        dialogClickListener = object : DialogClickListener {
                            override fun onButton1Clicked() {
                            }
                        })
                }
                is ScanResultState.Idle -> {
                    // Do nothing
                }
            }
        })

        viewModel.creditCheckState.observe(this@VitalScanActivity, Observer { state ->
            when (state) {
                is CreditCheckState.Loading -> {
                    // Optionally show loading indicator
                }
                is CreditCheckState.Success -> {
                    // Credits updated in SharedPreferences
                    viewModel.resetCreditCheckState()
                }
                is CreditCheckState.NoCredits -> {
                    // Credits updated in SharedPreferences (0 credits)
                    // Dialog will be shown when user clicks "Measure Now"
                    viewModel.resetCreditCheckState()
                }
                is CreditCheckState.Error -> {
                    viewModel.resetCreditCheckState()
                    Log.d(TAG, "Credit check error: ${state.message}")
                }
                is CreditCheckState.Idle -> {
                    // Do nothing
                }
            }
        })
    }

    private fun showNoCreditsDialog() {
        AlertDialogManager.showTwoButtonDialog(
            activity = this@VitalScanActivity,
            title = "No Credits Available",
            message = "You don't have any credits available to do scan. Please purchase a package to continue.",
            cancelButtonText = "Cancel",
            okButtonText = "Buy Package",
            cancelable = false,
            dialogClickListener = object : TwoButtonDialogClickListener {
                override fun onCancelClicked() {
                    // Do nothing, just dismiss the dialog
                }

                override fun onOkClicked() {
                    // Navigate to PackagesActivity
                    PackagesActivity.startActivity(this@VitalScanActivity)
                }
            }
        )
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
        viewModel.checkAvailableCredits(this)
    }

    override fun onStart() {
        super.onStart()
        val version = com.biosensesignal.sdk.BuildConfig.VERSION_NAME
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
            val key = PreferenceManager.Key

            val licenseDetails = LicenseDetails(key)

            if (subject != null){

                val sex = when(subject?.sex){
                    Sex.MALE -> Sex.MALE
                    Sex.FEMALE -> Sex.FEMALE
                    else  -> Sex.UNSPECIFIED
                }

                val userInformation = UserInformation.Builder()
                    .setSex(sex)
                    .setAge(subject!!.age!!.toDouble())
                    .setWeight(subject!!.weight!!.toDouble())
                    .setHeight(subject!!.height!!.toDouble())
                    .setSmokingStatus(subject!!.isSmoker).build()

                session = FaceSessionBuilder(applicationContext).apply {
                    withUserInformation(userInformation)
                    withImageListener(this@VitalScanActivity)
                    withDetectionAlwaysOn(true)
                    withVitalSignsListener(this@VitalScanActivity)
                    withSessionInfoListener(this@VitalScanActivity)
                }.run { build(licenseDetails) }
            }else{
                session = FaceSessionBuilder(applicationContext).apply {
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
            }

            binding.cameraView.lockCanvas()?.let { canvas ->
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

                imageData.roi?.let roi@{ faceDetectionRect ->
                    val targetRect = RectF(faceDetectionRect)
                    val m = Matrix()
                    m.postScale(1f, 1f, image.width / 2f, image.height / 2f)
                    m.postScale(
                        binding.cameraView.width.toFloat() / image.width.toFloat(),
                        binding.cameraView.height.toFloat() / image.height.toFloat()
                    )
                    m.mapRect(targetRect)
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

    override fun onFinalResults(finalResults: VitalSignsResults?) {
        runOnUiThread {
            stopTimeCount()
            if (isTerminated) {
                return@runOnUiThread
            }

            viewModel.processScanResults(finalResults)

            // If in doctor mode, reset session to user's own data for next scan
            if (tempPatientSubject != null) {
                resetSessionToUserData()
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
                    (PreferenceManager.availableCredits <= 0) -> {
                        // No credits available, show dialog
                        showNoCreditsDialog()
                    }
                    else -> {
                        // Check if user is a doctor
                        val isDoctor = PreferenceManager.authUser?.doctor == true

                        if (isDoctor) {
                            // Show patient info bottom sheet for doctor mode
                            showPatientInfoBottomSheet()
                        } else {
                            // Normal flow - show DialogHowToScan
                            showHowToScanDialogAndStartScan()
                        }
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
//              updateReadingProgressMsg()
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
                    }
                )
            }
            else -> {
                showAlert(
                    title = getString(R.string.error),
                    message = BinahErrorMessage.getErrorMessage(errorCode!!)
                )
            }
        }
    }

    private fun setupComposeBottomSheet() {
        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@VitalScanActivity)
            setViewTreeSavedStateRegistryOwner(this@VitalScanActivity)
            // Set layout params to match parent but not intercept touches when empty
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                VitalSelfTheme {
                    if (showPatientBottomSheet.value) {
                        PatientInfoBottomSheet(
                            state = patientInfoState.value,
                            onEvent = { handlePatientInfoEvent(it) },
                            onDismiss = { hidePatientInfoBottomSheet() }
                        )
                    }
                }
            }
        }
        // Add the ComposeView but make it not intercept touches when bottom sheet is not shown
        (binding.root as ViewGroup).addView(composeView)

        // Set visibility based on bottom sheet state
        composeView?.visibility = View.GONE
    }

    private fun showPatientInfoBottomSheet() {
        composeView?.visibility = View.VISIBLE
        showPatientBottomSheet.value = true
    }

    private fun hidePatientInfoBottomSheet() {
        showPatientBottomSheet.value = false
        patientInfoState.value = PatientInfoState()
        composeView?.visibility = View.GONE
    }

    private fun handlePatientInfoEvent(event: PatientInfoEvent) {
        when (event) {
            is PatientInfoEvent.NameChanged -> {
                patientInfoState.value = patientInfoState.value.copy(
                    name = event.value,
                    showError = false,
                    errorField = PatientFieldError.NONE
                )
            }
            is PatientInfoEvent.AgeChanged -> {
                patientInfoState.value = patientInfoState.value.copy(
                    age = event.value,
                    showError = false,
                    errorField = PatientFieldError.NONE
                )
            }
            is PatientInfoEvent.HeightChanged -> {
                patientInfoState.value = patientInfoState.value.copy(
                    height = event.value,
                    showError = false,
                    errorField = PatientFieldError.NONE
                )
            }
            is PatientInfoEvent.HeightUnitChanged -> {
                patientInfoState.value = patientInfoState.value.copy(heightUnit = event.value)
            }
            is PatientInfoEvent.WeightChanged -> {
                patientInfoState.value = patientInfoState.value.copy(
                    weight = event.value,
                    showError = false,
                    errorField = PatientFieldError.NONE
                )
            }
            is PatientInfoEvent.WeightUnitChanged -> {
                patientInfoState.value = patientInfoState.value.copy(weightUnit = event.value)
            }
            is PatientInfoEvent.GenderChanged -> {
                patientInfoState.value = patientInfoState.value.copy(
                    gender = event.value,
                    showError = false,
                    errorField = PatientFieldError.NONE
                )
            }
            is PatientInfoEvent.SmokerStatusChanged -> {
                patientInfoState.value = patientInfoState.value.copy(isSmoker = event.value)
            }
            is PatientInfoEvent.SubmitClicked -> {
                val validation = validatePatientInfo(patientInfoState.value)
                if (validation == PatientValidation.VALID) {
                    val patientDetails = createPatientSubjectDetails(patientInfoState.value)
                    hidePatientInfoBottomSheet()
                    handlePatientSubmit(patientDetails)
                } else {
                    patientInfoState.value = patientInfoState.value.copy(
                        showError = true,
                        errorField = getPatientErrorField(validation),
                        errorMessage = getPatientValidationErrorMessage(validation)
                    )
                }
            }
            is PatientInfoEvent.DismissClicked -> {
                hidePatientInfoBottomSheet()
            }
        }
    }

    private fun validatePatientInfo(state: PatientInfoState): PatientValidation {
        return when {
            state.name.isBlank() -> PatientValidation.INVALID_NAME
            state.age.isBlank() || state.age.toIntOrNull() == null -> PatientValidation.INVALID_AGE
            state.gender.isBlank() -> PatientValidation.INVALID_GENDER
            state.height.isBlank() || state.height.toDoubleOrNull() == null -> PatientValidation.INVALID_HEIGHT
            state.weight.isBlank() || state.weight.toDoubleOrNull() == null -> PatientValidation.INVALID_WEIGHT
            else -> PatientValidation.VALID
        }
    }

    private fun getPatientErrorField(validation: PatientValidation): PatientFieldError {
        return when (validation) {
            PatientValidation.INVALID_NAME -> PatientFieldError.NAME
            PatientValidation.INVALID_AGE -> PatientFieldError.AGE
            PatientValidation.INVALID_GENDER -> PatientFieldError.GENDER
            PatientValidation.INVALID_HEIGHT -> PatientFieldError.HEIGHT
            PatientValidation.INVALID_WEIGHT -> PatientFieldError.WEIGHT
            PatientValidation.VALID -> PatientFieldError.NONE
        }
    }

    private fun getPatientValidationErrorMessage(validation: PatientValidation): String {
        return when (validation) {
            PatientValidation.INVALID_NAME -> "Please enter patient name"
            PatientValidation.INVALID_AGE -> "Please enter a valid age"
            PatientValidation.INVALID_GENDER -> "Please select gender"
            PatientValidation.INVALID_HEIGHT -> "Please enter valid height"
            PatientValidation.INVALID_WEIGHT -> "Please enter valid weight"
            PatientValidation.VALID -> ""
        }
    }

    private fun createPatientSubjectDetails(state: PatientInfoState): Model.SubjectDetails {
        // Convert height to cm if needed
        val heightInCm = if (state.heightUnit == "ft") {
            state.height.toDoubleOrNull()?.times(30.48) ?: 0.0
        } else {
            state.height.toDoubleOrNull() ?: 0.0
        }

        // Convert weight to kg if needed
        val weightInKg = if (state.weightUnit == "lb") {
            state.weight.toDoubleOrNull()?.times(0.453592) ?: 0.0
        } else {
            state.weight.toDoubleOrNull() ?: 0.0
        }

        val sex = when (state.gender.lowercase()) {
            "male" -> Sex.MALE
            "female" -> Sex.FEMALE
            else -> Sex.UNSPECIFIED
        }

        val smokingStatus = when (state.isSmoker) {
            true -> SmokingStatus.SMOKER
            false -> SmokingStatus.NON_SMOKER
            null -> SmokingStatus.UNSPECIFIED
        }

        return Model.SubjectDetails(
            name = state.name,
            sex = sex,
            age = state.age.toDoubleOrNull() ?: 0.0,
            weight = weightInKg,
            height = heightInCm,
            heightUnit = "cm",
            weightUnit = "kg",
            isSmoker = smokingStatus
        )
    }

    private fun handlePatientSubmit(patientDetails: Model.SubjectDetails) {
        // Store temporarily (NOT in PreferenceManager)
        tempPatientSubject = patientDetails

        // Terminate existing session first
        session?.terminate()
        session = null

        // Use a handler to give the SDK time to clean up, then create new session
        Handler(Looper.getMainLooper()).postDelayed({
            createSessionWithPatient(patientDetails)
        }, 500)
    }

    private fun createSessionWithPatient(patientDetails: Model.SubjectDetails) {
        try {
            // Create new session with patient data
            val key = PreferenceManager.Key
            val licenseDetails = LicenseDetails(key)

            val sex = when(patientDetails.sex) {
                Sex.MALE -> Sex.MALE
                Sex.FEMALE -> Sex.FEMALE
                else -> Sex.UNSPECIFIED
            }

            val userInformation = UserInformation.Builder()
                .setSex(sex)
                .setAge(patientDetails.age ?: 0.0)
                .setWeight(patientDetails.weight ?: 0.0)
                .setHeight(patientDetails.height ?: 0.0)
                .setSmokingStatus(patientDetails.isSmoker)
                .build()

            session = FaceSessionBuilder(applicationContext).apply {
                withUserInformation(userInformation)
                withImageListener(this@VitalScanActivity)
                withDetectionAlwaysOn(true)
                withVitalSignsListener(this@VitalScanActivity)
                withSessionInfoListener(this@VitalScanActivity)
            }.run { build(licenseDetails) }

            // After session is created successfully, show the dialog to start scan
            showHowToScanDialogAndStartScan()

        } catch (e: HealthMonitorException) {
            showError(e.errorCode)
        }
    }

    private fun showHowToScanDialogAndStartScan() {
        val dialog = DialogHowToScan(this@VitalScanActivity, object : DialogHowToScan.OnClick {
            override fun onDismiss() {
                try {
                    binding.measurementsLayout.tvScanningMsg.visibility = View.VISIBLE
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

    private fun resetSessionToUserData() {
        // Clear temporary patient data
        tempPatientSubject = null

        // Reset to use user's own SubjectDetails from PreferenceManager
        subject = PreferenceManager.subjectDetails

        // Terminate current session
        session?.terminate()
        session = null

        // Use a handler to give the SDK time to clean up, then recreate session
        Handler(Looper.getMainLooper()).postDelayed({
            createSession()
        }, 500)
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

    private fun openMyResetApp() {
        val userEmail = PreferenceManager.authUser?.email ?: ""
        val deepLinkUrl = "https://deeplink.myreset.zone?email=$userEmail"
        val packageName = "com.unipharma.myreset"
        val appInstalled = isAppInstalled(packageName)

        if (appInstalled) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUrl))
            startActivity(intent)
        }else{
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
            startActivity(playStoreIntent)
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}