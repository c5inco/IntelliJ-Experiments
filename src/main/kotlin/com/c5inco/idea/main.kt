package com.c5inco.idea

import androidx.compose.desktop.Window
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import com.c5inco.idea.apps.colorpicker.ColorPicker

fun main() = Window(
    size = IntSize(width = 240, height = 500)
) {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        // Button(onClick = {
        //     text = "Hello, Dialog!"
        // }) {
        //     Text(text)
        // }
        //RotatingGlobe(modifier = Modifier.size(400.dp))

        // var expanded by remember { mutableStateOf(false) }
        // Box {
        //     Icon(
        //         imageVector = Icons.Default.ColorLens,
        //         contentDescription = "Button",
        //         modifier = Modifier.clickable {
        //             expanded = true
        //         }
        //     )
        //
        //     DropdownMenu(
        //         expanded = expanded,
        //         onDismissRequest = { expanded = false }
        //     ) {
        //         ColorPicker()
        //     }
        // }
        ColorPicker()
    }
}