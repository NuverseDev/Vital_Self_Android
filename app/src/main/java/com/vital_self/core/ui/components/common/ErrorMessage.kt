package com.vital_self.core.ui.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.core.ui.theme.ErrorMsgTextColor
import com.vital_self.core.ui.theme.InterFamily

@Composable
@Preview
fun ErrorMessagePreview() {
    ErrorMessage(message = "Sample Error Message", isVisible = true)
}


@Composable
fun ErrorMessage(
    message: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Text(
            text = message,
            color = ErrorMsgTextColor,
            fontSize = 14.sp,
            fontFamily = InterFamily,
            modifier = modifier.padding(top = 6.dp, start = 4.dp, bottom = 6.dp)
        )
    }
}
