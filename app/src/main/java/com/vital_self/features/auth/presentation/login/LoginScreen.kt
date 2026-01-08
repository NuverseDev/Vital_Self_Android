package com.vital_self.features.auth.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.delay

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
fun LoginScreenContent(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimations by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        startAnimations = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Animated Toolbar
        AnimatedToolbar(startAnimations = startAnimations)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Animated Header
            AnimatedHeader(startAnimations = startAnimations)

            Spacer(modifier = Modifier.height(8.dp))

            // Animated Subtitle
            AnimatedSubtitle(startAnimations = startAnimations)

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Form Fields
            AnimatedFormFields(
                state = state,
                onEvent = onEvent,
                startAnimations = startAnimations
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Forget Password
            AnimatedForgetPassword(
                startAnimations = startAnimations,
                onClick = { onEvent(LoginScreenEvent.ForgetPasswordClicked) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Login Button
            AnimatedLoginButton(
                startAnimations = startAnimations,
                isLoading = state.isLoading,
                onClick = { onEvent(LoginScreenEvent.LoginClicked) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Illustration
            AnimatedIllustration(startAnimations = startAnimations)

            Spacer(modifier = Modifier.weight(1f))

            // Animated Sign Up Row
            AnimatedSignUpRow(
                startAnimations = startAnimations,
                onClick = { onEvent(LoginScreenEvent.SignUpClicked) }
            )
        }
    }
}

@Composable
private fun AnimatedToolbar(startAnimations: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "toolbarAlpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else (-20).dp,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "toolbarOffset"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBackgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .alpha(alpha)
            .offset(y = offsetY),
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

@Composable
private fun AnimatedHeader(startAnimations: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(700, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "headerAlpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else 30.dp,
        animationSpec = tween(700, delayMillis = 100, easing = FastOutSlowInEasing),
        label = "headerOffset"
    )

    Text(
        text = "Welcome Back",
        color = SecondaryFrontColor,
        fontSize = 28.sp,
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .offset(y = offsetY)
    )
}

@Composable
private fun AnimatedSubtitle(startAnimations: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(700, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "subtitleAlpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else 20.dp,
        animationSpec = tween(700, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "subtitleOffset"
    )

    Text(
        text = "Sign in to continue your health journey",
        color = SecondaryThemeTextColor,
        fontSize = 14.sp,
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .offset(y = offsetY)
    )
}

@Composable
private fun AnimatedFormFields(
    state: LoginScreenState,
    onEvent: (LoginScreenEvent) -> Unit,
    startAnimations: Boolean
) {
    // Username Field
    val usernameAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "usernameAlpha"
    )
    val usernameOffsetX by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else (-30).dp,
        animationSpec = tween(600, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "usernameOffset"
    )

    Column(
        modifier = Modifier
            .alpha(usernameAlpha)
            .offset(x = usernameOffsetX)
    ) {
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
    val passwordAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "passwordAlpha"
    )
    val passwordOffsetX by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else (-30).dp,
        animationSpec = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "passwordOffset"
    )

    Column(
        modifier = Modifier
            .alpha(passwordAlpha)
            .offset(x = passwordOffsetX)
    ) {
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
private fun AnimatedForgetPassword(
    startAnimations: Boolean,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "forgetAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
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
private fun AnimatedLoginButton(
    startAnimations: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "buttonAlpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else 20.dp,
        animationSpec = tween(600, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "buttonOffset"
    )

    Column(
        modifier = Modifier
            .alpha(alpha)
            .offset(y = offsetY)
    ) {
        PrimaryButton(
            text = "Sign In",
            onClick = onClick,
            enabled = !isLoading
        )
    }
}

@Composable
private fun AnimatedIllustration(startAnimations: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(800, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "illustrationAlpha"
    )

    Image(
        painter = painterResource(id = R.drawable.ic_illustration_login),
        contentDescription = "Login Illustration",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 12.dp)
            .alpha(alpha)
    )
}

@Composable
private fun AnimatedSignUpRow(
    startAnimations: Boolean,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 800, easing = FastOutSlowInEasing),
        label = "signupAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .alpha(alpha),
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
