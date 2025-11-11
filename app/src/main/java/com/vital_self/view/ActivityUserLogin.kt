package com.vital_self.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vital_self.R
import com.vital_self.base.BaseActivity
import com.vital_self.databinding.ActivityUserLoginBinding
import com.vital_self.model.UserLoginRequest
import com.vital_self.network.NetworkErrorCode
import com.vital_self.network.Status
import com.vital_self.repository.ScanRepository
import com.vital_self.utils.AnimationsHandler
import com.vital_self.utils.DialogClickListener
import com.vital_self.repository.factory.ScanViewModelFactory
import com.vital_self.utils.AlertDialogManager
import com.vital_self.viewmodel.ScanViewModel
import com.vital_self.utils.Pref
import com.vital_self.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ActivityUserLogin :  BaseActivity(){

    lateinit var binding: ActivityUserLoginBinding

    lateinit var loginViewModel: LoginViewModel


    private val viewModel: ScanViewModel by viewModels {
        ScanViewModelFactory(ScanRepository())
    }

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ActivityUserLogin::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        initUi()
        observer()
        listners()

    }

    private fun initUi(){
        binding.tool.btnBack.visibility = View.GONE
        binding.buttonNext.apply {
            this.isEnabled = true
            setBackgroundResource(R.drawable.bg_primary_button)
        }
    }

    private fun listners() {

        binding.etUsername.doAfterTextChanged {
            showHideError(binding.tvCredError,false)
        }
        binding.etPasword.doAfterTextChanged {
            showHideError(binding.tvCredError,false)
        }

        binding.buttonNext.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPasword.text.toString()
            val error = validateLogin(username,password)

            when (error) {
                USERLOGIN.INVALID_USERNAME -> {
                    showHideError(binding.tvCredError, true)
                    return@setOnClickListener
                }
                USERLOGIN.INVALID_PASSWORD -> {
                    showHideError(binding.tvCredError, true)
                    return@setOnClickListener
                }
                USERLOGIN.VALID -> {
                    showHideError(binding.tvCredError, false)
                    val userRequest = UserLoginRequest(userName = username, password = password)
                    Log.d("TAG", "listners: login req $userRequest")
                    viewModel.getUser(userRequest,this)

                }
            }
        }
    }

    private fun showHideError(view: TextView, isErrorEnable: Boolean) {
        if (isErrorEnable) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


    private fun observer(){

        lifecycleScope.launch {
            viewModel.userData.observe(this@ActivityUserLogin, Observer {
                when (it?.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        Log.d("TAG", "observer: getuser ")
                        showHideProgress(false)
                        try {
                            if (it.data?.data?.availableScan!! > 0 && it.data.data.status == true){
                                Pref.user = it.data.data
                                Pref.isLoggedIn = true
                                Pref.isFirstTime = true
                                VitalScanActivity.startActivity(this@ActivityUserLogin)
                            }else{
                                AlertDialogManager.showConfirmationDialog(this@ActivityUserLogin,
                                    title = getString(R.string.license_expired),
                                    message = getString(R.string.license_expired_message),
                                    buttonMessage = getString(R.string.ok),
                                    cancelable = true,
                                    isDissable = true,
                                    dialogClickListener = object : DialogClickListener {
                                        override fun onButton1Clicked() {

                                        }
                                    })
                            }
                        }catch (e: Exception){
                            Log.d("TAG", "getUserById:catch ")
                            Toast.makeText(this@ActivityUserLogin,e.message, Toast.LENGTH_LONG).show()
                        }
                    }

                    Status.ERROR -> {
                        Log.d("TAG", "getUserById:error ")
                        showHideProgress(false)
                        AlertDialogManager.showConfirmationDialog(this@ActivityUserLogin,
                            title = getString(R.string.error),
                            message = NetworkErrorCode.getNetworkError(it.code,null),
                            buttonMessage = getString(R.string.ok),
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

    private fun validateLogin(
        username: String,
        password:String,
    ): USERLOGIN {

        if (username.isEmpty() || username.isBlank()) {
            return USERLOGIN.INVALID_USERNAME
        } else if (password.isEmpty() || password.isBlank()) {
            return USERLOGIN.INVALID_PASSWORD
        } else {
            return USERLOGIN.VALID
        }
    }
}

enum class USERLOGIN {
    INVALID_USERNAME,
    INVALID_PASSWORD,
    VALID
}

