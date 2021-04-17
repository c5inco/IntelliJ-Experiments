package com.c5inco.idea.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.shortcuts
import androidx.compose.ui.unit.dp

@Composable
fun <T> TextInput(
    modifier: Modifier = Modifier,
    value: T,
    label: @Composable () -> Unit = {},
    convert: (String) -> T,
    onValueChange: (T) -> Unit
) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    var focused by remember { mutableStateOf(false) }

    @Composable
    fun getBorderColor(): Color {
        if (focused) return MaterialTheme.colors.primary
        return MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
    }

    fun saveText() {
        val convertedValue: T = convert(text)
        if (convertedValue != null) {
            onValueChange(convertedValue)
        } else {
            text = value.toString()
            onValueChange(value)
        }
    }

    BasicTextField(
        value = text,
        onValueChange = { text = it },
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .border(width = 1.dp, color = getBorderColor())
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                label()
                innerTextField()
            }
        },
        modifier = modifier
            .shortcuts {
                on(Key.Enter) {
                    saveText()
                }
                on(Key.NumPadEnter) {
                    saveText()
                }
                on(Key.Escape) {
                    text = value.toString()
                }
            }
            .onFocusChanged {
                if (it == FocusState.Active) {
                    focused = true
                }
                if (it == FocusState.Inactive) {
                    focused = false
                    saveText()
                }
            }
            .height(24.dp)
        ,
        textStyle = MaterialTheme.typography.body2.copy(
            color = MaterialTheme.colors.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colors.onSurface)
    )
}