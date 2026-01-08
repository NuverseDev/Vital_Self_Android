package com.vital_self.core.ui.components.dropdowns

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.core.ui.theme.EditTextBackground
import com.vital_self.core.ui.theme.EditTextBorder
import com.vital_self.core.ui.theme.ErrorMsgTextColor
import com.vital_self.core.ui.theme.InterFamily
import com.vital_self.core.ui.theme.SecondaryFrontColor
import com.vital_self.core.ui.theme.TextBgColor
import com.vital_self.core.ui.theme.ThemeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalDropdown(
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    hint: String = "",
    leadingIcon: Painter? = null,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
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
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            singleLine = true,
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = EditTextBackground,
                unfocusedContainerColor = EditTextBackground,
                disabledContainerColor = EditTextBackground.copy(alpha = 0.5f),
                focusedBorderColor = if (isError) ErrorMsgTextColor else ThemeColor,
                unfocusedBorderColor = if (isError) ErrorMsgTextColor else EditTextBorder,
                errorBorderColor = ErrorMsgTextColor,
                focusedTextColor = SecondaryFrontColor,
                unfocusedTextColor = SecondaryFrontColor
            ),
            shape = RoundedCornerShape(10.dp),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = InterFamily,
                fontSize = 16.sp
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontFamily = InterFamily,
                            fontSize = 16.sp,
                            color = SecondaryFrontColor
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
