package com.vital_self.features.auth.presentation.signup

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.vital_self.core.ui.theme.PrimaryTextColor
import com.vital_self.core.ui.theme.SecondaryFrontColor
import com.vital_self.core.ui.theme.SecondaryThemeTextColor
import com.vital_self.core.ui.theme.ThemeBtnClickedColor

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

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreenContent(
        state = SignUpScreenState(),
        onEvent = {},
        modifier = Modifier
    )
}

@Composable
fun SignUpScreenContent(
    state: SignUpScreenState,
    onEvent: (SignUpScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryBackgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Toolbar
        SignUpToolbar(
            onBackClick = { onEvent(SignUpScreenEvent.BackClicked) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            SignUpHeader()

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            SignUpSubtitle()

            Spacer(modifier = Modifier.height(32.dp))

            // Form Fields
            SignUpFormFields(
                state = state,
                onEvent = onEvent
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Button
            SignUpButton(
                isLoading = state.isLoading,
                onClick = { onEvent(SignUpScreenEvent.SignUpClicked) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Illustration
            SignUpIllustration()

            Spacer(modifier = Modifier.weight(1f))

            // Terms Row
            TermsRow(
                onPrivacyClick = { onEvent(SignUpScreenEvent.PrivacyPolicyClicked) },
                onTermsClick = { onEvent(SignUpScreenEvent.TermsClicked) }
            )
        }
    }
}

@Composable
private fun SignUpToolbar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryBackgroundColor)
            .padding(horizontal = 4.dp, vertical = 8.dp),
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
private fun SignUpHeader() {
    Text(
        text = "Create Account",
        color = SecondaryFrontColor,
        fontSize = 28.sp,
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SignUpSubtitle() {
    Text(
        text = "Join VitalSelf to start monitoring your health",
        color = SecondaryThemeTextColor,
        fontSize = 14.sp,
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SignUpFormFields(
    state: SignUpScreenState,
    onEvent: (SignUpScreenEvent) -> Unit
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
            onValueChange = { onEvent(SignUpScreenEvent.UsernameChanged(it)) },
            hint = "Choose a username",
            leadingIcon = painterResource(id = R.drawable.ic_person),
            imeAction = ImeAction.Next,
            isError = state.showError
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Email Field
    Column {
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
private fun SignUpButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    PrimaryButton(
        text = "Get Started",
        onClick = onClick,
        enabled = !isLoading
    )
}

@Composable
private fun SignUpIllustration() {
    Image(
        painter = painterResource(id = R.drawable.ic_illustration_login),
        contentDescription = "Sign Up Illustration",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = 12.dp)
    )
}

@Composable
private fun TermsRow(
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
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
