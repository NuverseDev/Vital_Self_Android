package com.vital_self.core.ui.components.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.R
import com.vital_self.core.ui.theme.*

@Composable
@Preview(showBackground = true)
fun VitalTextFieldPreview(){
    VitalTextField("testing", onValueChange = {})
}

@Composable
fun VitalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "",
    leadingIcon: Painter? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    isError: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {

    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        enabled = enabled,
        placeholder = {
            Text(
                text = hint,
                color = TextBgColor.copy(alpha = 0.6f),
                fontSize = 16.sp,
                fontFamily = InterFamily
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    painter = it,
                    contentDescription = null,
                    tint = SecondaryFrontColor
                )
            }
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.ic_visible else R.drawable.ic_invisible
                        ),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = SecondaryFrontColor
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() },
            onNext = { onImeAction() },
            onGo = { onImeAction() }
        ),
        singleLine = singleLine,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = EditTextBackground,
            unfocusedContainerColor = EditTextBackground,
            disabledContainerColor = EditTextBackground.copy(alpha = 0.5f),
            focusedBorderColor = if (isError) ErrorMsgTextColor else ThemeColor,
            unfocusedBorderColor = if (isError) ErrorMsgTextColor else EditTextBorder,
            errorBorderColor = ErrorMsgTextColor,
            focusedTextColor = SecondaryFrontColor,
            unfocusedTextColor = SecondaryFrontColor,
            cursorColor = ThemeColor
        ),
        shape = RoundedCornerShape(10.dp),
        textStyle = LocalTextStyle.current.copy(
            fontFamily = InterFamily,
            fontSize = 16.sp
        )
    )
}
