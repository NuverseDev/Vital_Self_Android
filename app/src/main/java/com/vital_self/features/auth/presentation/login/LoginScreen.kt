package com.vital_self.features.auth.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.R
import com.vital_self.core.ui.components.buttons.PrimaryButton
import com.vital_self.core.ui.components.common.ErrorMessage
import com.vital_self.core.ui.components.textfields.VitalTextField
import com.vital_self.core.ui.theme.InterFamily
import com.vital_self.core.ui.theme.PrimaryBackgroundColor
import com.vital_self.core.ui.theme.SecondaryFrontColor
import com.vital_self.core.ui.theme.SecondaryThemeTextColor
import com.vital_self.core.ui.theme.TextBgColor
import com.vital_self.core.ui.theme.ThemeBtnClickedColor

data class LoginScreenState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val errorMessage: String = "Invalid credentials. Please verify your username and password."
)

sealed class LoginScreenEvent {
    data class UsernameChanged(val value: String) : LoginScreenEvent()
    data class PasswordChanged(val value: String) : LoginScreenEvent()
    data object LoginClicked : LoginScreenEvent()
    data object ForgetPasswordClicked : LoginScreenEvent()
    data object SignUpClicked : LoginScreenEvent()
    data object DismissError : LoginScreenEvent()
}

@Composable
@Preview
fun LoginScreenPreview(){
    LoginScreenContent(state = LoginScreenState(
        username = "",
        password = "",
        isLoading = false,
        showError = false,
    ), onEvent = {
        when(it){
            LoginScreenEvent.DismissError -> {

            }
            else -> {

            }
        }
    })
}

@Composable
fun LoginScreenContent(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Toolbar
        Toolbar()

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Header()

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Subtitle()

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            FormFields(
                state = state,
                onEvent = onEvent
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Forget Password
            ForgetPassword(
                onClick = { onEvent(LoginScreenEvent.ForgetPasswordClicked) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button
            LoginButton(
                isLoading = state.isLoading,
                onClick = { onEvent(LoginScreenEvent.LoginClicked) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Illustration
            Illustration()

            Spacer(modifier = Modifier.weight(1f))

            // Sign Up Row
            SignUpRow(
                onClick = { onEvent(LoginScreenEvent.SignUpClicked) }
            )
        }
    }
}


@Composable
private fun Toolbar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBackgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_vital_self),
            contentDescription = "VitalSelf Logo",
            modifier = Modifier.height(32.dp)
        )
    }
}

@Preview
@Composable
private fun ToolbarPreview() {
    Toolbar()
}


@Composable
private fun Header() {
    Text(
        text = "Welcome Back",
        color = SecondaryFrontColor,
        fontSize = 28.sp,
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun Subtitle() {
    Text(
        text = "Sign in to continue your health journey",
        color = SecondaryThemeTextColor,
        fontSize = 14.sp,
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FormFields(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit
) {
    // Username Field
    Column {
        Text(
            text = "Username",
            color = SecondaryFrontColor,
            fontSize = 14.sp,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        VitalTextField(
            value = state.username,
            onValueChange = { onEvent(LoginScreenEvent.UsernameChanged(it)) },
            hint = "Enter your username",
            leadingIcon = painterResource(id = R.drawable.ic_person),
            imeAction = ImeAction.Next,
            isError = state.showError
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Password Field
    Column {
        Text(
            text = "Password",
            color = SecondaryFrontColor,
            fontSize = 14.sp,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        VitalTextField(
            value = state.password,
            onValueChange = { onEvent(LoginScreenEvent.PasswordChanged(it)) },
            hint = "Enter your password",
            leadingIcon = painterResource(id = R.drawable.ic_password),
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = { onEvent(LoginScreenEvent.LoginClicked) },
            isError = state.showError
        )
    }

    // Error Message
    ErrorMessage(
        message = state.errorMessage,
        isVisible = state.showError,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
    )
}

@Composable
private fun ForgetPassword(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "Forgot Password?",
            color = ThemeBtnClickedColor,
            fontSize = 14.sp,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    PrimaryButton(
        text = "Sign In",
        onClick = onClick,
        enabled = !isLoading
    )
}

@Composable
private fun Illustration() {
    Image(
        painter = painterResource(id = R.drawable.ic_illustration_login),
        contentDescription = "Login Illustration",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 12.dp)
    )
}

@Composable
private fun SignUpRow(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "New to VitalSelf? ",
            color = TextBgColor,
            fontSize = 14.sp,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = "Create Account",
            color = ThemeBtnClickedColor,
            fontSize = 14.sp,
            fontFamily = InterFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onClick() }
        )
    }
}
