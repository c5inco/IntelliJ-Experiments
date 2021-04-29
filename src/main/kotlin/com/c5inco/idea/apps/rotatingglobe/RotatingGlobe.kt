package com.c5inco.idea.apps.rotatingglobe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RotatingGlobe() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            Modifier
                .shadow(16.dp, CutCornerShape(24.dp))
                .clip(CutCornerShape(24.dp))
                .fillMaxHeight(0.5f)
                .fillMaxWidth(0.5f)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val colors = listOf(Color.Red, Color.Cyan, Color.Magenta, Color.Green, Color.LightGray)
                    var activeColor by remember { mutableStateOf(colors[2]) }
                    var totalDots by remember { mutableStateOf(1000) }

                    GlobeModel(
                        Modifier.size(280.dp),
                        dotColor = activeColor,
                        totalDots = totalDots
                    )

                    @Composable
                    fun Swatch(color: Color, activeColor: Color, onClick: (Color) -> Unit = {}) {
                        Box(
                            Modifier
                                .shadow(8.dp, shape = CircleShape)
                                .size(36.dp)
                                .background(color = color, shape = CircleShape)
                                .border(
                                    4.dp,
                                    color = if (color == activeColor) MaterialTheme.colors.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    onClick(color)
                                }
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colors.forEach { color ->
                            Swatch(color, activeColor, onClick = { newColor ->
                                activeColor = newColor
                            })
                        }
                    }

                    var sliderValue by remember { mutableStateOf(totalDots.toFloat()) }
                    Slider(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        value = sliderValue,
                        valueRange = 100f..1000f,
                        steps = 7,
                        onValueChange = {
                            sliderValue = it
                        },
                        onValueChangeFinished = {
                            totalDots = sliderValue.toInt()
                        }
                    )
                }
            }
        }
    }
}

private const val GLOBE_RADIUS_FACTOR = 0.7f
private const val DOT_RADIUS_FACTOR = 0.005f
private const val FIELD_OF_VIEW_FACTOR = 0.8f
private const val TWO_PI = 2 * PI

@Composable
fun GlobeModel(
    modifier: Modifier = Modifier,
    dotColor: Color = Color.Magenta,
    totalDots: Int = 1000,
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 20000, easing = LinearEasing),
            ),
        )
    }

    val dotInfos = remember(totalDots) {
        (0 until totalDots).map {
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