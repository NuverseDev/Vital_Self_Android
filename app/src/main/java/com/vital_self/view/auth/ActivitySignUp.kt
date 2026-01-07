package com.vital_self.view.auth

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
import com.vital_self.enums.RegisterEnum
import com.vital_self.model.AuthResponse
import com.vital_self.model.SignupRequest
import com.vital_self.network.Status
import com.vital_self.repository.AuthRepository
import com.vital_self.repository.factory.AuthFactory
import com.vital_self.ui.components.common.LoadingDialog
import com.vital_self.ui.screens.auth.signup.SignUpScreenContent
import com.vital_self.ui.screens.auth.signup.SignUpScreenEvent
import com.vital_self.ui.screens.auth.signup.SignUpScreenState
import com.vital_self.ui.theme.VitalSelfTheme
import com.vital_self.utils.AlertDialogManager
import com.vital_self.utils.AnimationsHandler
import com.vital_self.utils.DialogClickListener
import com.vital_self.utils.Pref
import com.vital_self.view.scan.VitalScanActivity
import com.vital_self.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class ActivitySignUp : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthFactory(AuthRepository())
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

        setContent {
            VitalSelfTheme(darkTheme = false) {
                var screenState by remember { mutableStateOf(SignUpScreenState()) }

                val signupData by authViewModel.signupData.observeAsState()

                // Handle Signup API response
                LaunchedEffect(signupData) {
                    when (signupData?.status) {
                        Status.LOADING -> {
                            screenState = screenState.copy(isLoading = true)
                        }
                        Status.SUCCESS -> {
                            screenState = screenState.copy(isLoading = false)
                            handleSignupSuccess(signupData?.data)
                        }
                        Status.ERROR -> {
                            screenState = screenState.copy(isLoading = false)
                            showErrorDialog(signupData?.message ?: "Signup failed")
                        }
                        else -> {}
                    }
                }

                SignUpScreenContent(
                    state = screenState,
                    onEvent = { event ->
                        when (event) {
                            is SignUpScreenEvent.UsernameChanged -> {
                                screenState = screenState.copy(
                                    username = event.value,
                                    showError = false
                                )
                            }
                            is SignUpScreenEvent.EmailChanged -> {
                                screenState = screenState.copy(
                                    email = event.value,
                                    showError = false
                                )
                            }
                            is SignUpScreenEvent.PasswordChanged -> {
                                screenState = screenState.copy(
                                    password = event.value,
                                    showError = false
                                )
                            }
                            SignUpScreenEvent.SignUpClicked -> {
                                val error = validateRegister(
                                    screenState.username,
                                    screenState.email,
                                    screenState.password
                                )
                                when (error) {
                                    RegisterEnum.VALID -> {
                                        val request = SignupRequest(
                                            name = screenState.username,
                                            email = screenState.email,
                                            password = screenState.password
                                        )
                                        Log.d("TAG", "listeners: register req $request")
                                        authViewModel.signupUser(request, this)
                                    }
                                    else -> {
                                        screenState = screenState.copy(showError = true)
                                    }
                                }
                            }
                            SignUpScreenEvent.BackClicked -> {
                                onBackPressedDispatcher.onBackPressed()
                            }
                            SignUpScreenEvent.PrivacyPolicyClicked -> {
                                // TODO: Open privacy policy
                            }
                            SignUpScreenEvent.TermsClicked -> {
                                // TODO: Open terms and conditions
                            }
                        }
                    }
                )

                // Loading Dialog
                LoadingDialog(isVisible = screenState.isLoading)
            }
        }
    }

    private fun validateRegister(
        username: String,
        email: String,
        password: String
    ): RegisterEnum {
        return when {
            username.isEmpty() || username.isBlank() -> RegisterEnum.INVALID_USERNAME
            email.isEmpty() || email.isBlank() -> RegisterEnum.INVALID_EMAIL
            password.isEmpty() || password.isBlank() -> RegisterEnum.INVALID_PASSWORD
            else -> RegisterEnum.VALID
        }
    }

    private fun handleSignupSuccess(data: AuthResponse?) {
        try {
            if (data?.status == true && data.data != null) {
                Pref.authUser = data.data.user
                Pref.authToken = data.data.token
                Pref.isLoggedIn = true
                Pref.isFirstTime = true
                VitalScanActivity.startActivity(this)
            } else {
                showErrorDialog(data?.message ?: "Signup failed")
            }
        } catch (e: Exception) {
            Log.d("TAG", "handleSignupSuccess catch: ${e.message}")
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
}
