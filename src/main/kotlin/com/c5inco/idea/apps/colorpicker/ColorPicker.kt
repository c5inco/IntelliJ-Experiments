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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.c5inco.idea.utils.clamp
import com.github.ajalt.colormath.HSV
import com.github.ajalt.colormath.RGB
import kotlin.math.max
import kotlin.math.min
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

    val (h, s, v) = RGB(initialColor.red, initialColor.green, initialColor.blue).toHSV()

    var activeHue by remember { mutableStateOf(h.toFloat()) }
    var activeSaturation by remember { mutableStateOf(s.toFloat()/100) }
    var activeBrightness by remember { mutableStateOf(v.toFloat()/100) }
    var activeOpacity by remember { mutableStateOf(initialColor.alpha) }

    var activeColor by remember(
        activeHue,
        activeSaturation,
        activeBrightness,
        activeOpacity
    ) {
        mutableStateOf(
            Color(HSV(activeHue.toInt(), (activeSaturation * 100).toInt(), (activeBrightness * 100).toInt()).toRGB().toPackedInt()).copy(alpha = activeOpacity)
        )
    }

    var spectrumHue = Color(HSV(activeHue.toInt(), 100, 100).toRGB().toPackedInt())

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
                            activeBrightness =
                                clamp(1f - it.y / (SPECTRUM_HEIGHT.withDensity()), 0f, 1f)
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
                                activeSaturation =
                                    clamp(spectrumOffsetX / (PICKER_WIDTH.withDensity()), 0f, 1f)
                                spectrumOffsetY += dragAmount.y
                                activeBrightness = clamp(
                                    1f - spectrumOffsetY / (SPECTRUM_HEIGHT.withDensity()),
                                    0f,
                                    1f
                                )
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
                    Modifier.width(200.dp).height(24.dp),
                    activeHue / 360,
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
                        activeHue = it * 360
                    }
                )

                Spacer(Modifier.height(8.dp))

                RangeSlider(
                    Modifier.width(200.dp).height(24.dp),
                    activeOpacity,
                    Brush.horizontalGradient(
                        colors = listOf(activeColor.copy(alpha = 0f), activeColor)
                    ),
                    onValueChange = {
                        activeOpacity = it
                    }
                )
            }
        }



        Spacer(Modifier.height(16.dp))

        Divider()

        Spacer(Modifier.height(16.dp))
        fun toBaseTwo(fl: Float): Int { return (fl * 255).roundToInt() }
        Text("${toBaseTwo(activeColor.red)}, ${toBaseTwo(activeColor.green)}, ${toBaseTwo(activeColor.blue)}, ${activeColor.alpha}")
        Text("${HSV(h, s, v).toRGB()}")
        Text("$activeHue")
        Text("$activeSaturation")
        Text("$activeBrightness")
        Text("$activeOpacity")

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
        var offsetX by remember { mutableStateOf(value * widthAsPx) }

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
                            onValueChange(clamp(it.x / widthAsPx, 0f, 1f))
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
                        onValueChange(clamp(offsetX / widthAsPx, 0f, 1f))
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
    return clamp(adj, (min - width / 2), (max - width / 2))
}