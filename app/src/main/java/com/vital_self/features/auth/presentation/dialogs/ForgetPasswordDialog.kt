package com.vital_self.features.auth.presentation.dialogs

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vital_self.core.ui.components.buttons.SecondaryButton
import com.vital_self.core.ui.components.common.ErrorMessage
import com.vital_self.core.ui.components.textfields.VitalTextField
import com.vital_self.core.ui.theme.InterFamily
import com.vital_self.core.ui.theme.SecondaryTextColor
import com.vital_self.core.ui.theme.SecondaryThemeTextColor
import com.vital_self.core.ui.theme.ThemeColor
import com.vital_self.core.ui.theme.White

@Preview
@Composable
private fun PreviewForgetPassword(){
    ForgetPasswordDialog({}){

    }
}

@Composable
fun ForgetPasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Forgot Password",
                    color = SecondaryTextColor,
                    fontSize = 16.sp,
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = "Please enter your email address to reset your password.",
                    color = SecondaryThemeTextColor,
                    fontSize = 14.sp,
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email Field
                VitalTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        showError = false
                    },
                    hint = "Email Address",
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                    onImeAction = {
                        if (isValidEmail(email)) {
                            onSubmit(email)
                        } else {
                            showError = true
                        }
                    },
                    isError = showError
                )

                // Error Message
                ErrorMessage(
                    message = "Please enter valid email address",
                    isVisible = showError
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = SecondaryThemeTextColor
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            fontFamily = InterFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            if (isValidEmail(email)) {
                                onSubmit(email)
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ThemeColor,
                            contentColor = White
                        )
                    ) {
                        Text(
                            text = "Submit",
                            fontFamily = InterFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
