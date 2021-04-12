package com.c5inco.idea.apps.rotatingglobe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

private const val NUM_DOTS = 1000
private const val GLOBE_RADIUS_FACTOR = 0.7f
private const val DOT_RADIUS_FACTOR = 0.005f
private const val FIELD_OF_VIEW_FACTOR = 0.8f
private const val TWO_PI = 2 * PI
private var isSystemInDarkTheme = false

@Composable
fun RotatingGlobe(modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 20000, easing = LinearEasing),
            ),
        )
    }

    val dotColor = if (isSystemInDarkTheme) Color.White else Color.Magenta
    val dotInfos = remember {
        (0 until NUM_DOTS).map {
            val azimuthAngle = acos((Math.random().toFloat() * 2) - 1)
            val polarAngle = Math.random().toFloat() * TWO_PI
            DotInfo(azimuthAngle, polarAngle.toFloat())
        }
    }

    Canvas(modifier = modifier) {
        // Compute the animated rotation about the y-axis in radians.
        val rotationY = progress.value * TWO_PI
        dotInfos.forEach {
            val minSize = size.minDimension
            val globeRadius = minSize * GLOBE_RADIUS_FACTOR

            // Calculate the dot's coordinates in 3D space.
            val x = globeRadius * sin(it.azimuthAngle) * cos(it.polarAngle)
            val y = globeRadius * sin(it.azimuthAngle) * sin(it.polarAngle)
            val z = globeRadius * cos(it.azimuthAngle) - globeRadius

            // Rotate the dot's 3D coordinates about the y-axis.
            val rotatedX = cos(rotationY) * x + sin(rotationY) * (z + globeRadius)
            val rotatedZ = -sin(rotationY) * x + cos(rotationY) * (z + globeRadius) - globeRadius

            // Project the rotated 3D coordinates onto the 2D plane.
            val fieldOfView = minSize * FIELD_OF_VIEW_FACTOR
            val projectedScale = fieldOfView / (fieldOfView - rotatedZ)
            val projectedX = (rotatedX * projectedScale) + minSize / 2f
            val projectedY = (y * projectedScale) + minSize / 2f

            // Scale the dot such that dots further away from the camera appear smaller.
            val dotRadius = minSize * DOT_RADIUS_FACTOR
            val scaledDotRadius = dotRadius * projectedScale

            drawCircle(
                color = dotColor,
                radius = scaledDotRadius.toFloat(),
                center = Offset(projectedX.toFloat(), projectedY.toFloat()),
            )
        }
    }
}

private data class DotInfo(val azimuthAngle: Float, val polarAngle: Float)