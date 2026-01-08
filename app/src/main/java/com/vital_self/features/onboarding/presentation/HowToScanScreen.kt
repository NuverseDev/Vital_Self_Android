package com.vital_self.features.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.R
import com.vital_self.core.ui.components.toolbar.VitalToolbar
import com.vital_self.core.ui.theme.SecondaryThemeTextColor
import com.vital_self.core.ui.theme.ThemeColor
import com.vital_self.core.ui.theme.VitalSelfTheme
import com.vital_self.core.ui.theme.White

private val InterBold = FontFamily(Font(R.font.inter_bold, FontWeight.Bold))
private val InterMedium = FontFamily(Font(R.font.inter_medium, FontWeight.Medium))

@Composable
fun HowToScanScreen(
    title: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Toolbar
        VitalToolbar(
            title = title,
            onBackClick = onBackClick
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp)
        ) {
            BestPracticesCard()
        }
    }
}

@Composable
private fun BestPracticesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with light icon
            BestPracticesHeader()

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Important checks to be consider before scan",
                fontFamily = InterBold,
                fontSize = 14.sp,
                color = SecondaryThemeTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Points list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BulletPoint(text = stringResource(R.string.point_one))
                BulletPoint(text = stringResource(R.string.point_two))
                BulletPoint(text = stringResource(R.string.point_three))
                BulletPoint(text = stringResource(R.string.point_four))
                BulletPoint(text = stringResource(R.string.point_five))
            }
        }
    }
}

@Composable
private fun BestPracticesHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_light),
            contentDescription = "Light",
            modifier = Modifier.size(36.dp),
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "Best Practice",
            fontFamily = InterBold,
            fontSize = 20.sp,
            color = ThemeColor
        )
    }
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF9800))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text.trim(),
            fontFamily = InterMedium,
            fontSize = 13.sp,
            color = SecondaryThemeTextColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HowToScanScreenPreview() {
    VitalSelfTheme {
        HowToScanScreen(
            title = "How to Scan",
            onBackClick = {}
        )
    }
}
