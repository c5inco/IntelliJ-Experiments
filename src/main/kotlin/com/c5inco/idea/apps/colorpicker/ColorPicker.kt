package com.c5inco.idea.apps.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.intellij.util.MathUtil.clamp
import kotlin.math.roundToInt
import java.awt.Color as AWTColor

private const val PICKER_WIDTH = 240
private const val PICKER_HEIGHT = 400

@Composable
fun ColorPicker(
    initialColor: AWTColor = AWTColor.RED,
    onColorChange: (Color) -> Unit = {},
    onClose: () -> Unit = {}
) {
    var activeColor by remember { mutableStateOf(initialColor.asComposeColor) }

    var (hue, saturation, brightness) = AWTColor.RGBtoHSB(initialColor.red, initialColor.green, initialColor.blue, null)
    var activeHue = AWTColor(AWTColor.HSBtoRGB(hue, 1f, 1f)).asComposeColor

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

        Box(
            Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color.White)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(activeHue.copy(alpha = 0f), activeHue)
                    )
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0f), Color.Black)
                    )
                ),
        ) {
            val dotSize = 24
            fun clampOffset(value: Float): Float {
                var adj = value - dotSize / 2
                return clamp(adj, (0 - dotSize / 2).toFloat(), (PICKER_WIDTH - dotSize / 2).toFloat())
            }

            var offsetX by remember { mutableStateOf(saturation * PICKER_WIDTH) }
            var offsetY by remember { mutableStateOf((1f - brightness) * 240) }

            Column(
                Modifier
                    .offset { IntOffset(clampOffset(offsetX).roundToInt(), clampOffset(offsetY).roundToInt()) }
                    .size(dotSize.dp)
                    .border(2.dp, Color.White, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consumeAllChanges()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            ) { }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            Modifier
                .fillMaxWidth(0.75f)
                .height(16.dp)
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
        ) { }

        Spacer(Modifier.height(20.dp))

        Row(
            Modifier
                .fillMaxWidth(0.75f)
                .height(16.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(activeHue.copy(alpha = 0f), activeHue)
                    )
                )
                .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(percent = 50))
        ) { }

        Spacer(Modifier.height(20.dp))

        Divider()

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ColorSwatch(color = Color.Cyan)
            ColorSwatch(color = Color.Yellow)
            ColorSwatch(color = Color.Magenta)
            ColorSwatch(color = Color.Black)
        }
    }
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