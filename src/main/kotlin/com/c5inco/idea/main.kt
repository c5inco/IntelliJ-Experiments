package com.c5inco.idea

import androidx.compose.desktop.Window
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.c5inco.idea.apps.colorpicker.ColorPicker

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