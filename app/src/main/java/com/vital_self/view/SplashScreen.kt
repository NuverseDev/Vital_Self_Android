package com.vital_self.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.vital_self.BuildConfig
import com.vital_self.model.CheckVersionRequest
import com.vital_self.network.Status
import com.vital_self.repository.AuthRepository
import com.vital_self.repository.factory.AuthFactory
import com.vital_self.ui.screens.splash.SplashScreenContent
import com.vital_self.ui.theme.VitalSelfTheme
import com.vital_self.utils.AnimationsHandler
import com.vital_self.utils.Pref
import com.vital_self.view.auth.ActivityUserLogin
import com.vital_self.view.scan.VitalScanActivity
import com.vital_self.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class SplashScreen : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthFactory(AuthRepository())
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

        // Check app version on startup
        val checkAppVersion = CheckVersionRequest(
            platform = Constant.PLATFORM,
            app_version = BuildConfig.VERSION_NAME,
            app_name = Constant.APP_NAME
        )
        authViewModel.checkAppVersion(checkAppVersion, this)
        Pref.isFirstTime = Pref.subjectDetails == null

        setContent {
            VitalSelfTheme(darkTheme = true) {
                val versionCheckState by authViewModel.checkAppVersion.observeAsState()

                // Handle version check response
                LaunchedEffect(versionCheckState) {
                    when (versionCheckState?.status) {
                        Status.SUCCESS -> {
                            Pref.appVersionMatch = versionCheckState?.data?.version_match == true
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
        if (Pref.isLoggedIn) {
            VitalScanActivity.startActivity(this)
        } else {
            ActivityUserLogin.startActivity(this)
        }
        finish()
    }
}
