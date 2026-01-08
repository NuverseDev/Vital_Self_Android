package com.vital_self.features.auth.presentation.signup

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.R
import com.vital_self.core.ui.components.buttons.PrimaryButton
import com.vital_self.core.ui.components.common.ErrorMessage
import com.vital_self.core.ui.components.textfields.VitalTextField
import com.vital_self.core.ui.theme.DefaultBtnBgColor
import com.vital_self.core.ui.theme.InterFamily
import com.vital_self.core.ui.theme.PrimaryBackgroundColor
import com.vital_self.core.ui.theme.PrimaryTextColor
import com.vital_self.core.ui.theme.SecondaryFrontColor
import com.vital_self.core.ui.theme.SecondaryThemeTextColor
import com.vital_self.core.ui.theme.ThemeBtnClickedColor
import kotlinx.coroutines.delay

data class SignUpScreenState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val errorMessage: String = "Please fill in all required fields correctly."
)

sealed class SignUpScreenEvent {
    data class UsernameChanged(val value: String) : SignUpScreenEvent()
    data class EmailChanged(val value: String) : SignUpScreenEvent()
    data class PasswordChanged(val value: String) : SignUpScreenEvent()
    data object SignUpClicked : SignUpScreenEvent()
    data object BackClicked : SignUpScreenEvent()
    data object PrivacyPolicyClicked : SignUpScreenEvent()
    data object TermsClicked : SignUpScreenEvent()
}

@Composable
fun SignUpScreenContent(
    state: SignUpScreenState,
    onEvent: (SignUpScreenEvent) -> Unit,
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
        AnimatedSignUpToolbar(
            startAnimations = startAnimations,
            onBackClick = { onEvent(SignUpScreenEvent.BackClicked) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Animated Header
            AnimatedSignUpHeader(startAnimations = startAnimations)

            Spacer(modifier = Modifier.height(8.dp))

            // Animated Subtitle
            AnimatedSignUpSubtitle(startAnimations = startAnimations)

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Form Fields
            AnimatedSignUpFormFields(
                state = state,
                onEvent = onEvent,
                startAnimations = startAnimations
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Sign Up Button
            AnimatedSignUpButton(
                startAnimations = startAnimations,
                isLoading = state.isLoading,
                onClick = { onEvent(SignUpScreenEvent.SignUpClicked) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Illustration
            AnimatedSignUpIllustration(startAnimations = startAnimations)

            Spacer(modifier = Modifier.weight(1f))

            // Animated Terms Row
            AnimatedTermsRow(
                startAnimations = startAnimations,
                onPrivacyClick = { onEvent(SignUpScreenEvent.PrivacyPolicyClicked) },
                onTermsClick = { onEvent(SignUpScreenEvent.TermsClicked) }
            )
        }
    }
}

@Composable
private fun AnimatedSignUpToolbar(
    startAnimations: Boolean,
    onBackClick: () -> Unit
) {
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
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .alpha(alpha)
            .offset(y = offsetY),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                contentDescription = "Back",
                tint = PrimaryTextColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.ic_logo_vital_self),
            contentDescription = "VitalSelf Logo",
            modifier = Modifier.height(28.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun AnimatedSignUpHeader(startAnimations: Boolean) {
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
        text = "Create Account",
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
private fun AnimatedSignUpSubtitle(startAnimations: Boolean) {
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
        text = "Join VitalSelf to start monitoring your health",
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
private fun AnimatedSignUpFormFields(
    state: SignUpScreenState,
    onEvent: (SignUpScreenEvent) -> Unit,
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
            onValueChange = { onEvent(SignUpScreenEvent.UsernameChanged(it)) },
            hint = "Choose a username",
            leadingIcon = painterResource(id = R.drawable.ic_person),
            imeAction = ImeAction.Next,
            isError = state.showError
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Email Field
    val emailAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "emailAlpha"
    )
    val emailOffsetX by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else (-30).dp,
        animationSpec = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "emailOffset"
    )

    Column(
        modifier = Modifier
            .alpha(emailAlpha)
            .offset(x = emailOffsetX)
    ) {
        Text(
            text = "Email Address",
            color = SecondaryFrontColor,
            fontSize = 14.sp,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        VitalTextField(
            value = state.email,
            onValueChange = { onEvent(SignUpScreenEvent.EmailChanged(it)) },
            hint = "Enter your email address",
            leadingIcon = painterResource(id = R.drawable.ic_email),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            isError = state.showError
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Password Field
    val passwordAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "passwordAlpha"
    )
    val passwordOffsetX by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else (-30).dp,
        animationSpec = tween(600, delayMillis = 500, easing = FastOutSlowInEasing),
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
            onValueChange = { onEvent(SignUpScreenEvent.PasswordChanged(it)) },
            hint = "Create a secure password",
            leadingIcon = painterResource(id = R.drawable.ic_password),
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = { onEvent(SignUpScreenEvent.SignUpClicked) },
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
private fun AnimatedSignUpButton(
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
            text = "Get Started",
            onClick = onClick,
            enabled = !isLoading
        )
    }
}

@Composable
private fun AnimatedSignUpIllustration(startAnimations: Boolean) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(800, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "illustrationAlpha"
    )

    Image(
        painter = painterResource(id = R.drawable.ic_illustration_login),
        contentDescription = "Sign Up Illustration",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = 12.dp)
            .alpha(alpha)
    )
}

@Composable
private fun AnimatedTermsRow(
    startAnimations: Boolean,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 800, easing = FastOutSlowInEasing),
        label = "termsAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "By signing up, you agree to our",
            color = SecondaryThemeTextColor,
            fontSize = 12.sp,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Privacy Policy",
                color = ThemeBtnClickedColor,
                fontSize = 12.sp,
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onPrivacyClick() }
            )
            Text(
                text = " and ",
                color = SecondaryThemeTextColor,
                fontSize = 12.sp,
                fontFamily = InterFamily
            )
            Text(
                text = "Terms of Service",
                color = ThemeBtnClickedColor,
                fontSize = 12.sp,
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onTermsClick() }
            )
        }
    }
}
