package com.vitalself.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.vitalself.R
import com.vitalself.base.BaseActivity
import com.vitalself.databinding.ActivityBestPracticesBinding
import com.vitalself.utils.AnimationsHandler

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ActivityHowToScan :  BaseActivity(){

    lateinit var binding: ActivityBestPracticesBinding

    companion object {
        const val TITLE = "title"
        fun startActivity(activity: Activity,title:String) {
            Intent(activity, ActivityHowToScan::class.java).apply {
                putExtra(TITLE,title)
            }.run {
                activity.startActivity(this)
                AnimationsHandler.playActivityAnimation(
                    activity, AnimationsHandler.Animations.RightToLeft
                )
            }
        }
    }

    private val title by lazy { intent.getStringExtra(TITLE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_best_practices)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        initUi()
    }

    private fun initUi(){
        binding.tool.tvTitle.text = title
        binding.tool.btnBack.setOnClickListener {
            finish()
        }
    }




}



