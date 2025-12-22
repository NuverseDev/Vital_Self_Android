package com.vital_self.view.auth

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.vital_self.R
import com.vital_self.base.BaseActivity
import com.vital_self.databinding.ActivitySignUpBinding
import com.vital_self.repository.ScanRepository
import com.vital_self.repository.factory.ScanViewModelFactory
import com.vital_self.utils.AnimationsHandler
import com.vital_self.viewmodel.ScanViewModel
import kotlin.getValue

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ActivitySignUp :  BaseActivity(){

    lateinit var binding: ActivitySignUpBinding

    private val viewModel: ScanViewModel by viewModels {
        ScanViewModelFactory(ScanRepository())
    }

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ActivitySignUp::class.java).apply {
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        binding.tool.btnBack.setOnClickListener {
            this.onBackPressed()
        }

    }
}