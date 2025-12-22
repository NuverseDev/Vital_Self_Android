package com.vital_self.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.vital_self.R
import com.vital_self.base.BaseActivity
import com.vital_self.databinding.SplashScreenActivityBinding
import com.vital_self.model.CheckVersionRequest
import com.vital_self.model.Model
import com.vital_self.network.Status
import com.vital_self.repository.AuthRepository
import com.vital_self.repository.factory.AuthFactory
import com.vital_self.utils.AnimationsHandler
import com.vital_self.utils.Pref
import com.vital_self.view.auth.ActivityUserLogin
import com.vital_self.view.scan.VitalScanActivity
import com.vital_self.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class SplashScreen :  BaseActivity(){

    lateinit var binding: SplashScreenActivityBinding

    private val authViewModel: AuthViewModel by viewModels {
        AuthFactory(AuthRepository())
    }

    private var subject : Model.SubjectDetails? = null

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
        binding = DataBindingUtil.setContentView(this, R.layout.splash_screen_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        animateLogoWithObjectAnimator()

        subject = Pref.subjectDetails

        val checkAppVersion = CheckVersionRequest(
            platform = Constant.PLATFORM,
            app_version = com.vital_self.BuildConfig.APP_VERSION,
            app_name =  Constant.APP_NAME
        )
        authViewModel.checkAppVersion(checkAppVersion,this)
        Pref.isFirstTime = if (Pref.subjectDetails != null) false else true
        observer()

    }

    private fun observer(){
        lifecycleScope.launch {
            authViewModel.checkAppVersion.observe(this@SplashScreen, Observer {
                when (it?.status) {
                    Status.LOADING -> {
//                            showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        try {
                            if (it.data?.version_match == true){
                                Pref.appVersionMatch = true
                            }else{
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

    }

    private fun animateLogoWithObjectAnimator() {
        val logoImage = binding.icLogo

        // First make the logo invisible
        logoImage.alpha = 0f

        // Create scale X animation
        val scaleX = ObjectAnimator.ofFloat(logoImage, View.SCALE_X, 0.6f, 1.1f, 1.0f)
        scaleX.duration = 1500

        // Create scale Y animation
        val scaleY = ObjectAnimator.ofFloat(logoImage, View.SCALE_Y, 0.6f, 1.1f, 1.0f)
        scaleY.duration = 1500

        // Create fade in animation
        val alpha = ObjectAnimator.ofFloat(logoImage, View.ALPHA, 0f, 1f)
        alpha.duration = 1000

        // Play all animations together
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, alpha)
        animatorSet.start()

        Handler(Looper.getMainLooper()).postDelayed({
            if (Pref.isLoggedIn) {
                VitalScanActivity.startActivity(this)
            } else {
                ActivityUserLogin.startActivity(this)
            }
        }, 1800)
    }

}



