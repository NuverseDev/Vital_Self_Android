package com.vitalself.view

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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.vitalself.R
import com.vitalself.base.BaseActivity
import com.vitalself.databinding.SplashScreenActivityBinding
import com.vitalself.utils.AnimationsHandler
import com.vitalself.utils.Pref

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class SplashScreen :  BaseActivity(){

    lateinit var binding: SplashScreenActivityBinding

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

    }

    private fun initUi(){

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



