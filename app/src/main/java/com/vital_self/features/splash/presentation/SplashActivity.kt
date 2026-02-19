package com.vital_self.features.splash.presentation

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.vital_self.BuildConfig
import com.vital_self.core.data.remote.model.CheckVersionRequest
import com.vital_self.core.data.remote.model.Status
import com.vital_self.features.auth.data.repository.AuthRepository
import com.vital_self.features.auth.presentation.viewmodel.AuthViewModelFactory
import com.vital_self.features.splash.presentation.SplashScreenContent
import com.vital_self.core.ui.theme.VitalSelfTheme
import com.vital_self.core.utils.constants.AppConstants
import com.vital_self.core.utils.helpers.AnimationsHandler
import com.vital_self.core.data.local.preferences.PreferenceManager
import com.vital_self.features.auth.presentation.login.ActivityUserLogin
import com.vital_self.features.onboarding.presentation.HowToUseActivity
import com.vital_self.features.scan.presentation.scan.VitalScanActivity
import com.vital_self.features.auth.presentation.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class SplashActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository())
    }

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ActivityUserLogin::class.java).apply {
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

        intent?.data?.let { uri ->
            if (uri.scheme == "vitalself") {
                val email = uri.getQueryParameter("email")
                //
                Log.d("TAG", "onCreate: recieve email $email ")
            }
        }


        // Check app version on startup
        val checkAppVersion = CheckVersionRequest(
            platform = AppConstants.PLATFORM,
            app_version = BuildConfig.VERSION_NAME,
            app_name = AppConstants.APP_NAME
        )
        authViewModel.checkAppVersion(checkAppVersion, this)

        setContent {
            VitalSelfTheme(darkTheme = true) {
                val versionCheckState by authViewModel.checkAppVersion.observeAsState()
                Log.d("TAG", "onCreate: version check $versionCheckState ")
                // Handle version check response
                LaunchedEffect(versionCheckState) {
                    when (versionCheckState?.status) {
                        Status.SUCCESS -> {
                            PreferenceManager.appVersionMatch = versionCheckState?.data?.data?.isMatch == true
                        }
                        else -> { /* Handle other states */ }
                    }
                }

                SplashScreenContent(
                    versionName = BuildConfig.VERSION_NAME,
                    onSplashComplete = {
                        navigateToNextScreen()
                    }
                )
            }
        }
    }

    private fun navigateToNextScreen() {
        if (PreferenceManager.isLoggedIn) {
            VitalScanActivity.startActivity(this)
        } else if (PreferenceManager.isFreshInstalled) {
            HowToUseActivity.startActivity(this, fromOnboarding = true)
        } else {
            ActivityUserLogin.startActivity(this)
        }
        finish()
    }
}
