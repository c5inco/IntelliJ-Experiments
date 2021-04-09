package com.c5inco.idea

import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

fun main() = Window {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Dialog!"
        }) {
            Text(text)
        }
        //RotatingGlobe(modifier = Modifier.size(400.dp))
        ColorPicker()
    }
}

@Composable
fun ColorPicker() {
    var activeColor by remember { mutableStateOf(Color.Red) }

    Column(
        Modifier.fillMaxSize()
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
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(
            Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color.White)
                .background(Brush.horizontalGradient(
                    colors = listOf(activeColor.copy(alpha = 0f), activeColor)
                ))
                .background(Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0f), Color.Black)
                )),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .size(24.dp)
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
                    colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red)
                ))
                .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(percent = 50))
        ) { }

        Spacer(Modifier.height(20.dp))

        Row(
            Modifier
                .fillMaxWidth(0.75f)
                .height(16.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(Brush.horizontalGradient(
                    colors = listOf(activeColor.copy(alpha = 0f), activeColor)
                ))
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
