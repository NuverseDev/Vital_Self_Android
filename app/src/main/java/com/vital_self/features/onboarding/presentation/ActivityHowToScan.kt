package com.vital_self.features.onboarding.presentation

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.vital_self.R
import com.vital_self.core.ui.theme.VitalSelfTheme
import com.vital_self.core.utils.helpers.AnimationsHandler

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ActivityHowToScan : ComponentActivity() {

    companion object {
        const val TITLE = "title"
        fun startActivity(activity: Activity, title: String) {
            Intent(activity, ActivityHowToScan::class.java).apply {
                putExtra(TITLE, title)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private val title by lazy { intent.getStringExtra(TITLE) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        setContent {
            VitalSelfTheme(darkTheme = false) {
                HowToScanScreen(
                    title = title,
                    onBackClick = { finish() }
                )
            }
        }
    }
}
