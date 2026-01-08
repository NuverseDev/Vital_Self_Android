package com.vital_self.core.ui.components.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.R
import com.vital_self.core.ui.theme.SecondaryThemeTextColor
import com.vital_self.core.ui.theme.White

private val InterMedium = FontFamily(Font(R.font.inter_medium, FontWeight.Medium))

@Composable
fun VitalToolbar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 18.dp)
        ) {
            // Back button - CardView style (34dp circular)
            Surface(
                onClick = onBackClick,
                shape = CircleShape,
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(34.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "Back",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Centered title - inter_medium at 24sp
            Text(
                text = title,
                fontSize = 24.sp,
                fontFamily = InterMedium,
                fontWeight = FontWeight.Medium,
                color = SecondaryThemeTextColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Bottom divider/shadow
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x30555555)
                            )
                        )
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VitalToolbarPreview() {
    VitalToolbar(
        title = "Profile",
        onBackClick = {}
    )
}
