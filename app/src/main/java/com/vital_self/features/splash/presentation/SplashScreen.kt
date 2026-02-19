package com.vital_self.features.splash.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.BuildConfig
import com.vital_self.R
import com.vital_self.core.ui.theme.Black
import com.vital_self.core.ui.theme.GlowRingColor
import com.vital_self.core.ui.theme.InnerGlowColor
import com.vital_self.core.ui.theme.ParticleDotColor
import com.vital_self.core.ui.theme.SplashGradientCenter
import com.vital_self.core.ui.theme.SplashGradientEnd
import com.vital_self.core.ui.theme.SplashGradientStart
import com.vital_self.core.ui.theme.TaglineColor
import com.vital_self.core.ui.theme.Transparent
import com.vital_self.core.ui.theme.VersionTextColor
import com.vital_self.core.ui.theme.White
import kotlinx.coroutines.delay

@Composable
@Preview
fun previewScreenSplash(
){
    SplashScreenContent(BuildConfig.VERSION) { }
}

@Composable
fun SplashScreenContent(
    versionName: String,
    onSplashComplete: () -> Unit
) {
    var startAnimations by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimations = true
        delay(2000L)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Black)
//            .background(
//                brush = Brush.radialGradient(
//                    colors = listOf(
//                        SplashGradientCenter,
//                        SplashGradientStart,
//                        SplashGradientEnd
//                    ),
//                    radius = 1500f
//                )
//            )
    ) {
        // Glow Rings
//        GlowRings(
//            startAnimations = startAnimations,
//            modifier = Modifier
//                .align(Alignment.Center)
//                .offset(y = (-40).dp)
//        )

        // Floating Particles
//        FloatingParticles(startAnimations = startAnimations)

        // Logo
        AnimatedLogo(
            startAnimations = startAnimations,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-40).dp)
        )

//        // Tagline
//        AnimatedTagline(
//            startAnimations = startAnimations,
//            modifier = Modifier
//                .align(Alignment.Center)
//                .offset(y = 60.dp)
//        )
//
//        // Loading Dots
//        LoadingDots(
//            startAnimations = startAnimations,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 80.dp)
//        )

        // Version Text
        Text(
            text = "version ${versionName}",
            color = White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)

        )
    }
}



@Composable
private fun AnimatedLogo(
    startAnimations: Boolean,
    modifier: Modifier = Modifier
) {
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(800, delayMillis = 800, easing = FastOutSlowInEasing),
        label = "logoAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    Image(
        painter = painterResource(id = R.drawable.ic_logo_vital_self_night),
        contentDescription = "VitalSelf Logo",
        modifier = modifier
            .padding(horizontal = 60.dp)
            .alpha(logoAlpha)
            .scale(logoScale)
    )
}

