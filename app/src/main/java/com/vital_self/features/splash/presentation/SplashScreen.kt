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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vital_self.R
import com.vital_self.core.ui.theme.GlowRingColor
import com.vital_self.core.ui.theme.InnerGlowColor
import com.vital_self.core.ui.theme.ParticleDotColor
import com.vital_self.core.ui.theme.SplashGradientCenter
import com.vital_self.core.ui.theme.SplashGradientEnd
import com.vital_self.core.ui.theme.SplashGradientStart
import com.vital_self.core.ui.theme.TaglineColor
import com.vital_self.core.ui.theme.Transparent
import com.vital_self.core.ui.theme.VersionTextColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreenContent(
    versionName: String,
    onSplashComplete: () -> Unit
) {
    var startAnimations by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimations = true
        delay(3500L)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SplashGradientCenter,
                        SplashGradientStart,
                        SplashGradientEnd
                    ),
                    radius = 1500f
                )
            )
    ) {
        // Glow Rings
        GlowRings(
            startAnimations = startAnimations,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-40).dp)
        )

        // Floating Particles
        FloatingParticles(startAnimations = startAnimations)

        // Logo
        AnimatedLogo(
            startAnimations = startAnimations,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-40).dp)
        )

        // Tagline
        AnimatedTagline(
            startAnimations = startAnimations,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 60.dp)
        )

        // Loading Dots
        LoadingDots(
            startAnimations = startAnimations,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )

        // Version Text
        AnimatedVersion(
            versionName = versionName,
            startAnimations = startAnimations,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun GlowRings(
    startAnimations: Boolean,
    modifier: Modifier = Modifier
) {
    // Outer Ring
    val outerAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 0.6f else 0f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "outerAlpha"
    )
    val outerScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.5f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "outerScale"
    )

    // Middle Ring (delayed)
    val middleAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 0.7f else 0f,
        animationSpec = tween(1200, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "middleAlpha"
    )
    val middleScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.5f,
        animationSpec = tween(1200, delayMillis = 150, easing = FastOutSlowInEasing),
        label = "middleScale"
    )

    // Inner Glow (delayed more)
    val innerAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 0.8f else 0f,
        animationSpec = tween(1200, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "innerAlpha"
    )
    val innerScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.3f,
        animationSpec = tween(1200, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "innerScale"
    )

    // Pulsing animation for inner glow
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(modifier = modifier) {
        // Outer Ring
        GlowRing(
            size = 320.dp,
            alpha = outerAlpha,
            scale = outerScale,
            modifier = Modifier.align(Alignment.Center)
        )

        // Middle Ring
        GlowRing(
            size = 260.dp,
            alpha = middleAlpha,
            scale = middleScale,
            modifier = Modifier.align(Alignment.Center)
        )

        // Inner Glow with pulse
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(innerScale * if (startAnimations) pulseScale else 1f)
                .alpha(if (startAnimations) pulseAlpha else innerAlpha)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(InnerGlowColor, Transparent)
                    ),
                    shape = CircleShape
                )
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun GlowRing(
    size: Dp,
    alpha: Float,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .alpha(alpha)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(GlowRingColor, Transparent)
                ),
                shape = CircleShape
            )
    )
}

@Composable
private fun AnimatedLogo(
    startAnimations: Boolean,
    modifier: Modifier = Modifier
) {
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200, easing = FastOutSlowInEasing),
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

@Composable
private fun AnimatedTagline(
    startAnimations: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(800, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "taglineAlpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (startAnimations) 0.dp else 20.dp,
        animationSpec = tween(800, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "taglineOffset"
    )

    Text(
        text = "Your Health, Your Insight",
        color = TaglineColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Light,
        letterSpacing = 2.sp,
        modifier = modifier
            .alpha(alpha)
            .offset(y = offsetY)
    )
}

@Composable
private fun LoadingDots(
    startAnimations: Boolean,
    modifier: Modifier = Modifier
) {
    val containerAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(400, delayMillis = 800),
        label = "dotsContainerAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "loadingDots")

    Row(
        modifier = modifier.alpha(containerAlpha),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dotScale$index"
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dotAlpha$index"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .background(ParticleDotColor, CircleShape)
            )
        }
    }
}

@Composable
private fun AnimatedVersion(
    versionName: String,
    startAnimations: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 800),
        label = "versionAlpha"
    )

    Text(
        text = "v$versionName",
        color = VersionTextColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Light,
        modifier = modifier.alpha(alpha)
    )
}

@Composable
private fun FloatingParticles(startAnimations: Boolean) {
    val particleConfigs = listOf(
        ParticleConfig(0.2f, 0.3f, 4.dp),
        ParticleConfig(0.8f, 0.25f, 3.dp),
        ParticleConfig(0.15f, 0.6f, 5.dp),
        ParticleConfig(0.85f, 0.55f, 3.dp),
        ParticleConfig(0.35f, 0.75f, 4.dp),
        ParticleConfig(0.7f, 0.8f, 3.dp)
    )

    particleConfigs.forEachIndexed { index, config ->
        FloatingParticle(
            config = config,
            index = index,
            startAnimations = startAnimations
        )
    }
}

private data class ParticleConfig(
    val horizontalBias: Float,
    val verticalBias: Float,
    val size: Dp
)

@Composable
private fun FloatingParticle(
    config: ParticleConfig,
    index: Int,
    startAnimations: Boolean
) {
    val alpha by animateFloatAsState(
        targetValue = if (startAnimations) 0.4f + (index * 0.1f) else 0f,
        animationSpec = tween(600, delayMillis = 400 + index * 100),
        label = "particleAlpha$index"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "particle$index")

    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f - (index * 10f),
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + (index * 300), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY$index"
    )

    val swayX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (index % 2 == 0) 15f else -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500 + (index * 200), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "swayX$index"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(
                    x = (maxWidth * config.horizontalBias) + swayX.dp,
                    y = (maxHeight * config.verticalBias) + floatY.dp
                )
                .size(config.size)
                .alpha(alpha)
                .background(ParticleDotColor, CircleShape)
        )
    }
}
