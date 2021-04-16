package com.c5inco.idea.apps.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import java.awt.Color as AWTColor

private const val PICKER_WIDTH = 240
private const val PICKER_HEIGHT = 450
private const val SPECTRUM_HEIGHT = 200
private const val SLIDER_HEIGHT = 16

@Composable
fun ColorPicker(
    initialColor: AWTColor = AWTColor.RED,
    onColorChange: (Color) -> Unit = {},
    onClose: () -> Unit = {}
) {
    val density = LocalDensity.current.density
    fun Int.withDensity(): Float {
        return this * density
    }

    val (hue, saturation, brightness) = AWTColor.RGBtoHSB(initialColor.red, initialColor.green, initialColor.blue, null)
    var activeHue by remember { mutableStateOf(hue) }
    var activeSaturation by remember { mutableStateOf(saturation) }
    var activeBrightness by remember { mutableStateOf(brightness) }
    var activeOpacity by remember { mutableStateOf(alphaInPercent(initialColor.alpha)) }

    var activeColor by remember(
        activeHue,
        activeSaturation,
        activeBrightness,
        activeOpacity
    ) {
        mutableStateOf(
            Color(AWTColor.HSBtoRGB(activeHue, activeSaturation, activeBrightness)).copy(alpha = activeOpacity)
        )
    }

    var spectrumHue = AWTColor(AWTColor.HSBtoRGB(activeHue, 1f, 1f)).asComposeColor

    Column(
        Modifier.size(width = PICKER_WIDTH.dp, height = PICKER_HEIGHT.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Solid",
                fontWeight = FontWeight.SemiBold
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.InvertColors,
                    contentDescription = "Multiple icon",
                    modifier = Modifier.size(18.dp)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close icon",
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            onClose()
                        }
                )
            }
        }

        var spectrumOffsetX by remember { mutableStateOf(activeSaturation * (PICKER_WIDTH.withDensity())) }
        var spectrumOffsetY by remember { mutableStateOf((1f - activeBrightness) * (SPECTRUM_HEIGHT.withDensity())) }

        Box(
            Modifier
                .fillMaxWidth()
                .height(SPECTRUM_HEIGHT.dp)
                .background(Color.White)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(spectrumHue.copy(alpha = 0f), spectrumHue)
                    )
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black)
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            spectrumOffsetX = it.x
                            activeSaturation = clamp(it.x / (PICKER_WIDTH.withDensity()), 0f, 1f)
                            spectrumOffsetY = it.y
                            activeBrightness = clamp(1f - it.y / (SPECTRUM_HEIGHT.withDensity()), 0f, 1f)
                        }
                    )
                },
        ) {
            val dotSize = 14

            Column(
                Modifier
                    .offset { IntOffset(
                        clampOffset(spectrumOffsetX, max = PICKER_WIDTH.withDensity(), width = dotSize.withDensity()).roundToInt(),
                        clampOffset(spectrumOffsetY, max = SPECTRUM_HEIGHT.withDensity(), width = dotSize.withDensity()).roundToInt()
                    ) }
                    .size(dotSize.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consumeAllChanges()
                                spectrumOffsetX += dragAmount.x
                                activeSaturation = clamp(spectrumOffsetX / (PICKER_WIDTH.withDensity()), 0f, 1f)
                                spectrumOffsetY += dragAmount.y
                                activeBrightness = clamp(1f - spectrumOffsetY / (SPECTRUM_HEIGHT.withDensity()), 0f, 1f)
                            }
                        )
                    }
            ) { }
        }

        Spacer(Modifier.height(16.dp))

        Box(
            Modifier.width(200.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            var offsetX by remember { mutableStateOf( activeHue * 200.withDensity()) }

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(SLIDER_HEIGHT.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Red,
                                Color.Yellow,
                                Color.Green,
                                Color.Cyan,
                                Color.Blue,
                                Color.Magenta,
                                Color.Red
                            )
                        )
                    )
                    .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(percent = 50))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                offsetX = it.x
                                activeHue = clamp(it.x / 200.withDensity(), 0f, 1f)
                            }
                        )
                    },
            ) { }

            DraggableThumb(
                Modifier
                    .offset { IntOffset(clampOffset(offsetX, max = 200.withDensity(), width = (8 + 8).withDensity()).roundToInt(), 0) }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            offsetX += delta
                            activeHue = clamp(offsetX / 200.withDensity(), 0f, 1f)
                        }
                    ),
                Size(8f, 20f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            Modifier.width(200.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            var offsetX by remember { mutableStateOf( activeOpacity * 200.withDensity()) }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(SLIDER_HEIGHT.dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(spectrumHue.copy(alpha = 0f), spectrumHue)
                        )
                    )
                    .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(percent = 50))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                offsetX = it.x
                                activeOpacity = clamp(it.x / 200.withDensity(), 0f, 1f)
                            }
                        )
                    },
            ) { }

            DraggableThumb(
                Modifier
                    .offset { IntOffset(clampOffset(offsetX, max = 200.withDensity(), width = (8 + 8).withDensity()).roundToInt(), 0) }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            offsetX += delta
                            activeOpacity = clamp(offsetX / 200.withDensity(), 0f, 1f)
                        }
                    ),
                Size(8f, 20f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Divider()

        Spacer(Modifier.height(16.dp))
        fun toBaseTwo(fl: Float): Int { return (fl * 255).roundToInt() }
        Text("${toBaseTwo(activeColor.red)}, ${toBaseTwo(activeColor.green)}, ${toBaseTwo(activeColor.blue)}, ${activeColor.alpha}")
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ColorSwatch(color = Color.Cyan)
            ColorSwatch(color = Color.Yellow)
            ColorSwatch(color = Color.Magenta)
            ColorSwatch(color = Color.Black)
        }
        Button(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            onClick = { onColorChange(activeColor) }
        ) {
            Text("Apply")
        }
    }
}

fun hueInPercent(hue: Float): Float {
    return hue / 360f * 100 / 100
}

fun alphaInPercent(alpha: Int): Float {
    return (alpha.toDouble() / 255.0 * 100 / 100).toFloat()
}

@Composable
fun DraggableThumb(
    modifier: Modifier = Modifier,
    size: Size
) {
    val (width, height) = size

    Column(
        modifier
            .padding(4.dp)
            .size(width = width.dp, height = height.dp)
            .background(Color.White)
            .border(1.dp, Color.Black, RoundedCornerShape(2.dp))
    ) { }
}

@Composable
fun ColorSwatch(color: Color) {
    Row(
        Modifier
            .size(20.dp)
            .background(color = color, shape = RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.15f),
                shape = RoundedCornerShape(2.dp)
            )
    ) { }
}

fun clampOffset(value: Float, min: Float = 0f, max: Float, width: Float): Float {
    var adj = value - width / 2
    return clamp(adj, (min - width / 2), (max - width / 2))
}