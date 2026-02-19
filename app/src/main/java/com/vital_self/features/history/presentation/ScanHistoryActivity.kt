package com.vital_self.features.history.presentation

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vital_self.core.data.local.preferences.PreferenceManager
import com.vital_self.core.ui.theme.VitalSelfTheme
import com.vital_self.core.utils.helpers.AnimationsHandler
import com.vital_self.features.history.presentation.screen.ScanHistoryScreenContent
import com.vital_self.features.history.presentation.viewmodel.HistoryViewModel
import com.vital_self.features.scan.presentation.result.VitalResultActivity2

class ScanHistoryActivity : ComponentActivity() {

    private val historyViewModel: HistoryViewModel by viewModels()

    companion object {
        private const val EXTRA_TITLE = "extra_title"

        fun startActivity(activity: Activity, title: String) {
            Intent(activity, ScanHistoryActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private val title by lazy {
        intent.getStringExtra(EXTRA_TITLE) ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VitalSelfTheme {
                val state by historyViewModel.screenState.collectAsState()
                val userName = PreferenceManager.authUser?.name ?: ""

                ScanHistoryScreenContent(
                    title = title,
                    userName = userName,
                    state = state,
                    onEvent = historyViewModel::onEvent,
                    onNavigateBack = {
                        finish()
                        AnimationsHandler.playActivityAnimation(
                            this, AnimationsHandler.Animations.LeftToRight
                        )
                    },
                    onNavigateToResult = { scanId ->
                        VitalResultActivity2.startActivityWithScanId(this, scanId)
                    }
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        AnimationsHandler.playActivityAnimation(this, AnimationsHandler.Animations.LeftToRight)
    }
}
