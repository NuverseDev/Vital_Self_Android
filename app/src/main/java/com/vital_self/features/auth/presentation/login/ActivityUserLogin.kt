package com.vital_self.features.auth.presentation.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.vital_self.R
import com.vital_self.features.auth.data.model.LoginEnum
import com.vital_self.features.auth.data.model.AuthResponse
import com.vital_self.features.auth.data.model.ForgotPasswordRequest
import com.vital_self.features.auth.data.model.LoginRequest
import com.vital_self.core.data.remote.model.NetworkErrorCode
import com.vital_self.core.data.remote.model.Status
import com.vital_self.features.auth.data.repository.AuthRepository
import com.vital_self.features.auth.presentation.viewmodel.AuthViewModelFactory
import com.vital_self.core.ui.components.dialogs.LoadingDialog
import com.vital_self.features.auth.presentation.dialogs.ForgetPasswordDialog
import com.vital_self.features.auth.presentation.login.LoginScreenContent
import com.vital_self.features.auth.presentation.login.LoginScreenEvent
import com.vital_self.features.auth.presentation.login.LoginScreenState
import com.vital_self.core.ui.theme.VitalSelfTheme
import com.vital_self.core.utils.helpers.AlertDialogManager
import com.vital_self.core.utils.helpers.AnimationsHandler
import com.vital_self.core.utils.helpers.DialogClickListener
import com.vital_self.core.data.local.preferences.PreferenceManager
import com.vital_self.features.scan.presentation.scan.VitalScanActivity
import com.vital_self.features.auth.presentation.viewmodel.AuthViewModel
import com.vital_self.features.auth.presentation.signup.ActivitySignUp

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ActivityUserLogin : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository())
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

        setContent {
            VitalSelfTheme(darkTheme = false) {
                var screenState by remember { mutableStateOf(LoginScreenState()) }
                var showForgetPasswordDialog by remember { mutableStateOf(false) }

                val loginData by authViewModel.loginData.observeAsState()
                val forgotPasswordData by authViewModel.forgotPasswordData.observeAsState()

                // Handle Login API response
                LaunchedEffect(loginData) {
                    when (loginData?.status) {
                        Status.LOADING -> {
                            screenState = screenState.copy(isLoading = true)
                        }
                        Status.SUCCESS -> {
                            screenState = screenState.copy(isLoading = false)
                            handleLoginSuccess(loginData?.data)
                        }
                        Status.ERROR -> {
                            screenState = screenState.copy(isLoading = false)
                            showErrorDialog(loginData?.message ?: "Login failed")
                        }
                        else -> {}
                    }
                }

                // Handle Forgot Password API response
                LaunchedEffect(forgotPasswordData) {
                    when (forgotPasswordData?.status) {
                        Status.LOADING -> {
                            screenState = screenState.copy(isLoading = true)
                        }
                        Status.SUCCESS -> {
                            screenState = screenState.copy(isLoading = false)
                            showForgetPasswordDialog = false
                            showSuccessDialog(forgotPasswordData?.data?.message ?: "Password reset link sent")
                            authViewModel.resetForgotPasswordState()
                        }
                        Status.ERROR -> {
                            screenState = screenState.copy(isLoading = false)
                            showErrorDialog(forgotPasswordData?.message ?: "Failed to send reset link")
                            authViewModel.resetForgotPasswordState()
                        }
                        else -> {}
                    }
                }

                LoginScreenContent(
                    state = screenState,
                    onEvent = { event ->
                        when (event) {
                            is LoginScreenEvent.UsernameChanged -> {
                                screenState = screenState.copy(
                                    username = event.value,
                                    showError = false
                                )
                            }
                            is LoginScreenEvent.PasswordChanged -> {
                                screenState = screenState.copy(
                                    password = event.value,
                                    showError = false
                                )
                            }
                            LoginScreenEvent.LoginClicked -> {
                                val error = validateLogin(screenState.username, screenState.password)
                                when (error) {
                                    LoginEnum.VALID -> {
                                        val request = LoginRequest(
                                            email = screenState.username,
                                            password = screenState.password
                                        )
                                        authViewModel.loginUser(request, this)
                                    }
                                    else -> {
                                        screenState = screenState.copy(showError = true)
                                    }
                                }
                            }
                            LoginScreenEvent.ForgetPasswordClicked -> {
                                showForgetPasswordDialog = true
                            }
                            LoginScreenEvent.SignUpClicked -> {
                                ActivitySignUp.startActivity(this)
                            }
                            LoginScreenEvent.DismissError -> {
                                screenState = screenState.copy(showError = false)
                            }
                        }
                    }
                )

                // Loading Dialog
                LoadingDialog(isVisible = screenState.isLoading)

                // Forget Password Dialog
                if (showForgetPasswordDialog) {
                    ForgetPasswordDialog(
                        onDismiss = { showForgetPasswordDialog = false },
                        onSubmit = { email ->
                            Log.d("TAG", "onSubmit: email entered $email")
                            val request = ForgotPasswordRequest(email = email)
                            authViewModel.forgotPassword(request, this)
                        }
                    )
                }
            }
        }
    }

    private fun validateLogin(username: String, password: String): LoginEnum {
        return when {
            username.isEmpty() || username.isBlank() -> LoginEnum.INVALID_USERNAME
            password.isEmpty() || password.isBlank() -> LoginEnum.INVALID_PASSWORD
            else -> LoginEnum.VALID
        }
    }

    private fun handleLoginSuccess(data: AuthResponse?) {
        try {
            if (data?.status == true && data.data != null) {
                PreferenceManager.authUser = data.data.user
                PreferenceManager.authToken = data.data.accessToken
                PreferenceManager.Key = data.data.key
                Log.d("TAG", "token: login ${PreferenceManager.authToken}")
                PreferenceManager.isLoggedIn = true

                VitalScanActivity.startActivity(this)
            } else {
                showErrorDialog(data?.message ?: "Login failed")
            }
        } catch (e: Exception) {
            Log.d("TAG", "handleLoginSuccess catch: ${e.message}")
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialogManager.showConfirmationDialog(
            this,
            title = getString(R.string.error),
            message = message,
            buttonMessage = getString(R.string.ok),
            cancelable = true,
            dialogClickListener = object : DialogClickListener {
                override fun onButton1Clicked() {}
            }
        )
    }

    private fun showSuccessDialog(message: String) {
        AlertDialogManager.showConfirmationDialog(
            this,
            title = "Success",
            message = message,
            buttonMessage = getString(R.string.ok),
            cancelable = true,
            dialogClickListener = object : DialogClickListener {
                override fun onButton1Clicked() {}
            }
        )
    }

}
