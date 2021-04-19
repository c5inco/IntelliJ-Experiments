package com.c5inco.idea.apps.colorpicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.c5inco.idea.components.TextInput
import com.c5inco.idea.utils.WithoutTouchSlop
import com.c5inco.idea.utils.asHex
import com.c5inco.idea.utils.clampInt
import com.c5inco.idea.utils.clampFloat
import com.github.ajalt.colormath.HSV
import com.github.ajalt.colormath.RGB
import kotlin.math.roundToInt

private const val PICKER_WIDTH = 240
private const val PICKER_HEIGHT = 450
private const val SPECTRUM_HEIGHT = 200

@Composable
fun ColorPicker(
    initialColor: Color = Color.Cyan,
    onColorChange: (Color) -> Unit = {},
    onClose: () -> Unit = {}
) {
    val density = LocalDensity.current.density
    fun Int.withDensity(): Float {
        return this * density
    }
    var displayInRGB by remember { mutableStateOf(false) }

    val initialRGB = RGB(initialColor.red, initialColor.green, initialColor.blue)
    val (h, s, v) = initialRGB.toHSV()

    var activeHue by remember { mutableStateOf(h) }
    var activeSaturation by remember { mutableStateOf(s) }
    var activeBrightness by remember { mutableStateOf(v) }
    var activeRed by remember { mutableStateOf((initialColor.red * 255).toInt()) }
    var activeGreen by remember { mutableStateOf((initialColor.green * 255).toInt()) }
    var activeBlue by remember { mutableStateOf((initialColor.blue * 255).toInt()) }
    var activeOpacity by remember { mutableStateOf((initialColor.alpha * 100).toInt()) }

    var activeColor by remember(
        activeHue,
        activeSaturation,
        activeBrightness,
        activeRed,
        activeGreen,
        activeBlue,
        activeOpacity
    ) {
        mutableStateOf(
            if (displayInRGB) {
                Color(
                    RGB(activeRed, activeGreen, activeBlue).toPackedInt()
                ).copy(alpha = activeOpacity / 100f)
            } else {
                Color(
                    HSV(activeHue, activeSaturation, activeBrightness).toRGB().toPackedInt()
                ).copy(alpha = activeOpacity / 100f)
            }
        )
    }

    fun updateRGB() {
        val (r, g, b) = HSV(activeHue, activeSaturation, activeBrightness).toRGB()
        activeRed = r
        activeGreen = g
        activeBlue = b
    }

    fun updateHSV() {
        val (h, s, v) = RGB(activeRed, activeGreen, activeBlue).toHSV()
        activeHue = h
        activeSaturation = s
        activeBrightness = v
    }

    var spectrumHue = Color(HSV(activeHue, 100, 100).toRGB().toPackedInt())

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

        var spectrumOffsetX by remember(activeSaturation) { mutableStateOf(activeSaturation / 100f * (PICKER_WIDTH.withDensity())) }
        var spectrumOffsetY by remember(activeBrightness) { mutableStateOf((1f - activeBrightness / 100f) * (SPECTRUM_HEIGHT.withDensity())) }

        WithoutTouchSlop {
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
                                activeSaturation = (clampFloat(it.x / (PICKER_WIDTH.withDensity())) * 100).toInt()
                                spectrumOffsetY = it.y
                                activeBrightness = (clampFloat(1f - it.y / (SPECTRUM_HEIGHT.withDensity())) * 100).toInt()
                                updateRGB()
                            }
                        )
                    },
            ) {
                val dotSize = 14

                Column(
                    Modifier
                        .offset {
                            IntOffset(
                                clampOffset(
                                    spectrumOffsetX,
                                    max = PICKER_WIDTH.withDensity(),
                                    width = dotSize.withDensity()
                                ).roundToInt(),
                                clampOffset(
                                    spectrumOffsetY,
                                    max = SPECTRUM_HEIGHT.withDensity(),
                                    width = dotSize.withDensity()
                                ).roundToInt()
                            )
                        }
                        .size(dotSize.dp)
                        .border(2.dp, Color.White, CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consumeAllChanges()
                                    spectrumOffsetX += dragAmount.x
                                    activeSaturation = (clampFloat(spectrumOffsetX / (PICKER_WIDTH.withDensity())) * 100).toInt()
                                    spectrumOffsetY += dragAmount.y
                                    activeBrightness = (clampFloat(1f - spectrumOffsetY / (SPECTRUM_HEIGHT.withDensity())) * 100).toInt()
                                    updateRGB()
                                }
                            )
                        }
                ) { }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = Color.Black.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                        .background(activeColor)
                ) {}

                Spacer(Modifier.width(20.dp))

                Column {
                    RangeSlider(
                        Modifier
                            .width(200.dp)
                            .height(24.dp),
                        activeHue / 360f,
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
                        ),
                        onValueChange = {
                            activeHue = (it * 360).toInt()
                            updateRGB()
                        }
                    )

                    Spacer(Modifier.height(8.dp))

                    RangeSlider(
                        Modifier
                            .width(200.dp)
                            .height(24.dp),
                        activeOpacity / 100f,
                        Brush.horizontalGradient(
                            colors = listOf(activeColor.copy(alpha = 0f), activeColor.copy(alpha = 1f))
                        ),
                        onValueChange = {
                            activeOpacity = (it * 100).toInt()
                        }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                    val smallLabelWidth = Modifier.weight(1f)
                    val textStyle = MaterialTheme.typography.caption.copy(textAlign = TextAlign.Center)

                    Text(
                        text = "A%",
                        style = textStyle,
                        modifier = smallLabelWidth
                    )
                    Row(
                        Modifier
                            .weight(3f)
                            .padding(vertical = 2.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(2.dp))
                            .clickable {
                                displayInRGB = !displayInRGB
                            }
                    ) {
                        val labels = if (displayInRGB) "RGB" else "HSV"
                        labels.forEach {
                            Text(
                                text = it.toString(),
                                style = textStyle,
                                modifier = smallLabelWidth
                            )
                        }
                    }
                    Text(
                        text = "Hex",
                        style = textStyle,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val smallInputWidth = Modifier.weight(1f)
                TextInput(
                    modifier = smallInputWidth,
                    value = activeOpacity,
                    convert = { it.toIntOrNull() }
                ) {
                    activeOpacity = clampInt(it ?: activeOpacity, 0, 100)
                }
                TextInput(
                    modifier = smallInputWidth,
                    value = if (displayInRGB) activeRed else activeHue,
                    convert = { it.toIntOrNull() }
                ) {
                    if (displayInRGB) {
                        activeRed = clampInt(it ?: activeRed, max = 255)
                        updateHSV()
                    } else {
                        activeHue = clampInt(it ?: activeHue, max = 360)
                        updateRGB()
                    }
                }
                TextInput(
                    modifier = smallInputWidth,
                    value = if (displayInRGB) activeGreen else activeSaturation,
                    convert = { it.toIntOrNull() }
                ) {
                    if (displayInRGB) {
                        activeGreen = clampInt(it ?: activeGreen, max = 255)
                        updateHSV()
                    } else {
                        activeSaturation = clampInt(it ?: activeSaturation)
                        updateRGB()
                    }
                }
                TextInput(
                    modifier = smallInputWidth,
                    value = if (displayInRGB) activeBlue else activeBrightness,
                    convert = { it.toIntOrNull() }
                ) {
                    if (displayInRGB) {
                        activeBlue = clampInt(it ?: activeBlue, max = 255)
                        updateHSV()
                    } else {
                        activeBrightness = clampInt(it ?: activeBrightness)
                        updateRGB()
                    }
                }
                TextInput(
                    modifier = Modifier.weight(2f),
                    value = activeColor.asHex,
                    convert = {
                        var hex = it
                        if (it.length == 8) {
                            hex = it.substring(2 until it.length) + it.substring(0 until 2)
                        }
                        hex
                    }
                ) {
                    val (h, s, v, a) = RGB(it).toHSV()
                    activeHue = h
                    activeSaturation = s
                    activeBrightness = v
                    activeOpacity = (a * 100).toInt()
                }
            }
        }

        Divider()

        Spacer(Modifier.height(16.dp))
        fun toBaseTwo(fl: Float): Int { return (fl * 255).roundToInt() }
        Text("${toBaseTwo(activeColor.red)}, ${toBaseTwo(activeColor.green)}, ${toBaseTwo(activeColor.blue)}, ${activeColor.alpha}")
        Text("$activeHue")
        Text("$activeSaturation")
        Text("$activeBrightness")
        //Text("$activeOpacity")
        Text("X: $spectrumOffsetX")
        Text("Y: $spectrumOffsetY")

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            onClick = { onColorChange(activeColor) }
        ) {
            Text("Apply")
        }
    }
}

@Composable
private fun RangeSlider(
    modifier: Modifier = Modifier,
    value: Float,
    colorBrush: Brush = SolidColor(Color.Black),
    onValueChange: (Float) -> Unit = {}
) {
    val density = LocalDensity.current.density
    BoxWithConstraints(
        modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        val widthAsPx = maxWidth.value * density
        var offsetX by remember(value) { mutableStateOf(value * widthAsPx) }

        Canvas(
            modifier = Modifier
                .width(maxWidth)
                .padding(vertical = 4.dp)
                .fillMaxHeight()
                .clip(
                    RoundedCornerShape(percent = 25)
                )
                .border(
                    1.dp,
                    Color.Black.copy(alpha = 0.15f),
                    RoundedCornerShape(percent = 25)
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            offsetX = it.x
                            onValueChange(clampFloat(it.x / widthAsPx, 0f, 1f))
                        }
                    )
                },
        ) {
            drawGradientPill(
                size.width,
                size.height,
                colorBrush
            )
        }

        DraggableThumb(
            Modifier
                .offset {
                    IntOffset(
                        clampOffset(
                            offsetX,
                            max = widthAsPx,
                            width = 8f * density
                        ).roundToInt(), 0
                    )
                }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX += delta
                        onValueChange(clampFloat(offsetX / widthAsPx))
                    }
                ),
            Size(8f, maxHeight.value - 4)
        )
    }
}

private fun DrawScope.drawGradientPill(
    canvasWidth: Float,
    canvasHeight: Float,
    brush: Brush = SolidColor(Color.Black)
) {
    drawRect(
        brush,
        size = Size(canvasWidth, canvasHeight)
    )
}

@Composable
fun DraggableThumb(
    modifier: Modifier = Modifier,
    size: Size
) {
    val (width, height) = size

    Column(
        modifier
            .size(width = width.dp, height = height.dp)
            .clip(RoundedCornerShape(percent = 25))
            .background(Color.White)
            .border(1.dp, Color.Black, RoundedCornerShape(percent = 25))
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
    return clampFloat(adj, (min - width / 2), (max - width / 2))
}